<template>
  <view class="booking-page">
    <mod-nav-bar title="课程预约" title-color="#fff" :show-back="true"></mod-nav-bar>
    
    <!-- 课程信息 -->
    <view class="course-card">
      <image v-if="schedule.courseCoverImage" :src="schedule.courseCoverImage" class="course-cover" mode="aspectFill"></image>
      <view class="course-info">
        <view class="course-name">{{ schedule.courseName }}</view>
        <view class="course-meta">
          <text class="meta-item">
            <text class="fa fa-clock-o"></text> {{ schedule.courseDuration }}分钟
          </text>
          <text class="meta-item">
            <text class="fa fa-users"></text> {{ schedule.currentParticipants }}/{{ schedule.maxParticipants }}人
          </text>
        </view>
      </view>
    </view>

    <!-- 时间信息 -->
    <view class="info-card">
      <view class="card-title">
        <text class="fa fa-calendar"></text> 课程时间
      </view>
      <view class="time-info">
        <view class="time-row">
          <text class="label">开始时间</text>
          <text class="value">{{ formatDateTime(schedule.startTime) }}</text>
        </view>
        <view class="time-row">
          <text class="label">结束时间</text>
          <text class="value">{{ formatDateTime(schedule.endTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 价格信息 -->
    <view class="info-card">
      <view class="card-title">
        <text class="fa fa-money"></text> 价格信息
      </view>
      <view class="price-info">
        <view class="price-row">
          <text class="label">课程原价</text>
          <text class="value">¥{{ schedule.price }}</text>
        </view>
        <view v-if="userInfo && Number(userInfo.memberType) === 1" class="price-row discount">
          <text class="label">
            <text class="fa fa-star"></text> 黄金会员折扣
          </text>
          <text class="value">95折</text>
        </view>
        <view v-if="userInfo && Number(userInfo.memberType) === 2" class="price-row discount">
          <text class="label">
            <text class="fa fa-star"></text> 铂金会员折扣
          </text>
          <text class="value">9折</text>
        </view>
        <view class="price-row total">
          <text class="label">实付金额</text>
          <text class="value price-highlight">¥{{ actualPrice }}</text>
        </view>
        <view v-if="savedAmount > 0" class="saved-tip">
          <text class="fa fa-check-circle"></text> 已为您节省 ¥{{ savedAmount }}
        </view>
      </view>
    </view>

    <!-- 会员提示 -->
    <view v-if="userInfo && userInfo.memberType === 0" class="member-tip">
      <text class="fa fa-info-circle"></text>
      升级为铂金会员可享受课程9折优惠哦~
    </view>

    <!-- 预约按钮 -->
    <view class="booking-footer">
      <button 
        class="booking-btn" 
        :class="{ disabled: !canBook }" 
        :disabled="!canBook"
        @click="handleBooking"
      >
        {{ bookingBtnText }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createBooking, checkBooked } from '@/apis/booking.js'
import { useUserStore } from '@/store/user.js'
import { getFileUrl } from '@/utils/fileUtils.js'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 课程时间信息
const schedule = ref({
  id: null,
  courseId: '',
  courseName: '',
  courseCoverImage: '',
  courseDuration: 0,
  price: 0,
  startTime: '',
  endTime: '',
  maxParticipants: 0,
  currentParticipants: 0,
  status: 1
})

// 是否已预约
const isBooked = ref(false)

// 计算实付价格
const actualPrice = computed(() => {
  const price = parseFloat(schedule.value.price || 0)
  
  // 会员折扣计算（兼容字符串和数字）
  const memberType = Number(userInfo.value?.memberType)
  
  if (userInfo.value && memberType === 1) {
    // 黄金会员享受95折
    return (price * 0.95).toFixed(2)
  } else if (userInfo.value && memberType === 2) {
    // 铂金会员享受9折
    return (price * 0.9).toFixed(2)
  }
  
  return price.toFixed(2)
})

// 计算节省金额
const savedAmount = computed(() => {
  const price = parseFloat(schedule.value.price || 0)
  const actual = parseFloat(actualPrice.value)
  const saved = price - actual
  return saved > 0 ? saved.toFixed(2) : 0
})

// 是否可以预约
const canBook = computed(() => {
  if (isBooked.value) return false
  if (schedule.value.status !== 1) return false
  if (schedule.value.currentParticipants >= schedule.value.maxParticipants) return false
  return true
})

// 预约按钮文字
const bookingBtnText = computed(() => {
  if (isBooked.value) return '已预约'
  if (schedule.value.status !== 1) return '课程已取消'
  if (schedule.value.currentParticipants >= schedule.value.maxParticipants) return '已满员'
  return '立即预约'
})

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

// 检查是否已预约
const checkIsBooked = () => {
  checkBooked({ scheduleId: schedule.value.id }, {
    onSuccess: (res) => {
      isBooked.value = res
    }
  })
}

// 处理预约
const handleBooking = () => {
  if (!canBook.value) return
  
  uni.showModal({
    title: '确认预约',
    content: `确认预约"${schedule.value.courseName}"课程吗？\n实付金额：¥${actualPrice.value}`,
    success: (res) => {
      if (res.confirm) {
        createBooking({ scheduleId: schedule.value.id }, {
          successMsg: '预约成功',
          onSuccess: () => {
            isBooked.value = true
            // 跳转到我的预约页面
            setTimeout(() => {
              uni.navigateTo({
                url: '/pages/booking/my'
              })
            }, 1500)
          }
        })
      }
    }
  })
}

// 页面加载
onLoad((options) => {
  if (options.schedule) {
    try {
      const scheduleData = JSON.parse(decodeURIComponent(options.schedule))
      // 转换图片路径为完整URL
      if (scheduleData.courseCoverImage) {
        scheduleData.courseCoverImage = getFileUrl(scheduleData.courseCoverImage)
      }
      schedule.value = scheduleData
      checkIsBooked()
    } catch (e) {
      console.error('解析课程信息失败:', e)
      uni.showToast({
        title: '数据加载失败',
        icon: 'none'
      })
    }
  }
})
</script>

<style lang="scss" scoped>
.booking-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.course-card {
  background: #fff;
  margin-bottom: 20rpx;

  .course-cover {
    width: 100%;
    height: 400rpx;
  }

  .course-info {
    padding: 30rpx;

    .course-name {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
    }

    .course-meta {
      display: flex;
      gap: 30rpx;

      .meta-item {
        font-size: 28rpx;
        color: #666;

        .fa {
          margin-right: 10rpx;
          color: #FF6B35;
        }
      }
    }
  }
}

.info-card {
  background: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;

  .card-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;

    .fa {
      margin-right: 10rpx;
      color: #FF6B35;
    }
  }

  .time-info,
  .price-info {
    .time-row,
    .price-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

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
      }

      &.discount {
        .label {
          color: #FF6B35;

          .fa {
            margin-right: 8rpx;
          }
        }

        .value {
          color: #FF6B35;
          font-weight: bold;
        }
      }

      &.total {
        padding-top: 30rpx;
        margin-top: 20rpx;
        border-top: 2rpx solid #f0f0f0;

        .label {
          font-size: 32rpx;
          font-weight: bold;
        }

        .price-highlight {
          font-size: 40rpx;
          font-weight: bold;
          color: #ff4d4f;
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
  }
}

.member-tip {
  background: #fff7e6;
  margin: 0 30rpx 20rpx;
  padding: 24rpx 30rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #fa8c16;

  .fa {
    margin-right: 10rpx;
  }
}

.booking-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;

  .booking-btn {
    width: 100%;
    height: 88rpx;
    background: #FF6B35;
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 44rpx;
    border: none;

    &.disabled {
      background: #d9d9d9;
      color: #999;
    }
  }
}
</style>
