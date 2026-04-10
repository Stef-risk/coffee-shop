import request from '@/utils/request'

export const getDailyReport = (date) =>
  request.get('/reports/daily', { params: date ? { date } : {} })
export const getMonthlyReport = (year, month) =>
  request.get('/reports/monthly', { params: { year, month } })
export const getCustomReport = (startDate, endDate) =>
  request.get('/reports/custom', { params: { startDate, endDate } })
