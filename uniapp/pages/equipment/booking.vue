<template>
  <view class="booking-page">
    <mod-nav-bar title="器材预约" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 器材信息 -->
    <view class="equipment-card">
      <view class="equipment-info">
        <view class="equipment-name">{{ equipment.name }}</view>
        <view class="equipment-meta">
          <text class="meta-item">
            <text class="fa fa-tag"></text> {{ equipment.category }}
          </text>
          <text class="meta-item">
            <text class="fa fa-map-marker-alt"></text> {{ equipment.location }}
          </text>
        </view>
        <view class="equipment-code">编号：{{ equipment.code }}</view>
      </view>
    </view>

    <!-- 预约时间 -->
    <view class="info-card">
      <view class="card-title">
        <text class="fa fa-clock"></text> 预约时间
      </view>
      <view class="time-selector">
        <view class="time-item">
          <text class="time-label">开始时间</text>
          <picker 
            mode="multiSelector" 
            :value="startTimeIndex" 
            :range="timeRange"
            @change="handleStartTimeChange"
          >
            <view class="time-value">
              <text>{{ startTimeDisplay }}</text>
              <text class="fa fa-chevron-down"></text>
            </view>
          </picker>
        </view>
        <view class="time-item">
          <text class="time-label">结束时间</text>
          <picker 
            mode="multiSelector" 
            :value="endTimeIndex" 
            :range="timeRange"
            @change="handleEndTimeChange"
          >
            <view class="time-value">
              <text>{{ endTimeDisplay }}</text>
              <text class="fa fa-chevron-down"></text>
            </view>
          </picker>
        </view>
        <view class="duration-tip" v-if="duration > 0">
          预计使用时长：{{ duration }}分钟
        </view>
      </view>
    </view>

    <!-- 预约提示 -->
    <view class="info-card">
      <view class="card-title">
        <text class="fa fa-info-circle"></text> 预约须知
      </view>
      <view class="tips-content">
        <view class="tip-item">1. 请准时到达，超时15分钟未开始使用将自动取消预约</view>
        <view class="tip-item">2. 使用完毕后请点击"结束使用"，方便其他会员使用</view>
        <view class="tip-item">3. 使用器材时请注意安全，遵守健身房规定</view>
        <view class="tip-item">4. 如需取消预约，请提前在"我的预约"中操作</view>
      </view>
    </view>

    <!-- 预约按钮 -->
    <view class="booking-footer">
      <button 
        class="booking-btn" 
        :class="{ disabled: !canBook }" 
        :disabled="!canBook"
        @click="handleBooking"
      >
        {{ bookingBtnText }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getEquipmentById } from '@/apis/equipment.js'
import { createEquipmentBooking } from '@/apis/equipment.js'

// 器材信息
const equipment = ref({
  id: null,
  name: '',
  code: '',
  category: '',
  location: ''
})

// 时间选择器数据
const dates = ref([]) // 日期数组
const hours = ref([]) // 小时数组
const minutes = ref([]) // 分钟数组

const startTimeIndex = ref([0, 0, 0])
const endTimeIndex = ref([0, 1, 0])

// 初始化时间数据
const initTimeData = () => {
  // 生成未来7天的日期
  dates.value = []
  const today = new Date()
  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const weekDay = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]
    dates.value.push(`${month}-${day} ${weekDay}`)
  }
  
  // 生成小时（6:00 - 22:00）
  hours.value = []
  for (let i = 6; i <= 22; i++) {
    hours.value.push(String(i).padStart(2, '0') + ':00')
  }
  
  // 生成分钟
  minutes.value = ['00', '30']
}

const timeRange = computed(() => [dates.value, hours.value, minutes.value])

// 开始时间显示
const startTimeDisplay = computed(() => {
  const [dateIdx, hourIdx, minuteIdx] = startTimeIndex.value
  return `${dates.value[dateIdx]} ${hours.value[hourIdx]}:${minutes.value[minuteIdx]}`
})

// 结束时间显示
const endTimeDisplay = computed(() => {
  const [dateIdx, hourIdx, minuteIdx] = endTimeIndex.value
  return `${dates.value[dateIdx]} ${hours.value[hourIdx]}:${minutes.value[minuteIdx]}`
})

