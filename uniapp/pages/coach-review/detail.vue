<template>
  <view class="review-detail-page">
    <mod-nav-bar title="评价详情"></mod-nav-bar>
    
    <view v-if="loading" class="loading-container">
      <text class="fa fa-spinner fa-spin"></text>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="reviewDetail" class="content">
      <!-- 评价人信息 -->
      <view class="user-section">
        <image v-if="reviewDetail.userAvatar && !reviewDetail.isAnonymous" class="user-avatar" :src="reviewDetail.userAvatar" mode="aspectFill"></image>
        <view v-else class="user-avatar-placeholder">
          <text class="fa fa-user"></text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ reviewDetail.isAnonymous ? '匿名用户' : reviewDetail.userName }}</text>
          <text class="review-time">{{ formatTime(reviewDetail.createTime) }}</text>
        </view>
      </view>

      <!-- 评分 -->
      <view class="rating-section">
        <view class="rating-stars">
          <text v-for="i in 5" :key="i" class="fa fa-star" :class="{ active: i <= reviewDetail.rating }"></text>
        </view>
        <text class="rating-label">{{ getRatingLabel(reviewDetail.rating) }}</text>
      </view>

      <!-- 评价标签 -->
      <view v-if="reviewDetail.tagList && reviewDetail.tagList.length > 0" class="section">
        <view class="section-title">评价标签</view>
        <view class="tag-list">
          <view v-for="(tag, index) in reviewDetail.tagList" :key="index" class="tag">{{ tag }}</view>
        </view>
      </view>

      <!-- 评价内容 -->
      <view v-if="reviewDetail.content" class="section">
        <view class="section-title">评价内容</view>
        <view class="review-content">{{ reviewDetail.content }}</view>
      </view>

      <!-- 评价图片 -->
      <view v-if="reviewDetail.images && reviewDetail.images.length > 0" class="section">
        <view class="section-title">评价图片</view>
        <view class="image-grid">
          <image 
            v-for="(img, index) in reviewDetail.images" 
            :key="index" 
            class="review-image" 
            :src="img" 
            mode="aspectFill"
            @click="previewImages(reviewDetail.images, index)"
          ></image>
        </view>
      </view>

      <!-- 教练回复 -->
      <view class="section">
        <view class="section-title">教练回复</view>
        <view v-if="reviewDetail.reply" class="coach-reply">
          <view class="reply-header">
            <image v-if="reviewDetail.coachAvatar" class="coach-avatar" :src="reviewDetail.coachAvatar" mode="aspectFill"></image>
            <view v-else class="coach-avatar-placeholder">
              <text class="fa fa-user"></text>
            </view>
            <view class="coach-info">
              <text class="coach-name">{{ reviewDetail.coachName }}</text>
              <text class="reply-time">{{ formatTime(reviewDetail.replyTime) }}</text>
            </view>
          </view>
          <view class="reply-content">{{ reviewDetail.reply }}</view>
        </view>
        <view v-else class="no-reply">
          <text class="fa fa-clock-o"></text>
          <text>教练暂未回复</text>
        </view>
      </view>

      <!-- 互动信息 -->
      <view class="section">
        <view class="action-bar">
          <view class="action-item" @click="handleLike">
            <text class="fa" :class="reviewDetail.isHelpful ? 'fa-thumbs-up' : 'fa-thumbs-o-up'"></text>
            <text class="action-text">有用 {{ reviewDetail.helpfulCount || 0 }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty-container">
      <text class="fa fa-exclamation-circle empty-icon"></text>
      <text class="empty-text">评价不存在</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getReviewDetail, toggleHelpful } from '@/apis/coachReview.js'
import { formatDate } from '@/utils/dateUtils.js'

// 页面参数
const reviewId = ref(null)

// 评价详情
const reviewDetail = ref(null)
const loading = ref(false)

/**
 * 页面加载
 */
onLoad((options) => {
  if (options.id) {
    reviewId.value = parseInt(options.id)
  }
})

