import request from '@/utils/request'

export const getStaffList = (params) => request.get('/staff', { params })
export const getStaffById = (id) => request.get(`/staff/${id}`)
export const createStaff = (data) => request.post('/staff', data)
export const updateStaff = (id, data) => request.put(`/staff/${id}`, data)
export const deleteStaff = (id) => request.delete(`/staff/${id}`)
