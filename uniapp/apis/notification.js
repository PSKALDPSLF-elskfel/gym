import request from '@/utils/request.js'

/**
 * 获取我的通知列表
 * 功能描述：获取当前用户的通知列表（分页）
 * 入参：{ current: number, size: number, notificationType?: string, isRead?: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/notifications/my
 * 请求方式：GET
 */
export function getMyNotifications(params, config = {}) {
  return request.get('/notifications/my', params, config)
}

/**
 * 获取未读通知数量
 * 功能描述：获取当前用户的未读通知数量
 * 入参：无
 * 返回参数：number (未读数量)
 * url地址：/notifications/unread/count
 * 请求方式：GET
 */
export function getUnreadCount(config = {}) {
  return request.get('/notifications/unread/count', null, config)
}

/**
 * 标记通知为已读
 * 功能描述：标记指定通知为已读
 * 入参：notificationId: number
 * 返回参数：操作成功信息
 * url地址：/notifications/{notificationId}/read
 * 请求方式：PUT
 */
export function markAsRead(notificationId, config = {}) {
  return request.put(`/notifications/${notificationId}/read`, null, config)
}

/**
 * 全部标记为已读
 * 功能描述：标记当前用户所有通知为已读
 * 入参：无
 * 返回参数：操作成功信息
 * url地址：/notifications/read/all
 * 请求方式：PUT
 */
export function markAllAsRead(config = {}) {
  return request.put('/notifications/read/all', null, config)
}

/**
 * 删除通知
 * 功能描述：删除指定通知
 * 入参：notificationId: number
 * 返回参数：操作成功信息
 * url地址：/notifications/{notificationId}
 * 请求方式：DELETE
 */
export function deleteNotification(notificationId, config = {}) {
  return request.delete(`/notifications/${notificationId}`, config)
}
