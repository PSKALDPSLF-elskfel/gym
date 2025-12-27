import request from '@/utils/request'

/**
 * 创建教练
 */
export function createCoach(params, callbacks) {
  return request.post('/coaches', params, callbacks)
}

/**
 * 更新教练
 */
export function updateCoach(id, params, callbacks) {
  return request.put(`/api/coaches/${id}`, params, callbacks)
}

/**
 * 删除教练
 */
export function deleteCoach(id, callbacks) {
  return request.delete(`/api/coaches/${id}`, callbacks)
}

/**
 * 获取教练详情
 */
export function getCoachById(id, callbacks) {
  return request.get(`/api/coaches/${id}`, null, callbacks)
}

/**
 * 分页查询教练
 */
export function getCoachPage(params, callbacks) {
  return request.get('/coaches/page', params, callbacks)
}

/**
 * 获取所有在职教练
 */
export function getAllActiveCoaches(callbacks) {
  return request.get('/coaches/active', null, callbacks)
}
