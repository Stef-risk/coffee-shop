import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('access_token') || null,
    userInfo: (() => {
      try {
        return JSON.parse(localStorage.getItem('user_info')) || null
      } catch {
        return null
      }
    })()
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    currentUser: (state) => state.userInfo,
    isAdmin: (state) => state.userInfo?.role === 'ADMIN',
    isManager: (state) => ['ADMIN', 'MANAGER'].includes(state.userInfo?.role),
    isCashier: (state) => ['ADMIN', 'MANAGER', 'CASHIER'].includes(state.userInfo?.role)
  },

  actions: {
    async login(username, password) {
      const res = await loginApi({ username, password })
      const data = res.data
      this.token = data.accessToken
      this.userInfo = {
        id: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role
      }
      localStorage.setItem('access_token', data.accessToken)
      localStorage.setItem('user_info', JSON.stringify(this.userInfo))
    },

    logout() {
      this.token = null
      this.userInfo = null
      localStorage.removeItem('access_token')
      localStorage.removeItem('user_info')
    }
  }
})
