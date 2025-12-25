import request from '@/utils/request.js'

/**
 * 获取体测数据分页列表
 * 功能描述：分页查询用户的体测数据记录
 * 入参：{ currentPage: number, pageSize: number, userId?: number }
 * 返回参数：{ records: array, total: number, current: number, size: number }
 * url地址：/body-tests/page
 * 请求方式：GET
 */
export function getBodyTestPage(params, config = {}) {
  return request.get('/body-tests/page', params, config)
}

/**
 * 获取体测数据详情
 * 功能描述：根据体测ID获取详细信息
 * 入参：{ id: number }
 * 返回参数：体测数据详情对象
 * url地址：/body-tests/{id}
 * 请求方式：GET
 */
export function getBodyTestById(id, config = {}) {
  return request.get(`/body-tests/${id}`, null, config)
}

/**
 * 获取用户最新体测数据
 * 功能描述：获取指定用户最新一次的体测记录
 * 入参：{ userId: number }
 * 返回参数：最新体测数据对象
 * url地址：/body-tests/user/{userId}/latest
 * 请求方式：GET
 */
export function getLatestBodyTest(userId, config = {}) {
  return request.get(`/body-tests/user/${userId}/latest`, null, config)
}

/**
 * 创建体测数据
 * 功能描述：添加新的体测记录
 * 入参：{ userId: number, height?: number, weight?: number, bodyFat?: number, muscleMass?: number, visceralFat?: number, basalMetabolism?: number, testTime?: string, testerId?: number, remark?: string }
 * 返回参数：创建的体测数据对象
 * url地址：/body-tests
 * 请求方式：POST
 */
export function createBodyTest(params, config = {}) {
  return request.post('/body-tests', params, config)
}

/**
 * 删除体测数据
 * 功能描述：删除指定的体测记录
 * 入参：{ id: number }
 * 返回参数：删除成功信息
 * url地址：/body-tests/{id}
 * 请求方式：DELETE
 */
export function deleteBodyTest(id, config = {}) {
  return request.delete(`/body-tests/${id}`, null, config)
}
