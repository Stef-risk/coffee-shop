<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜品管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增菜品
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="mb-16">
      <el-row :gutter="12">
        <el-col :span="6">
          <el-select v-model="filter.categoryId" placeholder="全部分类" clearable style="width:100%"
                     @change="fetchData">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-input v-model="filter.keyword" placeholder="搜索菜品名称..." clearable
                    prefix-icon="Search" @keyup.enter="fetchData" />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filter.available" placeholder="上架状态" clearable style="width:100%"
                     @change="fetchData">
            <el-option label="已上架" :value="true" />
            <el-option label="已下架" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="items" v-loading="loading" stripe>
        <el-table-column prop="name" label="菜品名称" min-width="130" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">
            <span style="color:#e94560; font-weight:600;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="displayOrder" label="排序" width="70" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch
              v-model="row.available"
              @change="(val) => handleToggle(row, val)"
              active-color="#67c23a"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该菜品吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="text-right" style="margin-top:16px;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editRow ? '编辑菜品' : '新增菜品'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元)" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.displayOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="是否上架">
          <el-switch v-model="form.available" />
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
import { getItems, createItem, updateItem, toggleItemAvailability, deleteItem } from '@/api/menu'
import { getCategories } from '@/api/menu'
import { ElMessage } from 'element-plus'

const items = ref([])
const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editRow = ref(null)
const formRef = ref()

const filter = reactive({ categoryId: null, keyword: '', available: null })
const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  name: '', categoryId: null, price: 0, description: '',
  imageUrl: '', displayOrder: 0, available: true
})
const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getItems({
      categoryId: filter.categoryId || undefined,
      keyword: filter.keyword || undefined,
      available: filter.available !== null ? filter.available : undefined,
      page: pagination.page - 1,
      size: pagination.size
    })
    items.value = res.data?.content || []
    pagination.total = res.data?.totalElements || 0
  } finally { loading.value = false }
}

const resetFilter = () => {
  Object.assign(filter, { categoryId: null, keyword: '', available: null })
  pagination.page = 1
  fetchData()
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) {
    Object.assign(form, {
      name: row.name, categoryId: row.categoryId, price: row.price,
      description: row.description || '', imageUrl: row.imageUrl || '',
      displayOrder: row.displayOrder, available: row.available
    })
  } else {
    Object.assign(form, { name: '', categoryId: null, price: 0, description: '', imageUrl: '', displayOrder: 0, available: true })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editRow.value) {
      await updateItem(editRow.value.id, form)
      ElMessage.success('菜品已更新')
    } else {
      await createItem(form)
      ElMessage.success('菜品创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const handleToggle = async (row, val) => {
  try {
    await toggleItemAvailability(row.id, val)
    ElMessage.success(val ? '已上架' : '已下架')
  } catch { row.available = !val }
}

const handleDelete = async (id) => {
  await deleteItem(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data || []
  fetchData()
})
</script>
