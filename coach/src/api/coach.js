import request from '@/utils/request'

// 登录
export const login = (username, password) => {
  return request.post('/user/login', { username, password })
}

// 获取教练信息
export const getCoachInfo = () => {
  return request.get('/coaches/info')
}

// 获取首页数据
export const getDashboardData = () => {
  return request.get('/coaches/dashboard')
}

// 修改密码（已弃用，请使用 @/api/user 中的 changePassword）
export const changePassword = (oldPassword, newPassword) => {
  const userId = JSON.parse(localStorage.getItem('user'))?.id
  return request.put(`/user/password/${userId}`, { oldPassword, newPassword })
}

// 更新个人信息
export const updateCoachInfo = (data) => {
  return request.put('/coaches/info', data)
}
