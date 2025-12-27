<template>
  <view class="package-list-page">
    <mod-nav-bar title="会员套餐" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 页面标题 -->
    <view class="page-header">
      <text class="page-title">会员套餐</text>
      <text class="page-subtitle">选择适合您的会员套餐</text>
    </view>

    <!-- 会员类型切换 -->
    <view class="member-type-tabs">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'all' }"
        @click="switchTab('all')"
      >
        <text class="tab-text">全部套餐</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 1 }"
        @click="switchTab(1)"
      >
        <text class="tab-text">黄金会员</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 2 }"
        @click="switchTab(2)"
      >
        <text class="tab-text">铂金会员</text>
      </view>
    </view>

    <!-- 套餐列表 -->
    <view class="package-list">
      <view 
        v-for="pkg in filteredPackages" 
        :key="pkg.id" 
        class="package-card"
        :class="{ 'gold-card': pkg.memberType === 1, 'platinum-card': pkg.memberType === 2 }"
        @click="handlePackageClick(pkg)"
      >
        <!-- 会员类型标签 -->
        <view class="member-type-badge" :class="{ 'gold-badge': pkg.memberType === 1, 'platinum-badge': pkg.memberType === 2 }">
          <text class="badge-text">{{ pkg.memberTypeDisplayName }}</text>
        </view>

        <!-- 套餐名称 -->
        <view class="package-name">
          <text class="name-text">{{ pkg.name }}</text>
        </view>

        <!-- 价格 -->
        <view class="package-price">
          <text class="price-symbol">¥</text>
          <text class="price-value">{{ pkg.price }}</text>
          <text class="price-unit">/{{ formatDuration(pkg.durationDays) }}</text>
        </view>

        <!-- 套餐描述 -->
        <view class="package-description">
          <text class="description-text">{{ pkg.description }}</text>
        </view>

        <!-- 会员权益 -->
        <view class="package-benefits">
          <view class="benefits-title">
            <text class="title-icon">✓</text>
            <text class="title-text">会员权益</text>
          </view>
          <text class="benefits-text">{{ pkg.benefits }}</text>
        </view>

        <!-- 购买按钮 -->
        <view class="purchase-button">
          <text class="button-text">立即购买</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && filteredPackages.length === 0" class="empty-state">
      <text class="empty-icon">📦</text>
      <text class="empty-text">暂无套餐</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMembershipPackageListOnline, getMembershipPackageListByType } from '@/apis/membershipPackage.js'

// 数据
const packageList = ref([])
const loading = ref(false)
const activeTab = ref('all')

// 计算属性：过滤后的套餐列表
const filteredPackages = computed(() => {
  if (activeTab.value === 'all') {
    return packageList.value
  }
  return packageList.value.filter(pkg => pkg.memberType === activeTab.value)
})

// 加载套餐列表
const loadPackageList = () => {
  loading.value = true
  
  getMembershipPackageListOnline({
    onSuccess: (res) => {
      packageList.value = res || []
      loading.value = false
    },
    onError: () => {
      loading.value = false
      uni.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  })
}

// 切换标签
const switchTab = (tab) => {
  activeTab.value = tab
}

// 格式化有效期
const formatDuration = (days) => {
  if (days >= 365) {
    return `${Math.floor(days / 365)}年`
  } else if (days >= 30) {
    return `${Math.floor(days / 30)}个月`
  } else {
    return `${days}天`
  }
}

// 点击套餐卡片
const handlePackageClick = (pkg) => {
  uni.navigateTo({
    url: `/pages/membership/package-detail?id=${pkg.id}`
  })
}

// 页面加载
onLoad(() => {
  loadPackageList()
})
</script>

<style lang="scss" scoped>
.package-list-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40rpx 30rpx;
}

.page-header {
  text-align: center;
  margin-bottom: 40rpx;
}

.page-title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 16rpx;
}

.page-subtitle {
  display: block;
  font-size: 28rpx;
  color: #6b7280;
}

.member-type-tabs {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 8rpx;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  border-radius: 12rpx;
}

.tab-item.active {
  background: #FF6B35;
}

.tab-text {
  font-size: 28rpx;
  color: #6b7280;
}

.tab-item.active .tab-text {
  color: #ffffff;
  font-weight: 600;
}

.package-list {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.package-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  position: relative;
  overflow: hidden;
}

.member-type-badge {
  display: inline-block;
  padding: 8rpx 24rpx;
  border-radius: 20rpx;
  margin-bottom: 24rpx;
}

.gold-badge {
  background: #fef3c7;
}

.platinum-badge {
  background: #ede9fe;
}

.badge-text {
  font-size: 24rpx;
  font-weight: 600;
}

.gold-badge .badge-text {
  color: #92400e;
}

.platinum-badge .badge-text {
  color: #5b21b6;
}

.package-name {
  margin-bottom: 20rpx;
}

.name-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #1f2937;
}

.package-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 24rpx;
}

.price-symbol {
  font-size: 32rpx;
  color: #ef4444;
  font-weight: 600;
  margin-right: 4rpx;
}

.price-value {
  font-size: 56rpx;
  color: #ef4444;
  font-weight: bold;
}

.price-unit {
  font-size: 28rpx;
  color: #6b7280;
  margin-left: 8rpx;
}

.package-description {
  margin-bottom: 24rpx;
  padding: 20rpx;
  background: #f9fafb;
  border-radius: 12rpx;
}

.description-text {
  font-size: 28rpx;
  color: #4b5563;
  line-height: 1.6;
}

.package-benefits {
  margin-bottom: 32rpx;
  padding: 24rpx;
  background: #f0fdf4;
  border-radius: 12rpx;
}

.benefits-title {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.title-icon {
  font-size: 28rpx;
  color: #10b981;
  margin-right: 8rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #065f46;
}

.benefits-text {
  font-size: 26rpx;
  color: #047857;
  line-height: 1.6;
}

.purchase-button {
  background: #FF6B35;
  border-radius: 16rpx;
  padding: 24rpx 0;
  text-align: center;
}

.button-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 600;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 80rpx 0;
}

.loading-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
