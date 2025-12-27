import request from '@/utils/request'

// 获取教练月度排班
export const getMonthlySchedule = (params) => {
  return request.get('/coach-schedule/schedule/monthly', { params })
}

// 获取教练今日排班
export const getTodaySchedule = (params) => {
  return request.get('/coach-schedule/schedule/today', { params })
}

// 创建排班
export const createSchedule = (data) => {
  return request.post('/coach-schedule/schedule', data)
}

// 更新排班
export const updateSchedule = (id, data) => {
  return request.put(`/coach-schedule/schedule/${id}`, data)
}

// 删除排班
export const deleteSchedule = (id) => {
  return request.delete(`/coach-schedule/schedule/${id}`)
}

// 提交排班申请
export const submitScheduleRequest = (coachId, data) => {
  return request.post(`/coach-schedule/request?coachId=${coachId}`, data)
}

// 获取排班申请记录
export const getScheduleRequests = (params) => {
  return request.get('/coach-schedule/request/list', { params })
}

// 取消排班申请
export const cancelScheduleRequest = (requestId) => {
  return request.delete(`/coach-schedule/request/${requestId}`)
}

// 审批排班申请
export const approveScheduleRequest = (requestId, adminId, data) => {
  return request.put(`/coach-schedule/request/${requestId}/approve?adminId=${adminId}`, data)
}

// 打卡入场
export const checkIn = (coachId, data) => {
  return request.post(`/coach-schedule/attendance/check-in?coachId=${coachId}`, data)
}

// 打卡离场
export const checkOut = (coachId, data) => {
  return request.post(`/coach-schedule/attendance/check-out?coachId=${coachId}`, data)
}

// 获取打卡记录
export const getAttendanceRecords = (params) => {
  return request.get('/coach-schedule/attendance/records', { params })
}

// 获取月度统计
export const getMonthlyStatistics = (params) => {
  return request.get('/coach-schedule/statistics/monthly', { params })
}