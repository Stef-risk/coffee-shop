<template>
  <div class="page-container">
    <div class="page-header">
      <h2>厨房看板</h2>
      <div style="display:flex; gap:12px; align-items:center;">
        <el-tag v-for="s in displayStatuses" :key="s.key" :type="s.type">
          {{ s.label }}: {{ countByStatus(s.key) }}
        </el-tag>
        <el-button :icon="Refresh" circle @click="fetchData" :loading="loading" />
        <el-switch v-model="autoRefresh" active-text="自动刷新(30s)" />
      </div>
    </div>

    <div class="kitchen-board">
      <div
        v-for="order in activeOrders"
        :key="order.id"
        class="kitchen-card"
        :class="`status-${order.status}`"
      >
        <div class="kc-header">
          <span>{{ order.orderNumber }}</span>
          <div style="display:flex; gap:8px; align-items:center;">
            <el-tag size="small">{{ typeMap[order.orderType] }}</el-tag>
            <span v-if="order.tableNumber">{{ order.tableNumber }}</span>
            <span style="font-size:11px; opacity:.8;">{{ timeAgo(order.createdAt) }}</span>
          </div>
        </div>

        <div class="kc-body">
          <div v-for="item in order.items" :key="item.id" class="kc-item">
            <span>{{ item.itemName }}</span>
            <strong style="font-size:16px;">× {{ item.quantity }}</strong>
          </div>
          <div v-if="order.notes" style="margin-top:8px; font-size:12px; color:#6b7280; font-style:italic;">
            备注：{{ order.notes }}
          </div>
        </div>

        <div class="kc-footer">
          <el-button
            v-if="order.status === 'PENDING'"
            type="primary" size="small" style="width:100%;"
            @click="updateStatus(order, 'CONFIRMED')"
          >确认接单</el-button>
          <el-button
            v-if="order.status === 'CONFIRMED'"
            type="warning" size="small" style="width:100%;"
            @click="updateStatus(order, 'PREPARING')"
          >开始制作</el-button>
          <el-button
            v-if="order.status === 'PREPARING'"
            type="success" size="small" style="width:100%;"
            @click="updateStatus(order, 'READY')"
          >完成 · 可取餐</el-button>
          <el-button
            v-if="order.status === 'READY'"
            size="small" style="width:100%;"
            @click="updateStatus(order, 'SERVED')"
          >已上桌</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && activeOrders.length === 0"
              description="暂无待处理订单，一切顺利！" :image-size="120" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getActiveOrders, updateOrderStatus } from '@/api/order'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const activeOrders = ref([])
const loading = ref(false)
const autoRefresh = ref(true)
let timer = null

const displayStatuses = [
  { key: 'PENDING',   label: '待确认', type: 'warning' },
  { key: 'CONFIRMED', label: '已确认', type: '' },
  { key: 'PREPARING', label: '制作中', type: 'primary' },
  { key: 'READY',     label: '待取餐', type: 'success' }
]

const typeMap = { DINE_IN: '堂食', TAKEAWAY: '外带', DELIVERY: '外卖' }

const countByStatus = (status) =>
  activeOrders.value.filter(o => o.status === status).length

const timeAgo = (time) => dayjs(time).fromNow()

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getActiveOrders()
    activeOrders.value = (res.data || []).filter(o =>
      ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'].includes(o.status)
    )
  } finally { loading.value = false }
}

const updateStatus = async (order, status) => {
  await updateOrderStatus(order.id, status)
  ElMessage.success('状态已更新')
  fetchData()
}

onMounted(() => {
  fetchData()
  timer = setInterval(() => {
    if (autoRefresh.value) fetchData()
  }, 30000)
})

onUnmounted(() => clearInterval(timer))
</script>