onMounted(() => {
  if (reviewId.value) {
    loadReviewDetail()
  }
})

/**
 * 加载评价详情
 */
const loadReviewDetail = async () => {
  loading.value = true
  try {
    const data = await getReviewDetail(reviewId.value, { showDefaultMsg: false })
    reviewDetail.value = data || null
  } catch (error) {
    console.error('加载评价详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 点赞
 */
const handleLike = async () => {
  if (!reviewDetail.value) return
  
  try {
    await toggleHelpful(reviewDetail.value.id, { showDefaultMsg: false })
    
    // 更新本地状态
    reviewDetail.value.isHelpful = !reviewDetail.value.isHelpful
    reviewDetail.value.helpfulCount = reviewDetail.value.isHelpful 
      ? (reviewDetail.value.helpfulCount || 0) + 1 
      : Math.max(0, (reviewDetail.value.helpfulCount || 0) - 1)
    
    uni.showToast({ 
      title: reviewDetail.value.isHelpful ? '已点赞' : '已取消', 
      icon: 'success' 
    })
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

/**
 * 获取评分标签
 */
const getRatingLabel = (rating) => {
  const labels = ['', '很差', '较差', '一般', '满意', '非常满意']
  return labels[rating] || ''
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

/**
 * 预览图片
 */
const previewImages = (images, current) => {
  uni.previewImage({
    urls: images,
    current: current
  })
}
</script>

<style lang="scss" scoped>
.review-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  padding: 24rpx;
}

.user-section {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;

  .user-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    margin-right: 24rpx;
  }

  .user-avatar-placeholder {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;

    .fa {
      font-size: 48rpx;
      color: #999;
    }
  }

  .user-info {
    flex: 1;

    .user-name {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 12rpx;
    }

    .review-time {
      display: block;
      font-size: 24rpx;
      color: #999;
    }
  }
}

.rating-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
  text-align: center;

  .rating-stars {
    display: flex;
    justify-content: center;
    gap: 8rpx;
    margin-bottom: 16rpx;

    .fa {
      font-size: 64rpx;
      color: #e0e0e0;

      &.active {
        color: #fadb14;
      }
    }
  }

  .rating-label {
    font-size: 32rpx;
    font-weight: bold;
    color: #ff6b00;
  }
}

.section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;

  .section-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 24rpx;
  }
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .tag {
    padding: 12rpx 24rpx;
    background: #f0f5ff;
    color: #667eea;
    font-size: 26rpx;
    border-radius: 32rpx;
  }
}

.review-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #333;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;

  .review-image {
    width: 100%;
    height: 200rpx;
    border-radius: 12rpx;
  }
}

.coach-reply {
  .reply-header {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .coach-avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      margin-right: 20rpx;
    }

    .coach-avatar-placeholder {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      background: #f0f0f0;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;

      .fa {
        font-size: 40rpx;
        color: #999;
      }
    }

    .coach-info {
      flex: 1;

      .coach-name {
        display: block;
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 8rpx;
      }

      .reply-time {
        display: block;
        font-size: 24rpx;
        color: #999;
      }
    }
  }

  .reply-content {
    font-size: 28rpx;
    line-height: 1.8;
    color: #666;
    background: #f5f5f5;
    padding: 24rpx;
    border-radius: 12rpx;
  }
}

.no-reply {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 40rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: #999;

  .fa {
    font-size: 32rpx;
  }
}

.action-bar {
  display: flex;
  justify-content: center;
  padding-top: 20rpx;

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12rpx;
    padding: 20rpx 48rpx;
    background: #f0f5ff;
    border-radius: 16rpx;
    color: #667eea;

    .fa {
      font-size: 48rpx;
    }

    .action-text {
      font-size: 26rpx;
    }
  }
}

.loading-container, .empty-container {
  padding: 200rpx 0;
  text-align: center;

  .fa {
    font-size: 160rpx;
    color: #ccc;
  }

  .loading-text, .empty-text {
    display: block;
    margin-top: 32rpx;
    font-size: 28rpx;
    color: #999;
  }
}
</style>
