/**
 * 数据处理工具类
 * 提供统一的数据验证和清理方法
 */

/**
 * 确保数据为有效的数组
 * @param {any} data - 数据
 * @param {boolean} filter - 是否过滤空值
 * @returns {Array} 有效的数组
 */
export function ensureArray(data, filter = true) {
  if (!data) return []
  if (Array.isArray(data)) {
    return filter ? data.filter(item => item != null) : data
  }
  return []
}

/**
 * 确保数据为有效的对象
 * @param {any} data - 数据
 * @returns {Object} 有效的对象
 */
export function ensureObject(data) {
  if (!data || typeof data !== 'object') return {}
  return data
}

/**
 * 安全获取嵌套对象值
 * @param {Object} obj - 对象
 * @param {string} path - 路径，如 'user.profile.name'
 * @param {any} defaultValue - 默认值
 * @returns {any} 对象值或默认值
 */
export function safeGet(obj, path, defaultValue = null) {
  if (!obj || !path) return defaultValue
  
  const keys = path.split('.')
  let result = obj
  
  for (const key of keys) {
    if (result && typeof result === 'object' && key in result) {
      result = result[key]
    } else {
      return defaultValue
    }
  }
  
  return result
}

/**
 * 批量转换列表中的对象
 * @param {Array} list - 对象列表
 * @param {Function} transform - 转换函数
 * @returns {Array} 转换后的列表
 */
export function transformArray(list, transform) {
  return ensureArray(list).map(item => {
    try {
      return transform(item)
    } catch (error) {
      console.error('转换数据失败:', error, item)
      return item
    }
  })
}

/**
 * 数据验证
 * @param {any} data - 数据
 * @param {Object} rules - 验证规则
 * @returns {Object} { valid: boolean, errors: Array }
 */
export function validate(data, rules) {
  const errors = []
  
  for (const [key, rule] of Object.entries(rules)) {
    const value = safeGet(data, key)
    
    if (rule.required && !value) {
      errors.push(`${key} 是必需的`)
    }
    
    if (rule.type && value && typeof value !== rule.type) {
      errors.push(`${key} 类型应为 ${rule.type}`)
    }
    
    if (rule.min !== undefined && value < rule.min) {
      errors.push(`${key} 最小值为 ${rule.min}`)
    }
    
    if (rule.max !== undefined && value > rule.max) {
      errors.push(`${key} 最大值为 ${rule.max}`)
    }
  }
  
  return {
    valid: errors.length === 0,
    errors
  }
}

export default {
  ensureArray,
  ensureObject,
  safeGet,
  transformArray,
  validate
}
