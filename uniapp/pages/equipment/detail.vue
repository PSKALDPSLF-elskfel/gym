<template>
  <view class="equipment-detail-page">
    <mod-nav-bar title="器材详情" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <view v-if="!loading && equipmentDetail" class="detail-container">
      <!-- 器材基本信息 -->
      <view class="equipment-basic">
        <view class="equipment-header">
          <view class="equipment-title">{{ equipmentDetail.name }}</view>
          <view class="equipment-status" :class="getStatusClass(equipmentDetail.status)">
            {{ equipmentDetail.statusName }}
          </view>
        </view>
        
        <view class="equipment-code">编号：{{ equipmentDetail.code }}</view>
        
        <view class="equipment-meta-list">
          <view class="meta-row">
            <view class="meta-label">
              <text class="fa fa-tag"></text>
              <text>器材类型</text>
            </view>
            <view class="meta-value">{{ equipmentDetail.category || '未分类' }}</view>
          </view>
          
          <view class="meta-row" v-if="equipmentDetail.brand">
            <view class="meta-label">
              <text class="fa fa-bookmark"></text>
              <text>品牌型号</text>
            </view>
            <view class="meta-value">{{ equipmentDetail.brand }} {{ equipmentDetail.model || '' }}</view>
          </view>
          
          <view class="meta-row" v-if="equipmentDetail.location">
            <view class="meta-label">
              <text class="fa fa-map-marker-alt"></text>
              <text>器材位置</text>
            </view>
            <view class="meta-value">{{ equipmentDetail.location }}</view>
          </view>
          
          <view class="meta-row" v-if="equipmentDetail.purchaseDate">
            <view class="meta-label">
              <text class="fa fa-calendar"></text>
              <text>购买日期</text>
            </view>
            <view class="meta-value">{{ equipmentDetail.purchaseDate }}</view>
          </view>
          
          <view class="meta-row" v-if="equipmentDetail.currentUserName">
            <view class="meta-label">
              <text class="fa fa-user"></text>
              <text>当前使用</text>
            </view>
            <view class="meta-value">{{ equipmentDetail.currentUserName }}</view>
          </view>
        </view>
      </view>
      
      <!-- 器材说明 -->
      <view class="equipment-description" v-if="equipmentDetail.remark">
        <view class="section-title">
          <text class="fa fa-align-left"></text>
          <text>器材说明</text>
        </view>
        <view class="description-content">
          {{ equipmentDetail.remark }}
        </view>
      </view>
      
      <!-- 排队信息 -->
      <view class="queue-section" v-if="!equipmentDetail.available">
        <view class="section-title">
          <text class="fa fa-users"></text>
          <text>排队情况</text>
        </view>
        
        <view v-if="queueList.length > 0" class="queue-list">
          <view 
            v-for="(queue, index) in queueList" 
            :key="queue.id" 
            class="queue-item"
            :class="{ 'my-queue': queue.userId === currentUserId }"
          >
            <view class="queue-number">{{ queue.queueNumber }}</view>
            <view class="queue-info">
              <view class="queue-user">{{ queue.userName }}</view>
              <view class="queue-time">{{ formatDateTime(queue.joinTime) }}</view>
            </view>
            <view class="queue-status">
              <view class="status-tag" :class="getQueueStatusClass(queue.status)">
                {{ queue.statusName }}
              </view>
            </view>
          </view>
        </view>
        
        <view v-else class="no-queue">
          <text class="fa fa-inbox"></text>
          <text>暂无排队</text>
        </view>
      </view>
      
      <!-- 底部操作栏 -->
      <view class="bottom-bar">
        <button 
          v-if="equipmentDetail.available && !hasActiveBooking" 
          class="action-btn primary"
          @click="goToBooking"
        >
          立即预约
        </button>
        <button 
          v-else-if="!equipmentDetail.available && !myQueueId" 
          class="action-btn secondary"
          @click="handleJoinQueue"
        >
          加入排队
        </button>
        <button 
          v-else-if="myQueueId" 
          class="action-btn danger"
          @click="handleLeaveQueue"
        >
          退出排队
        </button>
        <button 
          v-else 
          class="action-btn disabled"
          disabled
        >
          不可预约
        </button>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <text>加载中...</text>
    </view>
    
    <!-- 错误状态 -->
    <view v-if="!loading && !equipmentDetail" class="error-container">
      <text class="fa fa-exclamation-circle error-icon"></text>
      <text class="error-text">器材不存在</text>
      <button class="back-btn" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getEquipmentById } from '@/apis/equipment.js'
import { getEquipmentQueueList, joinEquipmentQueue, leaveEquipmentQueue } from '@/apis/equipment.js'
import { getUserEquipmentBookings } from '@/apis/equipment.js'
import DateUtils from '@/utils/dateUtils.js'
import { useUserStore } from '@/store/user.js'

// 数据
const equipmentDetail = ref(null)
const queueList = ref([])
const loading = ref(false)
const equipmentId = ref('')
const currentUserId = ref(null)
const myQueueId = ref(null)
const hasActiveBooking = ref(false)

/**
 * 获取器材详情
 */
