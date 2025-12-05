<template>
  <view class="my-booking-page">
    <mod-nav-bar title="我的预约" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 状态筛选 -->
    <view class="status-tabs">
      <view 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentStatus === tab.value }"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 预约列表 -->
    <view v-if="!loading && bookingList.length > 0" class="booking-list">
      <view 
        v-for="booking in bookingList" 
        :key="booking.id"
        class="booking-item"
        @click="handleViewDetail(booking)"
      >
        <!-- 课程信息 -->
        <view class="booking-header">
          <image 
            v-if="booking.courseCoverImage" 
            :src="booking.courseCoverImage" 
            class="course-cover"
            mode="aspectFill"
          ></image>
          <view class="course-info">
            <view class="course-name">{{ booking.courseName }}</view>
            <view class="course-time">
              <text class="fa fa-clock-o"></text>
              {{ formatDateTime(booking.scheduleStartTime) }}
            </view>
            <view class="course-duration">
              时长: {{ booking.courseDuration }}分钟
            </view>
          </view>
        </view>

        <!-- 价格和状态 -->
        <view class="booking-footer">
          <view class="price-info">
            <text class="actual-price">¥{{ booking.actualPrice }}</text>
            <text v-if="booking.savedAmount > 0" class="saved-amount">
              已省¥{{ booking.savedAmount }}
            </text>
          </view>
          <view class="status-info">
            <view 
              class="status-tag"
              :class="getStatusClass(booking.status)"
            >
              {{ booking.statusDisplayName }}
            </view>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view v-if="booking.status === 1" class="booking-actions">
          <button 
            class="action-btn cancel-btn" 
            @click.stop="handleCancel(booking)"
          >
            取消预约
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else-if="!loading && bookingList.length === 0" class="empty-state">
      <text class="fa fa-calendar-times-o empty-icon"></text>
      <text class="empty-text">暂无预约记录</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text class="fa fa-spinner fa-spin"></text>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyBookings, cancelBooking } from '@/apis/booking.js'
import { getFileUrl } from '@/utils/fileUtils.js'

// 状态标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '已预约', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 0 }
]

// 当前状态
const currentStatus = ref(null)

// 预约列表
const bookingList = ref([])
const loading = ref(false)

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 兼容iOS：将 "-" 替换为 "/"
  const iosCompatibleDate = dateTime.replace(/-/g, '/')
  const date = new Date(iosCompatibleDate)
  if (isNaN(date.getTime())) return ''
  
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${month}-${day} ${hours}:${minutes}`
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 0:
      return 'status-cancelled'
    case 1:
      return 'status-booked'
    case 2:
      return 'status-completed'
    default:
      return ''
  }
}

// 查询预约列表
const fetchBookings = () => {
  loading.value = true
  
  const params = {}
  if (currentStatus.value !== null) {
    params.status = currentStatus.value
  }
  
  getMyBookings(params, {
    onSuccess: (res) => {
      // 转换图片路径为完整URL
      const bookings = (res || []).map(booking => ({
        ...booking,
        courseCoverImage: booking.courseCoverImage ? getFileUrl(booking.courseCoverImage) : ''
      }))
      bookingList.value = bookings
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

// 切换状态标签
const handleTabChange = (status) => {
  currentStatus.value = status
  fetchBookings()
}

// 查看详情
const handleViewDetail = (booking) => {
  uni.navigateTo({
    url: `/pages/booking/detail?id=${booking.id}`
  })
}

// 取消预约
const handleCancel = (booking) => {
  uni.showModal({
    title: '确认取消',
    content: `确定要取消"${booking.courseName}"的预约吗？`,
    success: (res) => {
      if (res.confirm) {
        cancelBooking(booking.id, {
          successMsg: '预约已取消',
          onSuccess: () => {
            fetchBookings()
          }
        })
      }
    }
  })
}

// 页面显示时刷新
onShow(() => {
  fetchBookings()
})

// 初始化
onMounted(() => {
  fetchBookings()
})
</script>

<style lang="scss" scoped>
.my-booking-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.status-tabs {
  display: flex;
  background: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;

  .tab-item {
    flex: 1;
    text-align: center;
    font-size: 28rpx;
    color: #666;
    padding: 20rpx 0;
    position: relative;

    &.active {
      color: #FF6B35;
      font-weight: bold;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60rpx;
        height: 4rpx;
        background: #FF6B35;
        border-radius: 2rpx;
      }
    }
  }
}

.booking-list {
  padding: 0 30rpx;

  .booking-item {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;

    .booking-header {
      display: flex;
      padding: 30rpx;

      .course-cover {
        width: 160rpx;
        height: 160rpx;
        border-radius: 12rpx;
        margin-right: 24rpx;
        flex-shrink: 0;
      }

      .course-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .course-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 12rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .course-time {
          font-size: 26rpx;
          color: #666;
          margin-bottom: 8rpx;

          .fa {
            margin-right: 8rpx;
            color: #FF6B35;
          }
        }

        .course-duration {
          font-size: 24rpx;
          color: #999;
        }
      }
    }

    .booking-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 30rpx;
      border-top: 1rpx solid #f0f0f0;

      .price-info {
        display: flex;
        align-items: baseline;
        gap: 16rpx;

        .actual-price {
          font-size: 36rpx;
          font-weight: bold;
          color: #ff4d4f;
        }

        .saved-amount {
          font-size: 24rpx;
          color: #52c41a;
        }
      }

      .status-info {
        .status-tag {
          padding: 8rpx 20rpx;
          border-radius: 20rpx;
          font-size: 24rpx;

          &.status-cancelled {
            background: #f5f5f5;
            color: #999;
          }

          &.status-booked {
            background: #f6ffed;
            color: #52c41a;
          }

          &.status-completed {
            background: #e6f7ff;
            color: #1890ff;
          }
        }
      }
    }

    .booking-actions {
      padding: 0 30rpx 30rpx;

      .action-btn {
        width: 100%;
        height: 68rpx;
        line-height: 68rpx;
        border-radius: 34rpx;
        font-size: 28rpx;
        border: none;

        &.cancel-btn {
          background: #fff;
          color: #ff4d4f;
          border: 1rpx solid #ff4d4f;
        }
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
    color: #d9d9d9;
    margin-bottom: 30rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;

  .fa-spinner {
    font-size: 80rpx;
    color: #FF6B35;
    margin-bottom: 30rpx;
  }

  .loading-text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>
