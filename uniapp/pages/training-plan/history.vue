<template>
  <view class="history-page">
    <!-- 自定义导航栏 -->
    <mod-nav-bar title="训练历史" :showBack="true"></mod-nav-bar>
    
    <view class="content">
      <!-- 历史记录列表 -->
      <view v-if="historyList.length > 0" class="history-list">
        <view 
          v-for="item in historyList" 
          :key="item.id" 
          class="history-item"
        >
          <view class="item-header">
            <text class="action-name">{{ item.actionName }}</text>
            <text class="complete-date">{{ formatDate(item.completeTime) }}</text>
          </view>
          
          <view class="item-params">
            <view v-if="item.actualSets || item.sets" class="param-row">
              <text class="param-label">组数:</text>
              <text class="param-value">{{ item.actualSets || item.sets }}</text>
            </view>
            <view v-if="item.actualReps || item.reps" class="param-row">
              <text class="param-label">次数:</text>
              <text class="param-value">{{ item.actualReps || item.reps }}</text>
            </view>
            <view v-if="item.weight" class="param-row">
              <text class="param-label">重量:</text>
              <text class="param-value">{{ item.weight }}kg</text>
            </view>
            <view v-if="item.duration" class="param-row">
              <text class="param-label">时长:</text>
              <text class="param-value">{{ item.duration }}分钟</text>
            </view>
          </view>
          
          <view v-if="item.executionNote" class="item-note">
            <text class="note-label">训练感受:</text>
            <text class="note-text">{{ item.executionNote }}</text>
          </view>
        </view>
      </view>
      
      <view v-else class="empty-tip">
        <text class="empty-icon">📝</text>
        <text class="empty-text">暂无训练历史</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getExecutionHistory } from '@/apis/trainingPlan.js'
import { getUserInfo } from '@/utils/auth.js'

export default {
  data() {
    return {
      historyList: []
    }
  },
  
  onLoad() {
    this.loadHistory()
  },
  
  methods: {
    // 加载历史记录
    loadHistory() {
      const userInfo = getUserInfo()
      if (!userInfo || !userInfo.id) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }
      
      uni.showLoading({ title: '加载中...' })
      
      getExecutionHistory({
        userId: userInfo.id,
        limit: 50
      }, {
        success: (res) => {
          uni.hideLoading()
          this.historyList = res || []
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
    
    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content {
  padding: 20rpx;
}

.history-list {
  /* 列表容器 */
}

.history-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.action-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.complete-date {
  font-size: 24rpx;
  color: #999;
}

.item-params {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.param-row {
  flex: 0 0 calc(50% - 10rpx);
  display: flex;
  align-items: center;
  padding: 12rpx 16rpx;
  background-color: #f9fafb;
  border-radius: 8rpx;
}

.param-label {
  font-size: 26rpx;
  color: #666;
  margin-right: 8rpx;
}

.param-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #3b82f6;
}

.item-note {
  padding: 16rpx;
  background-color: #fef3c7;
  border-radius: 8rpx;
  margin-top: 16rpx;
}

.note-label {
  font-size: 24rpx;
  color: #92400e;
  margin-right: 8rpx;
}

.note-text {
  font-size: 26rpx;
  color: #78350f;
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
</style>
