<template>
  <view class="announcement-list-page">
    <mod-nav-bar title="全部公告" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <view class="page-content">
      <!-- 公告列表 -->
      <view v-if="announcements.length > 0" class="announcement-list">
        <view 
          v-for="announcement in announcements" 
          :key="announcement.id"
          class="announcement-card"
          @click="handleAnnouncementClick(announcement)"
        >
          <view class="card-header">
            <view class="title-row">
              <text class="fa fa-bullhorn icon"></text>
              <text class="title">{{ announcement.title }}</text>
            </view>
            <text class="time">{{ formatDateTime(announcement.createTime) }}</text>
          </view>
          <view class="card-content">
            <text class="content">{{ announcement.content }}</text>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-else class="empty-state">
        <text class="fa fa-inbox empty-icon"></text>
        <text class="empty-text">暂无公告</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllAnnouncements } from '@/apis/announcement.js'

// 公告列表
const announcements = ref([])

/**
 * 获取所有公告
 */
const fetchAnnouncements = () => {
  getAllAnnouncements({
    showDefaultMsg: false,
    onSuccess: (res) => {
      announcements.value = res || []
    },
    onError: () => {
      announcements.value = []
    }
  })
}

/**
 * 格式化日期时间
 * 兼容iOS：将 "2025-10-28 13:26:36" 转换为 "2025/10/28 13:26:36"
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  
  // 将日期字符串中的 "-" 替换为 "/"，兼容iOS
  const iosCompatibleDate = dateTime.replace(/-/g, '/')
  const date = new Date(iosCompatibleDate)
  
  if (isNaN(date.getTime())) return ''
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

/**
 * 点击公告
 */
const handleAnnouncementClick = (announcement) => {
  uni.showModal({
    title: announcement.title,
    content: announcement.content,
    showCancel: false,
    confirmText: '知道了'
  })
}

// 页面加载时获取公告
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style lang="scss" scoped>
.announcement-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  padding: 20rpx 30rpx;
}

.announcement-list {
  .announcement-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
    
    &:active {
      opacity: 0.8;
    }
    
    .card-header {
      margin-bottom: 20rpx;
      
      .title-row {
        display: flex;
        align-items: center;
        margin-bottom: 12rpx;
        
        .icon {
          font-size: 28rpx;
          color: #667eea;
          margin-right: 12rpx;
        }
        
        .title {
          flex: 1;
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }
      }
      
      .time {
        font-size: 24rpx;
        color: #999;
        padding-left: 40rpx;
      }
    }
    
    .card-content {
      .content {
        font-size: 28rpx;
        color: #666;
        line-height: 1.6;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
  
  .empty-icon {
    font-size: 120rpx;
    color: #ccc;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>
