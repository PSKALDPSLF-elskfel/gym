<template>
  <view class="page-wrap">
    <mod-nav-bar title="运动详情"></mod-nav-bar>
    
    <view class="page-content">
      <view v-if="record" class="detail-container">
        <!-- 头部卡片 -->
        <view class="header-card">
          <view class="workout-type-section">
            <text class="workout-type">{{ record.workoutTypeName }}</text>
            <view class="category-tag" :class="getCategoryClass(record.workoutCategory)">
              {{ getCategoryText(record.workoutCategory) }}
            </view>
          </view>
          <view class="date-time">
            <text class="fa fa-calendar"></text>
            <text class="date-text">{{ formatDate(record.workoutDate) }}</text>
          </view>
          <view class="time-range">
            <text class="fa fa-clock-o"></text>
            <text class="time-text">{{ formatTime(record.startTime) }} - {{ formatTime(record.endTime) }}</text>
          </view>
        </view>
        
        <!-- 核心数据 -->
        <view class="data-section">
          <view class="section-title">运动数据</view>
          
          <view class="data-grid">
            <view class="data-item primary">
              <text class="fa fa-clock-o icon"></text>
              <text class="value">{{ record.duration }}</text>
              <text class="label">时长(分钟)</text>
            </view>
            
            <view v-if="record.calories" class="data-item primary">
              <text class="fa fa-fire icon"></text>
              <text class="value">{{ record.calories }}</text>
              <text class="label">热量(千卡)</text>
            </view>
            
            <view v-if="record.distance" class="data-item">
              <text class="fa fa-map-marker icon"></text>
              <text class="value">{{ record.distance }}</text>
              <text class="label">距离(公里)</text>
            </view>
            
            <view v-if="record.steps" class="data-item">
              <text class="fa fa-shoe-prints icon"></text>
              <text class="value">{{ record.steps }}</text>
              <text class="label">步数</text>
            </view>
            
            <view v-if="record.heartRateAvg" class="data-item">
              <text class="fa fa-heartbeat icon"></text>
              <text class="value">{{ record.heartRateAvg }}</text>
              <text class="label">平均心率</text>
            </view>
            
            <view v-if="record.heartRateMax" class="data-item">
              <text class="fa fa-heartbeat icon"></text>
              <text class="value">{{ record.heartRateMax }}</text>
              <text class="label">最大心率</text>
            </view>
          </view>
        </view>
        
        <!-- 力量训练详情 -->
        <view v-if="record.details && record.details.length > 0" class="data-section">
          <view class="section-title">训练详情</view>
          
          <view 
            v-for="(detail, index) in record.details" 
            :key="detail.id"
            class="detail-card"
          >
            <view class="detail-header">
              <text class="detail-index">{{ index + 1 }}</text>
              <text class="action-name">{{ detail.actionName }}</text>
            </view>
            
            <view class="detail-data">
              <view class="detail-item">
                <text class="detail-label">组数</text>
                <text class="detail-value">{{ detail.sets }}组</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">次数</text>
                <text class="detail-value">{{ detail.reps }}次</text>
              </view>
              <view v-if="detail.weight" class="detail-item">
                <text class="detail-label">重量</text>
                <text class="detail-value">{{ detail.weight }}kg</text>
              </view>
              <view v-if="detail.restTime" class="detail-item">
                <text class="detail-label">休息</text>
                <text class="detail-value">{{ detail.restTime }}秒</text>
              </view>
            </view>
            
            <view v-if="detail.note" class="detail-note">
              <text class="fa fa-comment-o"></text>
              <text class="note-text">{{ detail.note }}</text>
            </view>
          </view>
        </view>
        
        <!-- 运动感受 -->
        <view class="data-section">
          <view class="section-title">运动感受</view>
          
          <view class="feeling-grid">
            <view v-if="record.intensity" class="feeling-item">
              <text class="feeling-label">强度</text>
              <view class="intensity-tag" :class="getIntensityClass(record.intensity)">
                {{ getIntensityText(record.intensity) }}
              </view>
            </view>
            
            <view v-if="record.feeling" class="feeling-item">
              <text class="feeling-label">感受</text>
              <text class="feeling-value">{{ getFeelingText(record.feeling) }}</text>
            </view>
            
            <view v-if="record.weather" class="feeling-item">
              <text class="feeling-label">天气</text>
              <text class="feeling-value">{{ record.weather }}</text>
            </view>
            
            <view v-if="record.location" class="feeling-item">
              <text class="feeling-label">位置</text>
              <text class="feeling-value">{{ record.location }}</text>
            </view>
          </view>
          
          <view v-if="record.note" class="note-box">
            <text class="note-title">备注</text>
            <text class="note-content">{{ record.note }}</text>
          </view>
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-section">
        <button class="action-btn delete" @click="handleDelete">删除记录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getWorkoutRecordById, deleteWorkoutRecord } from '@/apis/workout.js'

const recordId = ref(null)
const record = ref(null)

/**
 * 获取记录详情
 */
const fetchRecordDetail = () => {
  if (!recordId.value) return
  
  getWorkoutRecordById(recordId.value, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      record.value = data
    }
  })
}

/**
 * 删除记录
 */
