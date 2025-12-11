import request from '@/utils/request.js'

// ==================== 器材管理 ====================

/**
 * 分页查询器材列表
 * 功能描述：查询所有正常状态的器材列表
 * 入参：{ current, size, name?, category? }
 * 返回参数：{ records: array, total: number }
 * url地址：/equipment/page
 * 请求方式：GET
 */
export function getEquipmentPage(params, callbacks) {
  return request.get('/equipment/page', params, callbacks)
}

/**
 * 根据ID查询器材详情
 * 功能描述：查询指定ID的器材详情
 * 入参：id - 器材ID
 * 返回参数：{ id, name, code, category, brand, model, location, status, ... }
 * url地址：/equipment/{id}
 * 请求方式：GET
 */
export function getEquipmentById(id, callbacks) {
  return request.get(`/equipment/${id}`, null, callbacks)
}

// ==================== 器材预约 ====================

/**
 * 创建器材预约
 * 功能描述：用户预约器材
 * 入参：{ equipmentId, startTime, endTime }
 * 返回参数：预约ID
 * url地址：/equipment-booking
 * 请求方式：POST
 */
export function createEquipmentBooking(params, callbacks) {
  return request.post('/equipment-booking', params, callbacks)
}

/**
 * 取消器材预约
 * 功能描述：取消指定的器材预约
 * 入参：bookingId - 预约ID
 * 返回参数：无
 * url地址：/equipment-booking/{bookingId}/cancel
 * 请求方式：PUT
 */
export function cancelEquipmentBooking(bookingId, callbacks) {
  return request.put(`/equipment-booking/${bookingId}/cancel`, null, callbacks)
}

/**
 * 查询用户预约列表
 * 功能描述：查询当前用户的器材预约记录
 * 入参：status - 状态（可选）
 * 返回参数：预约列表
 * url地址：/equipment-booking/my
 * 请求方式：GET
 */
export function getUserEquipmentBookings(params, callbacks) {
  return request.get('/equipment-booking/my', params, callbacks)
}

/**
 * 开始使用器材
 * 功能描述：用户开始使用已预约的器材
 * 入参：bookingId - 预约ID
 * 返回参数：无
 * url地址：/equipment-booking/{bookingId}/start
 * 请求方式：PUT
 */
export function startUsingEquipment(bookingId, callbacks) {
  return request.put(`/equipment-booking/${bookingId}/start`, null, callbacks)
}

/**
 * 结束使用器材
 * 功能描述：用户结束使用器材
 * 入参：bookingId - 预约ID
 * 返回参数：无
 * url地址：/equipment-booking/{bookingId}/end
 * 请求方式：PUT
 */
export function endUsingEquipment(bookingId, callbacks) {
  return request.put(`/equipment-booking/${bookingId}/end`, null, callbacks)
}

// ==================== 器材排队 ====================

/**
 * 加入器材排队
 * 功能描述：用户加入器材排队
 * 入参：{ equipmentId }
 * 返回参数：排队ID
 * url地址：/equipment-booking/queue
 * 请求方式：POST
 */
export function joinEquipmentQueue(params, callbacks) {
  return request.post('/equipment-booking/queue', params, callbacks)
}

/**
 * 退出器材排队
 * 功能描述：用户退出排队
 * 入参：queueId - 排队ID
 * 返回参数：无
 * url地址：/equipment-booking/queue/{queueId}/leave
 * 请求方式：PUT
 */
export function leaveEquipmentQueue(queueId, callbacks) {
  return request.put(`/equipment-booking/queue/${queueId}/leave`, null, callbacks)
}

/**
 * 查询器材排队列表
 * 功能描述：查询指定器材的排队列表
 * 入参：equipmentId - 器材ID
 * 返回参数：排队列表
 * url地址：/equipment-booking/queue/{equipmentId}
 * 请求方式：GET
 */
export function getEquipmentQueueList(equipmentId, callbacks) {
  return request.get(`/equipment-booking/queue/${equipmentId}`, null, callbacks)
}
