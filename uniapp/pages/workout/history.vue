<template>
  <view class="page-wrap">
    <mod-nav-bar title="运动历史"></mod-nav-bar>
    
    <view class="page-content">
      <!-- 筛选栏 -->
      <view class="filter-bar">
        <picker 
          mode="date" 
          :value="filterData.startDate"
          @change="onStartDateChange"
        >
          <view class="filter-item">
            <text class="fa fa-calendar"></text>
            <text class="filter-text">{{ filterData.startDate || '开始日期' }}</text>
          </view>
        </picker>
        
        <text class="filter-separator">至</text>
        
        <picker 
          mode="date" 
          :value="filterData.endDate"
          @change="onEndDateChange"
        >
          <view class="filter-item">
            <text class="fa fa-calendar"></text>
            <text class="filter-text">{{ filterData.endDate || '结束日期' }}</text>
          </view>
        </picker>
        
        <view class="filter-btn" @click="handleSearch">
          <text class="fa fa-search"></text>
        </view>
      </view>
      
      <!-- 列表 -->
      <view class="record-list">
        <view 
          v-for="record in recordList" 
          :key="record.id"
          class="record-card"
          @click="goToDetail(record.id)"
        >
          <view class="card-header">
            <view class="workout-info">
              <text class="workout-type">{{ record.workoutTypeName }}</text>
              <view class="category-tag" :class="getCategoryClass(record.workoutCategory)">
                {{ getCategoryText(record.workoutCategory) }}
              </view>
            </view>
            <view class="workout-date">{{ formatDate(record.workoutDate) }}</view>
          </view>
          
          <view class="card-body">
            <view class="data-item">
              <text class="fa fa-clock-o icon"></text>
              <text class="data-label">时长</text>
              <text class="data-value">{{ record.duration }}分钟</text>
            </view>
            
            <view v-if="record.calories" class="data-item">
              <text class="fa fa-fire icon"></text>
              <text class="data-label">热量</text>
              <text class="data-value">{{ record.calories }}千卡</text>
            </view>
            
            <view v-if="record.distance" class="data-item">
              <text class="fa fa-map-marker icon"></text>
              <text class="data-label">距离</text>
              <text class="data-value">{{ record.distance }}公里</text>
            </view>
            
            <view v-if="record.heartRateAvg" class="data-item">
              <text class="fa fa-heartbeat icon"></text>
              <text class="data-label">心率</text>
              <text class="data-value">{{ record.heartRateAvg }}bpm</text>
            </view>
          </view>
          
          <view v-if="record.note" class="card-footer">
            <text class="note-text">{{ record.note }}</text>
          </view>
          
          <view class="card-actions">
            <view class="feeling-tag" v-if="record.feeling">
              {{ getFeelingText(record.feeling) }}
            </view>
            <view class="intensity-tag" v-if="record.intensity" :class="getIntensityClass(record.intensity)">
              {{ getIntensityText(record.intensity) }}
            </view>
          </view>
        </view>
        
        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more" @click="loadMore">
          <text class="load-more-text">加载更多</text>
        </view>
        
        <!-- 空状态 -->
        <view v-if="!loading && recordList.length === 0" class="empty-state">
          <text class="fa fa-inbox empty-icon"></text>
          <text class="empty-text">暂无运动记录</text>
          <button class="add-btn" @click="goToAdd">添加记录</button>
        </view>
      </view>
      
      <!-- 悬浮按钮 -->
      <view class="fab" @click="goToAdd">
        <text class="fa fa-plus"></text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyWorkoutRecords } from '@/apis/workout.js'

// 筛选数据
const filterData = ref({
  startDate: '',
  endDate: ''
})

// 分页数据
const pageData = ref({
  current: 1,
  size: 10,
  total: 0
})

// 记录列表
const recordList = ref([])
const loading = ref(false)
const hasMore = ref(true)

/**
 * 获取记录列表
 */