// 计算使用时长（分钟）
const duration = computed(() => {
  const start = getDateTimeFromIndex(startTimeIndex.value)
  const end = getDateTimeFromIndex(endTimeIndex.value)
  if (end <= start) return 0
  return Math.round((end - start) / 1000 / 60)
})

// 是否可以预约
const canBook = computed(() => {
  return duration.value > 0
})

// 预约按钮文字
const bookingBtnText = computed(() => {
  if (duration.value <= 0) return '请选择正确的时间'
  return '确认预约'
})

// 从索引获取日期时间对象
const getDateTimeFromIndex = (index) => {
  const [dateIdx, hourIdx, minuteIdx] = index
  const today = new Date()
  const targetDate = new Date(today)
  targetDate.setDate(today.getDate() + dateIdx)
  
  const hour = parseInt(hours.value[hourIdx])
  const minute = parseInt(minutes.value[minuteIdx])
  
  targetDate.setHours(hour, minute, 0, 0)
  return targetDate
}

// 格式化为API需要的时间格式
const formatDateTime = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 处理开始时间变化
const handleStartTimeChange = (e) => {
  startTimeIndex.value = e.detail.value
}

// 处理结束时间变化
const handleEndTimeChange = (e) => {
  endTimeIndex.value = e.detail.value
}

// 获取器材详情
const fetchEquipmentDetail = (equipmentId) => {
  getEquipmentById(equipmentId, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      equipment.value = data
    },
    onError: (error) => {
      console.error('获取器材详情失败:', error)
      uni.showToast({
        title: '获取器材详情失败',
        icon: 'none'
      })
    }
  })
}

// 处理预约
const handleBooking = () => {
  if (!canBook.value) return
  
  const startTime = getDateTimeFromIndex(startTimeIndex.value)
  const endTime = getDateTimeFromIndex(endTimeIndex.value)
  
  uni.showModal({
    title: '确认预约',
    content: `确认预约"${equipment.value.name}"吗？\n时长：${duration.value}分钟`,
    success: (res) => {
      if (res.confirm) {
        createEquipmentBooking({
          equipmentId: equipment.value.id,
          startTime: formatDateTime(startTime),
          endTime: formatDateTime(endTime)
        }, {
          successMsg: '预约成功',
          onSuccess: () => {
            // 跳转到我的预约页面
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/equipment/my-booking'
              })
            }, 1500)
          }
        })
      }
    }
  })
}

// 页面加载
onLoad((options) => {
  if (options.id) {
    equipment.value.id = options.id
    fetchEquipmentDetail(options.id)
  }
  
  initTimeData()
})
</script>

<style lang="scss" scoped>
.booking-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.equipment-card {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.equipment-info {
  .equipment-name {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 12rpx;
  }
  
  .equipment-meta {
    display: flex;
    gap: 24rpx;
    margin-bottom: 12rpx;
    
    .meta-item {
      font-size: 26rpx;
      color: #666;
      display: flex;
      align-items: center;
      gap: 8rpx;
      
      .fa {
        color: #999;
      }
    }
  }
  
  .equipment-code {
    font-size: 24rpx;
    color: #999;
  }
}

.info-card {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.card-title {
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

.time-selector {
  .time-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .time-label {
    font-size: 28rpx;
    color: #666;
  }
  
  .time-value {
    display: flex;
    align-items: center;
    gap: 12rpx;
    font-size: 28rpx;
    color: #333;
    
    .fa {
      color: #999;
      font-size: 24rpx;
    }
  }
  
  .duration-tip {
    margin-top: 16rpx;
    padding: 12rpx 16rpx;
    background-color: #e8f5e9;
    border-radius: 8rpx;
    font-size: 26rpx;
    color: #4caf50;
    text-align: center;
  }
}

.tips-content {
  .tip-item {
    font-size: 26rpx;
    color: #666;
    line-height: 1.8;
    margin-bottom: 12rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.booking-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.booking-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  
  &.disabled {
    background: #f5f5f5;
    color: #999;
  }
}
</style>
