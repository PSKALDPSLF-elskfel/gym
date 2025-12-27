import request from '@/utils/request'

/**
 * 创建课程时间安排
 * @param {object} params - 请求参数 { courseId, startTime, endTime, maxParticipants, status }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function createCourseSchedule(params, callbacks) {
  return request.post('/course-schedule', params, callbacks)
}

/**
 * 更新课程时间安排
 * @param {string} id - 排课ID
 * @param {object} params - 请求参数 { courseId, startTime, endTime, maxParticipants, status }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function updateCourseSchedule(id, params, callbacks) {
  return request.put(`/course-schedule/${id}`, params, callbacks)
}

/**
 * 删除课程时间安排
 * @param {string} id - 排课ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function deleteCourseSchedule(id, callbacks) {
  return request.delete(`/course-schedule/${id}`, callbacks)
}

/**
 * 根据ID查询课程时间安排
 * @param {string} id - 排课ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getCourseScheduleById(id, callbacks) {
  return request.get(`/course-schedule/${id}`, null, callbacks)
}

/**
 * 分页查询课程时间安排
 * @param {object} params - 请求参数 { current, size, courseId, status, startDate, endDate }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getCourseSchedulePage(params, callbacks) {
  return request.get('/course-schedule/page', params, callbacks)
}

/**
 * 根据课程ID查询排课列表
 * @param {string} courseId - 课程ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getCourseScheduleListByCourseId(courseId, callbacks) {
  return request.get(`/course-schedule/list-by-course/${courseId}`, null, callbacks)
}

/**
 * 更新排课状态
 * @param {number} id - 排课ID
 * @param {number} status - 状态
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function updateCourseScheduleStatus(id, status, callbacks) {
  return request.put(`/course-schedule/${id}/status?status=${status}`, null, callbacks)
}

/**
 * 批量删除过期排课
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function deleteExpiredSchedules(callbacks) {
  return request.delete('/course-schedule/delete-expired', callbacks)
}
