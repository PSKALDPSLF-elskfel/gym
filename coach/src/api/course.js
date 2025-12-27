import request from '@/utils/request'

/**
 * 获取我负责的课程列表（教练端专用）
 * @returns {Promise} 返回课程列表
 */
export const getMyManagedCourses = () => {
  return request.get('/course/my-courses')
}

/**
 * 获取我的课程排课列表
 * @param {Object} params - 查询参数 { startDate, endDate, status }
 */
export const getMyCourses = (params) => {
  return request.get('/course-schedule/page', { params })
}

/**
 * 获取课程详情
 * @param {Number} scheduleId - 排课ID
 */
export const getCourseDetail = (scheduleId) => {
  return request.get(`/course-schedule/${scheduleId}`)
}

/**
 * 获取课程预约列表(参与情况)
 * @param {Number} scheduleId - 排课ID
 */
export const getCourseBookings = (scheduleId) => {
  return request.get(`/booking/schedule/${scheduleId}`)
}

/**
 * 学员签到
 * @param {Number} scheduleId - 排课ID
 * @param {Number} bookingId - 预约ID
 */
export const signInStudent = (scheduleId, bookingId) => {
  return request.post(`/course-schedule/${scheduleId}/sign-in`, { bookingId })
}

/**
 * 更新排课状态
 * @param {Number} scheduleId - 排课ID
 * @param {Number} status - 状态
 */
export const updateCourseStatus = (scheduleId, status) => {
  return request.put(`/course-schedule/${scheduleId}/status`, null, { params: { status } })
}
