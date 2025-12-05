<template>
  <view class="booking-detail-page">
    <mod-nav-bar title="预约详情" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <view v-if="!loading && booking" class="detail-container">
      <!-- 课程信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <text class="fa fa-book"></text>
          <text class="card-title">课程信息</text>
        </view>
        <view class="card-content">
          <image 
            v-if="booking.courseCoverImage" 
            :src="booking.courseCoverImage" 
            class="course-cover"
            mode="aspectFill"
          ></image>
          <view class="info-row">
            <text class="label">课程名称</text>
            <text class="value">{{ booking.courseName }}</text>
          </view>
          <view class="info-row">
            <text class="label">课程时长</text>
            <text class="value">{{ booking.courseDuration }}分钟</text>
          </view>
        </view>
      </view>

      <!-- 时间信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <text class="fa fa-calendar"></text>
          <text class="card-title">时间信息</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="label">开始时间</text>
            <text class="value">{{ formatDateTime(booking.scheduleStartTime) }}</text>
          </view>
          <view class="info-row">
            <text class="label">结束时间</text>
            <text class="value">{{ formatDateTime(booking.scheduleEndTime) }}</text>
          </view>
          <view class="info-row">
            <text class="label">预约时间</text>
            <text class="value">{{ formatDateTime(booking.bookingTime) }}</text>
          </view>
          <view v-if="booking.cancelTime" class="info-row">
            <text class="label">取消时间</text>
            <text class="value">{{ formatDateTime(booking.cancelTime) }}</text>
          </view>
        </view>
      </view>

      <!-- 价格信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <text class="fa fa-money"></text>
          <text class="card-title">价格信息</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="label">课程原价</text>
            <text class="value">¥{{ booking.originalPrice }}</text>
          </view>
          <view class="info-row">
            <text class="label">折扣率</text>
            <text class="value">{{ (booking.discountRate * 100).toFixed(0) }}%</text>
          </view>
          <view class="info-row highlight">
            <text class="label">实付金额</text>
            <text class="value price">¥{{ booking.actualPrice }}</text>
          </view>
          <view v-if="booking.savedAmount > 0" class="saved-tip">
            <text class="fa fa-check-circle"></text>
            已为您节省 ¥{{ booking.savedAmount }}
          </view>
        </view>
      </view>

      <!-- 状态信息卡片 -->
      <view class="info-card">
        <view class="card-header">
          <text class="fa fa-info-circle"></text>
          <text class="card-title">预约状态</text>
        </view>
        <view class="card-content">
          <view class="status-badge" :class="getStatusClass(booking.status)">
            {{ booking.statusDisplayName }}
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view v-if="booking.status === 1" class="action-footer">
        <button class="cancel-btn" @click="handleCancel">
          <text class="fa fa-times"></text>
          取消预约
        </button>
      </view>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text class="fa fa-spinner fa-spin"></text>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getBookingById, cancelBooking } from '@/apis/booking.js'
import { getFileUrl } from '@/utils/fileUtils.js'

// 预约详情
const booking = ref(null)
const loading = ref(false)

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 兼容iOS：将 "-" 替换为 "/"
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

// 查询预约详情
const fetchBookingDetail = (bookingId) => {
  loading.value = true
  
  getBookingById(bookingId, {
    onSuccess: (res) => {
      // 转换图片路径为完整URL
      if (res.courseCoverImage) {
        res.courseCoverImage = getFileUrl(res.courseCoverImage)
      }
      booking.value = res
      loading.value = false
    },
    onError: () => {
      loading.value = false
      uni.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  })
}

// 取消预约
const handleCancel = () => {
  uni.showModal({
    title: '确认取消',
    content: `确定要取消"${booking.value.courseName}"的预约吗？`,
    success: (res) => {
      if (res.confirm) {
        cancelBooking(booking.value.id, {
          successMsg: '预约已取消',
          onSuccess: () => {
            // 刷新详情
            fetchBookingDetail(booking.value.id)
          }
        })
      }
    }
  })
}

// 页面加载
onLoad((options) => {
  if (options.id) {
    fetchBookingDetail(options.id)
  } else {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
  }
})
</script>

<style lang="scss" scoped>
.booking-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.detail-container {
  padding: 30rpx;
}

.info-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;

  .card-header {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .fa {
      font-size: 32rpx;
      color: #FF6B35;
      margin-right: 12rpx;
    }

    .card-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .card-content {
    padding: 30rpx;

    .course-cover {
      width: 100%;
      height: 400rpx;
      border-radius: 12rpx;
      margin-bottom: 30rpx;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .label {
        font-size: 28rpx;
        color: #666;
      }

      .value {
        font-size: 28rpx;
        color: #333;
        text-align: right;
        flex: 1;
        margin-left: 20rpx;

        &.price {
          font-size: 36rpx;
          font-weight: bold;
          color: #ff4d4f;
        }
      }

      &.highlight {
        padding-top: 30rpx;
        margin-top: 20rpx;
        border-top: 2rpx solid #f0f0f0;

        .label {
          font-size: 30rpx;
          font-weight: bold;
        }
      }
    }

    .saved-tip {
      margin-top: 20rpx;
      padding: 20rpx;
      background: #f6ffed;
      border-radius: 8rpx;
      font-size: 26rpx;
      color: #52c41a;
      text-align: center;

      .fa {
        margin-right: 8rpx;
      }
    }

    .status-badge {
      padding: 20rpx 40rpx;
      border-radius: 40rpx;
      font-size: 32rpx;
      font-weight: bold;
      text-align: center;

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

.action-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);

  .cancel-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: #fff;
    color: #ff4d4f;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 44rpx;
    border: 2rpx solid #ff4d4f;

    .fa {
      margin-right: 10rpx;
    }
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
