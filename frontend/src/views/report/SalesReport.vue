<template>
  <div class="page-container">
    <div class="page-header">
      <h2>销售报表</h2>
      <div style="display:flex; gap:12px; align-items:center;">
        <el-radio-group v-model="reportType" @change="onTypeChange">
          <el-radio-button value="daily">日报</el-radio-button>
          <el-radio-button value="monthly">月报</el-radio-button>
          <el-radio-button value="custom">自定义</el-radio-button>
        </el-radio-group>

        <el-date-picker
          v-if="reportType === 'daily'"
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
          style="width:160px;"
          @change="fetchReport"
        />
        <el-date-picker
          v-if="reportType === 'monthly'"
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          style="width:160px;"
          @change="fetchReport"
        />
        <el-date-picker
          v-if="reportType === 'custom'"
          v-model="customRange"
          type="datetimerange"
          range-separator="~"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="YYYY-MM-DDTHH:mm:ss"
          style="width:340px;"
          @change="fetchReport"
        />

        <el-button type="primary" @click="fetchReport" :loading="loading">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <!-- 核心指标 -->
    <el-row :gutter="16" class="mb-20" v-loading="loading">
      <el-col :xs="12" :sm="6" v-for="card in summaryCards" :key="card.label">
        <el-card style="text-align:center; padding:8px 0;">
          <div style="font-size:28px; font-weight:700; margin-bottom:4px;" :style="{ color: card.color }">
            {{ card.value }}
          </div>
          <div style="font-size:13px; color:#6b7280;">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 逐日营收折线图 -->
      <el-col :span="14" v-if="report?.dailyRevenue?.length">
        <el-card class="mb-16">
          <template #header><span style="font-weight:600;">营收趋势</span></template>
          <div ref="chartRef" style="height:260px;"></div>
        </el-card>
      </el-col>

      <!-- Top10 热销商品 -->
      <el-col :span="report?.dailyRevenue?.length ? 10 : 24">
        <el-card>
          <template #header><span style="font-weight:600;">热销商品 Top 10</span></template>
          <el-table :data="report?.topItems || []" size="small" stripe>
            <el-table-column type="index" label="排名" width="55" />
            <el-table-column prop="itemName" label="商品名称" />
            <el-table-column prop="quantity" label="销量" width="70" />
            <el-table-column label="销售额" width="90">
              <template #default="{ row }">
                <span style="color:#e94560; font-weight:600;">¥{{ row.revenue }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!(report?.topItems?.length)" description="暂无销售数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 逐日明细表 -->
    <el-card v-if="report?.dailyRevenue?.length" style="margin-top:16px;">
      <template #header><span style="font-weight:600;">逐日明细</span></template>
      <el-table :data="report.dailyRevenue" size="small" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="orderCount" label="完成订单" width="100" />
        <el-table-column label="日营收" min-width="120">
          <template #default="{ row }">
            <span style="color:#e94560; font-weight:600;">¥{{ row.revenue }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { getDailyReport, getMonthlyReport, getCustomReport } from '@/api/report'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const reportType = ref('daily')
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const selectedMonth = ref(new Date())
const customRange = ref(null)
const loading = ref(false)
const report = ref(null)
const chartRef = ref()
let chart = null

const summaryCards = computed(() => {
  const r = report.value
  return [
    { label: '总营收', value: r ? `¥${r.totalRevenue}` : '-', color: '#e94560' },
    { label: '订单总数', value: r?.totalOrders ?? '-', color: '#1d4ed8' },
    { label: '完成订单', value: r?.completedOrders ?? '-', color: '#16a34a' },
    { label: '客单价', value: r ? `¥${r.avgOrderAmount}` : '-', color: '#d97706' }
  ]
})

const fetchReport = async () => {
  loading.value = true
  try {
    let res
    if (reportType.value === 'daily') {
      res = await getDailyReport(selectedDate.value)
    } else if (reportType.value === 'monthly') {
      const d = dayjs(selectedMonth.value)
      res = await getMonthlyReport(d.year(), d.month() + 1)
    } else if (customRange.value?.length === 2) {
      res = await getCustomReport(customRange.value[0], customRange.value[1])
    } else {
      return
    }
    report.value = res.data
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

const renderChart = () => {
  const data = report.value?.dailyRevenue
  if (!data?.length || !chartRef.value) return

  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.date) },
    yAxis: { type: 'value', name: '营收(元)' },
    series: [{
      name: '日营收',
      type: 'line',
      smooth: true,
      data: data.map(d => d.revenue),
      areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(233,69,96,.3)' }, { offset: 1, color: 'rgba(233,69,96,0)' }] } },
      lineStyle: { color: '#e94560', width: 2 },
      itemStyle: { color: '#e94560' }
    }]
  })
}

const onTypeChange = () => {
  report.value = null
  if (reportType.value !== 'custom') fetchReport()
}

onMounted(fetchReport)
</script>
