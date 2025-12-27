<template>
  <view class="my-reviews-page">
    <mod-nav-bar title="我的评价"></mod-nav-bar>
    
    <view class="review-list">
      <view v-if="loading && reviewList.length === 0" class="loading-container">
        <text class="fa fa-spinner fa-spin"></text>
        <text class="loading-text">加载中...</text>
      </view>

      <view v-else-if="reviewList.length === 0" class="empty-container">
        <text class="fa fa-comment-slash empty-icon"></text>
        <text class="empty-text">暂无评价记录</text>
        <text class="empty-hint">快去评价您的教练吧~</text>
      </view>

      <view v-else>
        <view 
          v-for="review in reviewList" 
          :key="review.id" 
          class="review-item"
          @click="goToDetail(review.id)"
        >
          <!-- 教练信息 -->
          <view class="coach-info">
            <image v-if="review.coachAvatar" class="coach-avatar" :src="review.coachAvatar" mode="aspectFill"></image>
            <view v-else class="coach-avatar-placeholder">
              <text class="fa fa-user"></text>
            </view>
            <view class="coach-detail">
              <text class="coach-name">{{ review.coachName }}</text>
              <text class="review-time">{{ formatTime(review.createTime) }}</text>
            </view>
            <view class="delete-btn" @click.stop="handleDelete(review)">
              <text class="fa fa-trash-o"></text>
            </view>
          </view>

          <!-- 评分 -->
          <view class="rating-container">
            <view class="rating-stars">
              <text v-for="i in 5" :key="i" class="fa fa-star" :class="{ active: i <= review.rating }"></text>
            </view>
            <text class="rating-label">{{ getRatingLabel(review.rating) }}</text>
          </view>

          <!-- 评价标签 -->
          <view v-if="review.tagList && review.tagList.length > 0" class="tag-list">
            <view v-for="(tag, index) in review.tagList" :key="index" class="tag">{{ tag }}</view>
          </view>

          <!-- 评价内容 -->
          <view v-if="review.content" class="review-content">{{ review.content }}</view>

          <!-- 评价图片 -->
          <view v-if="review.images && review.images.length > 0" class="image-list">
            <image 
              v-for="(img, index) in review.images.slice(0, 3)" 
              :key="index" 
              class="review-image" 
              :src="img" 
              mode="aspectFill"
              @click.stop="previewImages(review.images, index)"
            ></image>
            <view v-if="review.images.length > 3" class="image-more">
              <text>+{{ review.images.length - 3 }}</text>
            </view>
          </view>

          <!-- 状态标识 -->
          <view class="status-info">
            <view v-if="review.isAnonymous" class="status-tag">
              <text class="fa fa-user-secret"></text>
              <text>匿名</text>
            </view>
            <view class="helpful-count">
              <text class="fa fa-thumbs-up"></text>
              <text>{{ review.helpfulCount || 0 }}人觉得有用</text>
            </view>
          </view>

          <!-- 教练回复 -->
          <view v-if="review.reply" class="coach-reply">
            <text class="reply-label">教练回复：</text>
            <text class="reply-content">{{ review.reply }}</text>
            <text class="reply-time">{{ formatTime(review.replyTime) }}</text>
          </view>
          <view v-else class="no-reply">
            <text class="fa fa-clock-o"></text>
            <text>等待教练回复</text>
          </view>
        </view>

        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more" @click="loadMore">
          <text v-if="loading" class="fa fa-spinner fa-spin"></text>
          <text v-else>加载更多</text>
        </view>
        <view v-else-if="reviewList.length > 0" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyReviews, deleteReview } from '@/apis/coachReview.js'
import { formatDate } from '@/utils/dateUtils.js'

