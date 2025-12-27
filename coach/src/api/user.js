import request from '@/utils/request'
import { useUserStore } from '@/store/user'

// 获取当前用户信息
export const getCurrentUser = () => {
  return request.get('/user/current')
}

// 更新用户信息
export const updateUser = (userId, data) => {
  return request.put(`/user/${userId}`, data)
}

// 修改密码
export const changePassword = (oldPassword, newPassword) => {
  // 从 store 获取用户ID
  const userStore = useUserStore()
  const userId = userStore.user?.id
  
  if (!userId) {
    return Promise.reject(new Error('未登录或登录已过期'))
  }
  
  return request.put(`/user/password/${userId}`, {
    oldPassword,
    newPassword
  })
}
