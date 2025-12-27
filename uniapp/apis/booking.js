import request from '@/utils/request.js'

/**
 * 创建预约
 * @param {object} params - 请求参数 { scheduleId: Long }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function createBooking(params, callbacks) {
  return request.post('/booking', params, callbacks)
}

/**
 * 取消预约
 * @param {Long} bookingId - 预约ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function cancelBooking(bookingId, callbacks) {
  return request.put(`/booking/${bookingId}/cancel`, null, callbacks)
}

/**
 * 获取预约详情
 * @param {Long} bookingId - 预约ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getBookingById(bookingId, callbacks) {
  return request.get(`/booking/${bookingId}`, null, callbacks)
}

/**
 * 获取我的预约列表
 * @param {object} params - 请求参数 { status: Integer }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getMyBookings(params, callbacks) {
  return request.get('/booking/my', params, callbacks)
}

/**
 * 检查用户是否已预约
 * @param {object} params - 请求参数 { scheduleId: Long }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function checkBooked(params, callbacks) {
  return request.get('/booking/check', params, callbacks)
}
