<template>
  <div class="page-container">
    <div class="page-header">
      <h2>新建订单</h2>
    </div>

    <el-row :gutter="16">
      <!-- 左侧：菜品选择 -->
      <el-col :xs="24" :md="14">
        <el-card>
          <template #header>
            <div style="display:flex; gap:12px; align-items:center;">
              <span style="font-weight:600;">选择菜品</span>
              <el-radio-group v-model="selectedCategory" size="small" @change="filterItems">
                <el-radio-button :value="null">全部</el-radio-button>
                <el-radio-button v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-input v-model="searchKeyword" placeholder="搜索菜品..." prefix-icon="Search"
                    clearable class="mb-16" @input="filterItems" />

          <div style="display:grid; grid-template-columns:repeat(auto-fill,minmax(150px,1fr)); gap:10px; max-height:480px; overflow-y:auto;">
            <div
              v-for="item in filteredItems" :key="item.id"
              class="item-card"
              @click="addToCart(item)"
            >
              <div class="item-name">{{ item.name }}</div>
              <div class="item-category">{{ item.categoryName }}</div>
              <div class="item-price">¥{{ item.price }}</div>
              <el-icon class="item-add"><Plus /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：购物车 + 订单信息 -->
      <el-col :xs="24" :md="10">
        <el-card class="mb-16">
          <template #header><span style="font-weight:600;">订单信息</span></template>
          <el-form :model="orderForm" label-width="80px" size="small">
            <el-form-item label="订单类型">
              <el-radio-group v-model="orderForm.orderType">
                <el-radio-button value="DINE_IN">堂食</el-radio-button>
                <el-radio-button value="TAKEAWAY">外带</el-radio-button>
                <el-radio-button value="DELIVERY">外卖</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="orderForm.orderType === 'DINE_IN'" label="选择桌台">
              <el-select v-model="orderForm.tableId" placeholder="选择桌台" style="width:100%">
                <el-option
                  v-for="t in availableTables"
                  :key="t.id"
                  :label="`${t.tableNumber}（${t.location || ''} ${t.capacity}人）`"
                  :value="t.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item v-if="orderForm.orderType === 'DELIVERY'" label="配送地址">
              <el-input v-model="orderForm.deliveryAddress" placeholder="请输入配送地址" />
            </el-form-item>
            <el-form-item label="会员手机">
              <el-input v-model="memberPhone" placeholder="输入手机号查询会员" @blur="lookupMember">
                <template #append>
                  <el-button @click="lookupMember">查询</el-button>
                </template>
              </el-input>
              <div v-if="member" style="color:#67c23a; font-size:12px; margin-top:4px;">
                会员：{{ member.name }}（{{ memberLevelMap[member.memberLevel] }}）余额：¥{{ member.memberBalance }}
              </div>
            </el-form-item>
            <el-form-item label="折扣金额">
              <el-input-number v-model="orderForm.discountAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="orderForm.notes" type="textarea" :rows="2" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card>
          <template #header>
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <span style="font-weight:600;">购物车 ({{ cart.length }})</span>
              <el-button link type="danger" @click="cart = []" v-if="cart.length">清空</el-button>
            </div>
          </template>

          <div v-if="cart.length === 0" style="text-align:center; color:#9ca3af; padding:24px;">
            请从左侧选择菜品
          </div>

          <div v-for="item in cart" :key="item.menuItemId" class="cart-item">
            <div class="cart-name">{{ item.itemName }}</div>
            <div class="cart-controls">
              <el-button circle size="small" @click="changeQty(item, -1)">
                <el-icon><Minus /></el-icon>
              </el-button>
              <span style="width:28px; text-align:center;">{{ item.quantity }}</span>
              <el-button circle size="small" type="primary" @click="changeQty(item, 1)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
            <div class="cart-subtotal">¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</div>
          </div>

          <el-divider v-if="cart.length" />
          <div v-if="cart.length" style="text-align:right;">
            <div>合计：<strong>¥{{ totalAmount.toFixed(2) }}</strong></div>
            <div v-if="orderForm.discountAmount > 0" style="color:#e94560;">
              折扣：-¥{{ orderForm.discountAmount.toFixed(2) }}
            </div>
            <div style="font-size:18px; font-weight:700; color:#e94560; margin-top:8px;">
              应付：¥{{ payableAmount.toFixed(2) }}
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            style="width:100%; margin-top:16px;"
            :disabled="cart.length === 0"
            :loading="submitting"
            @click="handleSubmit"
          >
            提交订单
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getItems, getCategories } from '@/api/menu'
import { getTables } from '@/api/table'
import { getCustomerByPhone } from '@/api/customer'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const allItems = ref([])
const filteredItems = ref([])
const categories = ref([])
const availableTables = ref([])
const cart = ref([])
const member = ref(null)
const memberPhone = ref('')
const submitting = ref(false)
const selectedCategory = ref(null)
const searchKeyword = ref('')

