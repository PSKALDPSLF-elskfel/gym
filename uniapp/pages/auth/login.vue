<template>
  <view class="login-page">
    <mod-nav-bar title="登录" title-color="#333"></mod-nav-bar>
    
    <view class="login-container">
      <view class="logo-section">
        <view class="icon-wrapper">
          <text class="fa fa-user-circle"></text>
        </view>
        <text class="welcome-text">欢迎登录</text>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-user input-icon"></text>
          <input 
            class="input" 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-lock input-icon"></text>
          <input 
            class="input" 
            v-model="loginForm.password" 
            type="password"
            placeholder="请输入密码"
          />
        </view>
      </view>

      <button class="login-btn" @click="handleLogin" :loading="loading">
        <text class="fa fa-sign-in-alt"></text>
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <view class="link-section">
        <text class="link-text" @click="goToRegister">
          还没有账号？立即注册
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user.js'

// 数据
const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)

// 获取store
const userStore = useUserStore()

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value.username) {
    uni.showToast({
      title: '请输入用户名',
      icon: 'none'
    })
    return
  }

  if (!loginForm.value.password) {
    uni.showToast({
      title: '请输入密码',
      icon: 'none'
    })
    return
  }

  try {
    loading.value = true

    // 调用登录
    await userStore.login({
      username: loginForm.value.username,
      password: loginForm.value.password
    })

    // 登录成功提示
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })

    // 延迟跳转到我的页面
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/my/my'
      })
    }, 1500)

  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到注册页
 */
const goToRegister = () => {
  uni.navigateTo({
    url: '/pages/auth/register'
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.login-container {
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

.login-btn {
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

