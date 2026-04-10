import request from '@/utils/request'

// 分类
export const getCategories = () => request.get('/menu/categories')
export const createCategory = (data) => request.post('/menu/categories', data)
export const updateCategory = (id, data) => request.put(`/menu/categories/${id}`, data)
export const deleteCategory = (id) => request.delete(`/menu/categories/${id}`)

// 菜品
export const getItems = (params) => request.get('/menu/items', { params })
export const getItemsByCategory = (categoryId) => request.get(`/menu/items/category/${categoryId}`)
export const getItemById = (id) => request.get(`/menu/items/${id}`)
export const createItem = (data) => request.post('/menu/items', data)
export const updateItem = (id, data) => request.put(`/menu/items/${id}`, data)
export const toggleItemAvailability = (id, available) =>
  request.patch(`/menu/items/${id}/availability`, { available })
export const deleteItem = (id) => request.delete(`/menu/items/${id}`)
