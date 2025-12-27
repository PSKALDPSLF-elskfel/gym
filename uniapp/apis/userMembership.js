import request from '@/utils/request.js'

/**
 * 购买会员套餐
 * 功能描述：用户购买指定的会员套餐
 * 入参：{ userId, packageId }
 * 返回参数：无
 * url地址：/user-membership/purchase?userId=xxx
 * 请求方式：POST
 */
export function purchaseMembershipPackage(userId, packageId, callbacks) {
  return request.post(`/user-membership/purchase?userId=${userId}`, { packageId }, callbacks)
}

/**
 * 查询当前有效会员
 * 功能描述：查询用户当前有效的会员信息
 * 入参：userId - 用户ID
 * 返回参数：{ id, userId, userNickname, packageId, packageName, memberType, memberTypeName, startTime, endTime, price, status, statusName, purchaseTime, remainingDays, expired }
 * url地址：/user-membership/current
 * 请求方式：GET
 */
export function getCurrentMembership(userId, callbacks) {
  return request.get('/user-membership/current', { userId }, callbacks)
}

/**
 * 查询用户会员购买历史
 * 功能描述：查询用户所有的会员购买记录
 * 入参：userId - 用户ID
 * 返回参数：array - 会员记录列表
 * url地址：/user-membership/my-history
 * 请求方式：GET
 */
export function getUserMembershipHistory(userId, callbacks) {
  return request.get('/user-membership/my-history', { userId }, callbacks)
}
