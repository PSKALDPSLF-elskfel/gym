<template>
  <view class="change-password-page">
    <mod-nav-bar title="修改密码" title-color="#333" :show-back="true"></mod-nav-bar>
    
    <view class="form-container">
      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-lock input-icon"></text>
          <input 
            class="input" 
            v-model="passwordForm.oldPassword" 
            type="password"
            placeholder="请输入旧密码"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-key input-icon"></text>
          <input 
            class="input" 
            v-model="passwordForm.newPassword" 
            type="password"
            placeholder="请输入新密码（至少6位）"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="fa fa-key input-icon"></text>
          <input 
            class="input" 
            v-model="passwordForm.confirmPassword" 
            type="password"
            placeholder="请再次输入新密码"
          />
        </view>
      </view>

      <button class="submit-btn" @click="handleSubmit" :loading="loading">
        <text class="fa fa-check"></text>
        {{ loading ? '提交中...' : '确认修改' }}
      </button>

      <view class="tip-section">
        <text class="fa fa-info-circle"></text>
        <text class="tip-text">修改密码后需要重新登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user.js'
import { updatePassword } from '@/apis/user.js'

const userStore = useUserStore()

// 数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)

/**
 * 表单验证
 */
const validateForm = () => {
  if (!passwordForm.value.oldPassword) {
    uni.showToast({
      title: '请输入旧密码',
      icon: 'none'
    })
    return false
  }

  if (!passwordForm.value.newPassword) {
    uni.showToast({
      title: '请输入新密码',
      icon: 'none'
    })
    return false
  }

  if (passwordForm.value.newPassword.length < 6) {
    uni.showToast({
      title: '新密码长度不能少于6位',
      icon: 'none'
    })
    return false
  }

  if (!passwordForm.value.confirmPassword) {
    uni.showToast({
      title: '请再次输入新密码',
      icon: 'none'
    })
    return false
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return false
  }

  return true
}

/**
 * 提交修改
 */
const handleSubmit = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }

  try {
    loading.value = true

    // 调用修改密码接口
    await updatePassword(userStore.userId, {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    }, {
      successMsg: '密码修改成功',
      showDefaultMsg: true
    })

    // 提示用户重新登录
    uni.showModal({
      title: '密码修改成功',
      content: '为了您的账户安全，请重新登录',
      showCancel: false,
      success: async () => {
        // 清除用户信息
        await userStore.logout()
        
        // 跳转到登录页
        uni.reLaunch({
          url: '/pages/auth/login'
        })
      }
    })

  } catch (error) {
    console.error('密码修改失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.change-password-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.form-container {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  margin: 40rpx;
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

.submit-btn {
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

.tip-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-top: 32rpx;
  padding: 24rpx;
  background: #fff7e6;
  border-radius: 12rpx;

  .fa {
    font-size: 28rpx;
    color: #fa8c16;
  }

  .tip-text {
    font-size: 24rpx;
    color: #fa8c16;
  }
}
</style>
