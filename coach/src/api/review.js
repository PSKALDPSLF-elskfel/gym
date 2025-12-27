import request from '@/utils/request'

/**
 * 获取我收到的评价列表
 * @param {Object} params - 查询参数 {pageNum, pageSize}
 */
export const getReceivedReviews = (params) => {
  return request.get('/coach/reviews/received', { params })
}

/**
 * 获取评价详情
 * @param {Number} reviewId - 评价ID
 */
export const getReviewDetail = (reviewId) => {
  return request.get(`/coach/reviews/${reviewId}`)
}

/**
 * 回复评价
 * @param {Number} reviewId - 评价ID
 * @param {String} reply - 回复内容
 */
export const replyReview = (reviewId, data) => {
  return request.post(`/coach/reviews/${reviewId}/reply`, data)
}

/**
 * 获取我的评价统计
 */
export const getMyStatistics = () => {
  return request.get('/coach/reviews/statistics')
}

/**
 * 获取评价标签列表
 * @param {Number} tagType - 标签类型：1-正面，2-负面
 */
export const getReviewTags = (tagType) => {
  return request.get('/reviews/tags', { params: { tagType } })
}
