<template>
  <div class="page-container">
    <div class="page-header">
      <h2>订单管理</h2>
      <el-button type="primary" @click="$router.push('/orders/create')">
        <el-icon><Plus /></el-icon> 新建订单
      </el-button>
    </div>

    <!-- 筛选 -->
    <el-card class="mb-16">
      <el-row :gutter="12">
        <el-col :span="5">
          <el-select v-model="filter.status" placeholder="订单状态" clearable style="width:100%" @change="fetchData">
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v.label" :value="k" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filter.type" placeholder="订单类型" clearable style="width:100%" @change="fetchData">
            <el-option label="堂食" value="DINE_IN" />
            <el-option label="外带" value="TAKEAWAY" />
            <el-option label="外卖" value="DELIVERY" />
          </el-select>
        </el-col>
        <el-col :span="10">
          <el-date-picker
            v-model="filter.dateRange"
            type="datetimerange"
            range-separator="~"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width:100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
            @change="fetchData"
          />
        </el-col>
        <el-col :span="4">
          <el-button @click="resetFilter">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="orders" v-loading="loading" stripe row-key="id">
        <el-table-column prop="orderNumber" label="订单号" min-width="150" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.orderType]?.type" size="small">
              {{ typeMap[row.orderType]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tableNumber" label="桌号" width="80" />
        <el-table-column prop="customerName" label="会员" width="90" />
        <el-table-column prop="staffName" label="服务员" width="90" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span style="color:#e94560; font-weight:600;">¥{{ row.payableAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="90">
          <template #default="{ row }">
            {{ paymentMap[row.paymentMethod] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button
              v-if="nextStatus(row.status)"
              link type="success" size="small"
              @click="handleStatusUpdate(row, nextStatus(row.status))"
            >
              {{ nextStatusLabel(row.status) }}
            </el-button>
            <el-button
              v-if="['READY','CONFIRMED','PREPARING'].includes(row.status) && !row.paymentMethod"
              link type="warning" size="small"
              @click="openPayDialog(row)"
            >收款</el-button>
            <el-button
              v-if="['PENDING','CONFIRMED'].includes(row.status)"
              link type="danger" size="small"
              @click="handleCancel(row)"
            >取消</el-button>
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

    <!-- 订单详情 -->
    <el-drawer v-model="detailVisible" title="订单详情" size="480px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border size="small" class="mb-16">
          <el-descriptions-item label="订单号" :span="2">{{ currentOrder.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ typeMap[currentOrder.orderType]?.label }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusMap[currentOrder.status]?.type" size="small">
              {{ statusMap[currentOrder.status]?.label }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="桌号">{{ currentOrder.tableNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="会员">{{ currentOrder.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务员">{{ currentOrder.staffName }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.notes || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-table :data="currentOrder.items" size="small" border>
          <el-table-column prop="itemName" label="菜品" />
          <el-table-column prop="quantity" label="数量" width="60" />
          <el-table-column prop="unitPrice" label="单价" width="80">
            <template #default="{ row }">¥{{ row.unitPrice }}</template>
          </el-table-column>
          <el-table-column prop="subtotal" label="小计" width="80">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>

        <div style="margin-top:16px; text-align:right;">
          <div>合计：<strong>¥{{ currentOrder.totalAmount }}</strong></div>
          <div v-if="currentOrder.discountAmount > 0" style="color:#e94560;">
            折扣：-¥{{ currentOrder.discountAmount }}
          </div>
          <div style="font-size:18px; font-weight:700; color:#e94560; margin-top:8px;">
            应付：¥{{ currentOrder.payableAmount }}
          </div>
          <div v-if="currentOrder.paymentMethod" style="color:#67c23a; margin-top:4px;">
            已支付（{{ paymentMap[currentOrder.paymentMethod] }}）
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 收款弹窗 -->
    <el-dialog v-model="payVisible" title="收款" width="420px">
      <el-form ref="payFormRef" :model="payForm" :rules="payRules" label-width="90px">
        <el-form-item label="应付金额">
          <span style="font-size:20px; font-weight:700; color:#e94560;">
            ¥{{ payingOrder?.payableAmount }}
          </span>
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="payForm.paymentMethod">
            <el-radio-button label="CASH">现金</el-radio-button>
            <el-radio-button label="WECHAT">微信</el-radio-button>
            <el-radio-button label="ALIPAY">支付宝</el-radio-button>
            <el-radio-button label="CARD">银行卡</el-radio-button>
            <el-radio-button label="MEMBER">会员余额</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="实收金额" prop="paidAmount">
          <el-input-number v-model="payForm.paidAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="change > 0" label="找零">
          <span style="font-size:18px; font-weight:600; color:#e94560;">¥{{ change.toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="handlePay">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getOrders, updateOrderStatus, processPayment, cancelOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const payVisible = ref(false)
const currentOrder = ref(null)
const payingOrder = ref(null)
const paying = ref(false)
const payFormRef = ref()

const filter = reactive({ status: null, type: null, dateRange: null })
const pagination = reactive({ page: 1, size: 20, total: 0 })
const payForm = reactive({ paymentMethod: 'CASH', paidAmount: 0 })
const payRules = {
  paymentMethod: [{ required: true, message: '请选择支付方式' }],
  paidAmount: [{ required: true, message: '请输入实收金额' }]
}

const statusMap = {
  PENDING:   { label: '待确认', type: 'warning' },
  CONFIRMED: { label: '已确认', type: '' },
  PREPARING: { label: '制作中', type: 'primary' },
  READY:     { label: '待取餐', type: 'success' },
  SERVED:    { label: '已上桌', type: 'success' },
  COMPLETED: { label: '已完成', type: 'info' },
  CANCELLED: { label: '已取消', type: 'danger' }
}
const typeMap = {
  DINE_IN:  { label: '堂食', type: '' },
  TAKEAWAY: { label: '外带', type: 'warning' },
  DELIVERY: { label: '外卖', type: 'danger' }
}
const paymentMap = {
  CASH: '现金', WECHAT: '微信', ALIPAY: '支付宝', CARD: '银行卡', MEMBER: '会员余额'
}

const nextStatusFlow = {
  PENDING: 'CONFIRMED', CONFIRMED: 'PREPARING',
  PREPARING: 'READY', READY: 'SERVED', SERVED: 'COMPLETED'
}
const nextStatusLabelMap = {
  PENDING: '确认', CONFIRMED: '开始制作', PREPARING: '完成制作',
  READY: '上桌', SERVED: '完成订单'
}
const nextStatus = (s) => nextStatusFlow[s]
const nextStatusLabel = (s) => nextStatusLabelMap[s]

const change = computed(() => {
  if (!payingOrder.value) return 0
  return payForm.paidAmount - payingOrder.value.payableAmount
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      status: filter.status || undefined,
      type: filter.type || undefined,
      startDate: filter.dateRange?.[0] || undefined,
      endDate: filter.dateRange?.[1] || undefined
    }
    const res = await getOrders(params)
    orders.value = res.data?.content || []
    pagination.total = res.data?.totalElements || 0
  } finally { loading.value = false }
}

const resetFilter = () => {
  Object.assign(filter, { status: null, type: null, dateRange: null })
  pagination.page = 1
  fetchData()
}

const viewDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleStatusUpdate = async (row, status) => {
  await updateOrderStatus(row.id, status)
  ElMessage.success('订单状态已更新')
  fetchData()
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm(`确定取消订单 ${row.orderNumber}？`, '取消订单', {
    confirmButtonText: '确定取消', cancelButtonText: '保留', type: 'warning'
  })
  await cancelOrder(row.id)
  ElMessage.success('订单已取消')
  fetchData()
}

const openPayDialog = (row) => {
  payingOrder.value = row
  payForm.paymentMethod = 'CASH'
  payForm.paidAmount = row.payableAmount
  payVisible.value = true
}

const handlePay = async () => {
  await payFormRef.value.validate()
  paying.value = true
  try {
    await processPayment(payingOrder.value.id, payForm)
    ElMessage.success('收款成功！')
    payVisible.value = false
    fetchData()
  } finally { paying.value = false }
}

onMounted(fetchData)
</script>
