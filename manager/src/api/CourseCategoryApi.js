import request from '@/utils/request'

/**
 * 创建课程分类
 */
export function createCourseCategory(params, callbacks) {
  return request.post('/course-categories', params, callbacks)
}

/**
 * 更新课程分类
 */
export function updateCourseCategory(id, params, callbacks) {
  return request.put(`/course-categories/${id}`, params, callbacks)
}

/**
 * 删除课程分类
 */
export function deleteCourseCategory(id, callbacks) {
  return request.delete(`/course-categories/${id}`, callbacks)
}

/**
 * 获取课程分类详情
 */
export function getCourseCategoryById(id, callbacks) {
  return request.get(`/course-categories/${id}`, null, callbacks)
}

/**
 * 分页查询课程分类
 */
export function getCourseCategoryPage(params, callbacks) {
  return request.get('/course-categories/page', params, callbacks)
}

/**
 * 获取所有启用的课程分类
 */
export function getAllEnabledCategories(callbacks) {
  return request.get('/course-categories/enabled', null, callbacks)
}
