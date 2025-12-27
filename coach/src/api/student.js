import request from '@/utils/request'

/**
 * 获取我的学员列表（分页）
 * @param {Object} params - 查询参数
 * @param {string} params.keyword - 搜索关键词（昵称或手机号）
 * @param {number} params.memberType - 会员类型筛选（0-普通，1-黄金，2-铂金）
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 页大小
 */
export const getMyStudents = (params) => {
  return request.get('/coach/students/my', { params })
}

/**
 * 获取学员详情
 * @param {number} userId - 学员用户ID
 */
export const getStudentDetail = (userId) => {
  return request.get(`/coach/students/${userId}/detail`)
}

/**
 * 获取学员体测历史
 * @param {number} userId - 学员用户ID
 */
export const getStudentBodyTests = (userId) => {
  return request.get(`/coach/students/${userId}/body-tests`)
}

/**
 * 获取学员训练计划
 * @param {number} userId - 学员用户ID
 */
export const getStudentTrainingPlans = (userId) => {
  return request.get(`/coach/students/${userId}/training-plans`)
}

/**
 * 更新学员备注
 * @param {Object} data - 备注数据
 * @param {number} data.userId - 学员用户ID
 * @param {string} data.remark - 备注内容
 */
export const updateStudentRemark = (data) => {
  return request.put('/coach/students/remark', data)
}
