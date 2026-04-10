import request from '@/utils/request'

export const getReservations = (params) => request.get('/reservations', { params })
export const getTodayReservations = () => request.get('/reservations/today')
export const getReservationById = (id) => request.get(`/reservations/${id}`)
export const createReservation = (data) => request.post('/reservations', data)
export const updateReservationStatus = (id, status) =>
  request.patch(`/reservations/${id}/status`, { status })
export const cancelReservation = (id) => request.delete(`/reservations/${id}`)
