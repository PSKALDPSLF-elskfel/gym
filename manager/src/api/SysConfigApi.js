import request from '@/utils/request'

/**
 * 创建系统配置
 */
export function createSysConfig(params, callbacks) {
  return request.post('/sys-configs', params, callbacks)
}

/**
 * 更新系统配置
 */
export function updateSysConfig(id, params, callbacks) {
  return request.put(`/sys-configs/${id}`, params, callbacks)
}

/**
 * 删除系统配置
 */
export function deleteSysConfig(id, callbacks) {
  return request.delete(`/sys-configs/${id}`, callbacks)
}

/**
 * 获取系统配置详情
 */
export function getSysConfigById(id, callbacks) {
  return request.get(`/sys-configs/${id}`, null, callbacks)
}

/**
 * 根据配置键获取配置值
 */
export function getConfigValue(configKey, callbacks) {
  return request.get(`/sys-configs/key/${configKey}`, null, callbacks)
}

/**
 * 分页查询系统配置
 */
export function getSysConfigPage(params, callbacks) {
  return request.get('/sys-configs/page', params, callbacks)
}

/**
 * 获取所有配置分组
 */
export function getAllConfigGroups(callbacks) {
  return request.get('/sys-configs/groups', null, callbacks)
}
