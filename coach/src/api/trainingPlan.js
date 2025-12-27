import request from '@/utils/request'

// ==================== 训练计划接口 ====================

// 获取训练计划列表（教练端）
export const getTrainingPlans = (params) => {
  return request.get('/training-plans/page', { params })
}

// 创建训练计划
export const createTrainingPlan = (data) => {
  return request.post('/training-plans', data)
}

// 更新训练计划
export const updateTrainingPlan = (planId, data) => {
  return request.put(`/training-plans/${planId}`, data)
}

// 删除训练计划
export const deleteTrainingPlan = (planId) => {
  return request.delete(`/training-plans/${planId}`)
}

// 获取训练计划详情
export const getTrainingPlanDetail = (planId) => {
  return request.get(`/training-plans/${planId}`)
}

// 计算训练计划进度
export const getTrainingPlanProgress = (planId) => {
  return request.get(`/training-plans/${planId}/progress`)
}

// ==================== 训练计划模板接口 ====================

// 获取训练计划模板列表（分页）
export const getTemplates = (params) => {
  return request.get('/training-plan-templates/page', { params })
}

// 获取教练的模板列表
export const getCoachTemplates = (coachId, params) => {
  return request.get(`/training-plan-templates/coach/${coachId}/list`, { params })
}

// 获取模板详情
export const getTemplateDetail = (templateId) => {
  return request.get(`/training-plan-templates/${templateId}`)
}

// 创建训练计划模板
export const createTemplate = (coachId, data) => {
  return request.post('/training-plan-templates/coach', data, { 
    params: { coachId } 
  })
}

// 更新训练计划模板
export const updateTemplate = (templateId, data) => {
  return request.put(`/training-plan-templates/${templateId}`, data)
}

// 删除训练计划模板
export const deleteTemplate = (templateId) => {
  return request.delete(`/training-plan-templates/${templateId}`)
}

// 从模板创建训练计划
export const createPlanFromTemplate = (templateId, userId, planName) => {
  return request.post(`/training-plan-templates/${templateId}/create-plan`, null, {
    params: { userId, planName }
  })
}

// ==================== 训练动作接口 ====================

// 获取所有训练动作（用于选择）
export const getTrainingActions = (params) => {
  return request.get('/training-actions/page', { params })
}

// 获取训练动作详情
export const getTrainingActionDetail = (actionId) => {
  return request.get(`/training-actions/${actionId}`)
}
