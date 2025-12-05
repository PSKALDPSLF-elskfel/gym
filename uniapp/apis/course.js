import request from '@/utils/request.js'

/**
 * 查询所有上架的课程列表
 * 功能描述：查询所有上架状态的课程
 * 入参：无
 * 返回参数：array - 课程列表
 * url地址：/course/list-online
 * 请求方式：GET
 */
export function getCourseListOnline(callbacks) {
  return request.get('/course/list-online', null, callbacks)
}

/**
 * 根据ID查询课程详情
 * 功能描述：查询指定ID的课程详情
 * 入参：id - 课程ID
 * 返回参数：{ id, name, description, coverImage, duration, maxParticipants, price, status, statusDisplayName, createTime, updateTime }
 * url地址：/course/{id}
 * 请求方式：GET
 */
export function getCourseById(id, callbacks) {
  return request.get(`/course/${id}`, null, callbacks)
}

/**
 * 根据课程ID查询排课列表
 * 功能描述：查询指定课程的所有时间安排
 * 入参：courseId - 课程ID
 * 返回参数：array - 排课列表 [{ id, courseId, courseName, startTime, endTime, maxParticipants, currentParticipants, remainingSlots, status, statusDisplayName, canBook, isExpired, createTime, updateTime }]
 * url地址：/course-schedule/list-by-course/{courseId}
 * 请求方式：GET
 */
export function getCourseScheduleList(courseId, callbacks) {
  return request.get(`/course-schedule/list-by-course/${courseId}`, null, callbacks)
}
