<template>
  <div class="page-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-20">
      <el-col :xs="12" :sm="6" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{ background: card.bg }">
          <div class="stat-icon">{{ card.icon }}</div>
          <div class="stat-info">
            <div class="value">{{ card.value }}</div>
            <div class="label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 活跃订单 -->
      <el-col :xs="24" :md="14">
        <el-card>
          <template #header>
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <span style="font-weight:600;">进行中的订单</span>
              <el-button link type="primary" @click="$router.push('/orders')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="activeOrders" stripe size="small" v-loading="loadingOrders">
            <el-table-column prop="orderNumber" label="订单号" width="140" />
            <el-table-column prop="orderType" label="类型" width="70">
              <template #default="{ row }">
                <el-tag :type="typeTagMap[row.orderType]?.type" size="small">
                  {{ typeTagMap[row.orderType]?.label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tableNumber" label="桌号" width="70" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusTagMap[row.status]?.type" size="small">
                  {{ statusTagMap[row.status]?.label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="payableAmount" label="金额" width="80">
              <template #default="{ row }">¥{{ row.payableAmount }}</template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="80">
              <template #default="{ row }">
                <el-button link size="small" @click="$router.push('/orders')">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!loadingOrders && activeOrders.length === 0"
               style="text-align:center; padding:24px; color:#9ca3af;">
            暂无进行中订单
          </div>
        </el-card>
      </el-col>

      <!-- 桌台状态 + 快捷操作 -->
      <el-col :xs="24" :md="10">
        <el-card class="mb-16">
          <template #header><span style="font-weight:600;">桌台状态</span></template>
          <div style="display:flex; gap:12px; flex-wrap:wrap;">
            <div v-for="s in tableStats" :key="s.label"
                 :style="{ background: s.bg, borderRadius: '8px', padding: '12px 20px', flex: '1', minWidth: '80px', textAlign: 'center' }">
              <div style="font-size:24px; font-weight:700;" :style="{ color: s.color }">{{ s.count }}</div>
              <div style="font-size:12px; color:#6b7280; margin-top:4px;">{{ s.label }}</div>
            </div>
          </div>
        </el-card>

        <el-card>
          <template #header><span style="font-weight:600;">快捷操作</span></template>
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px;">
            <el-button v-for="q in quickActions" :key="q.label"
                       :type="q.type" plain style="height:64px; flex-direction:column; gap:4px;"
                       @click="$router.push(q.path)">
              <el-icon style="font-size:20px;"><component :is="q.icon" /></el-icon>
              <span style="font-size:12px;">{{ q.label }}</span>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getActiveOrders } from '@/api/order'
import { getTables } from '@/api/table'
import { getDailyReport } from '@/api/report'

const activeOrders = ref([])
const allTables = ref([])
const todayRevenue = ref(0)
const loadingOrders = ref(false)

const statusTagMap = {
  PENDING:   { label: '待确认', type: 'warning' },
  CONFIRMED: { label: '已确认', type: '' },
  PREPARING: { label: '制作中', type: 'primary' },
  READY:     { label: '待取餐', type: 'success' },
  SERVED:    { label: '已上桌', type: 'success' },
  COMPLETED: { label: '已完成', type: 'info' },
  CANCELLED: { label: '已取消', type: 'danger' }
}
const typeTagMap = {
  DINE_IN:  { label: '堂食', type: '' },
  TAKEAWAY: { label: '外带', type: 'warning' },
  DELIVERY: { label: '外卖', type: 'danger' }
}

const tableStats = computed(() => {
  const t = allTables.value
  return [
    { label: '空闲', count: t.filter(x => x.status === 'AVAILABLE').length, bg: '#f0fdf4', color: '#16a34a' },
    { label: '使用中', count: t.filter(x => x.status === 'OCCUPIED').length, bg: '#fff1f2', color: '#dc2626' },
    { label: '预订', count: t.filter(x => x.status === 'RESERVED').length, bg: '#fffbeb', color: '#d97706' },
    { label: '清洁中', count: t.filter(x => x.status === 'CLEANING').length, bg: '#eff6ff', color: '#2563eb' }
  ]
})

const statCards = computed(() => [
  {
    icon: '💰', label: '今日营收(元)', value: `¥${todayRevenue.value}`,
    bg: 'linear-gradient(135deg,#667eea,#764ba2)'
  },
  {
    icon: '📋', label: '进行中订单', value: activeOrders.value.length,
    bg: 'linear-gradient(135deg,#f093fb,#f5576c)'
  },
  {
    icon: '🪑', label: '在用桌台', value: allTables.value.filter(t => t.status === 'OCCUPIED').length,
    bg: 'linear-gradient(135deg,#4facfe,#00f2fe)'
  },
  {
    icon: '🪑', label: '空闲桌台', value: allTables.value.filter(t => t.status === 'AVAILABLE').length,
    bg: 'linear-gradient(135deg,#43e97b,#38f9d7)'
  }
])

const quickActions = [
  { label: '新建订单', icon: 'Plus', type: 'primary', path: '/orders/create' },
  { label: '厨房看板', icon: 'Monitor', type: 'warning', path: '/kitchen' },
  { label: '桌台管理', icon: 'Grid', type: 'success', path: '/tables' },
  { label: '销售报表', icon: 'TrendCharts', type: 'info', path: '/reports' }
]

onMounted(async () => {
  loadingOrders.value = true
  try {
    const [ordersRes, tablesRes, reportRes] = await Promise.all([
      getActiveOrders(),
      getTables(),
      getDailyReport()
    ])
    activeOrders.value = ordersRes.data || []
    allTables.value = tablesRes.data || []
    todayRevenue.value = reportRes.data?.totalRevenue ?? 0
  } catch {/* 已处理 */} finally {
    loadingOrders.value = false
  }
})
</script>