const fetchEquipmentDetail = () => {
  if (!equipmentId.value) {
    uni.showToast({
      title: '器材ID不能为空',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  getEquipmentById(equipmentId.value, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      equipmentDetail.value = data
      loading.value = false
      
      // 如果器材不可用，获取排队列表
      if (!data.available) {
        fetchQueueList()
      }
      
      // 检查用户是否有进行中的预约
      checkUserBooking()
    },
    onError: (error) => {
      console.error('获取器材详情失败:', error)
      loading.value = false
      uni.showToast({
        title: '获取器材详情失败',
        icon: 'none'
      })
    }
  })
}

/**
 * 获取排队列表
 */
const fetchQueueList = () => {
  getEquipmentQueueList(equipmentId.value, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      queueList.value = data || []
      // 检查当前用户是否在排队中
      const myQueue = queueList.value.find(q => q.userId === currentUserId.value && q.status === 1)
      myQueueId.value = myQueue ? myQueue.id : null
    },
    onError: (error) => {
      console.error('获取排队列表失败:', error)
      queueList.value = []
    }
  })
}

/**
 * 检查用户是否有进行中的预约
 */
const checkUserBooking = () => {
  getUserEquipmentBookings({ status: 1 }, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      // 检查是否有该器材的预约中记录
      hasActiveBooking.value = (Array.isArray(data) && data.length > 0) ? data.some(booking => 
        booking.equipmentId === equipmentId.value && booking.status === 1
      ) : false
    },
    onError: () => {
      hasActiveBooking.value = false
    }
  })
}

/**
 * 获取状态样式类
 */
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-fault',
    1: 'status-normal',
    2: 'status-maintenance',
    3: 'status-scrapped'
  }
  return classMap[status] || ''
}

/**
 * 获取排队状态样式类
 */
const getQueueStatusClass = (status) => {
  const classMap = {
    0: 'queue-cancelled',
    1: 'queue-waiting',
    2: 'queue-called'
  }
  return classMap[status] || ''
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return DateUtils.formatDateTime(dateTime)
}

/**
 * 跳转到预约页面
 */
const goToBooking = () => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/auth/login'
      })
    }, 1500)
    return
  }
  
  uni.navigateTo({
    url: `/pages/equipment/booking?id=${equipmentId.value}`
  })
}

/**
 * 加入排队
 */
const handleJoinQueue = () => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/auth/login'
      })
    }, 1500)
    return
  }
  
  uni.showModal({
    title: '提示',
    content: '确定要加入排队吗？',
    success: (res) => {
      if (res.confirm) {
        joinEquipmentQueue({ equipmentId: equipmentId.value }, {
          successMsg: '加入排队成功',
          onSuccess: () => {
            fetchQueueList()
          }
        })
      }
    }
  })
}

/**
 * 退出排队
 */
const handleLeaveQueue = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出排队吗？',
    success: (res) => {
      if (res.confirm) {
        leaveEquipmentQueue(myQueueId.value, {
          successMsg: '退出排队成功',
          onSuccess: () => {
            myQueueId.value = null
            fetchQueueList()
          }
        })
      }
    }
  })
}

/**
 * 返回
 */
const goBack = () => {
  uni.navigateBack()
}

// 页面加载
onLoad((options) => {
  equipmentId.value = options.id
  
  // 获取当前用户ID
  const userStore = useUserStore()
  if (userStore.isLoggedIn) {
    currentUserId.value = userStore.userId
  }
  
  fetchEquipmentDetail()
})
</script>

<style lang="scss" scoped>
.equipment-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.detail-container {
  padding: 20rpx;
}

.equipment-basic {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.equipment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.equipment-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.equipment-status {
  padding: 6rpx 16rpx;
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
  font-size: 26rpx;
  color: #999;
  margin-bottom: 24rpx;
}

.equipment-meta-list {
  .meta-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .meta-label {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 28rpx;
    color: #666;
    
    .fa {
      width: 32rpx;
      color: #999;
    }
  }
  
  .meta-value {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
  }
}

.equipment-description,
.queue-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  
  .fa {
    color: #667eea;
  }
}

.description-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.queue-list {
  .queue-item {
    display: flex;
    align-items: center;
    padding: 20rpx;
    margin-bottom: 16rpx;
    background-color: #f8f8f8;
    border-radius: 12rpx;
    
    &.my-queue {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      border: 2rpx solid #667eea;
    }
  }
  
  .queue-number {
    width: 60rpx;
    height: 60rpx;
    line-height: 60rpx;
    text-align: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 50%;
    font-size: 28rpx;
    font-weight: bold;
    margin-right: 16rpx;
  }
  
  .queue-info {
    flex: 1;
  }
  
  .queue-user {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 4rpx;
  }
  
  .queue-time {
    font-size: 24rpx;
    color: #999;
  }
  
  .queue-status {
    .status-tag {
      padding: 6rpx 12rpx;
      border-radius: 6rpx;
      font-size: 24rpx;
      
      &.queue-waiting {
        background-color: #e3f2fd;
        color: #2196f3;
      }
      
      &.queue-called {
        background-color: #e8f5e9;
        color: #4caf50;
      }
      
      &.queue-cancelled {
        background-color: #f5f5f5;
        color: #999;
      }
    }
  }
}

.no-queue {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  color: #999;
  
  .fa {
    font-size: 80rpx;
    margin-bottom: 16rpx;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.action-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  
  &.primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
  
  &.secondary {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: #fff;
  }
  
  &.danger {
    background-color: #f44336;
    color: #fff;
  }
  
  &.disabled {
    background-color: #f5f5f5;
    color: #999;
  }
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.error-icon {
  font-size: 120rpx;
  color: #ccc;
  margin-bottom: 20rpx;
}

.error-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.back-btn {
  padding: 16rpx 48rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}
</style>
