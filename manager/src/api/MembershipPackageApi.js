import request from '@/utils/request'

/**
 * 创建会员套餐
 * 功能描述：创建新的会员套餐
 * 入参：{ name: string, memberType: number, durationDays: number, price: number, description: string, benefits: string, status: number }
 * 返回参数：无
 * url地址：/membership-package
 * 请求方式：POST
 */
export function createMembershipPackage(params, callbacks) {
  return request.post('/membership-package', params, callbacks)
}

/**
 * 更新会员套餐
 * 功能描述：更新已存在的会员套餐信息
 * 入参：{ id: number, name: string, memberType: number, durationDays: number, price: number, description: string, benefits: string, status: number }
 * 返回参数：无
 * url地址：/membership-package/{id}
 * 请求方式：PUT
 */
export function updateMembershipPackage(id, params, callbacks) {
  return request.put(`/membership-package/${id}`, params, callbacks)
}

/**
 * 删除会员套餐
 * 功能描述：删除指定的会员套餐
 * 入参：id - 套餐ID
 * 返回参数：无
 * url地址：/membership-package/{id}
 * 请求方式：DELETE
 */
export function deleteMembershipPackage(id, callbacks) {
  return request.delete(`/membership-package/${id}`, callbacks)
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

/**
 * 分页查询会员套餐
 * 功能描述：分页查询会员套餐列表，支持按名称、会员类型、状态筛选
 * 入参：{ current: number, size: number, name?: string, memberType?: number, status?: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/membership-package/page
 * 请求方式：GET
 */
export function getMembershipPackagePage(params, callbacks) {
  return request.get('/membership-package/page', params, callbacks)
}

/**
 * 更新套餐状态
 * 功能描述：更新会员套餐的上架/下架状态
 * 入参：id - 套餐ID, status - 状态（0-下架，1-上架）
 * 返回参数：无
 * url地址：/membership-package/{id}/status
 * 请求方式：PUT
 */
export function updateMembershipPackageStatus(id, status, callbacks) {
  return request.put(`/membership-package/${id}/status?status=${status}`, null, callbacks)
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
