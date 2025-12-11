<template>
  <view class="notification-page">
    <!-- 自定义导航栏 -->
    <mod-nav-bar title="系统通知" :show-back="true"></mod-nav-bar>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view 
          v-for="(tab, index) in filterTabs" 
          :key="index"
          class="tab-item"
          :class="{ active: activeTab === tab.value }"
          @click="handleTabChange(tab.value)"
        >
          {{ tab.label }}
        </view>
      </view>
      
      <view class="action-bar" v-if="notifications.length > 0">
        <text class="read-all-btn" @click="handleMarkAllRead">全部已读</text>
      </view>
    </view>

    <!-- 通知列表 -->
    <scroll-view 
      class="notification-list" 
      scroll-y 
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 加载状态 -->
      <view v-if="loading && notifications.length === 0" class="loading-container">
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <!-- 通知列表项 -->
      <view 
        v-for="item in notifications" 
        :key="item.id" 
        class="notification-item"
        :class="{ unread: !item.isRead }"
        @click="handleItemClick(item)"
      >
        <!-- 未读标记 -->
        <view v-if="!item.isRead" class="unread-dot"></view>
        
        <view class="item-content">
          <!-- 图标和标题 -->
          <view class="item-header">
            <view class="icon-wrapper">
              <image 
                v-if="item.icon" 
                :src="item.icon" 
                class="notification-icon"
                mode="aspectFill"
              ></image>
              <text v-else class="icon-placeholder">{{ getIconText(item.notificationType) }}</text>
            </view>
            
            <view class="header-info">
              <view class="title-row">
                <text class="notification-title">{{ item.title }}</text>
                <view class="priority-badge" v-if="item.priority === 2">
                  <text>重要</text>
                </view>
              </view>
              <text class="notification-type">{{ item.notificationTypeDesc || '通知' }}</text>
            </view>
          </view>

          <!-- 内容 -->
          <view class="item-body">
            <text class="notification-content">{{ item.content }}</text>
          </view>

          <!-- 底部信息 -->
          <view class="item-footer">
            <text class="time">{{ formatTime(item.sendTime) }}</text>
            <text class="delete-btn" @click.stop="handleDelete(item)">删除</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="!loading && notifications.length === 0" class="empty-container">
        <image src="/static/images/empty.png" class="empty-icon" mode="aspectFit"></image>
        <text class="empty-text">暂无通知</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="notifications.length > 0" class="load-more">
        <uni-load-more :status="loadMoreStatus"></uni-load-more>
      </view>
    </scroll-view>

    <!-- 通知详情弹窗 -->
    <uni-popup ref="detailPopup" type="bottom" :safe-area="true">
      <view class="detail-popup">
        <view class="popup-header">
          <text class="popup-title">通知详情</text>
          <text class="close-btn" @click="closeDetailPopup">✕</text>
        </view>
        
        <scroll-view class="popup-content" scroll-y>
          <view v-if="selectedNotification" class="detail-content">
            <view class="detail-title">{{ selectedNotification.title }}</view>
            <view class="detail-meta">
              <text class="meta-item">{{ selectedNotification.notificationTypeDesc }}</text>
              <text class="meta-divider">·</text>
              <text class="meta-item">{{ formatTime(selectedNotification.sendTime) }}</text>
            </view>
            <view class="detail-text">{{ selectedNotification.content }}</view>
          </view>
        </scroll-view>
        
        <view class="popup-footer">
          <button class="popup-btn" @click="handleJumpToRelated">查看详情</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getMyNotifications, markAsRead, markAllAsRead, deleteNotification, getUnreadCount } from '@/apis/notification.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'

