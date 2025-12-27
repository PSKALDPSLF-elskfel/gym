import axios from 'axios'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/store/user'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8888'

const instance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 确保所有请求都加上 /api 前缀
    if (!config.url.startsWith('/api')) {
      config.url = '/api' + config.url
    }
    
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    console.log('响应拦截器 - 原始响应:', response)
    const res = response.data
    console.log('响应拦截器 - 响应数据:', res)
    
    // 后端返回格式: { code: '200', message: "...", data: ... }
    // 注意: code 可能是字符串或数字类型
    if (res.code && parseInt(res.code) !== 200) {
      console.error('响应拦截器 - 业务错误:', res)
      const errorMsg = res.message || res.msg || '请求失败'
      message.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    console.log('响应拦截器 - 返回data字段:', res.data)
    // 只返回 data 字段的内容，让前端代码直接使用
    return res.data
  },
  (error) => {
    console.error('响应拦截器 - 请求错误:', error)
    const userStore = useUserStore()
    
    if (error.response?.status === 401) {
      message.error('登录已过期，请重新登录')
      userStore.logout()
      window.location.href = '/login'
    } else {
      const errorMsg = error.response?.data?.message || error.response?.data?.msg || error.message || '请求失败'
      message.error(errorMsg)
    }
    
    return Promise.reject(error)
  }
)

export default instance