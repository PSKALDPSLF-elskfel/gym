import request from "@/utils/request";

/**
 * 分页查询会员记录
 * @param {object} params - 请求参数, { current: number, size: number, userId: number, memberType: number, status: number }
 * @param {object} callbacks - 回调函数, { onSuccess, onError }
 * @returns {Promise}
 */
export function getUserMembershipPage(params, callbacks) {
  return request.get('/user-membership/page', params, callbacks);
}

/**
 * 查询用户会员购买历史
 * @param {object} params - 请求参数, { userId: number }
 * @param {object} callbacks - 回调函数, { onSuccess, onError }
 * @returns {Promise}
 */
export function getUserMembershipHistory(params, callbacks) {
  return request.get('/user-membership/my-history', params, callbacks);
}

/**
 * 查询当前有效会员
 * @param {object} params - 请求参数, { userId: number }
 * @param {object} callbacks - 回调函数, { onSuccess, onError }
 * @returns {Promise}
 */
export function getCurrentMembership(params, callbacks) {
  return request.get('/user-membership/current', params, callbacks);
}