const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条运动记录吗？',
    success: (res) => {
      if (res.confirm) {
        deleteWorkoutRecord(recordId.value, {
          successMsg: '删除成功',
          onSuccess: () => {
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          }
        })
      }
    }
  })
}

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr.replace(/-/g, '/'))
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const weekDay = weekDays[date.getDay()]
  return `${year}年${month}月${day}日 周${weekDay}`
}

/**
 * 格式化时间
 */
const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const parts = dateTimeStr.split(' ')
  if (parts.length < 2) return ''
  return parts[1].substring(0, 5)
}

/**
 * 获取分类样式
 */
const getCategoryClass = (category) => {
  const classMap = {
    'CARDIO': 'cardio',
    'STRENGTH': 'strength',
    'FLEXIBILITY': 'flexibility',
    'SPORTS': 'sports'
  }
  return classMap[category] || 'other'
}

/**
 * 获取分类文本
 */
const getCategoryText = (category) => {
  const textMap = {
    'CARDIO': '有氧运动',
    'STRENGTH': '力量训练',
    'FLEXIBILITY': '柔韧性训练',
    'SPORTS': '球类运动',
    'OTHER': '其他'
  }
  return textMap[category] || '其他'
}

/**
 * 获取强度样式
 */
const getIntensityClass = (intensity) => {
  const classMap = {
    'LOW': 'low',
    'MEDIUM': 'medium',
    'HIGH': 'high'
  }
  return classMap[intensity] || ''
}

/**
 * 获取强度文本
 */
const getIntensityText = (intensity) => {
  const textMap = {
    'LOW': '低强度',
    'MEDIUM': '中强度',
    'HIGH': '高强度'
  }
  return textMap[intensity] || ''
}

/**
 * 获取感受文本
 */
const getFeelingText = (feeling) => {
  const textMap = {
    'GREAT': '😊 非常好',
    'GOOD': '🙂 良好',
    'NORMAL': '😐 一般',
    'TIRED': '😓 疲惫',
    'BAD': '😞 不佳'
  }
  return textMap[feeling] || ''
}

// 页面加载
onLoad((options) => {
  if (options.id) {
    recordId.value = parseInt(options.id)
    fetchRecordDetail()
  }
})
</script>

<style lang="scss" scoped>
.page-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 160rpx;
}

.detail-container {
  padding: 30rpx;
}

.header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
  color: #fff;
  
  .workout-type-section {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 24rpx;
    
    .workout-type {
      font-size: 40rpx;
      font-weight: bold;
    }
    
    .category-tag {
      padding: 6rpx 20rpx;
      border-radius: 20rpx;
      font-size: 22rpx;
      background: rgba(255, 255, 255, 0.3);
    }
  }
  
  .date-time,
  .time-range {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 12rpx;
    font-size: 26rpx;
    opacity: 0.9;
    
    .fa {
      font-size: 24rpx;
    }
  }
  
  .time-range {
    margin-bottom: 0;
  }
}

.data-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30rpx;
  
  .data-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    
    &.primary {
      background: linear-gradient(135deg, #fff5f0 0%, #ffe8e0 100%);
    }
    
    .icon {
      font-size: 36rpx;
      color: #ff6b35;
      margin-bottom: 12rpx;
    }
    
    .value {
      font-size: 40rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .label {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.detail-card {
  background: #f8f8f8;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .detail-header {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 20rpx;
    
    .detail-index {
      width: 48rpx;
      height: 48rpx;
      background: #ff6b35;
      color: #fff;
      font-size: 24rpx;
      font-weight: bold;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .action-name {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }
  }
  
  .detail-data {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16rpx;
    margin-bottom: 16rpx;
    
    .detail-item {
      display: flex;
      justify-content: space-between;
      
      .detail-label {
        font-size: 26rpx;
        color: #999;
      }
      
      .detail-value {
        font-size: 26rpx;
        font-weight: 500;
        color: #333;
      }
    }
  }
  
  .detail-note {
    display: flex;
    align-items: flex-start;
    gap: 10rpx;
    padding-top: 16rpx;
    border-top: 1rpx solid #e8e8e8;
    
    .fa {
      font-size: 24rpx;
      color: #999;
      margin-top: 4rpx;
    }
    
    .note-text {
      flex: 1;
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
    }
  }
}

.feeling-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin-bottom: 30rpx;
  
  .feeling-item {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
    
    .feeling-label {
      font-size: 24rpx;
      color: #999;
    }
    
    .feeling-value {
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
    }
    
    .intensity-tag {
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      text-align: center;
      
      &.low { background: #d4edda; color: #155724; }
      &.medium { background: #fff3cd; color: #856404; }
      &.high { background: #f8d7da; color: #721c24; }
    }
  }
}

.note-box {
  padding: 24rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
  
  .note-title {
    display: block;
    font-size: 24rpx;
    color: #999;
    margin-bottom: 12rpx;
  }
  
  .note-content {
    display: block;
    font-size: 28rpx;
    color: #333;
    line-height: 1.6;
  }
}

.action-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx;
  background: #fff;
  border-top: 1rpx solid #e8e8e8;
  
  .action-btn {
    width: 100%;
    height: 88rpx;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 44rpx;
    border: none;
    
    &::after {
      border: none;
    }
    
    &.delete {
      background: #ff4444;
      color: #fff;
    }
  }
}
</style>
