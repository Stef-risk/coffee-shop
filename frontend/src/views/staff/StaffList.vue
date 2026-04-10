<template>
  <div class="page-container">
    <div class="page-header">
      <h2>员工管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增员工
      </el-button>
    </div>

    <el-card class="mb-16">
      <el-input v-model="keyword" placeholder="搜索员工姓名..." prefix-icon="Search"
                clearable style="width:300px;" @keyup.enter="fetchData" />
      <el-button style="margin-left:8px;" type="primary" @click="fetchData">查询</el-button>
    </el-card>

    <el-card>
      <el-table :data="staffList" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="fullName" label="姓名" width="100" />
        <el-table-column label="角色" width="110">
          <template #default="{ row }">
            <el-tag :type="roleMap[row.role]?.type" size="small">{{ roleMap[row.role]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'info'" size="small">
              {{ row.active ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="入职时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm
              :title="`确定${row.active ? '停用' : '启用'}员工 ${row.fullName}？`"
              @confirm="handleToggle(row)"
            >
              <template #reference>
                <el-button link :type="row.active ? 'danger' : 'success'">
                  {{ row.active ? '停用' : '启用' }}
                </el-button>
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
          layout="total, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editRow ? '编辑员工' : '新增员工'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!editRow" />
        </el-form-item>
        <el-form-item label="密码" :prop="editRow ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password
                    :placeholder="editRow ? '留空则不修改' : '至少6位'" />
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option v-for="(v, k) in roleMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
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
import { getStaffList, createStaff, updateStaff, deleteStaff } from '@/api/staff'
import { ElMessage } from 'element-plus'

const staffList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editRow = ref(null)
const keyword = ref('')
const formRef = ref()

const pagination = reactive({ page: 1, size: 20, total: 0 })
const form = reactive({ username: '', password: '', fullName: '', role: 'CASHIER', phone: '', email: '' })

const roleMap = {
  ADMIN:   { label: '超级管理员', type: 'danger' },
  MANAGER: { label: '店长/经理', type: 'warning' },
  CASHIER: { label: '收银员', type: 'primary' },
  KITCHEN: { label: '厨房/吧台', type: 'success' }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  fullName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStaffList({ keyword: keyword.value || undefined, page: pagination.page - 1, size: pagination.size })
    staffList.value = res.data?.content || []
    pagination.total = res.data?.totalElements || 0
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) {
    Object.assign(form, { username: row.username, password: '', fullName: row.fullName, role: row.role, phone: row.phone || '', email: row.email || '' })
  } else {
    Object.assign(form, { username: '', password: '', fullName: '', role: 'CASHIER', phone: '', email: '' })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = { ...form }
    if (!payload.password) delete payload.password
    if (editRow.value) {
      await updateStaff(editRow.value.id, payload)
      ElMessage.success('员工信息已更新')
    } else {
      await createStaff(payload)
      ElMessage.success('员工创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const handleToggle = async (row) => {
  await updateStaff(row.id, { ...row, active: !row.active })
  ElMessage.success(row.active ? '已停用' : '已启用')
  fetchData()
}

onMounted(fetchData)
</script>