const fetchRecords = (append = false) => {
  if (loading.value) return
  
  loading.value = true
  
  const params = {
    current: pageData.value.current,
    size: pageData.value.size,
    startDate: filterData.value.startDate || undefined,
    endDate: filterData.value.endDate || undefined
  }
  
  getMyWorkoutRecords(params, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      if (append) {
        recordList.value = [...recordList.value, ...data.records]
      } else {
        recordList.value = data.records || []
      }
      
      pageData.value.total = data.total
      hasMore.value = pageData.value.current < data.pages
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

/**
 * 开始日期改变
 */
const onStartDateChange = (e) => {
  filterData.value.startDate = e.detail.value
}

/**
 * 结束日期改变
 */
const onEndDateChange = (e) => {
  filterData.value.endDate = e.detail.value
}

/**
 * 搜索
 */
const handleSearch = () => {
  pageData.value.current = 1
  fetchRecords(false)
}

/**
 * 加载更多
 */
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  pageData.value.current++
  fetchRecords(true)
}

/**
 * 跳转到详情
 */
const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/workout/detail?id=${id}`
  })
}

/**
 * 跳转到添加页面
 */
const goToAdd = () => {
  uni.navigateTo({
    url: '/pages/workout/add-record'
  })
}

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr.replace(/-/g, '/'))
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const weekDay = weekDays[date.getDay()]
  return `${month}-${day} 周${weekDay}`
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
    'CARDIO': '有氧',
    'STRENGTH': '力量',
    'FLEXIBILITY': '柔韧',
    'SPORTS': '运动',
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
onMounted(() => {
  // 设置默认日期范围为最近30天
  const today = new Date()
  const endYear = today.getFullYear()
  const endMonth = String(today.getMonth() + 1).padStart(2, '0')
  const endDay = String(today.getDate()).padStart(2, '0')
  filterData.value.endDate = `${endYear}-${endMonth}-${endDay}`
  
  const startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
  const startYear = startDate.getFullYear()
  const startMonth = String(startDate.getMonth() + 1).padStart(2, '0')
  const startDay = String(startDate.getDate()).padStart(2, '0')
  filterData.value.startDate = `${startYear}-${startMonth}-${startDay}`
  
  fetchRecords()
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
  position: relative;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #e8e8e8;
  
  .filter-item {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 10rpx;
    padding: 20rpx;
    background: #f8f8f8;
    border-radius: 8rpx;
    
    .fa {
      font-size: 24rpx;
      color: #666;
    }
    
    .filter-text {
      font-size: 26rpx;
      color: #333;
    }
  }
  
  .filter-separator {
    font-size: 26rpx;
    color: #999;
  }
  
  .filter-btn {
    width: 80rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #ff6b35;
    border-radius: 8rpx;
    
    .fa {
      font-size: 28rpx;
      color: #fff;
    }
  }
}

.record-list {
  padding: 30rpx;
  padding-bottom: 140rpx;
}

.record-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24rpx;
    
    .workout-info {
      display: flex;
      align-items: center;
      gap: 16rpx;
      
      .workout-type {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
      
      .category-tag {
        padding: 4rpx 16rpx;
        border-radius: 20rpx;
        font-size: 22rpx;
        color: #fff;
        
        &.cardio { background: #4facfe; }
        &.strength { background: #fa709a; }
        &.flexibility { background: #a8edea; color: #333; }
        &.sports { background: #feca57; color: #333; }
        &.other { background: #dfe6e9; color: #333; }
      }
    }
    
    .workout-date {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .card-body {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;
    margin-bottom: 24rpx;
    
    .data-item {
      display: flex;
      align-items: center;
      gap: 8rpx;
      
      .icon {
        font-size: 24rpx;
        color: #ff6b35;
      }
      
      .data-label {
        font-size: 24rpx;
        color: #999;
      }
      
      .data-value {
        font-size: 26rpx;
        color: #333;
        font-weight: 500;
      }
    }
  }
  
  .card-footer {
    padding: 20rpx;
    background: #f8f8f8;
    border-radius: 8rpx;
    margin-bottom: 20rpx;
    
    .note-text {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
    }
  }
  
  .card-actions {
    display: flex;
    gap: 16rpx;
    justify-content: flex-end;
    
    .feeling-tag,
    .intensity-tag {
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      font-size: 22rpx;
      background: #f0f0f0;
      color: #666;
    }
    
    .intensity-tag {
      &.low { background: #d4edda; color: #155724; }
      &.medium { background: #fff3cd; color: #856404; }
      &.high { background: #f8d7da; color: #721c24; }
    }
  }
}

.load-more {
  padding: 30rpx;
  text-align: center;
  
  .load-more-text {
    font-size: 28rpx;
    color: #999;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 60rpx;
  
  .empty-icon {
    font-size: 120rpx;
    color: #ddd;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 40rpx;
  }
  
  .add-btn {
    padding: 20rpx 60rpx;
    background: #ff6b35;
    color: #fff;
    font-size: 28rpx;
    border-radius: 40rpx;
    border: none;
    
    &::after {
      border: none;
    }
  }
}

.fab {
  position: fixed;
  right: 60rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  background: #ff6b35;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(255, 107, 53, 0.4);
  
  .fa {
    font-size: 48rpx;
    color: #fff;
  }
}
</style>
