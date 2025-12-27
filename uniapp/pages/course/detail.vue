<template>
  <view class="course-detail-page">
    <mod-nav-bar title="课程详情" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <view v-if="!loading && courseDetail" class="detail-container">
      <!-- 课程封面 -->
      <view class="course-cover">
        <image 
          v-if="courseDetail.coverImage" 
          :src="getCoverImageUrl(courseDetail.coverImage)" 
          mode="aspectFill"
        ></image>
        <view v-else class="no-image">
          <text class="fa fa-image"></text>
        </view>
      </view>
      
      <!-- 课程基本信息 -->
      <view class="course-basic">
        <view class="course-title">{{ courseDetail.name }}</view>
        
        <!-- 分类和教练信息 -->
        <view class="course-tags">
          <view v-if="courseDetail.categoryName" class="tag-item category-tag">
            <text class="fa fa-tag"></text>
            <text>{{ courseDetail.categoryName }}</text>
          </view>
          <view v-if="courseDetail.coachName" class="tag-item coach-tag">
            <text class="fa fa-user-tie"></text>
            <text>教练：{{ courseDetail.coachName }}</text>
          </view>
        </view>
        
        <view class="course-price-box">
          <view class="price-label">课程价格</view>
          <view class="price-value">
            <text class="price-symbol">¥</text>
            <text class="price-number">{{ courseDetail.price }}</text>
          </view>
        </view>
        
        <view class="course-meta-list">
          <view class="meta-row">
            <view class="meta-label">
              <text class="fa fa-clock"></text>
              <text>课程时长</text>
            </view>
            <view class="meta-value">{{ courseDetail.duration }}分钟</view>
          </view>
          
          <view class="meta-row">
            <view class="meta-label">
              <text class="fa fa-users"></text>
              <text>最大人数</text>
            </view>
            <view class="meta-value">{{ courseDetail.maxParticipants }}人</view>
          </view>
        </view>
      </view>
      
      <!-- 课程描述 -->
      <view class="course-description">
        <view class="section-title">
          <text class="fa fa-align-left"></text>
          <text>课程介绍</text>
        </view>
        <view class="description-content">
          {{ courseDetail.description || '暂无课程介绍' }}
        </view>
      </view>
      
      <!-- 课程时间安排 -->
      <view class="course-schedule-section">
        <view class="section-title">
          <text class="fa fa-calendar-alt"></text>
          <text>课程时间安排</text>
        </view>
        
        <view v-if="scheduleList.length > 0" class="schedule-list">
          <view 
            v-for="schedule in scheduleList" 
            :key="schedule.id" 
            class="schedule-item"
            :class="{ 'disabled': !schedule.canBook || schedule.isExpired }"
            @click="handleSelectSchedule(schedule)"
          >
            <view class="schedule-time">
              <view class="time-row">
                <text class="fa fa-clock time-icon"></text>
                <text class="time-text">{{ formatDateTime(schedule.startTime) }}</text>
              </view>
              <view class="time-divider">至</view>
              <view class="time-row">
                <text class="fa fa-clock time-icon"></text>
                <text class="time-text">{{ formatDateTime(schedule.endTime) }}</text>
              </view>
            </view>
            
            <view class="schedule-info">
              <view class="info-item">
                <text class="fa fa-users"></text>
                <text>{{ schedule.currentParticipants }}/{{ schedule.maxParticipants }}人</text>
              </view>
              <view class="info-item">
                <text class="fa fa-ticket-alt"></text>
                <text>剩余{{ schedule.remainingSlots }}名额</text>
              </view>
            </view>
            
            <view class="schedule-status">
              <view 
                class="status-tag" 
                :class="{
                  'status-normal': schedule.status === 1 && schedule.canBook,
                  'status-full': schedule.status === 2,
                  'status-cancelled': schedule.status === 0,
                  'status-expired': schedule.isExpired
                }"
              >
                {{ getScheduleStatusText(schedule) }}
              </view>
            </view>
          </view>
        </view>
        
        <view v-else class="no-schedule">
          <text class="fa fa-calendar-times"></text>
          <text>暂无可预约的时间安排</text>
        </view>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <text>加载中...</text>
    </view>
    
    <!-- 错误状态 -->
    <view v-if="!loading && !courseDetail" class="error-container">
      <text class="fa fa-exclamation-circle error-icon"></text>
      <text class="error-text">课程不存在或已下架</text>
      <button class="back-btn" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getCourseById, getCourseScheduleList } from '@/apis/course.js'
