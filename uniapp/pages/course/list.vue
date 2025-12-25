<template>
  <view class="course-list-page">
    <mod-nav-bar title="课程列表" title-color="#fff"></mod-nav-bar>
    
    <!-- 课程列表 -->
    <view class="course-container">
      <view 
        v-for="course in courseList" 
        :key="course.id" 
        class="course-card"
        @click="goToCourseDetail(course.id)"
      >
        <view class="course-image">
          <image 
            v-if="course.coverImage" 
            :src="getCoverImageUrl(course.coverImage)" 
            mode="aspectFill"
          ></image>
          <view v-else class="no-image">
            <text class="fa fa-image"></text>
          </view>
        </view>
        
        <view class="course-info">
          <view class="course-header">
            <view class="course-name">{{ course.name }}</view>
            <view v-if="course.categoryName" class="course-category">
              <text class="fa fa-tag"></text>
              <text>{{ course.categoryName }}</text>
            </view>
          </view>
          
          <view class="course-desc">{{ course.description || '暂无描述' }}</view>
          
          <view class="course-meta">
            <view class="meta-item">
              <text class="fa fa-clock"></text>
              <text class="meta-text">{{ course.duration }}分钟</text>
            </view>
            <view class="meta-item">
              <text class="fa fa-users"></text>
              <text class="meta-text">{{ course.maxParticipants }}人</text>
            </view>
            <view v-if="course.coachName" class="meta-item">
              <text class="fa fa-user-tie"></text>
              <text class="meta-text">{{ course.coachName }}</text>
            </view>
          </view>
          
          <view class="course-footer">
            <view class="course-price">
              <text class="price-symbol">¥</text>
              <text class="price-value">{{ course.price }}</text>
            </view>
            <view class="course-action">
              <text>查看详情</text>
              <text class="fa fa-chevron-right"></text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="!loading && courseList.length === 0" class="empty-state">
        <text class="fa fa-inbox empty-icon"></text>
        <text class="empty-text">暂无课程</text>
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
import { getCourseListOnline } from '@/apis/course.js'
import { getFileUrl } from '@/utils/fileUtils.js'

// 数据
const courseList = ref([])
const loading = ref(false)

/**
 * 获取课程列表
 */
const fetchCourseList = () => {
  loading.value = true
  getCourseListOnline({
    showDefaultMsg: false,
    onSuccess: (data) => {
      // 确保数据是数组
      courseList.value = Array.isArray(data) ? data : []
      loading.value = false
    },
    onError: (error) => {
      console.error('获取课程列表失败:', error)
      loading.value = false
      uni.showToast({
        title: '获取课程列表失败',
        icon: 'none'
      })
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
 * 跳转到课程详情
 */
const goToCourseDetail = (courseId) => {
  uni.navigateTo({
    url: `/pages/course/detail?id=${courseId}`
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchCourseList()
})
</script>

<style lang="scss" scoped>
.course-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.course-container {
  padding: 20rpx;
}

.course-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.course-image {
  width: 100%;
  height: 360rpx;
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
      font-size: 80rpx;
      color: #ccc;
    }
  }
}

.course-info {
  padding: 24rpx;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
  gap: 16rpx;
}

.course-name {
  flex: 1;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-category {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 6rpx 16rpx;
  background-color: #667eea;
  color: #fff;
  font-size: 22rpx;
  border-radius: 24rpx;
  white-space: nowrap;
  
  .fa {
    font-size: 20rpx;
  }
}

.course-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  gap: 32rpx;
  margin-bottom: 20rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #999;
  
  .fa {
    font-size: 24rpx;
  }
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.course-price {
  display: flex;
  align-items: baseline;
  
  .price-symbol {
    font-size: 24rpx;
    color: #ff6b6b;
    font-weight: bold;
  }
  
  .price-value {
    font-size: 36rpx;
    color: #ff6b6b;
    font-weight: bold;
    margin-left: 4rpx;
  }
}

.course-action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #667eea;
  
  .fa {
    font-size: 24rpx;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  
  .empty-icon {
    font-size: 120rpx;
    color: #ddd;
    margin-bottom: 24rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60rpx 0;
  font-size: 28rpx;
  color: #999;
}
</style>
