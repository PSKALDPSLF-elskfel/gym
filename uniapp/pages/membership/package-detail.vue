<template>
  <view class="package-detail-page">
    <mod-nav-bar title="套餐详情" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 套餐信息卡片 -->
    <view class="package-card" :class="{ 'gold-card': packageInfo.memberType === 1, 'platinum-card': packageInfo.memberType === 2 }">
      <!-- 会员类型标签 -->
      <view class="member-type-badge" :class="{ 'gold-badge': packageInfo.memberType === 1, 'platinum-badge': packageInfo.memberType === 2 }">
        <text class="badge-text">{{ packageInfo.memberTypeDisplayName }}</text>
      </view>

      <!-- 套餐名称 -->
      <view class="package-name">
        <text class="name-text">{{ packageInfo.name }}</text>
      </view>

      <!-- 价格 -->
      <view class="package-price">
        <text class="price-symbol">¥</text>
        <text class="price-value">{{ packageInfo.price }}</text>
        <text class="price-unit">/{{ formatDuration(packageInfo.durationDays) }}</text>
      </view>

      <!-- 有效期 -->
      <view class="package-duration">
        <text class="duration-label">有效期：</text>
        <text class="duration-value">{{ packageInfo.durationDays }}天</text>
      </view>
    </view>

    <!-- 套餐描述 -->
    <view class="section-card">
      <view class="section-title">
        <text class="title-text">套餐介绍</text>
      </view>
      <view class="section-content">
        <text class="content-text">{{ packageInfo.description }}</text>
      </view>
    </view>

    <!-- 会员权益 -->
    <view class="section-card">
      <view class="section-title">
        <text class="title-text">会员权益</text>
      </view>
      <view class="benefits-list">
        <view v-for="(benefit, index) in benefitsList" :key="index" class="benefit-item">
          <text class="benefit-icon">✓</text>
          <text class="benefit-text">{{ benefit }}</text>
        </view>
      </view>
    </view>

    <!-- 当前会员状态（如果已是会员） -->
    <view v-if="currentMembership" class="section-card membership-status">
      <view class="section-title">
        <text class="title-text">当前会员状态</text>
      </view>
      <view class="status-content">
        <view class="status-item">
          <text class="status-label">会员类型：</text>
          <text class="status-value">{{ currentMembership.memberTypeName }}</text>
        </view>
        <view class="status-item">
          <text class="status-label">到期时间：</text>
          <text class="status-value">{{ formatDateTime(currentMembership.endTime) }}</text>
        </view>
        <view class="status-item">
          <text class="status-label">剩余天数：</text>
          <text class="status-value status-highlight">{{ currentMembership.remainingDays }}天</text>
        </view>
      </view>
      <view class="renew-tip">
        <text class="tip-text">购买后将在当前会员到期时间基础上延长</text>
      </view>
    </view>

    <!-- 底部购买按钮 -->
    <view class="bottom-bar">
      <view class="price-info">
        <text class="price-label">价格</text>
        <view class="price-display">
          <text class="price-symbol">¥</text>
          <text class="price-value">{{ packageInfo.price }}</text>
        </view>
      </view>
      <view class="purchase-button" @click="handlePurchase">
        <text class="button-text">{{ purchaseButtonText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMembershipPackageById } from '@/apis/membershipPackage.js'
import { purchaseMembershipPackage, getCurrentMembership } from '@/apis/userMembership.js'
import DateUtils from '@/utils/dateUtils.js'
import { useUserStore } from '@/store/user.js'

// 数据
const packageId = ref(null)
const packageInfo = ref({})
const currentMembership = ref(null)
const userStore = useUserStore()

// 使用store中的userId，每详一个会员应当查看自己的会员信息
const userId = computed(() => userStore.userId)

// 计算属性
const benefitsList = computed(() => {
  if (!packageInfo.value.benefits) return []
  return packageInfo.value.benefits.split('+').map(item => item.trim())
})

const purchaseButtonText = computed(() => {
  if (currentMembership.value && currentMembership.value.memberType === packageInfo.value.memberType) {
    return '续费会员'
  }
  return '立即购买'
})

// 页面加载
onLoad((options) => {
  if (options.id) {
    packageId.value = options.id
    loadPackageDetail()
    loadCurrentMembership()
  }
})

// 加载套餐详情
const loadPackageDetail = () => {
  getMembershipPackageById(packageId.value, {
    onSuccess: (res) => {
      packageInfo.value = res
    },
    onError: (err) => {
      uni.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  })
}

// 加载当前会员状态
const loadCurrentMembership = () => {
  getCurrentMembership(userId.value, {
    onSuccess: (res) => {
      currentMembership.value = res
    }
  })
}

// 格式化时长
const formatDuration = (days) => {
  if (!days) return ''
  if (days >= 365) {
    return Math.floor(days / 365) + '年'
  } else if (days >= 30) {
    return Math.floor(days / 30) + '个月'
  }
  return days + '天'
}

// 格式化日期时间
// 需要在顶部导入：import DateUtils from '@/utils/dateUtils.js'
const formatDateTime = (dateTime) => {
  return DateUtils.format(dateTime, 'YYYY-MM-DD')
}

// 处理购买
const handlePurchase = () => {
  uni.showModal({
    title: '确认购买',
    content: `确认购买${packageInfo.value.name}吗？`,
    success: (res) => {
      if (res.confirm) {
        doPurchase()
      }
    }
  })
}

// 执行购买
const doPurchase = () => {
  uni.showLoading({
    title: '购买中...'
  })

  purchaseMembershipPackage(userId.value, packageId.value, {
    successMsg: '购买成功',
    onSuccess: (res) => {
      uni.hideLoading()
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/membership/my-membership'
        })
      }, 1500)
    },
    onError: (err) => {
      uni.hideLoading()
    }
  })
}
</script>