import { getFileUrl } from '@/utils/fileUtils.js'
import DateUtils from '@/utils/dateUtils.js'
import { ensureArray } from '@/utils/dataUtils.js'

// 数据
const courseDetail = ref(null)
const scheduleList = ref([])
const loading = ref(false)
const courseId = ref('')

/**
 * 获取课程详情
 */
const fetchCourseDetail = () => {
  if (!courseId.value) {
    uni.showToast({
      title: '课程ID不能为空',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  getCourseById(courseId.value, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      courseDetail.value = data
      loading.value = false
      // 获取课程时间安排
      fetchScheduleList()
    },
    onError: (error) => {
      console.error('获取课程详情失败:', error)
      loading.value = false
      uni.showToast({
        title: '获取课程详情失败',
        icon: 'none'
      })
    }
  })
}

/**
 * 获取课程时间安排列表
 */
const fetchScheduleList = () => {
  getCourseScheduleList(courseId.value, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      // 添加空值检查，确保 data 是数组
      if (!data || !Array.isArray(data)) {
        console.warn('课程时间安排数据格式错误:', data)
        scheduleList.value = []
        return
      }
      // 过滤掉已取消和已过期的排课
      scheduleList.value = data.filter(schedule => 
        schedule.status !== 0 && !schedule.isExpired
      )
    },
    onError: (error) => {
      console.error('获取课程时间安排失败:', error)
      scheduleList.value = []
    }
  })
}

/**
 * 获取封面图片URL
 * 使用工具函数转换文件路径为完整URL
 */
const getCoverImageUrl = (coverImage) => {
  if (!coverImage) return ''
  // 使用工具函数转换路径，解决小程序端无法访问后端文件的问题
  return getFileUrl(coverImage)
}

/**
 * 格式化日期时间
 * 使用统一的DateUtils工具，已内置iOS兼容性处理
 */
const formatDateTime = (dateTime) => {
  return DateUtils.format(dateTime, 'MM-DD HH:mm')
}

/**
 * 获取排课状态文本
 */
const getScheduleStatusText = (schedule) => {
  if (schedule.isExpired) {
    return '已过期'
  }
  if (schedule.status === 0) {
    return '已取消'
  }
  if (schedule.status === 2 || !schedule.canBook) {
    return '已满'
  }
  return '可预约'
}

/**
 * 选择排课进行预约
 */
const handleSelectSchedule = (schedule) => {
  if (!schedule.canBook || schedule.isExpired) {
    uni.showToast({
      title: '该时间段不可预约',
      icon: 'none'
    })
    return
  }
  
  // 构建预约页面需要的数据
  const bookingData = {
    id: schedule.id,
    courseId: courseDetail.value.id,
    courseName: courseDetail.value.name,
    courseCoverImage: courseDetail.value.coverImage,
    courseDuration: courseDetail.value.duration,
    price: courseDetail.value.price,
    startTime: schedule.startTime,
    endTime: schedule.endTime,
    maxParticipants: schedule.maxParticipants,
    currentParticipants: schedule.currentParticipants,
    status: schedule.status
  }
  
  // 跳转到预约页面
  uni.navigateTo({
    url: `/pages/course/booking?schedule=${encodeURIComponent(JSON.stringify(bookingData))}`
  })
}

/**
 * 返回上一页
 */
const goBack = () => {
  uni.navigateBack()
}

// 页面加载时获取参数
onLoad((options) => {
  if (options.id) {
    courseId.value = options.id
    fetchCourseDetail()
  }
})
</script>

<style lang="scss" scoped>
.course-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.detail-container {
  background-color: #fff;
}

