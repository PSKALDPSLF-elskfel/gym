import request from '@/utils/request.js'

// ==================== 训练计划管理 ====================

/**
 * 分页查询训练计划列表
 * 功能描述：查询训练计划列表（支持按用户、教练、目标、状态筛选）
 * 入参：{ currentPage, pageSize, userId?, coachId?, goal?, status? }
 * 返回参数：{ records: array, total: number }
 * url地址：/training-plans/page
 * 请求方式：GET
 */
export function getTrainingPlanPage(params, config = {}) {
  return request.get('/training-plans/page', params, config)
}

/**
 * 根据ID查询训练计划详情
 * 功能描述：查询指定ID的训练计划详情，包含训练明细
 * 入参：id - 计划ID
 * 返回参数：{ id, userId, coachId, name, goal, startDate, endDate, status, details: [] }
 * url地址：/training-plans/{id}
 * 请求方式：GET
 */
export function getTrainingPlanById(id, config = {}) {
  return request.get(`/training-plans/${id}`, null, config)
}

/**
 * 创建训练计划
 * 功能描述：创建新的训练计划
 * 入参：{ userId, coachId, name, goal, startDate, endDate, details, remark }
 * 返回参数：新创建的训练计划对象
 * url地址：/training-plans
 * 请求方式：POST
 */
export function createTrainingPlan(params, config = {}) {
  return request.post('/training-plans', params, config)
}

/**
 * 更新训练计划
 * 功能描述：更新指定的训练计划
 * 入参：{ id, name?, goal?, startDate?, endDate?, status?, details?, remark? }
 * 返回参数：更新后的训练计划对象
 * url地址：/training-plans/{id}
 * 请求方式：PUT
 */
export function updateTrainingPlan(id, params, config = {}) {
  return request.put(`/training-plans/${id}`, params, config)
}

/**
 * 删除训练计划
 * 功能描述：删除指定的训练计划
 * 入参：id - 计划ID
 * 返回参数：无
 * url地址：/training-plans/{id}
 * 请求方式：DELETE
 */
export function deleteTrainingPlan(id, config = {}) {
  return request.delete(`/training-plans/${id}`, config)
}

// ==================== 训练明细管理 ====================

/**
 * 更新训练明细完成状态
 * 功能描述：标记训练项目完成状态，记录实际完成情况和感受
 * 入参：{ detailId, isCompleted?, actualSets?, actualReps?, executionNote? }
 * 返回参数：无
 * url地址：/training-plans/detail/{detailId}/completion
 * 请求方式：PUT
 */
export function updateDetailCompletion(detailId, params, config = {}) {
  return request.put(`/training-plans/detail/${detailId}/completion`, params, config)
}

/**
 * 添加执行备注
 * 功能描述：为训练明细添加执行感受和备注
 * 入参：{ detailId, note }
 * 返回参数：无
 * url地址：/training-plans/detail/{detailId}/note
 * 请求方式：POST
 */
export function addExecutionNote(detailId, note, config = {}) {
  return request.post(`/training-plans/detail/${detailId}/note`, { note }, config)
}

/**
 * 获取执行历史
 * 功能描述：查询用户的训练执行历史记录
 * 入参：{ userId, limit? }
 * 返回参数：训练明细列表
 * url地址：/training-plans/execution-history
 * 请求方式：GET
 */
export function getExecutionHistory(params, config = {}) {
  return request.get('/training-plans/execution-history', params, config)
}

/**
 * 计算计划进度
 * 功能描述：计算训练计划的完成进度百分比
 * 入参：id - 计划ID
 * 返回参数：进度百分比（0-100）
 * url地址：/training-plans/{id}/progress
 * 请求方式：GET
 */
export function calculateProgress(id, config = {}) {
  return request.get(`/training-plans/${id}/progress`, null, config)
}

// ==================== 训练计划模板管理 ====================

/**
 * 分页查询训练计划模板列表
 * 功能描述：查询可用的训练计划模板（系统模板+公开模板）
 * 入参：{ currentPage, pageSize, goal?, difficulty?, status? }
 * 返回参数：{ records: array, total: number }
 * url地址：/training-plan-templates/page
 * 请求方式：GET
 */
export function getTemplatePage(params, config = {}) {
  return request.get('/training-plan-templates/page', params, config)
}

/**
 * 根据ID查询模板详情
 * 功能描述：查询指定ID的训练计划模板详情，包含训练动作明细
 * 入参：id - 模板ID
 * 返回参数：{ id, name, goal, difficulty, durationDays, description, details: [] }
 * url地址：/training-plan-templates/{id}
 * 请求方式：GET
 */
export function getTemplateById(id, config = {}) {
  return request.get(`/training-plan-templates/${id}`, null, config)
}

/**
 * 从模板创建训练计划
 * 功能描述：从选中的模板快速创建一个新的训练计划
 * 入参：{ templateId, userId, planName }
 * 返回参数：新创建的训练计划对象
 * url地址：/training-plan-templates/{templateId}/create-plan?userId=xxx&planName=xxx
 * 请求方式：POST
 */
export function createPlanFromTemplate(templateId, userId, planName, config = {}) {
  return request.post(`/training-plan-templates/${templateId}/create-plan?userId=${userId}&planName=${encodeURIComponent(planName)}`, null, config)
}
