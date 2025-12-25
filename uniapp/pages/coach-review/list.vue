<template>
  <view class="review-list-page">
    <mod-nav-bar :title="pageTitle"></mod-nav-bar>
    
    <!-- 教练信息卡片 -->
    <view v-if="coachInfo" class="coach-card">
      <image v-if="coachInfo.avatar" class="coach-avatar" :src="coachInfo.avatar" mode="aspectFill"></image>
      <view v-else class="coach-avatar-placeholder">
        <text class="fa fa-user"></text>
      </view>
      <view class="coach-info">
        <text class="coach-name">{{ coachInfo.name }}</text>
        <view class="rating-info">
          <view class="stars">
            <text v-for="i in 5" :key="i" class="fa" :class="i <= Math.floor(statistics.averageRating) ? 'fa-star' : 'fa-star-o'"></text>
          </view>
          <text class="rating-text">{{ statistics.averageRating?.toFixed(1) || '0.0' }} 分</text>
          <text class="review-count">({{ statistics.totalReviews || 0 }}条评价)</text>
        </view>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view 
        v-for="item in filterOptions" 
        :key="item.value" 
        class="filter-item"
        :class="{ active: currentFilter === item.value }"
        @click="handleFilterChange(item.value)"
      >
        <text>{{ item.label }}</text>
        <text v-if="item.count !== undefined" class="count">({{ item.count }})</text>
      </view>
    </view>

    <!-- 评价列表 -->
    <view class="review-list">
      <view v-if="loading && reviewList.length === 0" class="loading-container">
        <text class="fa fa-spinner fa-spin"></text>
        <text class="loading-text">加载中...</text>
      </view>

      <view v-else-if="reviewList.length === 0" class="empty-container">
        <text class="fa fa-comment-slash empty-icon"></text>
        <text class="empty-text">暂无评价</text>
      </view>

      <view v-else>
        <view 
          v-for="review in reviewList" 
          :key="review.id" 
          class="review-item"
          @click="goToDetail(review.id)"
        >
          <!-- 用户信息 -->
          <view class="user-info">
            <image v-if="review.userAvatar && !review.isAnonymous" class="user-avatar" :src="review.userAvatar" mode="aspectFill"></image>
            <view v-else class="user-avatar-placeholder">
              <text class="fa fa-user"></text>
            </view>
            <view class="user-detail">
              <text class="user-name">{{ review.isAnonymous ? '匿名用户' : review.userName }}</text>
              <view class="rating-stars">
                <text v-for="i in 5" :key="i" class="fa fa-star" :class="{ active: i <= review.rating }"></text>
              </view>
            </view>
            <text class="review-time">{{ formatTime(review.createTime) }}</text>
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

          <!-- 教练回复 -->
          <view v-if="review.reply" class="coach-reply">
            <text class="reply-label">教练回复：</text>
            <text class="reply-content">{{ review.reply }}</text>
          </view>

          <!-- 操作栏 -->
          <view class="action-bar">
            <view class="action-item" @click.stop="handleLike(review)">
              <text class="fa" :class="review.isHelpful ? 'fa-thumbs-up' : 'fa-thumbs-o-up'"></text>
              <text class="action-text">有用 {{ review.helpfulCount || 0 }}</text>
            </view>
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

    <!-- 发布评价按钮 -->
    <view v-if="coachId" class="fab-button" @click="goToCreate">
      <text class="fa fa-plus"></text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getReviewList, getReviewStatistics, toggleHelpful } from '@/apis/coachReview.js'
import DateUtils from '@/utils/dateUtils.js'
import { getFileUrl } from '@/utils/fileUtils.js'

// 页面参数
const coachId = ref(null)
const coachInfo = ref(null)

// 评价数据
const reviewList = ref([])
const statistics = ref({})
const currentFilter = ref(null) // null-全部, 5-5星, 4-4星...

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 计算属性
const pageTitle = computed(() => coachInfo.value ? `${coachInfo.value.name}的评价` : '教练评价')
const hasMore = computed(() => reviewList.value.length < total.value)

const filterOptions = computed(() => [
  { label: '全部', value: null, count: statistics.value.totalReviews },
  { label: '5星', value: 5, count: statistics.value.rating5Count },
  { label: '4星', value: 4, count: statistics.value.rating4Count },
  { label: '3星', value: 3, count: statistics.value.rating3Count },
  { label: '2星', value: 2, count: statistics.value.rating2Count },
  { label: '1星', value: 1, count: statistics.value.rating1Count }
])

/**
 * 页面加载
 */
onLoad((options) => {
  if (options.coachId) {
    coachId.value = parseInt(options.coachId)
  }
  if (options.coachName) {
    const avatar = options.coachAvatar ? getFileUrl(options.coachAvatar) : null
    coachInfo.value = { name: options.coachName, avatar: avatar }
  }
})

onMounted(() => {
  if (coachId.value) {
    loadStatistics()
    loadReviewList()
  }
})

/**
 * 加载评价统计
 */
