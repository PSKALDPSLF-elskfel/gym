import request from '@/utils/request.js'

/**
 * 创建教练评价
 * 功能描述：学员对教练进行评价
 * 入参：{ coachId, planId, courseBookingId, reviewType, rating, tagList, content, images, isAnonymous }
 * 返回参数：评价ID
 * url地址：/reviews
 * 请求方式：POST
 */
export function createReview(data, callbacks) {
  return request.post('/reviews', data, callbacks)
}

/**
 * 删除评价
 * 功能描述：删除自己的评价
 * 入参：reviewId - 评价ID
 * 返回参数：无
 * url地址：/reviews/{reviewId}
 * 请求方式：DELETE
 */
export function deleteReview(reviewId, callbacks) {
  return request.delete(`/reviews/${reviewId}`, null, callbacks)
}

/**
 * 点赞/取消点赞评价
 * 功能描述：对评价进行点赞或取消点赞
 * 入参：reviewId - 评价ID
 * 返回参数：无
 * url地址：/reviews/{reviewId}/helpful
 * 请求方式：POST
 */
export function toggleHelpful(reviewId, callbacks) {
  return request.post(`/reviews/${reviewId}/helpful`, null, callbacks)
}

/**
 * 分页查询教练评价列表
 * 功能描述：查询指定教练的评价列表
 * 入参：{ coachId, rating, pageNum, pageSize }
 * 返回参数：分页数据 { records, total, size, current, pages }
 * url地址：/reviews
 * 请求方式：GET
 */
export function getReviewList(params, callbacks) {
  return request.get('/reviews', params, callbacks)
}

/**
 * 获取我的评价列表
 * 功能描述：查询当前用户发布的所有评价
 * 入参：{ pageNum, pageSize }
 * 返回参数：分页数据 { records, total, size, current, pages }
 * url地址：/reviews/my
 * 请求方式：GET
 */
export function getMyReviews(params, callbacks) {
  return request.get('/reviews/my', params, callbacks)
}

/**
 * 获取评价详情
 * 功能描述：查询指定评价的详细信息
 * 入参：reviewId - 评价ID
 * 返回参数：评价详情对象
 * url地址：/reviews/{reviewId}
 * 请求方式：GET
 */
export function getReviewDetail(reviewId, callbacks) {
  return request.get(`/reviews/${reviewId}`, null, callbacks)
}

/**
 * 获取教练评价统计
 * 功能描述：获取教练的评价统计信息（平均分、总数、星级分布等）
 * 入参：coachId - 教练ID
 * 返回参数：{ totalReviews, averageRating, rating5Count, rating4Count, rating3Count, rating2Count, rating1Count, replyRate }
 * url地址：/reviews/statistics/{coachId}
 * 请求方式：GET
 */
export function getReviewStatistics(coachId, callbacks) {
  return request.get(`/reviews/statistics/${coachId}`, null, callbacks)
}

/**
 * 获取评价标签列表
 * 功能描述：获取可用的评价标签（正面/负面）
 * 入参：{ tagType } - 标签类型：1-正面，2-负面
 * 返回参数：标签列表 [{ id, tagName, tagType, useCount }]
 * url地址：/reviews/tags
 * 请求方式：GET
 */
export function getReviewTags(params, callbacks) {
  return request.get('/reviews/tags', params, callbacks)
}
