import request from '@/utils/request'

/**
 * 获取仪表盘统计数据（增强版）
 * @param {object} params - 请求参数（无参数）
 * @param {object} callbacks - 回调函数 { onSuccess, onError }
 * @returns {Promise}
 */
export function getDashboardStatistics(params, callbacks) {
  return request.get('/dashboard/statistics', params, callbacks)
}

/**
 * 导出统计报表
 * @param {object} data - 导出参数 { reportType, startDate, endDate, exportFormat }
 * @returns {Promise}
 */
export function exportDashboardReport(data) {
  return request.post('/dashboard/export', data, {
    responseType: 'blob'
  })
}