const loadStatistics = async () => {
  try {
    const data = await getReviewStatistics(coachId.value, { showDefaultMsg: false })
    statistics.value = data || {}
  } catch (error) {
    console.error('加载评价统计失败:', error)
  }
}

/**
 * 加载评价列表
 */
const loadReviewList = async (isLoadMore = false) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params = {
      coachId: coachId.value,
      rating: currentFilter.value,
      pageNum: isLoadMore ? currentPage.value + 1 : 1,
      pageSize: pageSize.value
    }
    
    const result = await getReviewList(params, { showDefaultMsg: false })
    
    // 转换评价中的图片路径，包括用户个人填写的传图
    if (result.records && Array.isArray(result.records)) {
      result.records.forEach(review => {
    // 个人一首被填写的照片路径需要转换
    if (review.images && Array.isArray(review.images)) {
      review.images = review.images.map(img => getFileUrl(img))
    }
    // 也需要转换用户的头像路径
    if (review.userAvatar) {
      review.userAvatar = getFileUrl(review.userAvatar)
    }
      })
    }
    
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
 * 切换筛选条件
 */
const handleFilterChange = (value) => {
  if (currentFilter.value === value) return
  currentFilter.value = value
  loadReviewList()
}

/**
 * 加载更多
 */
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  loadReviewList(true)
}

/**
 * 点赞
 */
const handleLike = async (review) => {
  try {
    await toggleHelpful(review.id, { showDefaultMsg: false })
    
    // 更新本地状态
    review.isHelpful = !review.isHelpful
    review.helpfulCount = review.isHelpful 
      ? (review.helpfulCount || 0) + 1 
      : Math.max(0, (review.helpfulCount || 0) - 1)
    
    uni.showToast({ 
      title: review.isHelpful ? '已点赞' : '已取消', 
      icon: 'success' 
    })
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  return DateUtils.format(time, 'YYYY-MM-DD HH:mm')
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

/**
 * 跳转到发布评价
 */
const goToCreate = () => {
  uni.navigateTo({
    url: `/pages/coach-review/create?coachId=${coachId.value}&coachName=${coachInfo.value?.name || ''}`
  })
}
</script>

<style lang="scss" scoped>
.review-list-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.coach-card {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 32rpx;
  margin-bottom: 16rpx;

  .coach-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    margin-right: 24rpx;
  }

  .coach-avatar-placeholder {
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

  .coach-info {
    flex: 1;

    .coach-name {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 12rpx;
    }

    .rating-info {
      display: flex;
      align-items: center;
      gap: 12rpx;

      .stars {
        display: flex;
        gap: 4rpx;

        .fa {
          font-size: 24rpx;
          color: #fadb14;
        }
      }

      .rating-text {
        font-size: 28rpx;
        font-weight: bold;
        color: #ff6b00;
      }

      .review-count {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

.filter-bar {
  display: flex;
  background: #fff;
  padding: 24rpx 32rpx;
  margin-bottom: 16rpx;
  gap: 24rpx;
  overflow-x: auto;

  .filter-item {
    flex-shrink: 0;
    padding: 12rpx 24rpx;
    border-radius: 32rpx;
    background: #f5f5f5;
    font-size: 26rpx;
    color: #666;
    white-space: nowrap;

    &.active {
      background: #667eea;
      color: #fff;
    }

    .count {
      margin-left: 4rpx;
    }
  }
}

.review-list {
  .review-item {
    background: #fff;
    padding: 32rpx;
    margin-bottom: 16rpx;

    .user-info {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;

      .user-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        margin-right: 20rpx;
      }

      .user-avatar-placeholder {
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

      .user-detail {
        flex: 1;

        .user-name {
          display: block;
          font-size: 28rpx;
          color: #333;
          margin-bottom: 8rpx;
        }

        .rating-stars {
          display: flex;
          gap: 4rpx;

          .fa {
            font-size: 24rpx;
            color: #e0e0e0;

            &.active {
              color: #fadb14;
            }
          }
        }
      }

      .review-time {
        font-size: 24rpx;
        color: #999;
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

    .coach-reply {
      background: #f5f5f5;
      padding: 20rpx;
      border-radius: 12rpx;
      margin-bottom: 20rpx;

      .reply-label {
        font-size: 24rpx;
        color: #667eea;
        font-weight: bold;
      }

      .reply-content {
        font-size: 26rpx;
        color: #666;
        line-height: 1.6;
      }
    }

    .action-bar {
      display: flex;
      justify-content: flex-end;
      padding-top: 20rpx;
      border-top: 1rpx solid #f0f0f0;

      .action-item {
        display: flex;
        align-items: center;
        gap: 8rpx;
        color: #999;
        font-size: 26rpx;

        .fa {
          font-size: 28rpx;
        }
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

.fab-button {
  position: fixed;
  right: 48rpx;
  bottom: 120rpx;
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: #667eea;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;

  .fa {
    font-size: 48rpx;
    color: #fff;
  }
}
</style>
