<template>
  <view class="my-membership-page">
    <mod-nav-bar title="我的会员" title-color="#fff"></mod-nav-bar>
    
    <!-- 当前会员状态卡片 -->
    <view v-if="currentMembership" class="membership-card" :class="{ 'gold-card': currentMembership.memberType === 1, 'platinum-card': currentMembership.memberType === 2 }">
      <view class="card-header">
        <text class="member-type-text">{{ currentMembership.memberTypeName }}</text>
        <view class="status-badge" :class="{ 'active-badge': !currentMembership.expired, 'expired-badge': currentMembership.expired }">
          <text class="badge-text">{{ currentMembership.expired ? '已过期' : '使用中' }}</text>
        </view>
      </view>

      <view class="card-content">
        <view class="info-item">
          <text class="info-label">套餐名称</text>
          <text class="info-value">{{ currentMembership.packageName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">到期时间</text>
          <text class="info-value">{{ formatDateTime(currentMembership.endTime) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">剩余天数</text>
          <text class="info-value highlight">{{ currentMembership.remainingDays }}天</text>
        </view>
      </view>

      <view class="card-footer">
        <view class="renew-button" @click="goToPackageList">
          <text class="button-text">续费会员</text>
        </view>
      </view>
    </view>

    <!-- 无会员状态 -->
    <view v-else class="no-membership-card">
      <text class="no-membership-icon">👤</text>
      <text class="no-membership-text">您还不是会员</text>
      <text class="no-membership-desc">开通会员享受更多权益</text>
      <view class="purchase-button" @click="goToPackageList">
        <text class="button-text">立即开通</text>
      </view>
    </view>

    <!-- 购买历史 -->
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">购买历史</text>
      </view>

      <view v-if="historyList.length > 0" class="history-list">
        <view v-for="item in historyList" :key="item.id" class="history-item">
          <view class="item-header">
            <text class="package-name">{{ item.packageName }}</text>
            <view class="status-badge" :class="{ 'active-badge': !item.expired, 'expired-badge': item.expired }">
              <text class="badge-text">{{ item.statusName }}</text>
            </view>
          </view>

          <view class="item-content">
            <view class="info-row">
              <text class="info-label">会员类型：</text>
              <text class="info-value">{{ item.memberTypeName }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">购买时间：</text>
              <text class="info-value">{{ formatDateTime(item.purchaseTime) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">有效期：</text>
              <text class="info-value">{{ formatDateTime(item.startTime) }} 至 {{ formatDateTime(item.endTime) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">购买价格：</text>
              <text class="info-value price">¥{{ item.price }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-history">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无购买记录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCurrentMembership, getUserMembershipHistory } from '@/apis/userMembership.js'

// 数据
const currentMembership = ref(null)
const historyList = ref([])
const userId = ref(1) // TODO: 从登录状态获取用户ID

// 页面显示时加载数据
onShow(() => {
  loadCurrentMembership()
  loadHistory()
})

// 加载当前会员状态
const loadCurrentMembership = () => {
  getCurrentMembership(userId.value, {
    onSuccess: (res) => {
      currentMembership.value = res
    }
  })
}

// 加载购买历史
const loadHistory = () => {
  getUserMembershipHistory(userId.value, {
    onSuccess: (res) => {
      historyList.value = res || []
    }
  })
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 兼容iOS：将 "-" 替换为 "/"
  const iosCompatibleDate = dateTime.replace(/-/g, '/')
  const date = new Date(iosCompatibleDate)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 跳转到套餐列表
const goToPackageList = () => {
  uni.navigateTo({
    url: '/pages/membership/package-list'
  })
}
</script>

<style lang="scss" scoped>
.my-membership-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40rpx 30rpx;
}

// 会员卡片
.membership-card {
  background: #FF6B35;
  border-radius: 32rpx;
  padding: 50rpx 40rpx;
  margin-bottom: 40rpx;
  position: relative;
  overflow: hidden;

  &.gold-card {
    background: #f093fb;
  }

  &.platinum-card {
    background: #4facfe;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;

  .member-type-text {
    font-size: 40rpx;
    color: #fff;
    font-weight: bold;
  }
}

.status-badge {
  padding: 8rpx 24rpx;
  border-radius: 30rpx;

  &.active-badge {
    background: rgba(82, 196, 26, 0.2);
    border: 2rpx solid rgba(82, 196, 26, 0.5);

    .badge-text {
      color: #52c41a;
    }
  }

  &.expired-badge {
    background: rgba(245, 34, 45, 0.2);
    border: 2rpx solid rgba(245, 34, 45, 0.5);

    .badge-text {
      color: #f5222d;
    }
  }

  .badge-text {
    font-size: 24rpx;
    font-weight: bold;
  }
}

.card-content {
  margin-bottom: 40rpx;

  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .info-label {
      font-size: 28rpx;
      color: rgba(255, 255, 255, 0.8);
    }

    .info-value {
      font-size: 28rpx;
      color: #fff;
      font-weight: bold;

      &.highlight {
        font-size: 36rpx;
        color: #fff;
      }
    }
  }
}

.card-footer {
  .renew-button {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 60rpx;
    padding: 25rpx 0;
    text-align: center;

    .button-text {
      font-size: 32rpx;
      color: #fff;
      font-weight: bold;
    }
  }
}

// 无会员状态
.no-membership-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 80rpx 40rpx;
  margin-bottom: 40rpx;
  text-align: center;

  .no-membership-icon {
    font-size: 120rpx;
    display: block;
    margin-bottom: 30rpx;
  }

  .no-membership-text {
    font-size: 36rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 15rpx;
  }

  .no-membership-desc {
    font-size: 28rpx;
    color: #999;
    display: block;
    margin-bottom: 50rpx;
  }

  .purchase-button {
    background: #FF6B35;
    border-radius: 60rpx;
    padding: 25rpx 0;
    margin: 0 60rpx;

    .button-text {
      font-size: 32rpx;
      color: #fff;
      font-weight: bold;
    }
  }
}

// 历史记录
.history-section {
  .section-header {
    margin-bottom: 30rpx;

    .section-title {
      font-size: 36rpx;
      color: #333;
      font-weight: bold;
    }
  }
}

.history-list {
  .history-item {
    background: #fff;
    border-radius: 24rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25rpx;
    padding-bottom: 20rpx;
    border-bottom: 2rpx solid #f0f0f0;

    .package-name {
      font-size: 32rpx;
      color: #333;
      font-weight: bold;
    }
  }

  .item-content {
    .info-row {
      display: flex;
      align-items: flex-start;
      margin-bottom: 15rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: 26rpx;
        color: #999;
        margin-right: 10rpx;
        flex-shrink: 0;
      }

      .info-value {
        font-size: 26rpx;
        color: #666;
        flex: 1;

        &.price {
          color: #f5222d;
          font-weight: bold;
        }
      }
    }
  }
}

.empty-history {
  background: #fff;
  border-radius: 24rpx;
  padding: 100rpx 40rpx;
  text-align: center;

  .empty-icon {
    font-size: 100rpx;
    display: block;
    margin-bottom: 20rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>