const memberLevelMap = { REGULAR: '普通', SILVER: '银卡', GOLD: '金卡', PLATINUM: '铂金' }

const orderForm = reactive({
  orderType: 'DINE_IN', tableId: null, customerId: null,
  discountAmount: 0, notes: '', deliveryAddress: ''
})

const totalAmount = computed(() =>
  cart.value.reduce((s, i) => s + i.unitPrice * i.quantity, 0)
)
const payableAmount = computed(() =>
  Math.max(0, totalAmount.value - (orderForm.discountAmount || 0))
)

const filterItems = () => {
  filteredItems.value = allItems.value.filter(item => {
    const catMatch = !selectedCategory.value || item.categoryId === selectedCategory.value
    const kwMatch = !searchKeyword.value ||
      item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    return catMatch && kwMatch
  })
}

const addToCart = (item) => {
  const existing = cart.value.find(i => i.menuItemId === item.id)
  if (existing) {
    existing.quantity++
  } else {
    cart.value.push({
      menuItemId: item.id, itemName: item.name,
      unitPrice: item.price, quantity: 1, notes: ''
    })
  }
}

const changeQty = (item, delta) => {
  item.quantity += delta
  if (item.quantity <= 0) {
    cart.value = cart.value.filter(i => i.menuItemId !== item.menuItemId)
  }
}

const lookupMember = async () => {
  if (!memberPhone.value) return
  try {
    const res = await getCustomerByPhone(memberPhone.value)
    member.value = res.data
    orderForm.customerId = member.value.id
    ElMessage.success(`已关联会员：${member.value.name}`)
  } catch {
    member.value = null
    orderForm.customerId = null
  }
}

const handleSubmit = async () => {
  if (orderForm.orderType === 'DINE_IN' && !orderForm.tableId) {
    ElMessage.warning('请选择桌台')
    return
  }
  if (orderForm.orderType === 'DELIVERY' && !orderForm.deliveryAddress) {
    ElMessage.warning('请输入配送地址')
    return
  }
  submitting.value = true
  try {
    const payload = {
      orderType: orderForm.orderType,
      tableId: orderForm.tableId || undefined,
      customerId: orderForm.customerId || undefined,
      discountAmount: orderForm.discountAmount || 0,
      notes: orderForm.notes || undefined,
      deliveryAddress: orderForm.deliveryAddress || undefined,
      items: cart.value.map(i => ({
        menuItemId: i.menuItemId, quantity: i.quantity, notes: i.notes || undefined
      }))
    }
    await createOrder(payload)
    ElMessage.success('订单创建成功！')
    router.push('/orders')
  } finally { submitting.value = false }
}

onMounted(async () => {
  const [itemsRes, catRes, tableRes] = await Promise.all([
    getItems({ available: true, size: 200 }),
    getCategories(),
    getTables({ status: 'AVAILABLE' })
  ])
  allItems.value = itemsRes.data?.content || []
  filteredItems.value = allItems.value
  categories.value = catRes.data || []
  availableTables.value = tableRes.data || []
})
</script>

<style scoped>
.item-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  position: relative;
  transition: all .15s;
  background: #fff;
}
.item-card:hover {
  border-color: #e94560;
  box-shadow: 0 2px 8px rgba(233,69,96,.15);
}
.item-name { font-weight: 600; font-size: 14px; margin-bottom: 4px; }
.item-category { font-size: 11px; color: #9ca3af; margin-bottom: 8px; }
.item-price { color: #e94560; font-weight: 700; font-size: 16px; }
.item-add {
  position: absolute;
  right: 8px; top: 8px;
  background: #e94560; color: #fff;
  border-radius: 50%; padding: 2px;
  font-size: 14px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}
.cart-name { flex: 1; font-size: 14px; }
.cart-controls { display: flex; align-items: center; gap: 4px; }
.cart-subtotal { width: 70px; text-align: right; font-weight: 600; color: #374151; }
</style>
