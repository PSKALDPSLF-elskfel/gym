import request from '@/utils/request'

/**
 * 获取通知列表（管理端）
 */
export function getNotificationList(params, config = {}) {
  return request.get('/notifications/admin/list', params, config)
}

/**
 * 发送通知
 */
export function sendNotification(data, config = {}) {
  return request.post('/notifications/send', data, config)
}

/**
 * 批量发送通知
 */
export function sendBatchNotification(data, userIds, config = {}) {
  return request.post('/notifications/send/batch', data, {
    params: { userIds },
    ...config
  })
}

/**
 * 删除通知
 */
export function deleteNotification(notificationId, config = {}) {
  return request.delete(`/notifications/admin/${notificationId}`, config)
}

/**
 * 获取用户列表（用于批量发送）
 */
export function getUserList(params, config = {}) {
  return request.get('/users', params, config)
}
