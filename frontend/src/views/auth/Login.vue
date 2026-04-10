<template>
  <div class="login-wrap">
    <div class="login-left">
      <div class="brand">
        <div class="brand-icon">☕</div>
        <h1>咖啡餐吧管理系统</h1>
        <p>Coffee & Restaurant Management Platform</p>
      </div>
      <div class="features">
        <div class="feat-item" v-for="f in features" :key="f.title">
          <el-icon class="feat-icon"><component :is="f.icon" /></el-icon>
          <div>
            <div class="feat-title">{{ f.title }}</div>
            <div class="feat-desc">{{ f.desc }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <el-card class="login-card" shadow="always">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>请输入账号和密码登录系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              style="width:100%; height:44px; font-size:16px;"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-hint">
          <el-alert type="info" :closable="false" show-icon>
            <template #default>
              默认账号：<strong>admin / Admin@123456</strong>
            </template>
          </el-alert>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const features = [
  { icon: 'Document', title: '订单管理', desc: '全流程订单跟踪，支持堂食/外带/外卖' },
  { icon: 'Food', title: '菜单管理', desc: '分类管理，随时上下架菜品' },
  { icon: 'TrendCharts', title: '销售报表', desc: '日报/月报，Top10热销商品分析' },
  { icon: 'Monitor', title: '厨房看板', desc: '实时显示制作中订单，提升出餐效率' }
]

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/dashboard')
  } catch {
    // 错误已在 request.js 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  color: #fff;
}

.brand { margin-bottom: 60px; }
.brand-icon { font-size: 64px; margin-bottom: 16px; }
.brand h1 { font-size: 32px; font-weight: 700; margin-bottom: 8px; }
.brand p  { font-size: 16px; opacity: .7; }

.features { display: flex; flex-direction: column; gap: 24px; }
.feat-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.feat-icon {
  font-size: 24px;
  color: #e94560;
  margin-top: 2px;
  flex-shrink: 0;
}
.feat-title { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.feat-desc  { font-size: 13px; opacity: .7; }

.login-right {
  width: 440px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card { width: 100%; border-radius: 16px !important; }

.login-header { text-align: center; margin-bottom: 32px; }
.login-header h2 { font-size: 26px; font-weight: 700; color: #1f2937; }
.login-header p  { color: #6b7280; margin-top: 6px; }

.login-hint { margin-top: 16px; }

@media (max-width: 768px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
