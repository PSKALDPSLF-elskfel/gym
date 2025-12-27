/**
 * 文件工具函数
 */

/**
 * 获取文件完整访问URL
 * @param {string} filePath - 文件路径(如: /files/bussiness/course/xxx.png)
 * @returns {string} 完整的文件访问URL
 */
export function getFileUrl(filePath) {
  if (!filePath) {
    return ''
  }

  // 如果已经是完整URL(http或https开头)，直接返回
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath
  }

  // 标准化路径
  const path = filePath.startsWith('/') ? filePath : `/${filePath}`
  
  // 开发环境：直接返回相对路径（vite会通过proxy代理）
  if (import.meta.env.DEV) {
    return path
  }
  
  // 生产环境：拼接完整URL
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8888'
  const fullUrl = `${apiUrl}${path}`
  return fullUrl
}

/**
 * 获取图片完整访问URL
 * @param {string} imagePath - 图片路径
 * @returns {string} 完整的图片访问URL
 */
export function getImageUrl(imagePath) {
  return getFileUrl(imagePath)
}

/**
 * 获取头像完整访问URL
 * @param {string} avatarPath - 头像路径
 * @returns {string} 完整的头像访问URL
 */
export function getAvatarUrl(avatarPath) {
  return getFileUrl(avatarPath)
}

/**
 * 获取文件名
 * @param {string} filePath - 文件路径
 * @returns {string} 文件名
 */
export function getFileName(filePath) {
  if (!filePath) return ''
  return filePath.split('/').pop()
}

/**
 * 获取文件扩展名
 * @param {string} filePath - 文件路径或文件名
 * @returns {string} 文件扩展名(不含点)
 */
export function getFileExtension(filePath) {
  if (!filePath) return ''
  const fileName = getFileName(filePath)
  const lastDotIndex = fileName.lastIndexOf('.')
  return lastDotIndex !== -1 ? fileName.substring(lastDotIndex + 1).toLowerCase() : ''
}

/**
 * 判断是否为图片文件
 * @param {string} filePath - 文件路径
 * @returns {boolean} 是否为图片
 */
export function isImageFile(filePath) {
  const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']
  const ext = getFileExtension(filePath)
  return imageExtensions.includes(ext)
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`
}

export default {
  getFileUrl,
  getImageUrl,
  getAvatarUrl,
  getFileName,
  getFileExtension,
  isImageFile,
  formatFileSize
}
