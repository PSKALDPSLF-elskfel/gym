<template>
  <view class="my-booking-page">
    <mod-nav-bar title="我的器材预约" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 状态筛选 -->
    <view class="status-tabs">
      <view 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentStatus === tab.value }"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 预约列表 -->
    <view v-if="!loading && bookingList.length > 0" class="booking-list">
      <view 
        v-for="booking in bookingList" 
        :key="booking.id"
        class="booking-item"
      >
        <!-- 器材信息 -->
        <view class="booking-header">
          <view class="equipment-icon">
            <text class="fa fa-bicycle"></text>
          </view>
          <view class="equipment-info">
            <view class="equipment-name">{{ booking.equipmentName }}</view>
            <view class="equipment-code">编号: {{ booking.equipmentCode }}</view>
          </view>
          <view class="status-tag" :class="getStatusClass(booking.status)">
            {{ booking.statusName }}
          </view>
        </view>

        <!-- 时间信息 -->
        <view class="booking-time">
          <view class="time-row">
            <text class="fa fa-clock time-icon"></text>
            <text class="time-label">预约时间:</text>
            <text class="time-value">{{ formatDateTime(booking.startTime) }}</text>
          </view>
          <view class="time-row">
            <text class="fa fa-clock time-icon"></text>
            <text class="time-label">结束时间:</text>
            <text class="time-value">{{ formatDateTime(booking.endTime) }}</text>
          </view>
          <view class="time-row" v-if="booking.actualStartTime">
            <text class="fa fa-check-circle time-icon success"></text>
            <text class="time-label">实际开始:</text>
            <text class="time-value">{{ formatDateTime(booking.actualStartTime) }}</text>
          </view>
          <view class="time-row" v-if="booking.actualEndTime">
            <text class="fa fa-check-circle time-icon success"></text>
            <text class="time-label">实际结束:</text>
            <text class="time-value">{{ formatDateTime(booking.actualEndTime) }}</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="booking-actions">
          <!-- 预约中 -->
          <button 
            v-if="booking.status === 1" 
            class="action-btn primary-btn" 
            @click="handleStartUsing(booking)"
          >
            开始使用
          </button>
          <button 
            v-if="booking.status === 1" 
            class="action-btn cancel-btn" 
            @click="handleCancel(booking)"
          >
            取消预约
          </button>
          
          <!-- 使用中 -->
          <button 
            v-if="booking.status === 2" 
            class="action-btn danger-btn" 
            @click="handleEndUsing(booking)"
          >
            结束使用
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else-if="!loading && bookingList.length === 0" class="empty-state">
      <text class="fa fa-inbox empty-icon"></text>
      <text class="empty-text">暂无预约记录</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text class="fa fa-spinner fa-spin"></text>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getUserEquipmentBookings, cancelEquipmentBooking, startUsingEquipment, endUsingEquipment } from '@/apis/equipment.js'
import DateUtils from '@/utils/dateUtils.js'

// 状态标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '预约中', value: 1 },
  { label: '使用中', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 0 }
]

// 当前状态
const currentStatus = ref(null)

// 预约列表
const bookingList = ref([])
const loading = ref(false)

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return DateUtils.formatDateTime(dateTime)
}

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-cancelled',  // 已取消
    1: 'status-booked',     // 预约中
    2: 'status-using',      // 使用中
    3: 'status-completed',  // 已完成
    4: 'status-timeout'     // 超时未使用
  }
  return classMap[status] || ''
}

// 查询预约列表
const fetchBookings = () => {
  loading.value = true
  
  const params = {}
  if (currentStatus.value !== null) {
    params.status = currentStatus.value
  }
  
  getUserEquipmentBookings(params, {
    showDefaultMsg: false,
    onSuccess: (res) => {
      bookingList.value = res || []
      loading.value = false
    },
    onError: () => {
      loading.value = false
      uni.showToast({
        title: '获取预约列表失败',
        icon: 'none'
      })
    }
  })
}

// 切换状态标签
const handleTabChange = (status) => {
  currentStatus.value = status
  fetchBookings()
}

// 开始使用
const handleStartUsing = (booking) => {
  uni.showModal({
    title: '确认开始',
    content: `确定开始使用"${booking.equipmentName}"吗？`,
    success: (res) => {
      if (res.confirm) {
        startUsingEquipment(booking.id, {
          successMsg: '开始使用成功',
          onSuccess: () => {
            fetchBookings()
          }
        })
      }
    }
  })
}

// 结束使用
const handleEndUsing = (booking) => {
  uni.showModal({
    title: '确认结束',
    content: `确定结束使用"${booking.equipmentName}"吗？`,
    success: (res) => {
      if (res.confirm) {
        endUsingEquipment(booking.id, {
          successMsg: '结束使用成功',
          onSuccess: () => {
            fetchBookings()
          }
        })
      }
    }
  })
}

// 取消预约
const handleCancel = (booking) => {
  uni.showModal({
    title: '确认取消',
    content: `确定要取消"${booking.equipmentName}"的预约吗？`,
    success: (res) => {
      if (res.confirm) {
        cancelEquipmentBooking(booking.id, {
          successMsg: '预约已取消',
          onSuccess: () => {
            fetchBookings()
          }
        })
      }
    }
  })
}

// 页面显示时刷新
onShow(() => {
  fetchBookings()
})
</script>

<style lang="scss" scoped>
.my-booking-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.status-tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #666;
  border-bottom: 3rpx solid transparent;
  transition: all 0.3s;
  
  &.active {
    color: #667eea;
    border-bottom-color: #667eea;
    font-weight: bold;
  }
}

.booking-list {
  padding: 0 20rpx;
}

.booking-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.booking-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.equipment-icon {
  width: 80rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
  
  .fa {
    font-size: 40rpx;
    color: #fff;
  }
}

.equipment-info {
  flex: 1;
}

.equipment-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 6rpx;
}

.equipment-code {
  font-size: 24rpx;
  color: #999;
}

.status-tag {
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  
  &.status-booked {
    background-color: #e3f2fd;
    color: #2196f3;
  }
  
  &.status-using {
    background-color: #fff3e0;
    color: #ff9800;
  }
  
  &.status-completed {
    background-color: #e8f5e9;
    color: #4caf50;
  }
  
  &.status-cancelled {
    background-color: #f5f5f5;
    color: #999;
  }
  
  &.status-timeout {
    background-color: #ffebee;
    color: #f44336;
  }
}

.booking-time {
  margin-bottom: 20rpx;
}

.time-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.time-icon {
  width: 32rpx;
  font-size: 24rpx;
  color: #999;
  margin-right: 8rpx;
  
  &.success {
    color: #4caf50;
  }
}

.time-label {
  font-size: 26rpx;
  color: #666;
  margin-right: 8rpx;
}

.time-value {
  font-size: 26rpx;
  color: #333;
}

.booking-actions {
  display: flex;
  gap: 16rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  flex: 1;
  height: 68rpx;
  line-height: 68rpx;
  border-radius: 34rpx;
  font-size: 28rpx;
  border: none;
  
  &.primary-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
  
  &.danger-btn {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: #fff;
  }
  
  &.cancel-btn {
    background-color: #fff;
    color: #999;
    border: 1rpx solid #e0e0e0;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.fa-spin {
  font-size: 80rpx;
  color: #667eea;
  margin-bottom: 20rpx;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}
</style>
