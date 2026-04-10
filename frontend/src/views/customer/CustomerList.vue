<template>
  <div class="page-container">
    <div class="page-header">
      <h2>会员管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增会员
      </el-button>
    </div>

    <el-card class="mb-16">
      <el-row :gutter="12">
        <el-col :span="12">
          <el-input v-model="keyword" placeholder="搜索姓名/手机号..." clearable prefix-icon="Search"
                    @keyup.enter="fetchData" />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="keyword = ''; fetchData()">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="customers" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag :type="levelMap[row.memberLevel]?.type" size="small">
              {{ levelMap[row.memberLevel]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberBalance" label="余额(元)" width="100">
          <template #default="{ row }">¥{{ row.memberBalance }}</template>
        </el-table-column>
        <el-table-column prop="visitCount" label="消费次数" width="90" />
        <el-table-column prop="totalSpent" label="累计消费" width="110">
          <template #default="{ row }">¥{{ row.totalSpent }}</template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="注册时间" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" @click="openTopUp(row)">充值</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editRow ? '编辑会员' : '新增会员'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item v-if="!editRow" label="充值金额">
          <el-input-number v-model="form.topUpAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 充值弹窗 -->
    <el-dialog v-model="topUpVisible" title="会员充值" width="360px">
      <div v-if="topUpTarget" style="margin-bottom:16px; color:#374151;">
        会员：<strong>{{ topUpTarget.name }}</strong>（{{ topUpTarget.phone }}）<br/>
        当前余额：<strong style="color:#e94560;">¥{{ topUpTarget.memberBalance }}</strong>
      </div>
      <el-form label-width="90px">
        <el-form-item label="充值金额">
          <el-input-number v-model="topUpAmount" :min="1" :precision="2" :step="100" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="topUpVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleTopUp">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCustomers, createCustomer, updateCustomer } from '@/api/customer'
import { ElMessage } from 'element-plus'

const customers = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const topUpVisible = ref(false)
const submitting = ref(false)
const editRow = ref(null)
const topUpTarget = ref(null)
const topUpAmount = ref(100)
const keyword = ref('')
const formRef = ref()

const pagination = reactive({ page: 1, size: 20, total: 0 })
const form = reactive({ name: '', phone: '', email: '', notes: '', topUpAmount: 0 })

const levelMap = {
  REGULAR:  { label: '普通', type: 'info' },
  SILVER:   { label: '银卡', type: '' },
  GOLD:     { label: '金卡', type: 'warning' },
  PLATINUM: { label: '铂金', type: 'danger' }
}
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCustomers({ keyword: keyword.value || undefined, page: pagination.page - 1, size: pagination.size })
    customers.value = res.data?.content || []
    pagination.total = res.data?.totalElements || 0
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) {
    Object.assign(form, { name: row.name, phone: row.phone || '', email: row.email || '', notes: row.notes || '', topUpAmount: 0 })
  } else {
    Object.assign(form, { name: '', phone: '', email: '', notes: '', topUpAmount: 0 })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editRow.value) {
      await updateCustomer(editRow.value.id, form)
      ElMessage.success('会员信息已更新')
    } else {
      await createCustomer(form)
      ElMessage.success('会员创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const openTopUp = (row) => {
  topUpTarget.value = row
  topUpAmount.value = 100
  topUpVisible.value = true
}

const handleTopUp = async () => {
  submitting.value = true
  try {
    await updateCustomer(topUpTarget.value.id, {
      name: topUpTarget.value.name,
      phone: topUpTarget.value.phone,
      topUpAmount: topUpAmount.value
    })
    ElMessage.success(`充值成功，已充入 ¥${topUpAmount.value}`)
    topUpVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

onMounted(fetchData)
</script>
