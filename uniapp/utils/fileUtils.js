/**
 * 文件路径工具函数
 * 解决小程序端无法使用代理访问后端文件的问题
 */

/**
 * 获取文件服务器基础地址
 * @returns {string} 文件服务器地址
 */
function getFileServerUrl() {
  const isDev = true // 手动切换环境：true=开发环境, false=生产环境
  
  // #ifdef H5
  // H5端使用空字符串，因为有代理配置
  return ''
  // #endif
  
  // #ifndef H5
  // 小程序/App端使用完整地址
  if (isDev) {
    return 'http://localhost:8888'
  }
  return 'https://api.yourdomain.com'
  // #endif
}

/**
 * 转换文件路径为完整URL
 * @param {string} filePath - 文件路径（如：/files/bussiness/course/xxx.png）
 * @returns {string} 完整的文件URL
 */
export function getFileUrl(filePath) {
  if (!filePath) return ''
  
  // 如果已经是完整URL，直接返回
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath
  }
  
  // 如果是静态资源路径（/static/），直接返回
  if (filePath.startsWith('/static/')) {
    return filePath
  }
  
  // 拼接完整URL
  const baseUrl = getFileServerUrl()
  
  // 确保路径以 / 开头
  const normalizedPath = filePath.startsWith('/') ? filePath : `/${filePath}`
  
  return `${baseUrl}${normalizedPath}`
}

/**
 * 批量转换文件路径
 * @param {Array} filePaths - 文件路径数组
 * @returns {Array} 完整URL数组
 */
export function getFileUrls(filePaths) {
  if (!Array.isArray(filePaths)) return []
  return filePaths.map(path => getFileUrl(path))
}

/**
 * 转换对象中的文件路径字段
 * @param {Object} obj - 包含文件路径的对象
 * @param {Array<string>} fields - 需要转换的字段名数组
 * @returns {Object} 转换后的对象
 */
export function convertFileFields(obj, fields = []) {
  if (!obj || typeof obj !== 'object') return obj
  
  const result = { ...obj }
  
  fields.forEach(field => {
    if (result[field]) {
      result[field] = getFileUrl(result[field])
    }
  })
  
  return result
}

/**
 * 转换数组中对象的文件路径字段
 * @param {Array} list - 对象数组
 * @param {Array<string>} fields - 需要转换的字段名数组
 * @returns {Array} 转换后的数组
 */
export function convertFileFieldsInList(list, fields = []) {
  if (!Array.isArray(list)) return []
  return list.map(item => convertFileFields(item, fields))
}

export default {
  getFileUrl,
  getFileUrls,
  convertFileFields,
  convertFileFieldsInList
}