export default {
  components: {
    ModNavBar
  },
  
  data() {
    return {
      // 筛选标签
      filterTabs: [
        { label: '全部', value: '' },
        { label: '未读', value: 0 },
        { label: '已读', value: 1 }
      ],
      activeTab: '', // 当前激活的标签
      
      // 通知列表
      notifications: [],
      
      // 分页参数
      current: 1,
      size: 10,
      total: 0,
      
      // 加载状态
      loading: false,
      refreshing: false,
      loadMoreStatus: 'more', // more/loading/noMore
      
      // 选中的通知
      selectedNotification: null
    }
  },
  
  onLoad() {
    this.loadNotifications()
  },
  
  onShow() {
    // 页面显示时刷新未读数量
    this.updateUnreadCount()
  },
  
  methods: {
    /**
     * 加载通知列表
     */
    async loadNotifications(isRefresh = false) {
      if (this.loading) return
      
      this.loading = true
      
      try {
        const params = {
          current: this.current,
          size: this.size
        }
        
        // 添加筛选条件
        if (this.activeTab !== '') {
          params.isRead = this.activeTab
        }
        
        const result = await getMyNotifications(params, { showDefaultMsg: false })
        
        if (isRefresh) {
          this.notifications = result.records || []
        } else {
          this.notifications = [...this.notifications, ...(result.records || [])]
        }
        
        this.total = result.total || 0
        
        // 更新加载更多状态
        if (this.notifications.length >= this.total) {
          this.loadMoreStatus = 'noMore'
        } else {
          this.loadMoreStatus = 'more'
        }
      } catch (error) {
        console.error('加载通知列表失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    
    /**
     * 下拉刷新
     */
    onRefresh() {
      this.refreshing = true
      this.current = 1
      this.loadNotifications(true)
    },
    
    /**
     * 加载更多
     */
    loadMore() {
      if (this.loadMoreStatus !== 'more') return
      if (this.loading) return
      
      this.loadMoreStatus = 'loading'
      this.current++
      this.loadNotifications()
    },
    
    /**
     * 切换筛选标签
     */
    handleTabChange(value) {
      this.activeTab = value
      this.current = 1
      this.notifications = []
      this.loadNotifications(true)
    },
    
    /**
     * 点击通知项
     */
    async handleItemClick(item) {
      // 标记为已读
      if (!item.isRead) {
        try {
          await markAsRead(item.id, { showDefaultMsg: false })
          item.isRead = true
          this.updateUnreadCount()
        } catch (error) {
          console.error('标记已读失败:', error)
        }
      }
      
      // 显示详情
      this.selectedNotification = item
      this.$refs.detailPopup.open()
    },
    
    /**
     * 关闭详情弹窗
     */
    closeDetailPopup() {
      this.$refs.detailPopup.close()
    },
    
    /**
     * 跳转到相关页面
     */
    handleJumpToRelated() {
      if (!this.selectedNotification) return
      
      const { relatedType, relatedId } = this.selectedNotification
      
      // 根据业务类型跳转
      if (relatedType === 'COURSE_BOOKING') {
        uni.navigateTo({
          url: `/pages/booking/detail?id=${relatedId}`
        })
      } else if (relatedType === 'COURSE') {
        uni.navigateTo({
          url: `/pages/course/detail?id=${relatedId}`
        })
      } else if (relatedType === 'EQUIPMENT') {
        uni.navigateTo({
          url: `/pages/equipment/detail?id=${relatedId}`
        })
      }
      
      this.closeDetailPopup()
    },
    
    /**
     * 全部标记为已读
     */
    handleMarkAllRead() {
      uni.showModal({
        title: '提示',
        content: '确定将所有通知标记为已读吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await markAllAsRead()
              
              // 更新本地数据
              this.notifications.forEach(item => {
                item.isRead = true
              })
              
              this.updateUnreadCount()
              
              uni.showToast({
                title: '操作成功',
                icon: 'success'
              })
            } catch (error) {
              console.error('标记失败:', error)
            }
          }
        }
      })
    },
    
    /**
     * 删除通知
     */
    handleDelete(item) {
      uni.showModal({
        title: '提示',
        content: '确定要删除这条通知吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await deleteNotification(item.id)
              
              // 从列表中移除
              const index = this.notifications.findIndex(n => n.id === item.id)
              if (index > -1) {
                this.notifications.splice(index, 1)
              }
              
              this.updateUnreadCount()
              
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              })
            } catch (error) {
              console.error('删除失败:', error)
            }
          }
        }
      })
    },
    
    /**
     * 更新未读数量（通知父页面）
     */
    async updateUnreadCount() {
      try {
        const count = await getUnreadCount({ showDefaultMsg: false })
        
        // 更新TabBar角标
        if (count > 0) {
          uni.setTabBarBadge({
            index: 3, // 我的页面的索引
            text: count > 99 ? '99+' : count.toString()
          })
        } else {
          uni.removeTabBarBadge({
            index: 3
          })
        }
      } catch (error) {
        console.error('获取未读数量失败:', error)
      }
    },
    
    /**
     * 获取图标文字
     */
    getIconText(type) {
      const iconMap = {
        'SYSTEM': '系统',
        'BOOKING': '预约',
        'COURSE': '课程',
        'EQUIPMENT': '器材',
        'MEMBERSHIP': '会员'
      }
      return iconMap[type] || '通知'
    },
    
    /**
     * 格式化时间
     */
    formatTime(timeStr) {
      if (!timeStr) return ''
      
      const time = new Date(timeStr)
      const now = new Date()
      const diff = now - time
      
      // 一分钟内
      if (diff < 60000) {
        return '刚刚'
      }
      
      // 一小时内
      if (diff < 3600000) {
        return `${Math.floor(diff / 60000)}分钟前`
      }
      
      // 今天
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      if (time >= today) {
        return `今天 ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`
      }
      
      // 昨天
      const yesterday = new Date(today - 86400000)
      if (time >= yesterday) {
        return `昨天 ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`
      }
      
      // 其他
      return `${time.getMonth() + 1}月${time.getDate()}日`
    }
  }
}
</script>

