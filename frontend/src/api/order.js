import request from '@/utils/request'

export const getOrders = (params) => request.get('/orders', { params })
export const getActiveOrders = () => request.get('/orders/active')
export const getOrderById = (id) => request.get(`/orders/${id}`)
export const createOrder = (data) => request.post('/orders', data)
export const updateOrderStatus = (id, status) =>
  request.patch(`/orders/${id}/status`, { status })
export const processPayment = (id, data) => request.post(`/orders/${id}/payment`, data)
export const cancelOrder = (id) => request.post(`/orders/${id}/cancel`)