<style lang="scss" scoped>
.package-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40rpx 30rpx 200rpx;
}

// 套餐信息卡片
.package-card {
  background: #FF6B35;
  border-radius: 32rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 30rpx;
  position: relative;
  overflow: hidden;

  &.gold-card {
    background: #f093fb;
  }

  &.platinum-card {
    background: #4facfe;
  }
}

.member-type-badge {
  display: inline-block;
  padding: 10rpx 30rpx;
  border-radius: 40rpx;
  background: rgba(255, 255, 255, 0.3);
  margin-bottom: 30rpx;

  .badge-text {
    font-size: 24rpx;
    color: #fff;
    font-weight: bold;
  }
}

.package-name {
  margin-bottom: 20rpx;

  .name-text {
    font-size: 48rpx;
    color: #fff;
    font-weight: bold;
  }
}

.package-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 20rpx;

  .price-symbol {
    font-size: 36rpx;
    color: #fff;
    margin-right: 10rpx;
  }

  .price-value {
    font-size: 72rpx;
    color: #fff;
    font-weight: bold;
  }

  .price-unit {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.9);
    margin-left: 10rpx;
  }
}

.package-duration {
  display: flex;
  align-items: center;

  .duration-label {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }

  .duration-value {
    font-size: 28rpx;
    color: #fff;
    font-weight: bold;
  }
}

// 区块卡片
.section-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
}

.section-title {
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;

  .title-text {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
  }
}

.section-content {
  .content-text {
    font-size: 28rpx;
    color: #666;
    line-height: 1.8;
  }
}

// 权益列表
.benefits-list {
  .benefit-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .benefit-icon {
      font-size: 28rpx;
      color: #52c41a;
      margin-right: 15rpx;
      flex-shrink: 0;
    }

    .benefit-text {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
      flex: 1;
    }
  }
}

// 会员状态
.membership-status {
  background: #fff5e6;
}

.status-content {
  .status-item {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .status-label {
      font-size: 28rpx;
      color: #666;
      margin-right: 20rpx;
    }

    .status-value {
      font-size: 28rpx;
      color: #333;
      font-weight: bold;

      &.status-highlight {
        color: #f5222d;
      }
    }
  }
}

.renew-tip {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 2rpx dashed #ffd591;

  .tip-text {
    font-size: 24rpx;
    color: #fa8c16;
  }
}

// 底部购买栏
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 20rpx 30rpx;
  z-index: 100;
}

.price-info {
  .price-label {
    font-size: 24rpx;
    color: #999;
    display: block;
    margin-bottom: 5rpx;
  }

  .price-display {
    display: flex;
    align-items: baseline;

    .price-symbol {
      font-size: 28rpx;
      color: #f5222d;
      margin-right: 5rpx;
    }

    .price-value {
      font-size: 40rpx;
      color: #f5222d;
      font-weight: bold;
    }
  }
}

.purchase-button {
  background: #FF6B35;
  border-radius: 60rpx;
  padding: 25rpx 80rpx;

  .button-text {
    font-size: 32rpx;
    color: #fff;
    font-weight: bold;
  }
}
</style>
