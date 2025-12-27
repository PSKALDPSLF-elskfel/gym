import request from "@/utils/request";

/**
 * 分页查询公告
 * @param {object} params - 请求参数 { current, size, title, status }
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function getAnnouncementPage(params, callbacks) {
  return request.get('/announcement/page', params, callbacks);
}

/**
 * 创建公告
 * @param {object} params - 请求参数 { title, content, status }
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function createAnnouncement(params, callbacks) {
  return request.post('/announcement', params, callbacks);
}

/**
 * 更新公告
 * @param {Long} id - 公告ID
 * @param {object} params - 请求参数 { title, content, status }
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function updateAnnouncement(id, params, callbacks) {
  return request.put(`/announcement/${id}`, params, callbacks);
}

/**
 * 删除公告
 * @param {Long} id - 公告ID
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function deleteAnnouncement(id, callbacks) {
  return request.delete(`/announcement/${id}`, callbacks);
}

/**
 * 更新公告状态
 * @param {Long} id - 公告ID
 * @param {Integer} status - 状态
 * @param {object} callbacks - 回调函数
 * @returns {Promise}
 */
export function updateAnnouncementStatus(id, status, callbacks) {
  return request.put(`/announcement/${id}/status`, { status }, callbacks);
}
