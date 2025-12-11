<template>
  <view class="training-plan-page">
    <!-- 自定义导航栏 -->
    <mod-nav-bar title="我的训练计划" :showBack="true"></mod-nav-bar>
    
    <!-- 状态筛选 -->
    <view class="status-tabs">
      <view 
        v-for="tab in statusTabs" 
        :key="tab.value" 
        :class="['tab-item', { active: currentStatus === tab.value }]"
        @click="changeStatus(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>
    
    <!-- 训练计划列表 -->
    <view class="plan-list">
      <view v-if="planList.length === 0" class="empty-tip">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无训练计划</text>
      </view>
      
      <view 
        v-for="plan in planList" 
        :key="plan.id" 
        class="plan-card"
        @click="goToPlanDetail(plan.id)"
      >
        <view class="plan-header">
          <view class="plan-name">{{ plan.name }}</view>
          <view :class="['plan-status', plan.status === 1 ? 'active' : 'ended']">
            {{ plan.status === 1 ? '进行中' : '已结束' }}
          </view>
        </view>
        
        <view class="plan-info">
          <view class="info-item">
            <text class="label">训练目标:</text>
            <text class="value">{{ plan.goal }}</text>
          </view>
          <view class="info-item">
            <text class="label">教练:</text>
            <text class="value">{{ plan.coachNickname || '暂无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">周期:</text>
            <text class="value">{{ plan.startDate }} 至 {{ plan.endDate }}</text>
          </view>
        </view>
        
        <view class="plan-progress">
          <view class="progress-info">
            <text class="progress-label">完成进度</text>
            <text class="progress-value">{{ plan.progress || 0 }}%</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: (plan.progress || 0) + '%' }"></view>
          </view>
        </view>
        
        <view class="plan-footer">
          <text class="create-time">创建于 {{ formatDate(plan.createTime) }}</text>
        </view>
      </view>
    </view>
    
    <!-- 加载更多 -->
    <view v-if="hasMore" class="load-more" @click="loadMore">
      <text>加载更多</text>
    </view>
    <view v-else-if="planList.length > 0" class="no-more">
      <text>没有更多了</text>
    </view>
  </view>
</template>

<script>
import { getTrainingPlanPage, calculateProgress } from '@/apis/trainingPlan.js'
import { getUserInfo } from '@/utils/auth.js'

export default {
  data() {
    return {
      statusTabs: [
        { label: '全部', value: null },
        { label: '进行中', value: 1 },
        { label: '已结束', value: 0 }
      ],
      currentStatus: null,
      planList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      hasMore: false
    }
  },
  
  onLoad() {
    this.loadPlanList()
  },
  
  methods: {
    // 加载训练计划列表
    async loadPlanList() {
      const userInfo = getUserInfo()
      if (!userInfo || !userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }
      
      uni.showLoading({ title: '加载中...' })
      
      getTrainingPlanPage({
        currentPage: this.currentPage,
        pageSize: this.pageSize,
        userId: userInfo.id,
        status: this.currentStatus
      }, {
        success: async (res) => {
          uni.hideLoading()
          const records = res.records || []
          
          // 加载每个计划的进度
          for (let plan of records) {
            await this.loadProgress(plan)
          }
          
          if (this.currentPage === 1) {
            this.planList = records
          } else {
            this.planList = [...this.planList, ...records]
          }
          
          this.total = res.total
          this.hasMore = this.planList.length < this.total
        },
        fail: (err) => {
          uni.hideLoading()
          uni.showToast({
            title: err || '加载失败',
            icon: 'none'
          })
        }
      })
    },
    
    // 加载计划进度
    async loadProgress(plan) {
      return new Promise((resolve) => {
        calculateProgress(plan.id, {
          success: (progress) => {
            plan.progress = Math.round(progress || 0)
            resolve()
          },
          fail: () => {
            plan.progress = 0
            resolve()
          }
        })
      })
    },
    
    // 切换状态
    changeStatus(status) {
      this.currentStatus = status
      this.currentPage = 1
      this.planList = []
      this.loadPlanList()
    },
    
    // 加载更多
    loadMore() {
      if (this.hasMore) {
        this.currentPage++
        this.loadPlanList()
      }
    },
    
    // 跳转到计划详情
    goToPlanDetail(planId) {
      uni.navigateTo({
        url: `/pages/training-plan/detail?id=${planId}`
      })
    },
    
    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.training-plan-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.status-tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 8rpx;
}

.tab-item.active {
  background-color: #3b82f6;
  color: #fff;
  font-weight: bold;
}

.plan-list {
  padding: 0 30rpx;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.plan-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.plan-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.plan-status {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.plan-status.active {
  background-color: #dcfce7;
  color: #16a34a;
}

.plan-status.ended {
  background-color: #f3f4f6;
  color: #6b7280;
}

.plan-info {
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
  font-size: 26rpx;
}

.info-item .label {
  color: #999;
  margin-right: 10rpx;
}

.info-item .value {
  color: #333;
}

.plan-progress {
  margin-bottom: 20rpx;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.progress-label {
  font-size: 26rpx;
  color: #666;
}

.progress-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #3b82f6;
}

.progress-bar {
  height: 12rpx;
  background-color: #e5e7eb;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  border-radius: 6rpx;
  transition: width 0.3s;
}

.plan-footer {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.create-time {
  font-size: 24rpx;
  color: #999;
}

.load-more, .no-more {
  text-align: center;
  padding: 30rpx;
  font-size: 28rpx;
  color: #999;
}
</style>
