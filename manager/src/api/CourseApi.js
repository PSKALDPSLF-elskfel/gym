import request from '@/utils/request'

/**
 * 创建课程
 * 功能描述：创建新的课程
 * 入参：{ id: string, name: string, description: string, coverImage: string, duration: number, maxParticipants: number, price: number, status: number }
 * 返回参数：无
 * url地址：/course
 * 请求方式：POST
 */
export function createCourse(params, callbacks) {
  return request.post('/course', params, callbacks)
}

/**
 * 更新课程
 * 功能描述：更新已存在的课程信息
 * 入参：{ id: string, name: string, description: string, coverImage: string, duration: number, maxParticipants: number, price: number, status: number }
 * 返回参数：无
 * url地址：/course/{id}
 * 请求方式：PUT
 */
export function updateCourse(id, params, callbacks) {
  return request.put(`/course/${id}`, params, callbacks)
}

/**
 * 删除课程
 * 功能描述：删除指定的课程
 * 入参：id - 课程ID
 * 返回参数：无
 * url地址：/course/{id}
 * 请求方式：DELETE
 */
export function deleteCourse(id, callbacks) {
  return request.delete(`/course/${id}`, callbacks)
}

/**
 * 根据ID查询课程
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
 * 分页查询课程
 * 功能描述：分页查询课程列表，支持按名称、状态筛选
 * 入参：{ current: number, size: number, name?: string, status?: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/course/page
 * 请求方式：GET
 */
export function getCoursePage(params, callbacks) {
  return request.get('/course/page', params, callbacks)
}

/**
 * 更新课程状态
 * 功能描述：更新课程的上架/下架状态
 * 入参：id - 课程ID, status - 状态（0-下架，1-上架）
 * 返回参数：无
 * url地址：/course/{id}/status
 * 请求方式：PUT
 */
export function updateCourseStatus(id, status, callbacks) {
  return request.put(`/course/${id}/status?status=${status}`, null, callbacks)
}

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
