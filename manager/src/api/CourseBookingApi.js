import request from "@/utils/request";

/**
 * 创建预约
 * @param {object} params - 请求参数 { scheduleId: Long }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function createBooking(params, callbacks) {
  return request.post('/booking', params, callbacks);
}

/**
 * 取消预约
 * @param {Long} bookingId - 预约ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function cancelBooking(bookingId, callbacks) {
  return request.put(`/booking/${bookingId}/cancel`, null, callbacks);
}

/**
 * 获取预约详情
 * @param {Long} bookingId - 预约ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getBookingById(bookingId, callbacks) {
  return request.get(`/booking/${bookingId}`, null, callbacks);
}

/**
 * 获取我的预约列表
 * @param {object} params - 请求参数 { status: Integer }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getMyBookings(params, callbacks) {
  return request.get('/booking/my', params, callbacks);
}

/**
 * 获取课程时间的预约列表
 * @param {Long} scheduleId - 排课ID
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getScheduleBookings(scheduleId, callbacks) {
  return request.get(`/booking/schedule/${scheduleId}`, null, callbacks);
}

/**
 * 分页查询预约(管理端)
 * @param {object} params - 请求参数 { current: Long, size: Long, userId: Long, scheduleId: Long, status: Integer, startDate: String, endDate: String }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getBookingPage(params, callbacks) {
  return request.get('/booking/page', params, callbacks);
}

/**
 * 检查用户是否已预约
 * @param {object} params - 请求参数 { scheduleId: Long }
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function checkBooked(params, callbacks) {
  return request.get('/booking/check', params, callbacks);
}