.course-cover {
  width: 100%;
  height: 500rpx;
  position: relative;
  
  image {
    width: 100%;
    height: 100%;
  }
  
  .no-image {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f0f0f0;
    
    .fa {
      font-size: 120rpx;
      color: #ccc;
    }
  }
}

.course-basic {
  padding: 32rpx;
  background-color: #fff;
}

.course-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  line-height: 1.4;
}

.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 20rpx;
  border-radius: 28rpx;
  font-size: 24rpx;
  
  .fa {
    font-size: 22rpx;
  }
}

.category-tag {
  background-color: #e3f2fd;
  color: #1976d2;
  
  .fa {
    color: #1976d2;
  }
}

.coach-tag {
  background-color: #fff3e0;
  color: #f57c00;
  
  .fa {
    color: #f57c00;
  }
}

.course-price-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx;
  background: #FF6B35;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
}

.price-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
}

.price-value {
  display: flex;
  align-items: baseline;
  
  .price-symbol {
    font-size: 28rpx;
    color: #fff;
    font-weight: bold;
  }
  
  .price-number {
    font-size: 48rpx;
    color: #fff;
    font-weight: bold;
    margin-left: 4rpx;
  }
}

.course-meta-list {
  border-top: 1rpx solid #f0f0f0;
  padding-top: 24rpx;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  
  &:not(:last-child) {
    border-bottom: 1rpx solid #f8f8f8;
  }
}

.meta-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 28rpx;
  color: #666;
  
  .fa {
    font-size: 28rpx;
    color: #FF6B35;
  }
}

.meta-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.course-description {
  margin-top: 20rpx;
  padding: 32rpx;
  background-color: #fff;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  
  .fa {
    font-size: 28rpx;
    color: #FF6B35;
  }
}

.description-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
}

.course-schedule-section {
  margin-top: 20rpx;
  padding: 32rpx;
  background-color: #fff;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.schedule-item {
  padding: 24rpx;
  background-color: #f8f9fa;
  border-radius: 16rpx;
  border: 2rpx solid #e9ecef;
  
  &.disabled {
    opacity: 0.6;
    background-color: #f5f5f5;
  }
  
  &:not(.disabled):active {
    background-color: #fff5f0;
    border-color: #FF6B35;
  }
}

.schedule-time {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.time-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.time-icon {
  font-size: 24rpx;
  color: #FF6B35;
}

.time-text {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.time-divider {
  font-size: 24rpx;
  color: #999;
  padding-left: 36rpx;
}

.schedule-info {
  display: flex;
  gap: 32rpx;
  margin-bottom: 16rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #666;
  
  .fa {
    font-size: 24rpx;
    color: #FF6B35;
  }
}

.schedule-status {
  display: flex;
  justify-content: flex-end;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 500;
  
  &.status-normal {
    background-color: #d4edda;
    color: #155724;
  }
  
  &.status-full {
    background-color: #f8d7da;
    color: #721c24;
  }
  
  &.status-cancelled {
    background-color: #e2e3e5;
    color: #383d41;
  }
  
  &.status-expired {
    background-color: #fff3cd;
    color: #856404;
  }
}

.no-schedule {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  color: #999;
  
  .fa {
    font-size: 80rpx;
    margin-bottom: 20rpx;
  }
  
  text:last-child {
    font-size: 28rpx;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  background-color: #fff;
  z-index: 100;
}

.book-btn {
  width: 100%;
  height: 88rpx;
  background: #FF6B35;
  border-radius: 44rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
  
  .fa {
    font-size: 32rpx;
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  font-size: 28rpx;
  color: #999;
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 80vh;
  padding: 0 60rpx;
  
  .error-icon {
    font-size: 120rpx;
    color: #ff6b6b;
    margin-bottom: 32rpx;
  }
  
  .error-text {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 48rpx;
  }
  
  .back-btn {
    width: 240rpx;
    height: 72rpx;
    background-color: #FF6B35;
    color: #fff;
    border: none;
    border-radius: 36rpx;
    font-size: 28rpx;
  }
}
</style>
