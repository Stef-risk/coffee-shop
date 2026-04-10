import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { public: true, title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/Dashboard.vue'),
          meta: { title: '数据总览', icon: 'DataAnalysis' }
        },
        {
          path: 'orders',
          name: 'OrderList',
          component: () => import('@/views/order/OrderList.vue'),
          meta: { title: '订单管理', icon: 'Document' }
        },
        {
          path: 'orders/create',
          name: 'OrderCreate',
          component: () => import('@/views/order/OrderCreate.vue'),
          meta: { title: '新建订单', icon: 'Plus' }
        },
        {
          path: 'kitchen',
          name: 'KitchenDisplay',
          component: () => import('@/views/order/KitchenDisplay.vue'),
          meta: { title: '厨房看板', icon: 'Monitor' }
        },
        {
          path: 'tables',
          name: 'TableManage',
          component: () => import('@/views/table/TableManage.vue'),
          meta: { title: '桌台管理', icon: 'Grid' }
        },
        {
          path: 'menu/categories',
          name: 'CategoryManage',
          component: () => import('@/views/menu/CategoryManage.vue'),
          meta: { title: '菜单分类', icon: 'Menu' }
        },
        {
          path: 'menu/items',
          name: 'ItemManage',
          component: () => import('@/views/menu/ItemManage.vue'),
          meta: { title: '菜品管理', icon: 'Food' }
        },
        {
          path: 'customers',
          name: 'CustomerList',
          component: () => import('@/views/customer/CustomerList.vue'),
          meta: { title: '会员管理', icon: 'User' }
        },
        {
          path: 'reservations',
          name: 'ReservationList',
          component: () => import('@/views/reservation/ReservationList.vue'),
          meta: { title: '预订管理', icon: 'Calendar' }
        },
        {
          path: 'reports',
          name: 'SalesReport',
          component: () => import('@/views/report/SalesReport.vue'),
          meta: { title: '销售报表', icon: 'TrendCharts' }
        },
        {
          path: 'staff',
          name: 'StaffList',
          component: () => import('@/views/staff/StaffList.vue'),
          meta: { title: '员工管理', icon: 'Avatar' }
        }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '管理系统'} - 咖啡餐吧`
  const token = localStorage.getItem('access_token')
  if (!to.meta.public && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
