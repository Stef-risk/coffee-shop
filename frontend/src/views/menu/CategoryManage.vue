<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜单分类</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <el-card>
      <el-table :data="categories" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="分类名称" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="displayOrder" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch
              v-model="row.active"
              @change="toggleActive(row)"
              active-color="#67c23a"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm
              title="确定删除该分类吗？（该分类下的菜品将一并隐藏）"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editRow ? '编辑分类' : '新增分类'"
      width="480px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="如：精品咖啡" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="分类描述（可选）" />
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.displayOrder" :min="0" :max="999" />
          <span style="font-size:12px; color:#9ca3af; margin-left:8px;">数值越大越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.active" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/menu'
import { ElMessage } from 'element-plus'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editRow = ref(null)
const formRef = ref()

const form = reactive({ name: '', description: '', displayOrder: 0, active: true })
const rules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) {
    Object.assign(form, { name: row.name, description: row.description || '', displayOrder: row.displayOrder, active: row.active })
  } else {
    Object.assign(form, { name: '', description: '', displayOrder: 0, active: true })
  }
  dialogVisible.value = true
}

const resetForm = () => { formRef.value?.resetFields() }

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editRow.value) {
      await updateCategory(editRow.value.id, form)
      ElMessage.success('分类已更新')
    } else {
      await createCategory(form)
      ElMessage.success('分类创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const toggleActive = async (row) => {
  try {
    await updateCategory(row.id, { ...row, active: row.active })
    ElMessage.success(row.active ? '分类已启用' : '分类已禁用')
  } catch { row.active = !row.active }
}

const handleDelete = async (id) => {
  await deleteCategory(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>
