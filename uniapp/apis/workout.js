import request from '@/utils/request.js'

// ==================== 运动记录管理 ====================

/**
 * 创建运动记录
 * 功能描述：创建新的运动记录，支持有氧运动和力量训练
 * 入参：{ workoutTypeId, workoutDate, startTime, endTime, duration, intensity, calories, distance, steps, heartRateAvg, heartRateMax, note, feeling, weather, location, isCompleted, source, details }
 * 返回参数：记录ID
 * url地址：/workout/record
 * 请求方式：POST
 */
export function createWorkoutRecord(data, config) {
  return request.post('/workout/record', data, config)
}

/**
 * 更新运动记录
 * 功能描述：更新指定的运动记录
 * 入参：{ duration, calories, distance, note, feeling }
 * 返回参数：无
 * url地址：/workout/record/{id}
 * 请求方式：PUT
 */
export function updateWorkoutRecord(id, data, config) {
  return request.put(`/workout/record/${id}`, data, config)
}

/**
 * 删除运动记录
 * 功能描述：删除指定的运动记录
 * 入参：id - 记录ID
 * 返回参数：无
 * url地址：/workout/record/{id}
 * 请求方式：DELETE
 */
export function deleteWorkoutRecord(id, config) {
  return request.delete(`/workout/record/${id}`, config)
}

/**
 * 查询运动记录详情
 * 功能描述：查询指定ID的运动记录详情，包含详细信息
 * 入参：id - 记录ID
 * 返回参数：运动记录详细信息
 * url地址：/workout/record/{id}
 * 请求方式：GET
 */
export function getWorkoutRecordById(id, config) {
  return request.get(`/workout/record/${id}`, null, config)
}

/**
 * 分页查询我的运动记录
 * 功能描述：查询当前用户的运动记录列表，支持筛选
 * 入参：{ current, size, startDate, endDate, workoutTypeId, intensity, isCompleted }
 * 返回参数：{ records: array, total: number, current: number, pages: number }
 * url地址：/workout/record/my-page
 * 请求方式：GET
 */
export function getMyWorkoutRecords(params, config) {
  return request.get('/workout/record/my-page', params, config)
}

// ==================== 运动类型管理 ====================

/**
 * 查询运动类型列表
 * 功能描述：查询可用的运动类型列表
 * 入参：{ category } - 运动分类（可选：CARDIO/STRENGTH/FLEXIBILITY/SPORTS/OTHER）
 * 返回参数：运动类型列表
 * url地址：/workout/type/list
 * 请求方式：GET
 */
export function getWorkoutTypeList(params, config) {
  return request.get('/workout/type/list', params, config)
}

// ==================== 运动数据统计 ====================

/**
 * 查询运动数据统计汇总
 * 功能描述：查询当前用户的运动数据总体统计
 * 入参：无
 * 返回参数：{ totalWorkouts, totalDuration, totalCalories, totalDistance, avgDuration, workoutDays, streakDays, weekWorkouts, monthWorkouts, cardioPercentage, strengthPercentage }
 * url地址：/workout/statistics/summary
 * 请求方式：GET
 */
export function getWorkoutStatisticsSummary(config) {
  return request.get('/workout/statistics/summary', null, config)
}

/**
 * 查询每日统计数据
 * 功能描述：查询指定时间段的每日统计数据
 * 入参：{ startDate, endDate } - 开始日期和结束日期（格式：yyyy-MM-dd）
 * 返回参数：每日统计数据数组
 * url地址：/workout/statistics/daily
 * 请求方式：GET
 */
export function getWorkoutDailyStats(params, config) {
  return request.get('/workout/statistics/daily', params, config)
}
