import request from '@/utils/request'

/**
 * 创建训练动作
 */
export function createTrainingAction(params, callbacks) {
  return request.post('/training-actions', params, callbacks)
}

/**
 * 更新训练动作
 */
export function updateTrainingAction(id, params, callbacks) {
  return request.put(`/training-actions/${id}`, params, callbacks)
}

/**
 * 删除训练动作
 */
export function deleteTrainingAction(id, callbacks) {
  return request.delete(`/training-actions/${id}`, callbacks)
}

/**
 * 获取训练动作详情
 */
export function getTrainingActionById(id, callbacks) {
  return request.get(`/training-actions/${id}`, null, callbacks)
}

/**
 * 分页查询训练动作
 */
export function getTrainingActionPage(params, callbacks) {
  return request.get('/training-actions/page', params, callbacks)
}

/**
 * 获取所有启用的训练动作
 */
export function getAllEnabledActions(callbacks) {
  return request.get('/training-actions/enabled', null, callbacks)
}
