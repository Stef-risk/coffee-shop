import request from '@/utils/request'

export const getCustomers = (params) => request.get('/customers', { params })
export const getCustomerById = (id) => request.get(`/customers/${id}`)
export const getCustomerByPhone = (phone) => request.get(`/customers/phone/${phone}`)
export const createCustomer = (data) => request.post('/customers', data)
export const updateCustomer = (id, data) => request.put(`/customers/${id}`, data)
