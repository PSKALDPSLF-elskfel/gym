<template>
  <view class="page-wrap">
    <mod-nav-bar title="首页" title-color="#fff"></mod-nav-bar>
    
    <view class="page-content">
      <!-- 轮播图 -->
      <view class="banner-section">
        <swiper 
          class="banner-swiper" 
          :indicator-dots="true" 
          :autoplay="true" 
          :interval="3000" 
          :duration="500"
          :circular="true"
          indicator-color="rgba(255, 255, 255, 0.5)"
          indicator-active-color="#ff6b35"
        >
          <swiper-item v-for="(banner, index) in banners" :key="index">
            <image 
              :src="banner.image" 
              class="banner-image" 
              mode="aspectFill"
              @click="handleBannerClick(banner)"
            ></image>
          </swiper-item>
        </swiper>
      </view>
      
      <!-- 公告栏 -->
      <view v-if="announcements.length > 0" class="announcement-section">
        <view class="announcement-header">
          <text class="fa fa-bullhorn header-icon"></text>
          <text class="header-title">最新公告</text>
          <view class="view-all-btn" @click="goToAllAnnouncements">
            <text class="btn-text">查看全部</text>
            <text class="fa fa-chevron-right"></text>
          </view>
        </view>
        
        <!-- 公告轮播 -->
        <swiper 
          class="announcement-swiper" 
          :indicator-dots="false" 
          :autoplay="true" 
          :interval="4000" 
          :duration="500"
          :circular="true"
          vertical
        >
          <swiper-item 
            v-for="announcement in announcements" 
            :key="announcement.id"
            @click="handleAnnouncementClick(announcement)"
          >
            <view class="announcement-item">
              <view class="announcement-dot"></view>
              <text class="announcement-title">{{ announcement.title }}</text>
              <text class="announcement-time">{{ formatTime(announcement.createTime) }}</text>
            </view>
          </swiper-item>
        </swiper>
      </view>
      
      <text class="welcome">欢迎来到健身房多功能平台</text>
      
      <!-- 快捷功能入口 -->
      <view class="quick-menu">
        <view class="menu-card" @click="goToCourseList">
          <view class="card-icon">
            <text class="fa fa-book"></text>
          </view>
          <text class="card-title">健身课程</text>
          <text class="card-desc">专业教练指导</text>
        </view>
        
        <view class="menu-card" @click="goToMembershipPackage">
          <view class="card-icon">
            <text class="fa fa-star"></text>
          </view>
          <text class="card-title">会员办理</text>
          <text class="card-desc">享受优惠价格</text>
        </view>
        
        <view class="menu-card" @click="goToMyBookings">
          <view class="card-icon">
            <text class="fa fa-calendar"></text>
          </view>
          <text class="card-title">我的预约</text>
          <text class="card-desc">管理预约记录</text>
        </view>
        
        <view class="menu-card" @click="goToWorkoutHistory">
          <view class="card-icon">
            <text class="fa fa-heartbeat"></text>
          </view>
          <text class="card-title">运动记录</text>
          <text class="card-desc">记录运动数据</text>
        </view>
        
        <view class="menu-card" @click="goToWorkoutStats">
          <view class="card-icon">
            <text class="fa fa-bar-chart"></text>
          </view>
          <text class="card-title">运动统计</text>
          <text class="card-desc">查看数据分析</text>
        </view>
        
        <view class="menu-card" @click="goToTrainingPlan">
          <view class="card-icon">
            <text class="fa fa-trophy"></text>
          </view>
          <text class="card-title">训练计划</text>
          <text class="card-desc">制定训练目标</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useNavBarStyle } from "@/utils/system.js"
import siteConfig from '@/config/site.js'
import { getLatestAnnouncements } from '@/apis/announcement.js'

const { navBarHeight } = useNavBarStyle()

// 公告数据
const announcements = ref([])

// 轮播图数据
const banners = ref([
  {
    id: 1,
    image: '/static/images/7077.jpg_wh860.jpg',
    title: '健身房预约系统',
    link: '/pages/course/list' // 点击跳转到课程列表
  },
  {
    id: 2,
    image: '/static/images/banner2.png',
    title: '会员套餐优惠',
    link: '/pages/membership/package-list' // 点击跳转到会员套餐
  },
  {
    id: 3,
    image: '/static/images/6369.jpg_wh860.jpg',
    title: '专业健身器材',
    link: '/pages/equipment/list' // 点击跳转到器材列表
  }
])

/**
 * 处理轮播图点击
 */
