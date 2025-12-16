<template>
  <view class="my-page">
    <mod-nav-bar title="我的" title-color="#fff"></mod-nav-bar>
    
    <!-- 未登录状态 -->
    <view v-if="!isLoggedIn" class="login-section">
      <view class="avatar-placeholder">
        <text class="fa fa-user-circle icon"></text>
      </view>
      <text class="login-text">点击登录</text>
      <button class="login-btn" @click="goToLogin">
        <text class="fa fa-sign-in-alt"></text>
        立即登录
      </button>
    </view>

    <!-- 已登录状态 -->
    <view v-else class="user-section">
      <view class="user-info">
        <image v-if="avatar" class="avatar" :src="getAvatarUrl(avatar)" mode="aspectFill"></image>
        <view v-else class="avatar-placeholder">
          <text class="fa fa-user-circle icon"></text>
        </view>
        <text class="username">{{ displayName }}</text>
        <text class="user-type" :class="getMemberTypeClass()">
          <text class="fa" :class="getMemberTypeIcon()"></text>
          {{ getUserTypeLabel(userType) }}
        </text>
      </view>

      <!-- 功能菜单 -->
      <view class="menu-section">
        <view class="menu-item" @click="goToNotifications">
          <view class="menu-left">
            <text class="fa fa-bell menu-icon" style="color: #ff6b00;"></text>
            <text class="menu-text">系统通知</text>
          </view>
          <view class="menu-right">
            <view v-if="unreadCount > 0" class="badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
            <text class="fa fa-chevron-right menu-arrow"></text>
          </view>
        </view>

        <view class="menu-item" @click="goToMyBookings">
          <view class="menu-left">
            <text class="fa fa-calendar menu-icon" style="color: #52c41a;"></text>
            <text class="menu-text">我的预约</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToMyMembership">
          <view class="menu-left">
            <text class="fa fa-id-card menu-icon" style="color: #667eea;"></text>
            <text class="menu-text">我的会员</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToMembershipPackage">
          <view class="menu-left">
            <text class="fa fa-star menu-icon" style="color: #fbbf24;"></text>
            <text class="menu-text">会员套餐</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToTrainingPlan">
          <view class="menu-left">
            <text class="fa fa-dumbbell menu-icon" style="color: #10b981;"></text>
            <text class="menu-text">我的训练计划</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToTrainingHistory">
          <view class="menu-left">
            <text class="fa fa-history menu-icon" style="color: #8b5cf6;"></text>
            <text class="menu-text">训练历史</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToBodyTest">
          <view class="menu-left">
            <text class="fa fa-heartbeat menu-icon" style="color: #ef4444;"></text>
            <text class="menu-text">体测报告</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToProfile">
          <view class="menu-left">
            <text class="fa fa-user menu-icon"></text>
            <text class="menu-text">个人信息</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToChangePassword">
          <view class="menu-left">
            <text class="fa fa-key menu-icon"></text>
            <text class="menu-text">修改密码</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>

        <view class="menu-item" @click="goToApiTest" style="border-top: 2px solid #ff6b00; margin-top: 20px; padding-top: 20px;">
          <view class="menu-left">
            <text class="fa fa-bug menu-icon" style="color: #ff6b00;"></text>
            <text class="menu-text">🔧 API诊断</text>
          </view>
          <text class="fa fa-chevron-right menu-arrow"></text>
        </view>
      </view>
      
      <button class="logout-btn" @click="handleLogout">
        <text class="fa fa-sign-out-alt"></text>
        退出登录
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user.js'
import { safeLogout } from '@/utils/auth.js'
import { getFileUrl } from '@/utils/fileUtils.js'
import { getUnreadCount } from '@/apis/notification.js'

// 获取用户store
const userStore = useUserStore()

// 未读通知数量
const unreadCount = ref(0)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const displayName = computed(() => userStore.displayName)
const userType = computed(() => userStore.userType)
const avatar = computed(() => userStore.avatar)
const memberType = computed(() => userStore.userInfo?.memberType || 0)

/**
 * 跳转到登录页
 */
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/auth/login'
  })
}

/**
 * 跳转到系统通知页
 */
const goToNotifications = () => {
  uni.navigateTo({
    url: '/pages/notification/list'
  })
}

/**
 * 跳转到我的预约页
 */
const goToMyBookings = () => {
  uni.navigateTo({
    url: '/pages/booking/my'
  })
}

/**
 * 跳转到我的会员页
 */
const goToMyMembership = () => {
  uni.navigateTo({
    url: '/pages/membership/my-membership'
  })
}

/**
 * 跳转到会员套餐页
 */
const goToMembershipPackage = () => {
  uni.navigateTo({
    url: '/pages/membership/package-list'
  })
}

/**
 * 跳转到个人信息页
 */
const goToProfile = () => {
  uni.navigateTo({
    url: '/pages/my/profile'
  })
}

/**
 * 跳转到修改密码页
 */
