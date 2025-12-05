/**
 * Uniapp 请求工具封装
 * 基于 uni.request API，复用 Vue3 项目的核心逻辑
 */

import { useUserStore } from '@/store/user.js'

// 错误类型枚举
const ErrorTypes = {
  NETWORK: 'network',
  BUSINESS: 'business',
  HTTP: 'http',
  TIMEOUT: 'timeout'
}

// 请求计数器
let requestId = 0

// 基础配置
const baseConfig = {
  // #ifdef H5
  baseURL: '/api', // H5端使用代理
  // #endif
  // #ifndef H5
  baseURL: 'http://localhost:8888/api', // 非H5端使用完整地址
  // #endif
  timeout: 30000, // 增加超时时间到30秒，避免首次加载超时
  header: {
    'Content-Type': 'application/json;charset=utf-8'
  }
}

/**
 * 获取完整的请求URL
 */
function getFullUrl(url) {
  if (url.startsWith('http')) {
    return url
  }
  return baseConfig.baseURL + url
}

/**
 * 获取用户token
 */
function getAuthToken() {
  try {
    const userStore = useUserStore()
    if (userStore && userStore.isLoggedIn) {
      return userStore.token
    }
  } catch (error) {
    console.warn('无法获取用户store')
  }
  return null
}

/**
 * 处理token过期
 */
function handleTokenExpired() {
  try {
    const userStore = useUserStore()
    if (userStore) {
      userStore.clearUserInfo()
    }
  } catch (error) {
    console.warn('清除用户信息失败')
  }

  // 跳转到登录页
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  if (currentPage && currentPage.route !== 'pages/auth/login') {
    uni.reLaunch({
      url: '/pages/auth/login'
    })
  }
}

/**
 * 显示提示消息
 */
function showMessage(content, type = 'success') {
  if (type === 'success') {
    uni.showToast({
      title: content,
      icon: 'success',
      duration: 2000
    })
  } else if (type === 'error') {
    uni.showToast({
      title: content,
      icon: 'none',
      duration: 3000
    })
  }
}

/**
 * 统一的请求方法
 */
function request(options = {}) {
  const {
    url,
    method = 'GET',
    data = null,
    params = null,
    showDefaultMsg = true,
    successMsg = '',
    errorMsg = '',
    onSuccess = null,
    onError = null,
    ...otherConfig
  } = options

  // 生成请求ID
  const currentRequestId = ++requestId
  const requestTime = Date.now()

  return new Promise((resolve, reject) => {
    // 构建请求头
    const header = {
      ...baseConfig.header,
      ...otherConfig.header
    }

    // 添加认证token
    const token = getAuthToken()
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    // 处理GET请求参数
    let finalUrl = url
    if (params && method.toUpperCase() === 'GET') {
      const queryString = Object.keys(params)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
        .join('&')
      finalUrl = `${url}${url.includes('?') ? '&' : '?'}${queryString}`
    }

    console.log(`📤 发送请求 [${currentRequestId}]:`, {
      method: method.toUpperCase(),
      url: finalUrl
    })

    // 发起请求
    uni.request({
      url: getFullUrl(finalUrl),
      method: method.toUpperCase(),
      data: method.toUpperCase() === 'GET' ? null : data,
      header,
      timeout: baseConfig.timeout,
      ...otherConfig,

      success: (res) => {
        const responseTime = Date.now() - requestTime

        console.log(`📥 收到响应 [${currentRequestId}]:`, {
          method: method.toUpperCase(),
          url: finalUrl,
          code: res.data?.code,
          time: `${responseTime}ms`
        })

        // HTTP状态码检查
        if (res.statusCode !== 200) {
          const error = {
            type: ErrorTypes.HTTP,
            code: res.statusCode,
            message: `HTTP错误(${res.statusCode})`,
            requestId: currentRequestId
          }

          if (errorMsg) {
            showMessage(errorMsg, 'error')
          } else if (showDefaultMsg) {
            showMessage(error.message, 'error')
          }

          if (typeof onError === 'function') {
            onError(error)
          }

          reject(error)
          return
        }

        const responseData = res.data

        // 业务状态码检查
        if (responseData.code === "200") {
          // 成功处理
          try {
            if (successMsg) {
              showMessage(successMsg, 'success')
            } else if (showDefaultMsg && method.toUpperCase() !== 'GET') {
              showMessage('操作成功', 'success')
            }

            if (typeof onSuccess === 'function') {
              onSuccess(responseData.data)
            }

            resolve(responseData.data)
          } catch (err) {
            console.error('成功回调执行错误:', err)
            resolve(responseData.data)
          }
        } else {
          // 业务错误处理
          const error = {
            type: ErrorTypes.BUSINESS,
            code: responseData.code,
            message: responseData.msg || '请求失败',
            data: responseData.data,
            requestId: currentRequestId
          }

          // 401特殊处理
          if (responseData.code === "401") {
            if (!url.includes('/login')) {
              handleTokenExpired()
              error.message = '登录已过期，请重新登录'
            }
          }

          try {
            if (errorMsg) {
              showMessage(errorMsg, 'error')
            } else if (showDefaultMsg) {
              showMessage(error.message, 'error')
            }

            if (typeof onError === 'function') {
              onError(error)
            }
          } catch (err) {
            console.error('错误回调执行错误:', err)
          }

          reject(error)
        }
      },

      fail: (err) => {
        console.error(`请求失败 [${currentRequestId}]:`, {
          method: method.toUpperCase(),
          url: finalUrl,
          error: err.errMsg
        })

        // 构建错误信息
        let error = {
          type: ErrorTypes.NETWORK,
          requestId: currentRequestId,
          message: '网络请求失败'
        }

        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            error.type = ErrorTypes.TIMEOUT
            error.message = '请求超时，请检查网络连接'
          } else if (err.errMsg.includes('fail')) {
            error.message = '网络连接失败，请检查网络设置'
          }
        }

        try {
          if (errorMsg) {
            showMessage(errorMsg, 'error')
          } else if (showDefaultMsg) {
            showMessage(error.message, 'error')
          }

          if (typeof onError === 'function') {
            onError(error)
          }
        } catch (callbackErr) {
          console.error('错误处理回调执行失败:', callbackErr)
        }

        reject(error)
      }
    })
  })
}

/**
 * GET 请求
 */
function get(url, params, config = {}) {
  return request({
    url,
    method: 'GET',
    params,
    ...config
  })
}

/**
 * POST 请求
 */
function post(url, data, config = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...config
  })
}

/**
 * PUT 请求
 */
function put(url, data, config = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...config
  })
}

/**
 * DELETE 请求
 */
function deleteRequest(url, config = {}) {
  return request({
    url,
    method: 'DELETE',
    ...config
  })
}

// 导出请求对象
export default {
  get,
  post,
  put,
  delete: deleteRequest,
  request
}

