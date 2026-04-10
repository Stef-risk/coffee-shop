<template>
  <el-container style="height: 100vh;">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar" style="transition: width .25s;">
      <!-- Logo -->
      <div class="sidebar-logo">
        <span class="logo-icon">☕</span>
        <span v-if="!isCollapsed" class="logo-text">餐吧管理系统</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        class="sidebar-menu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        :default-active="activeMenu"
        router
        background-color="#1a1a2e"
        text-color="#a0aec0"
        active-text-color="#fff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据总览</template>
        </el-menu-item>

        <el-sub-menu index="order-group">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单中心</span>
          </template>
          <el-menu-item index="/orders">订单管理</el-menu-item>
          <el-menu-item index="/orders/create">新建订单</el-menu-item>
          <el-menu-item index="/kitchen">厨房看板</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/tables">
          <el-icon><Grid /></el-icon>
          <template #title>桌台管理</template>
        </el-menu-item>

        <el-sub-menu index="menu-group">
          <template #title>
            <el-icon><Food /></el-icon>
            <span>菜单管理</span>
          </template>
          <el-menu-item index="/menu/categories">菜单分类</el-menu-item>
          <el-menu-item index="/menu/items">菜品管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/customers">
          <el-icon><User /></el-icon>
          <template #title>会员管理</template>
        </el-menu-item>

        <el-menu-item index="/reservations">
          <el-icon><Calendar /></el-icon>
          <template #title>预订管理</template>
        </el-menu-item>

        <el-menu-item index="/reports">
          <el-icon><TrendCharts /></el-icon>
          <template #title>销售报表</template>
        </el-menu-item>

        <el-menu-item v-if="authStore.isManager" index="/staff">
          <el-icon><Avatar /></el-icon>
          <template #title>员工管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container direction="vertical">
      <!-- 顶栏 -->
      <el-header class="main-header" height="64px">
        <div style="display:flex; align-items:center; gap:12px;">
          <el-icon
            style="cursor:pointer; font-size:20px; color:#6b7280;"
            @click="isCollapsed = !isCollapsed"
          >
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div style="display:flex; align-items:center; gap:16px;">
          <!-- 快速下单 -->
          <el-button type="primary" size="small" @click="$router.push('/orders/create')">
            <el-icon><Plus /></el-icon> 快速下单
          </el-button>

          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand">
            <div style="display:flex; align-items:center; gap:8px; cursor:pointer;">
              <el-avatar :size="32" style="background:#e94560; font-size:14px;">
                {{ userInitial }}
              </el-avatar>
              <span style="font-size:14px; color:#374151;">{{ authStore.currentUser?.fullName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-tag size="small" :type="roleTagType">{{ roleLabel }}</el-tag>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main style="padding:0; background:#f5f7fa; overflow:hidden;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')
const userInitial = computed(() => {
  const name = authStore.currentUser?.fullName || ''
  return name.charAt(0)
})

const roleMap = {
  ADMIN: { label: '超级管理员', type: 'danger' },
  MANAGER: { label: '店长', type: 'warning' },
  CASHIER: { label: '收银员', type: 'primary' },
  KITCHEN: { label: '厨房/吧台', type: 'success' }
}
const roleLabel = computed(() => roleMap[authStore.currentUser?.role]?.label || '-')
const roleTagType = computed(() => roleMap[authStore.currentUser?.role]?.type || '')

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
    authStore.logout()
    router.push('/login')
  }
}
</script>
