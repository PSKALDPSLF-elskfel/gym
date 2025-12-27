import request from '@/utils/request'

// ==================== 器材管理 ====================

/**
 * 创建器材
 * 功能描述：创建新的器材
 * 入参：{ name, code, category, brand, model, location, purchaseDate, warrantyExpire, status, remark }
 * 返回参数：{ id, name, code, ... }
 * url地址：/equipment
 * 请求方式：POST
 */
export function createEquipment(params, callbacks) {
  return request.post('/equipment', params, callbacks)
}

/**
 * 更新器材
 * 功能描述：更新已存在的器材信息
 * 入参：{ name, code, category, brand, model, location, purchaseDate, warrantyExpire, status, remark }
 * 返回参数：{ id, name, code, ... }
 * url地址：/equipment/{id}
 * 请求方式：PUT
 */
export function updateEquipment(id, params, callbacks) {
  return request.put(`/equipment/${id}`, params, callbacks)
}

/**
 * 删除器材
 * 功能描述：删除指定的器材
 * 入参：id - 器材ID
 * 返回参数：无
 * url地址：/equipment/{id}
 * 请求方式：DELETE
 */
export function deleteEquipment(id, callbacks) {
  return request.delete(`/equipment/${id}`, callbacks)
}

/**
 * 根据ID查询器材
 * 功能描述：查询指定ID的器材详情
 * 入参：id - 器材ID
 * 返回参数：{ id, name, code, category, ... }
 * url地址：/equipment/{id}
 * 请求方式：GET
 */
export function getEquipmentById(id, callbacks) {
  return request.get(`/equipment/${id}`, null, callbacks)
}

/**
 * 分页查询器材
 * 功能描述：分页查询器材列表，支持按名称、类型、状态筛选
 * 入参：{ current, size, name?, category?, status? }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/equipment/page
 * 请求方式：GET
 */
export function getEquipmentPage(params, callbacks) {
  return request.get('/equipment/page', params, callbacks)
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

/**
 * 分页查询所有预约记录（后台管理）
 * 功能描述：后台管理查询所有器材预约记录
 * 入参：{ current, size, equipmentId?, userId?, status? }
 * 返回参数：{ records: array, total: number }
 * url地址：/equipment-booking/page
 * 请求方式：GET
 */
export function getEquipmentBookingPage(params, callbacks) {
  return request.get('/equipment-booking/page', params, callbacks)
}

// ==================== 器材排队 ====================

/**
 * 加入器材排队
 * 功能描述：用户加入器材排队
 * 入参：{ equipmentId }
 * 返回参数：排队ID
 * url地址：/equipment-booking/queue/join
 * 请求方式：POST
 */
export function joinEquipmentQueue(params, callbacks) {
  return request.post('/equipment-booking/queue/join', params, callbacks)
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

/**
 * 叫号（管理端）
 * 功能描述：管理端叫下一个排队号
 * 入参：equipmentId - 器材ID
 * 返回参数：无
 * url地址：/equipment-booking/queue/{equipmentId}/call
 * 请求方式：PUT
 */
export function callNextInQueue(equipmentId, callbacks) {
  return request.put(`/equipment-booking/queue/${equipmentId}/call`, null, callbacks)
}

// ==================== 器材维护 ====================

/**
 * 添加维护记录
 * 功能描述：添加器材维护记录
 * 入参：{ equipmentId, maintenanceType, maintenanceDate, content, cost, operator, nextMaintenanceDate, remark }
 * 返回参数：无
 * url地址：/equipment-maintenance
 * 请求方式：POST
 */
export function createMaintenance(params, callbacks) {
  return request.post('/equipment-maintenance', params, callbacks)
}

/**
 * 分页查询维护记录
 * 功能描述：分页查询器材维护记录
 * 入参：{ current, size, equipmentId?, maintenanceType? }
 * 返回参数：{ records: array, total: number }
 * url地址：/equipment-maintenance/page
 * 请求方式：GET
 */
export function getMaintenancePage(params, callbacks) {
  return request.get('/equipment-maintenance/page', params, callbacks)
}