<style lang="scss" scoped>
.notification-page {
  width: 100%;
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

// 筛选栏
.filter-bar {
  background-color: #fff;
  padding: 20rpx 30rpx;
  border-bottom: 1px solid #eee;
  
  .filter-tabs {
    display: flex;
    gap: 40rpx;
    margin-bottom: 20rpx;
    
    .tab-item {
      font-size: 28rpx;
      color: #666;
      padding: 10rpx 0;
      position: relative;
      transition: all 0.3s;
      
      &.active {
        color: #FF6B00;
        font-weight: 500;
        
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 4rpx;
          background-color: #FF6B00;
          border-radius: 2rpx;
        }
      }
    }
  }
  
  .action-bar {
    display: flex;
    justify-content: flex-end;
    
    .read-all-btn {
      font-size: 26rpx;
      color: #FF6B00;
    }
  }
}

// 通知列表
.notification-list {
  flex: 1;
  padding: 20rpx;
}

.notification-item {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  position: relative;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  
  &.unread {
    background-color: #fffbf5;
    
    .unread-dot {
      position: absolute;
      top: 30rpx;
      right: 30rpx;
      width: 16rpx;
      height: 16rpx;
      background-color: #ff4d4f;
      border-radius: 50%;
    }
  }
}

.item-content {
  .item-header {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20rpx;
    
    .icon-wrapper {
      width: 80rpx;
      height: 80rpx;
      margin-right: 20rpx;
      flex-shrink: 0;
      
      .notification-icon {
        width: 100%;
        height: 100%;
        border-radius: 12rpx;
      }
      
      .icon-placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        font-size: 28rpx;
        font-weight: 500;
        border-radius: 12rpx;
      }
    }
    
    .header-info {
      flex: 1;
      
      .title-row {
        display: flex;
        align-items: center;
        gap: 16rpx;
        margin-bottom: 8rpx;
        
        .notification-title {
          font-size: 32rpx;
          font-weight: 500;
          color: #333;
          flex: 1;
        }
        
        .priority-badge {
          padding: 4rpx 12rpx;
          background-color: #ff4d4f;
          border-radius: 8rpx;
          
          text {
            font-size: 20rpx;
            color: #fff;
          }
        }
      }
      
      .notification-type {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
  
  .item-body {
    margin-bottom: 20rpx;
    
    .notification-content {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
  }
  
  .item-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .time {
      font-size: 24rpx;
      color: #999;
    }
    
    .delete-btn {
      font-size: 26rpx;
      color: #ff4d4f;
    }
  }
}

// 空状态
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .empty-icon {
    width: 300rpx;
    height: 300rpx;
    margin-bottom: 30rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

// 加载状态
.loading-container {
  padding: 100rpx 0;
}

.load-more {
  padding: 20rpx 0;
}

// 详情弹窗
.detail-popup {
  background-color: #fff;
  border-radius: 32rpx 32rpx 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 40rpx 30rpx 20rpx;
    border-bottom: 1px solid #eee;
    
    .popup-title {
      font-size: 32rpx;
      font-weight: 500;
      color: #333;
    }
    
    .close-btn {
      font-size: 40rpx;
      color: #999;
      width: 60rpx;
      text-align: center;
    }
  }
  
  .popup-content {
    flex: 1;
    padding: 30rpx;
    
    .detail-content {
      .detail-title {
        font-size: 36rpx;
        font-weight: 500;
        color: #333;
        margin-bottom: 20rpx;
      }
      
      .detail-meta {
        display: flex;
        align-items: center;
        gap: 16rpx;
        margin-bottom: 30rpx;
        
        .meta-item {
          font-size: 24rpx;
          color: #999;
        }
        
        .meta-divider {
          color: #ddd;
        }
      }
      
      .detail-text {
        font-size: 28rpx;
        color: #666;
        line-height: 1.8;
        white-space: pre-wrap;
      }
    }
  }
  
  .popup-footer {
    padding: 30rpx;
    border-top: 1px solid #eee;
    
    .popup-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      font-size: 32rpx;
      border-radius: 44rpx;
      border: none;
    }
  }
}
</style>
