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
    
    <!-- 浮动创建按钮 -->
    <view class="fab-container">
      <view class="fab-btn" @click="showCreateOptions">
        <text class="fab-icon">+</text>
      </view>
    </view>
    
    <!-- 创建方式选择弹窗 -->
    <view v-if="showCreatePopup" class="popup-mask" @click="hideCreateOptions">
      <view class="create-popup" @click.stop>
        <view class="popup-title">创建训练计划</view>
        <view class="create-options">
          <view class="create-option" @click="createFromTemplate">
            <view class="option-icon">📋</view>
            <view class="option-content">
              <view class="option-title">从模板创建</view>
              <view class="option-desc">选择专业模板快速创建</view>
            </view>
          </view>
          <view class="create-option" @click="createFromScratch">
            <view class="option-icon">✍️</view>
            <view class="option-content">
              <view class="option-title">自定义创建</view>
              <view class="option-desc">完全自定义您的计划</view>
            </view>
          </view>
        </view>
        <view class="popup-cancel" @click="hideCreateOptions">
          <text>取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getTrainingPlanPage, calculateProgress } from '@/apis/trainingPlan.js'
import { getCurrentUser } from '@/utils/auth.js'

export default {
  components: {},
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
      hasMore: false,
      showCreatePopup: false
    }
  },
  
  onLoad() {
    this.loadPlanList()
  },
  
  methods: {
    // 加载训练计划列表
    async loadPlanList() {
      const userInfo = getCurrentUser()
      if (!userInfo || !userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }
      
      try {
        uni.showLoading({ title: '加载中...' })
        
        const res = await getTrainingPlanPage({
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          userId: userInfo.id,
          status: this.currentStatus
        })
        
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
        uni.hideLoading()
      } catch (err) {
        uni.hideLoading()
        uni.showToast({
          title: err?.message || '加载失败',
          icon: 'none'
        })
      }
    },
    
    // 加载计划进度
    async loadProgress(plan) {
      try {
        const progress = await calculateProgress(plan.id)
        plan.progress = Math.round(progress || 0)
      } catch (err) {
        plan.progress = 0
      }
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
    },
    
    // 显示创建选项
    showCreateOptions() {
      this.showCreatePopup = true
    },
    
    // 隐藏创建选项
    hideCreateOptions() {
      this.showCreatePopup = false
    },
    
    // 从模板创建
    createFromTemplate() {
      this.showCreatePopup = false
      uni.navigateTo({
        url: '/pages/training-plan/template-select'
      })
    },
    
    // 自定义创建
    createFromScratch() {
      this.showCreatePopup = false
      uni.showToast({
        title: '自定义创建功能待开发',
        icon: 'none'
      })
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

/* 浮动按钮 */
.fab-container {
  position: fixed;
  right: 30rpx;
  bottom: 150rpx;
  z-index: 999;
}

.fab-btn {
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
}

.fab-icon {
  font-size: 60rpx;
  color: #fff;
  font-weight: 300;
  line-height: 1;
}

/* 弹窗蒙版 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 9999;
}

/* 创建弹窗 */
.create-popup {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 40rpx 30rpx 60rpx;
  width: 100%;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
  color: #333;
}

.create-options {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.create-option {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background-color: #f9f9f9;
  border-radius: 16rpx;
  transition: all 0.3s;
}

.create-option:active {
  background-color: #f0f0f0;
  transform: scale(0.98);
}

.option-icon {
  font-size: 60rpx;
  margin-right: 20rpx;
}

.option-content {
  flex: 1;
}

.option-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.option-desc {
  font-size: 24rpx;
  color: #999;
}

.popup-cancel {
  text-align: center;
  padding: 24rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #666;
}
</style>
