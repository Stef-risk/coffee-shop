<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预订管理</h2>
      <div style="display:flex; gap:12px;">
        <el-button @click="viewToday" :type="showingToday ? 'primary' : ''">今日预订</el-button>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon> 新增预订
        </el-button>
      </div>
    </div>

    <el-card class="mb-16" v-if="todayReservations.length">
      <template #header>
        <span style="font-weight:600; color:#e94560;">📅 今日预订 ({{ todayReservations.length }})</span>
      </template>
      <el-row :gutter="12">
        <el-col :xs="24" :sm="12" :md="8" v-for="r in todayReservations" :key="r.id">
          <div style="border:1px solid #e5e7eb; border-radius:8px; padding:12px; background:#fffbeb;">
            <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
              <strong>{{ r.contactName }}</strong>
              <el-tag :type="resStatusMap[r.status]?.type" size="small">{{ resStatusMap[r.status]?.label }}</el-tag>
            </div>
            <div style="font-size:13px; color:#6b7280;">
              📞 {{ r.contactPhone }} &nbsp;·&nbsp; 👥 {{ r.partySize }}人
            </div>
            <div style="font-size:13px; color:#6b7280; margin-top:4px;">
              ⏰ {{ r.reservationTime }} &nbsp;·&nbsp; 🪑 {{ r.tableNumber || '未指定' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 主列表 -->
    <el-card>
      <div class="mb-16" style="display:flex; gap:12px;">
        <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width:160px" @change="fetchData">
          <el-option v-for="(v, k) in resStatusMap" :key="k" :label="v.label" :value="k" />
        </el-select>
      </div>

      <el-table :data="reservations" v-loading="loading" stripe>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="手机号" width="130" />
        <el-table-column prop="partySize" label="人数" width="70" />
        <el-table-column prop="reservationTime" label="预订时间" min-width="160" />
        <el-table-column prop="tableNumber" label="桌台" width="90" />
        <el-table-column prop="customerName" label="会员" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="resStatusMap[row.status]?.type" size="small">
              {{ resStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" v-if="row.status === 'PENDING'"
                       @click="updateStatus(row, 'CONFIRMED')">确认</el-button>
            <el-button link type="primary" v-if="row.status === 'CONFIRMED'"
                       @click="updateStatus(row, 'ARRIVED')">已到店</el-button>
            <el-button link type="warning" v-if="row.status === 'CONFIRMED'"
                       @click="updateStatus(row, 'NO_SHOW')">未到店</el-button>
            <el-popconfirm title="确定取消此预订？" @confirm="handleCancel(row.id)"
                           v-if="!['CANCELLED','ARRIVED'].includes(row.status)">
              <template #reference>
                <el-button link type="danger">取消</el-button>
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

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增预订" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-form-item label="预订时间" prop="reservationTime">
          <el-date-picker v-model="form.reservationTime" type="datetime" style="width:100%"
                          value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择预订时间" />
        </el-form-item>
        <el-form-item label="用餐人数" prop="partySize">
          <el-input-number v-model="form.partySize" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getReservations, getTodayReservations, createReservation, updateReservationStatus, cancelReservation } from '@/api/reservation'
import { ElMessage } from 'element-plus'

const reservations = ref([])
const todayReservations = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const filterStatus = ref(null)
const showingToday = ref(false)
const formRef = ref()

const pagination = reactive({ page: 1, size: 20, total: 0 })
const form = reactive({ contactName: '', contactPhone: '', reservationTime: '', partySize: 2, notes: '' })

const resStatusMap = {
  PENDING:   { label: '待确认', type: 'warning' },
  CONFIRMED: { label: '已确认', type: 'primary' },
  ARRIVED:   { label: '已到店', type: 'success' },
  CANCELLED: { label: '已取消', type: 'danger' },
  NO_SHOW:   { label: '未到店', type: 'info' }
}

const rules = {
  contactName: [{ required: true, message: '请输入联系人姓名' }],
  contactPhone: [{ required: true, message: '请输入联系电话' }],
  reservationTime: [{ required: true, message: '请选择预订时间' }],
  partySize: [{ required: true, message: '请输入用餐人数' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getReservations({ status: filterStatus.value || undefined, page: pagination.page - 1, size: pagination.size })
    reservations.value = res.data?.content || []
    pagination.total = res.data?.totalElements || 0
  } finally { loading.value = false }
}

const viewToday = async () => {
  showingToday.value = !showingToday.value
  if (showingToday.value) {
    const res = await getTodayReservations()
    todayReservations.value = res.data || []
  } else {
    todayReservations.value = []
  }
}

const openDialog = () => {
  Object.assign(form, { contactName: '', contactPhone: '', reservationTime: '', partySize: 2, notes: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createReservation(form)
    ElMessage.success('预订已创建')
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const updateStatus = async (row, status) => {
  await updateReservationStatus(row.id, status)
  ElMessage.success('状态已更新')
  fetchData()
}

const handleCancel = async (id) => {
  await cancelReservation(id)
  ElMessage.success('预订已取消')
  fetchData()
}

onMounted(fetchData)
</script>
