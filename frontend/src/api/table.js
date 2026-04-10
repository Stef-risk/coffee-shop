import request from '@/utils/request'

export const getTables = (params) => request.get('/tables', { params })
export const getTableById = (id) => request.get(`/tables/${id}`)
export const createTable = (data) => request.post('/tables', data)
export const updateTable = (id, data) => request.put(`/tables/${id}`, data)
export const updateTableStatus = (id, status) =>
  request.patch(`/tables/${id}/status`, { status })
export const deleteTable = (id) => request.delete(`/tables/${id}`)
