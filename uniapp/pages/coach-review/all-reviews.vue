<template>
  <view class="all-reviews-page">
    <mod-nav-bar title="查看教练评价"></mod-nav-bar>
    
    <!-- 教练列表 -->
    <view class="coach-list">
      <view v-if="loading && coachList.length === 0" class="loading-container">
        <text class="fa fa-spinner fa-spin"></text>
        <text class="loading-text">加载中...</text>
      </view>

      <view v-else-if="coachList.length === 0" class="empty-container">
        <text class="fa fa-user-times empty-icon"></text>
        <text class="empty-text">暂无教练信息</text>
      </view>

      <view v-else>
        <view 
          v-for="coach in coachList" 
          :key="coach.id" 
          class="coach-item"
          @click="goToCoachReviews(coach)"
        >
          <!-- 教练头像 -->
          <image v-if="coach.avatar" class="coach-avatar" :src="getAvatarUrl(coach.avatar)" mode="aspectFill"></image>
          <view v-else class="coach-avatar-placeholder">
            <text class="fa fa-user"></text>
          </view>

          <!-- 教练信息 -->
          <view class="coach-info">
            <view class="coach-header">
              <text class="coach-name">{{ coach.nickname || '未设置昵称' }}</text>
              <view v-if="coach.averageRating > 0" class="rating-badge">
                <text class="fa fa-star"></text>
                <text class="rating-text">{{ coach.averageRating.toFixed(1) }}</text>
              </view>
            </view>
            
            <view v-if="coach.specialty" class="specialty">
              <text class="fa fa-dumbbell"></text>
              <text class="specialty-text">{{ coach.specialty }}</text>
            </view>
            
            <view class="stats">
              <view class="stat-item">
                <text class="fa fa-comment"></text>
                <text class="stat-text">{{ coach.totalReviews || 0 }}条评价</text>
              </view>
              <view v-if="coach.replyRate !== null && coach.replyRate !== undefined" class="stat-item">
                <text class="fa fa-reply"></text>
                <text class="stat-text">回复率{{ coach.replyRate.toFixed(0) }}%</text>
              </view>
            </view>
          </view>

          <text class="fa fa-chevron-right arrow"></text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllActiveCoaches } from '@/apis/coach.js'
import { getReviewStatistics } from '@/apis/coachReview.js'
import { getFileUrl } from '@/utils/fileUtils.js'

// 教练列表
const coachList = ref([])
const loading = ref(false)

onMounted(() => {
  loadCoachList()
})

/**
 * 加载教练列表
 */
const loadCoachList = async () => {
  loading.value = true
  try {
    const coaches = await getAllActiveCoaches({ showDefaultMsg: false })
    
    // 为每个教练加载评价统计
    if (coaches && coaches.length > 0) {
      const coachesWithStats = await Promise.all(
        coaches.map(async (coach) => {
          try {
            const stats = await getReviewStatistics(coach.id, { showDefaultMsg: false })
            return {
              ...coach,
              totalReviews: stats?.totalReviews || 0,
              averageRating: stats?.averageRating || 0,
              replyRate: stats?.replyRate || 0
            }
          } catch (error) {
            console.error(`获取教练${coach.id}评价统计失败:`, error)
            return {
              ...coach,
              totalReviews: 0,
              averageRating: 0,
              replyRate: 0
            }
          }
        })
      )
      
      // 按评分降序排序
      coachList.value = coachesWithStats.sort((a, b) => b.averageRating - a.averageRating)
    }
  } catch (error) {
    console.error('加载教练列表失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 获取头像URL
 */
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  return getFileUrl(avatar)
}

/**
 * 跳转到教练评价列表
 */
const goToCoachReviews = (coach) => {
  uni.navigateTo({
    url: `/pages/coach-review/list?coachId=${coach.id}&coachName=${coach.nickname || '未设置昵称'}&coachAvatar=${encodeURIComponent(getAvatarUrl(coach.avatar) || '')}`
  })
}
</script>

<style lang="scss" scoped>
.all-reviews-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.coach-list {
  padding: 24rpx;

  .coach-item {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

    .coach-avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      margin-right: 24rpx;
      flex-shrink: 0;
    }

    .coach-avatar-placeholder {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;
      flex-shrink: 0;

      .fa {
        font-size: 60rpx;
        color: #fff;
      }
    }

    .coach-info {
      flex: 1;
      min-width: 0;

      .coach-header {
        display: flex;
        align-items: center;
        gap: 16rpx;
        margin-bottom: 12rpx;

        .coach-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          flex-shrink: 0;
        }

        .rating-badge {
          display: flex;
          align-items: center;
          gap: 6rpx;
          padding: 6rpx 12rpx;
          background: linear-gradient(135deg, #fadb14 0%, #ff6b00 100%);
          border-radius: 20rpx;

          .fa {
            font-size: 22rpx;
            color: #fff;
          }

          .rating-text {
            font-size: 22rpx;
            font-weight: bold;
            color: #fff;
          }
        }
      }

      .specialty {
        display: flex;
        align-items: center;
        gap: 8rpx;
        margin-bottom: 12rpx;

        .fa {
          font-size: 24rpx;
          color: #667eea;
        }

        .specialty-text {
          font-size: 26rpx;
          color: #666;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .stats {
        display: flex;
        align-items: center;
        gap: 32rpx;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 8rpx;

          .fa {
            font-size: 24rpx;
            color: #999;
          }

          .stat-text {
            font-size: 24rpx;
            color: #999;
          }
        }
      }
    }

    .arrow {
      font-size: 28rpx;
      color: #ccc;
      margin-left: 16rpx;
      flex-shrink: 0;
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
</style>
