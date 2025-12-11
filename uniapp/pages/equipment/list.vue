<template>
  <view class="equipment-list-page">
    <mod-nav-bar title="器材列表" title-color="#fff"></mod-nav-bar>
    
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <scroll-view scroll-x class="filter-scroll">
        <view class="filter-item" 
          :class="{ active: selectedCategory === '' }"
          @click="filterByCategory('')"
        >
          全部
        </view>
        <view class="filter-item" 
          :class="{ active: selectedCategory === '有氧' }"
          @click="filterByCategory('有氧')"
        >
          有氧器材
        </view>
        <view class="filter-item" 
          :class="{ active: selectedCategory === '力量' }"
          @click="filterByCategory('力量')"
        >
          力量器材
        </view>
        <view class="filter-item" 
          :class="{ active: selectedCategory === '自由重量' }"
          @click="filterByCategory('自由重量')"
        >
          自由重量
        </view>
      </scroll-view>
    </view>
    
    <!-- 器材列表 -->
    <view class="equipment-container">
      <view 
        v-for="equipment in equipmentList" 
        :key="equipment.id" 
        class="equipment-card"
        @click="goToEquipmentDetail(equipment.id)"
      >
        <view class="equipment-header">
          <view class="equipment-title">
            <text class="equipment-name">{{ equipment.name }}</text>
            <view class="equipment-status" :class="getStatusClass(equipment.status)">
              {{ equipment.statusName }}
            </view>
          </view>
          <view class="equipment-code">{{ equipment.code }}</view>
        </view>
        
        <view class="equipment-info">
          <view class="info-row">
            <text class="fa fa-tag info-icon"></text>
            <text class="info-text">{{ equipment.category || '未分类' }}</text>
          </view>
          <view class="info-row" v-if="equipment.brand">
            <text class="fa fa-bookmark info-icon"></text>
            <text class="info-text">{{ equipment.brand }} {{ equipment.model || '' }}</text>
          </view>
          <view class="info-row" v-if="equipment.location">
            <text class="fa fa-map-marker-alt info-icon"></text>
            <text class="info-text">{{ equipment.location }}</text>
          </view>
          <view class="info-row" v-if="equipment.currentUserName">
            <text class="fa fa-user info-icon"></text>
            <text class="info-text">使用中：{{ equipment.currentUserName }}</text>
          </view>
        </view>
        
        <view class="equipment-footer">
          <view class="availability">
            <text class="fa fa-circle" :class="equipment.available ? 'available' : 'unavailable'"></text>
            <text class="availability-text">{{ equipment.available ? '可预约' : '不可用' }}</text>
          </view>
          <view class="action-btn">
            <text>{{ equipment.available ? '立即预约' : '查看详情' }}</text>
            <text class="fa fa-chevron-right"></text>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!loading && equipmentList.length === 0" class="empty-state">
        <text class="fa fa-inbox empty-icon"></text>
        <text class="empty-text">暂无器材</text>
      </view>
      
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <text>加载中...</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getEquipmentPage } from '@/apis/equipment.js'

// 数据
const equipmentList = ref([])
const loading = ref(false)
const selectedCategory = ref('')

/**
 * 获取器材列表
 */
const fetchEquipmentList = () => {
  loading.value = true
  getEquipmentPage({
    current: 1,
    size: 100,
    category: selectedCategory.value || undefined,
    status: 1 // 只显示正常状态的器材
  }, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      equipmentList.value = data.records || []
      loading.value = false
    },
    onError: (error) => {
      console.error('获取器材列表失败:', error)
      loading.value = false
      uni.showToast({
        title: '获取器材列表失败',
        icon: 'none'
      })
    }
  })
}

/**
 * 按分类筛选
 */
const filterByCategory = (category) => {
  selectedCategory.value = category
  fetchEquipmentList()
}

/**
 * 获取状态样式类
 */
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-fault',     // 故障
    1: 'status-normal',    // 正常
    2: 'status-maintenance', // 维护中
    3: 'status-scrapped'   // 报废
  }
  return classMap[status] || ''
}

/**
 * 跳转到器材详情
 */
const goToEquipmentDetail = (equipmentId) => {
  uni.navigateTo({
    url: `/pages/equipment/detail?id=${equipmentId}`
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchEquipmentList()
})
</script>

<style lang="scss" scoped>
.equipment-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.filter-bar {
  background-color: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;
}

.filter-scroll {
  white-space: nowrap;
  padding: 0 20rpx;
}

.filter-item {
  display: inline-block;
  padding: 12rpx 24rpx;
  margin-right: 16rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 28rpx;
  color: #666;
  transition: all 0.3s;
  
  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
}

.equipment-container {
  padding: 0 20rpx;
}

.equipment-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.equipment-header {
  margin-bottom: 16rpx;
}

.equipment-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.equipment-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.equipment-status {
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  
  &.status-normal {
    background-color: #e8f5e9;
    color: #4caf50;
  }
  
  &.status-fault {
    background-color: #ffebee;
    color: #f44336;
  }
  
  &.status-maintenance {
    background-color: #fff3e0;
    color: #ff9800;
  }
  
  &.status-scrapped {
    background-color: #f5f5f5;
    color: #999;
  }
}

.equipment-code {
  font-size: 24rpx;
  color: #999;
}

.equipment-info {
  margin-bottom: 16rpx;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.info-icon {
  width: 32rpx;
  font-size: 24rpx;
  color: #999;
  margin-right: 8rpx;
}

.info-text {
  font-size: 26rpx;
  color: #666;
}

.equipment-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.availability {
  display: flex;
  align-items: center;
  gap: 8rpx;
  
  .fa-circle {
    font-size: 20rpx;
    
    &.available {
      color: #4caf50;
    }
    
    &.unavailable {
      color: #999;
    }
  }
}

.availability-text {
  font-size: 26rpx;
  color: #666;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #fff;
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
  color: #ccc;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.loading-state {
  text-align: center;
  padding: 40rpx 0;
  font-size: 28rpx;
  color: #999;
}
</style>
