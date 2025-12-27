import request from '@/utils/request.js'

/**
 * 获取所有在职教练
 * 功能描述：获取所有在职状态的教练列表
 * 入参：无
 * 返回参数：教练列表 [{ id, userId, name, specialty, averageRating, ... }]
 * url地址：/coaches/active
 * 请求方式：GET
 */
export function getAllActiveCoaches(callbacks) {
  return request.get('/coaches/active', null, callbacks)
}

/**
 * 获取教练详情
 * 功能描述：根据教练ID获取教练详细信息
 * 入参：coachId - 教练ID
 * 返回参数：教练详情对象
 * url地址：/coaches/{id}
 * 请求方式：GET
 */
export function getCoachById(coachId, callbacks) {
  return request.get(`/coaches/${coachId}`, null, callbacks)
}

/**
 * 分页查询教练
 * 功能描述：分页查询教练列表
 * 入参：{ currentPage, pageSize, name, specialty, status }
 * 返回参数：分页数据 { records, total, size, current, pages }
 * url地址：/coaches/page
 * 请求方式：GET
 */
export function getCoachPage(params, callbacks) {
  return request.get('/coaches/page', params, callbacks)
}