const goToChangePassword = () => {
  uni.navigateTo({
    url: '/pages/my/change-password'
  })
}

/**
 * 跳转到API诊断页
 */
const goToApiTest = () => {
  uni.navigateTo({
    url: '/pages/debug/api-test'
  })
}

/**
 * 跳转到我的训练计划页
 */
const goToTrainingPlan = () => {
  uni.navigateTo({
    url: '/pages/training-plan/list'
  })
}

/**
 * 跳转到训练历史页
 */
const goToTrainingHistory = () => {
  uni.navigateTo({
    url: '/pages/training-plan/history'
  })
}

/**
 * 跳转到体测报告页
 */
const goToBodyTest = () => {
  uni.navigateTo({
    url: '/pages/body-test/report'
  })
}

/**
 * 退出登录
 */
const handleLogout = async () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        await safeLogout()
      }
    }
  })
}

/**
 * 加载未读通知数量
 */
const loadUnreadCount = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const count = await getUnreadCount({ showDefaultMsg: false })
    unreadCount.value = count || 0
  } catch (error) {
    console.error('获取未读通知数量失败:', error)
  }
}

// 页面加载时获取未读数量
onMounted(() => {
  loadUnreadCount()
})

/**
 * 获取头像URL
 * 使用工具函数转换文件路径为完整URL
 */
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  // 使用工具函数转换路径，解决小程序端无法访问后端文件的问题
  return getFileUrl(avatar)
}

/**
 * 获取用户类型标签
 */
const getUserTypeLabel = (userType) => {
  // 如果是管理员，直接返回管理员
  if (userType === 'ADMIN') {
    return '管理员'
  }
  
  // 如果是普通用户，根据会员类型返回
  if (userType === 'USER') {
    const currentMemberType = memberType.value
    
    if (currentMemberType === 2) {
      return '铂金会员'
    } else if (currentMemberType === 1) {
      return '黄金会员'
    } else {
      return '普通用户'
    }
  }
  
  return '未知'
}

/**
 * 获取会员类型样式类
 */
const getMemberTypeClass = () => {
  if (userType.value === 'ADMIN') {
    return 'admin-type'
  }
  
  const currentMemberType = memberType.value
  if (currentMemberType === 2) {
    return 'platinum-member'
  } else if (currentMemberType === 1) {
    return 'gold-member'
  }
  
  return ''
}

/**
 * 获取会员类型图标
 */
const getMemberTypeIcon = () => {
  if (userType.value === 'ADMIN') {
    return 'fa-user-shield'
  }
  
  const currentMemberType = memberType.value
  if (currentMemberType === 2) {
    return 'fa-crown'
  } else if (currentMemberType === 1) {
    return 'fa-crown'
  }
  
  return 'fa-id-badge'
}
</script>

<style lang="scss" scoped>
.my-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.login-section, .user-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  margin: 40rpx;
  text-align: center;
}

.avatar-placeholder {
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .icon {
    font-size: 120rpx;
    color: #667eea;
  }
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin: 0 auto 24rpx;
  display: block;
}

.login-text {
  display: block;
  font-size: 32rpx;
  color: #333;
  margin-bottom: 32rpx;
}

.username {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.user-type {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 32rpx;

  .fa {
    font-size: 22rpx;
  }
  
  // 管理员样式
  &.admin-type {
    color: #f5222d;
    font-weight: bold;
  }
  
  // 铂金会员样式
  &.platinum-member {
    color: #722ed1;
    font-weight: bold;
  }
  
  // 黄金会员样式
  &.gold-member {
    color: #faad14;
    font-weight: bold;
  }
}

.login-btn {
  width: 200rpx;
  height: 72rpx;
  background: #667eea;
  color: #ffffff;
  border-radius: 36rpx;
  font-size: 28rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  
  .fa {
    font-size: 24rpx;
  }

  &::after {
    border: none;
  }
}

.menu-section {
  margin: 32rpx 0;

  .menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    .menu-left {
      display: flex;
      align-items: center;
      gap: 16rpx;

      .menu-icon {
        font-size: 32rpx;
        color: #667eea;
        width: 40rpx;
      }

      .menu-text {
        font-size: 28rpx;
        color: #333;
      }
    }

    .menu-right {
      display: flex;
      align-items: center;
      gap: 16rpx;
      
      .badge {
        min-width: 36rpx;
        height: 36rpx;
        line-height: 36rpx;
        padding: 0 8rpx;
        background-color: #ff4d4f;
        color: #fff;
        font-size: 20rpx;
        text-align: center;
        border-radius: 18rpx;
      }
    }

    .menu-arrow {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.logout-btn {
  width: 200rpx;
  height: 72rpx;
  background: #ff4d4f;
  color: #ffffff;
  border-radius: 36rpx;
  font-size: 28rpx;
  border: none;
  margin: 32rpx auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;

  .fa {
    font-size: 24rpx;
  }
  
  &::after {
    border: none;
  }
}
</style>