// 评价数据
const reviewList = ref([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 计算属性
const hasMore = computed(() => reviewList.value.length < total.value)

onMounted(() => {
  loadReviewList()
})

/**
 * 加载评价列表
 */
const loadReviewList = async (isLoadMore = false) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params = {
      pageNum: isLoadMore ? currentPage.value + 1 : 1,
      pageSize: pageSize.value
    }
    
    const result = await getMyReviews(params, { showDefaultMsg: false })
    
    if (isLoadMore) {
      reviewList.value = [...reviewList.value, ...result.records]
      currentPage.value++
    } else {
      reviewList.value = result.records || []
      currentPage.value = 1
    }
    
    total.value = result.total || 0
  } catch (error) {
    console.error('加载评价列表失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多
 */
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  loadReviewList(true)
}

/**
 * 删除评价
 */
const handleDelete = (review) => {
  uni.showModal({
    title: '提示',
    content: '确定要删除这条评价吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteReview(review.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          
          // 从列表中移除
          const index = reviewList.value.findIndex(item => item.id === review.id)
          if (index > -1) {
            reviewList.value.splice(index, 1)
          }
        } catch (error) {
          console.error('删除失败:', error)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
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

/**
 * 跳转到评价详情
 */
const goToDetail = (reviewId) => {
  uni.navigateTo({
    url: `/pages/coach-review/detail?id=${reviewId}`
  })
}
</script>

<style lang="scss" scoped>
.my-reviews-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.review-list {
  padding: 24rpx;

  .review-item {
    background: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;

    .coach-info {
      display: flex;
      align-items: center;
      margin-bottom: 24rpx;

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

      .coach-detail {
        flex: 1;

        .coach-name {
          display: block;
          font-size: 30rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 8rpx;
        }

        .review-time {
          display: block;
          font-size: 24rpx;
          color: #999;
        }
      }

      .delete-btn {
        padding: 12rpx;
        color: #ff4d4f;

        .fa {
          font-size: 32rpx;
        }
      }
    }

    .rating-container {
      display: flex;
      align-items: center;
      gap: 16rpx;
      margin-bottom: 20rpx;

      .rating-stars {
        display: flex;
        gap: 4rpx;

        .fa {
          font-size: 28rpx;
          color: #e0e0e0;

          &.active {
            color: #fadb14;
          }
        }
      }

      .rating-label {
        font-size: 26rpx;
        color: #ff6b00;
      }
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-bottom: 20rpx;

      .tag {
        padding: 8rpx 16rpx;
        background: #f0f5ff;
        color: #667eea;
        font-size: 24rpx;
        border-radius: 8rpx;
      }
    }

    .review-content {
      font-size: 28rpx;
      line-height: 1.6;
      color: #333;
      margin-bottom: 20rpx;
    }

    .image-list {
      display: flex;
      gap: 12rpx;
      margin-bottom: 20rpx;

      .review-image {
        width: 200rpx;
        height: 200rpx;
        border-radius: 12rpx;
      }

      .image-more {
        width: 200rpx;
        height: 200rpx;
        border-radius: 12rpx;
        background: rgba(0, 0, 0, 0.5);
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 32rpx;
        margin-left: -212rpx;
      }
    }

    .status-info {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20rpx 0;
      border-top: 1rpx solid #f0f0f0;
      border-bottom: 1rpx solid #f0f0f0;
      margin-bottom: 20rpx;

      .status-tag {
        display: flex;
        align-items: center;
        gap: 8rpx;
        font-size: 24rpx;
        color: #999;

        .fa {
          font-size: 26rpx;
        }
      }

      .helpful-count {
        display: flex;
        align-items: center;
        gap: 8rpx;
        font-size: 24rpx;
        color: #999;

        .fa {
          font-size: 26rpx;
        }
      }
    }

    .coach-reply {
      background: #f0f5ff;
      padding: 20rpx;
      border-radius: 12rpx;
      border-left: 4rpx solid #667eea;

      .reply-label {
        display: block;
        font-size: 24rpx;
        color: #667eea;
        font-weight: bold;
        margin-bottom: 8rpx;
      }

      .reply-content {
        display: block;
        font-size: 26rpx;
        color: #666;
        line-height: 1.6;
        margin-bottom: 8rpx;
      }

      .reply-time {
        display: block;
        font-size: 22rpx;
        color: #999;
        text-align: right;
      }
    }

    .no-reply {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;
      padding: 24rpx;
      background: #f5f5f5;
      border-radius: 12rpx;
      font-size: 26rpx;
      color: #999;

      .fa {
        font-size: 28rpx;
      }
    }
  }
}

.loading-container, .empty-container {
  padding: 120rpx 0;
  text-align: center;

  .fa {
    font-size: 120rpx;
    color: #ccc;
  }

  .loading-text, .empty-text {
    display: block;
    margin-top: 24rpx;
    font-size: 28rpx;
    color: #999;
  }

  .empty-hint {
    display: block;
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #bbb;
  }
}

.load-more, .no-more {
  padding: 32rpx;
  text-align: center;
  font-size: 28rpx;
  color: #999;

  .fa {
    margin-right: 8rpx;
  }
}
</style>
