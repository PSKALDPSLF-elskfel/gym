import request from '@/utils/request.js'

/**
 * 查询所有上架的套餐列表
 * 功能描述：查询所有上架状态的会员套餐
 * 入参：无
 * 返回参数：array - 套餐列表
 * url地址：/membership-package/list-online
 * 请求方式：GET
 */
export function getMembershipPackageListOnline(callbacks) {
  return request.get('/membership-package/list-online', null, callbacks)
}

/**
 * 根据会员类型查询套餐列表
 * 功能描述：查询指定会员类型的上架套餐列表
 * 入参：memberType - 会员类型（1-黄金，2-铂金）
 * 返回参数：array - 套餐列表
 * url地址：/membership-package/list-by-type
 * 请求方式：GET
 */
export function getMembershipPackageListByType(memberType, callbacks) {
  const params = memberType ? { memberType } : null
  return request.get('/membership-package/list-by-type', params, callbacks)
}

/**
 * 根据ID查询会员套餐
 * 功能描述：查询指定ID的会员套餐详情
 * 入参：id - 套餐ID
 * 返回参数：{ id, name, memberType, memberTypeDisplayName, durationDays, price, description, benefits, status, statusDisplayName, createTime, updateTime }
 * url地址：/membership-package/{id}
 * 请求方式：GET
 */
export function getMembershipPackageById(id, callbacks) {
  return request.get(`/membership-package/${id}`, null, callbacks)
}
