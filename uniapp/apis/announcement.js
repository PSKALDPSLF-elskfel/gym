import request from '@/utils/request.js'

/**
 * 获取最新公告列表
 * @param {object} params - 请求参数 { limit: Integer }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getLatestAnnouncements(params, callbacks) {
  return request.get('/announcement/latest', params, callbacks)
}

/**
 * 获取所有启用的公告
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getAllAnnouncements(callbacks) {
  return request.get('/announcement/list', null, callbacks)
}