const handleBannerClick = (banner) => {
  if (banner.link) {
    // 判断是否为tabbar页面
    const tabbarPages = ['/pages/course/list', '/pages/equipment/list', '/pages/my/my']
    if (tabbarPages.includes(banner.link)) {
      uni.switchTab({
        url: banner.link
      })
    } else {
      uni.navigateTo({
        url: banner.link
      })
    }
  }
}

/**
 * 跳转到课程列表
 */
const goToCourseList = () => {
  uni.switchTab({
    url: '/pages/course/list'
  })
}

/**
 * 跳转到会员套餐页
 */
const goToMembershipPackage = () => {
  uni.navigateTo({
    url: '/pages/membership/package-list'
  })
}

/**
 * 跳转到我的预约
 */
const goToMyBookings = () => {
  uni.navigateTo({
    url: '/pages/booking/my'
  })
}

/**
 * 跳转到运动历史
 */
const goToWorkoutHistory = () => {
  uni.navigateTo({
    url: '/pages/workout/history'
  })
}

/**
 * 跳转到运动统计
 */
const goToWorkoutStats = () => {
  uni.navigateTo({
    url: '/pages/workout/statistics'
  })
}

/**
 * 跳转到训练计划
 */
const goToTrainingPlan = () => {
  uni.navigateTo({
    url: '/pages/training-plan/list'
  })
}

/**
 * 获取公告列表
 */
const fetchAnnouncements = () => {
  getLatestAnnouncements({ limit: 3 }, {
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
 * 格式化时间
 */
const formatTime = (dateTime) => {
  if (!dateTime) return ''
  // 兼容iOS：将 "-" 替换为 "/"
  const iosCompatibleDate = dateTime.replace(/-/g, '/')
  const date = new Date(iosCompatibleDate)
  if (isNaN(date.getTime())) return ''
  
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
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

/**
 * 跳转到全部公告页面
 */
const goToAllAnnouncements = () => {
  uni.navigateTo({
    url: '/pages/announcement/list'
  })
}

// 页面加载时获取公告
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style lang="scss" scoped>
.page-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 140rpx;
  background: #f5f5f5;
  
  // 轮播图区域
  .banner-section {
    margin-bottom: 30rpx;
    
    .banner-swiper {
      width: 100%;
      height: 400rpx;
      
      .banner-image {
        width: 100%;
        height: 100%;
      }
    }
  }
  
  // 公告栏
  .announcement-section {
    margin: 0 30rpx 30rpx;
    background: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    border: 1rpx solid #e8e8e8;
    
    .announcement-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 24rpx 30rpx;
      background: #000000;
      
      .header-icon {
        font-size: 32rpx;
        color: #fff;
        margin-right: 12rpx;
      }
      
      .header-title {
        flex: 1;
        font-size: 28rpx;
        font-weight: bold;
        color: #fff;
      }
      
      .view-all-btn {
        display: flex;
        align-items: center;
        gap: 6rpx;
        padding: 8rpx 16rpx;
        background: #fff;
        border-radius: 20rpx;
        
        .btn-text {
          font-size: 24rpx;
          color: #ff6b35;
        }
        
        .fa {
          font-size: 20rpx;
          color: #ff6b35;
        }
      }
    }
    
    .announcement-swiper {
      height: 100rpx;
      
      .announcement-item {
        display: flex;
        align-items: center;
        padding: 20rpx 30rpx;
        height: 100rpx;
        
        .announcement-dot {
          width: 12rpx;
          height: 12rpx;
          background: #ff6b35;
          border-radius: 50%;
          margin-right: 16rpx;
          flex-shrink: 0;
        }
        
        .announcement-title {
          flex: 1;
          font-size: 28rpx;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .announcement-time {
          font-size: 24rpx;
          color: #999;
          margin-left: 16rpx;
          flex-shrink: 0;
        }
      }
    }
  }
  
  .welcome {
    display: block;
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin: 50rpx 0 40rpx;
    padding: 0 40rpx;
    letter-spacing: 2rpx;
  }
}

.quick-menu {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  padding: 0 30rpx 30rpx;
}

.menu-card {
  background: #fff;
  border: 1rpx solid #e8e8e8;
  border-radius: 16rpx;
  padding: 30rpx 20rpx;
  text-align: center;
  min-height: 180rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.card-icon {
  margin-bottom: 16rpx;
  width: 80rpx;
  height: 80rpx;
  background: #fff5f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .fa {
    font-size: 40rpx;
    color: #ff6b35;
  }
}

.card-title {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 8rpx;
}

.card-desc {
  display: block;
  font-size: 22rpx;
  color: #999;
  line-height: 1.4;
}
</style>
