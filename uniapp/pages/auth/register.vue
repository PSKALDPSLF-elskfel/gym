<template>
  <view class="register-page">
    <mod-nav-bar title="注册" title-color="#333"></mod-nav-bar>
    
    <view class="register-container">
      <view class="logo-section">
        <view class="icon-wrapper">
          <text class="fa fa-user-plus"></text>
        </view>
        <text class="welcome-text">创建新账号</text>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-user input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.username" 
            placeholder="请输入用户名（3-50字符）"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-envelope input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.email" 
            placeholder="请输入邮箱"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-phone input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.phone" 
            placeholder="请输入手机号（可选）"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-id-card input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.name" 
            placeholder="请输入姓名（可选）"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-lock input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.password" 
            type="password"
            placeholder="请输入密码（至少6位）"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-lock input-icon"></text>
          <input 
            class="input" 
            v-model="registerForm.confirmPassword" 
            type="password"
            placeholder="请再次输入密码"
          />
        </view>
      </view>

      <button class="register-btn" @click="handleRegister" :loading="loading">
        <text class="fa fa-user-plus"></text>
        {{ loading ? '注册中...' : '立即注册' }}
      </button>

      <view class="link-section">
        <text class="link-text" @click="goToLogin">
          已有账号？立即登录
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { register } from '@/apis/user.js'

// 数据
const registerForm = ref({
  username: '',
  email: '',
  phone: '',
  name: '',
  password: '',
  confirmPassword: '',
  userType: 'USER' // 默认注册为普通用户
})

const loading = ref(false)

/**
 * 表单验证
 */
const validateForm = () => {
  if (!registerForm.value.username) {
    uni.showToast({
      title: '请输入用户名',
      icon: 'none'
    })
    return false
  }

  if (registerForm.value.username.length < 3 || registerForm.value.username.length > 50) {
    uni.showToast({
      title: '用户名长度必须在3-50字符之间',
      icon: 'none'
    })
    return false
  }

  if (!registerForm.value.email) {
    uni.showToast({
      title: '请输入邮箱',
      icon: 'none'
    })
    return false
  }

  // 邮箱格式验证
  const emailRegex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (!emailRegex.test(registerForm.value.email)) {
    uni.showToast({
      title: '邮箱格式不正确',
      icon: 'none'
    })
    return false
  }

  // 手机号验证（如果填写了）
  if (registerForm.value.phone) {
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(registerForm.value.phone)) {
      uni.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return false
    }
  }

  if (!registerForm.value.password) {
    uni.showToast({
      title: '请输入密码',
      icon: 'none'
    })
    return false
  }

  if (registerForm.value.password.length < 6) {
    uni.showToast({
      title: '密码长度不能少于6位',
      icon: 'none'
    })
    return false
  }

  if (!registerForm.value.confirmPassword) {
    uni.showToast({
      title: '请再次输入密码',
      icon: 'none'
    })
    return false
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return false
  }

  return true
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }

  try {
    loading.value = true

    // 调用注册接口
    await register(registerForm.value, {
      successMsg: '注册成功',
      showDefaultMsg: true
    })

    // 延迟跳转到登录页
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到登录页
 */
const goToLogin = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.register-container {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  margin: 40rpx;
}

.logo-section {
  text-align: center;
  margin-bottom: 60rpx;

  .icon-wrapper {
    margin-bottom: 20rpx;

    .fa {
      font-size: 120rpx;
      color: #667eea;
    }
  }

  .welcome-text {
    display: block;
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
  }
}

.form-item {
  margin-bottom: 32rpx;

  .input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: #f5f5f5;
    border-radius: 12rpx;
    padding: 0 24rpx;

    .input-icon {
      font-size: 32rpx;
      color: #999;
      margin-right: 16rpx;
    }

    .input {
      flex: 1;
      height: 88rpx;
      font-size: 28rpx;
      color: #333;
      background: transparent;
      border: none;
    }
  }
}

.register-btn {
  width: 100%;
  height: 88rpx;
  background: #667eea;
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 12rpx;
  border: none;
  margin-top: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  
  .fa {
    font-size: 28rpx;
  }

  &::after {
    border: none;
  }
}

.link-section {
  text-align: center;
  margin-top: 32rpx;

  .link-text {
    color: #667eea;
    font-size: 28rpx;
  }
}
</style>
