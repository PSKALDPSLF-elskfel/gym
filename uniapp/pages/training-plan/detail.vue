<template>
  <view class="plan-detail-page">
    <!-- 自定义导航栏 -->
    <mod-nav-bar title="计划详情" :showBack="true"></mod-nav-bar>
    
    <view v-if="plan" class="content">
      <!-- 计划基本信息 -->
      <view class="plan-card">
        <view class="plan-header">
          <view class="plan-name">{{ plan.name }}</view>
          <view :class="['plan-status', plan.status === 1 ? 'active' : 'ended']">
            {{ plan.status === 1 ? '进行中' : '已结束' }}
          </view>
        </view>
        
        <view class="plan-info">
          <view class="info-row">
            <text class="label">训练目标</text>
            <text class="value">{{ plan.goal }}</text>
          </view>
          <view class="info-row">
            <text class="label">指导教练</text>
            <text class="value">{{ plan.coachNickname || '暂无' }}</text>
          </view>
          <view class="info-row">
            <text class="label">计划周期</text>
            <text class="value">{{ plan.startDate }} 至 {{ plan.endDate }}</text>
          </view>
          <view v-if="plan.remark" class="info-row">
            <text class="label">备注说明</text>
            <text class="value">{{ plan.remark }}</text>
          </view>
        </view>
        
        <view class="progress-section">
          <view class="progress-header">
            <text class="progress-title">完成进度</text>
            <text class="progress-percent">{{ progress }}%</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progress + '%' }"></view>
          </view>
          <view class="progress-stats">
            <text class="stats-text">已完成 {{ completedCount }}/{{ totalCount }} 项</text>
          </view>
        </view>
      </view>
      
      <!-- 每周训练计划 -->
      <view class="weekly-plan">
        <view class="section-title">每周训练安排</view>
        
        <view 
          v-for="day in weeklyPlan" 
          :key="day.dayOfWeek" 
          class="day-card"
        >
          <view class="day-header">
            <text class="day-name">{{ getDayName(day.dayOfWeek) }}</text>
            <text class="action-count">{{ day.actions.length }} 个动作</text>
          </view>
          
          <view class="action-list">
            <view 
              v-for="action in day.actions" 
              :key="action.id" 
              :class="['action-item', { completed: action.isCompleted === 1 }]"
            >
              <view class="action-header">
                <view class="action-name">
                  <text class="action-icon">{{ action.isCompleted === 1 ? '✓' : '○' }}</text>
                  <text class="name-text">{{ action.actionName }}</text>
                </view>
                <view 
                  v-if="plan.status === 1"
                  class="action-btn" 
                  @click.stop="toggleCompletion(action)"
                >
                  {{ action.isCompleted === 1 ? '取消完成' : '标记完成' }}
                </view>
              </view>
              
              <view class="action-params">
                <text v-if="action.sets" class="param-item">{{ action.sets }}组</text>
                <text v-if="action.reps" class="param-item">× {{ action.reps }}次</text>
                <text v-if="action.weight" class="param-item">{{ action.weight }}kg</text>
                <text v-if="action.duration" class="param-item">{{ action.duration }}分钟</text>
                <text v-if="action.restTime" class="param-item">休息{{ action.restTime }}秒</text>
              </view>
              
              <view v-if="action.isCompleted === 1 && (action.actualSets || action.actualReps)" class="actual-params">
                <text class="actual-label">实际完成:</text>
                <text v-if="action.actualSets" class="param-item">{{ action.actualSets }}组</text>
                <text v-if="action.actualReps" class="param-item">× {{ action.actualReps }}次</text>
              </view>
              
              <view v-if="action.executionNote" class="execution-note">
                <text class="note-label">感受:</text>
                <text class="note-text">{{ action.executionNote }}</text>
              </view>
              
              <view v-if="action.isCompleted === 1 && action.completeTime" class="complete-time">
                完成时间: {{ formatDateTime(action.completeTime) }}
              </view>
            </view>
          </view>
        </view>
        
        <view v-if="weeklyPlan.length === 0" class="empty-plan">
          <text>暂无训练安排</text>
        </view>
      </view>
    </view>
    
    <!-- 标记完成弹窗 -->
    <view v-if="showCompletionPopup" class="popup-mask" @click="closeCompletionPopup">
      <view class="completion-popup" @click.stop>
        <view class="popup-header">
          <text class="popup-title">标记完成</text>
          <text class="close-btn" @click="closeCompletionPopup">×</text>
        </view>
        
        <view class="popup-content">
          <view class="form-item">
            <text class="form-label">实际组数</text>
            <input 
              v-model.number="completionForm.actualSets" 
              type="number" 
              placeholder="请输入实际组数"
              class="form-input"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">实际次数</text>
            <input 
              v-model.number="completionForm.actualReps" 
              type="number" 
              placeholder="请输入实际次数"
              class="form-input"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">训练感受</text>
            <textarea 
              v-model="completionForm.executionNote" 
              placeholder="记录您的训练感受..."
              class="form-textarea"
              maxlength="200"
            />
          </view>
        </view>
        
        <view class="popup-footer">
          <button class="cancel-btn" @click="closeCompletionPopup">取消</button>
          <button class="confirm-btn" @click="confirmCompletion">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getTrainingPlanById, updateDetailCompletion, calculateProgress } from '@/apis/trainingPlan.js'

export default {
  data() {
    return {
      planId: null,
      plan: null,
      weeklyPlan: [],
      progress: 0,
      completedCount: 0,
      totalCount: 0,
      currentAction: null,
      completionForm: {
        actualSets: null,
        actualReps: null,
        executionNote: ''
      },
      
      // 弹窗显示状态
      showCompletionPopup: false
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.planId = options.id
      this.loadPlanDetail()
    }
  },
  
  methods: {
    // 加载计划详情
    async loadPlanDetail() {
      try {
        uni.showLoading({ title: '加载中...' })
        
        const res = await getTrainingPlanById(this.planId)
        this.plan = res
        this.processWeeklyPlan(res.details || [])
        await this.loadProgress()
        uni.hideLoading()
      } catch (err) {
        uni.hideLoading()
        uni.showToast({
          title: err?.message || '加载失败',
          icon: 'none'
        })
      }
    },
    
    // 处理每周训练计划
    processWeeklyPlan(details) {
      const weekMap = {}
      
      details.forEach(detail => {
        const day = detail.dayOfWeek
        if (!weekMap[day]) {
          weekMap[day] = {
            dayOfWeek: day,
            actions: []
          }
        }
        weekMap[day].actions.push(detail)
      })
      
      // 按星期排序
      this.weeklyPlan = Object.values(weekMap).sort((a, b) => a.dayOfWeek - b.dayOfWeek)
      
      // 计算完成统计
      this.totalCount = details.length
      this.completedCount = details.filter(d => d.isCompleted === 1).length
    },
    
    // 加载进度
    async loadProgress() {
      try {
        const progress = await calculateProgress(this.planId)
        this.progress = Math.round(progress || 0)
      } catch (err) {
        // 加载失败晃无，穿行报告
      }
    },
    
    // 切换完成状态
    toggleCompletion(action) {
      if (action.isCompleted === 1) {
        // 取消完成
        this.updateCompletion(action.id, 0, null, null, null)
      } else {
        // 标记完成 - 弹出表单
        this.currentAction = action
        this.completionForm = {
          actualSets: action.sets,
          actualReps: action.reps,
          executionNote: ''
        }
        this.showCompletionPopup = true
      }
    },
    
    // 确认完成
    confirmCompletion() {
      if (!this.currentAction) return
      
      this.updateCompletion(
        this.currentAction.id,
        1,
        this.completionForm.actualSets,
        this.completionForm.actualReps,
        this.completionForm.executionNote
      )
      
      this.closeCompletionPopup()
    },
    
    // 关闭弹窗
    closeCompletionPopup() {
      this.showCompletionPopup = false
      this.currentAction = null
    },
    
    // 更新完成状态
    async updateCompletion(detailId, isCompleted, actualSets, actualReps, executionNote) {
      try {
        await updateDetailCompletion(detailId, {
          isCompleted,
          actualSets,
          actualReps,
          executionNote
        })
        
        uni.showToast({
          title: isCompleted === 1 ? '已标记完成' : '已取消完成',
          icon: 'success'
        })
        this.loadPlanDetail()
      } catch (err) {
        uni.showToast({
          title: err?.message || '操作失败',
          icon: 'none'
        })
      }
    },
    
    // 获取星期名称
    getDayName(dayOfWeek) {
      const days = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
      return days[dayOfWeek] || ''
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.plan-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content {
  padding: 20rpx;
}

.plan-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.plan-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
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
  margin-bottom: 30rpx;
}

.info-row {
  display: flex;
  margin-bottom: 20rpx;
}

.info-row .label {
  width: 160rpx;
  font-size: 28rpx;
  color: #999;
}

.info-row .value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.progress-section {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12rpx;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.progress-title {
  font-size: 28rpx;
  color: #fff;
}

.progress-percent {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
}

.progress-bar {
  height: 16rpx;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 8rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.progress-fill {
  height: 100%;
  background-color: #fff;
  border-radius: 8rpx;
  transition: width 0.3s;
}

.progress-stats {
  text-align: right;
}

.stats-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

.weekly-plan {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.day-card {
  margin-bottom: 30rpx;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  border-radius: 12rpx;
  margin-bottom: 16rpx;
}

.day-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
}

.action-count {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

.action-list {
  padding-left: 20rpx;
}

.action-item {
  padding: 20rpx;
  background-color: #f9fafb;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  border-left: 4rpx solid #e5e7eb;
}

.action-item.completed {
  background-color: #ecfdf5;
  border-left-color: #10b981;
}

.action-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.action-name {
  display: flex;
  align-items: center;
  flex: 1;
}

.action-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
  color: #3b82f6;
}

.action-item.completed .action-icon {
  color: #10b981;
}

.name-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.action-btn {
  padding: 8rpx 20rpx;
  background-color: #3b82f6;
  color: #fff;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.action-item.completed .action-btn {
  background-color: #e5e7eb;
  color: #6b7280;
}

.action-params, .actual-params {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.param-item {
  padding: 6rpx 16rpx;
  background-color: #fff;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;
}

.actual-label {
  font-size: 24rpx;
  color: #10b981;
  margin-right: 8rpx;
}

.execution-note {
  margin-top: 12rpx;
  padding: 12rpx;
  background-color: #fff;
  border-radius: 8rpx;
}

.note-label {
  font-size: 24rpx;
  color: #999;
  margin-right: 8rpx;
}

.note-text {
  font-size: 26rpx;
  color: #666;
}

.complete-time {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #999;
}

.empty-plan {
  text-align: center;
  padding: 100rpx 0;
  font-size: 28rpx;
  color: #999;
}

/* 弹窗样式 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
}

.completion-popup {
  width: 100%;
  background-color: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx;
  animation: slideUp 0.3s ease-out;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.close-btn {
  font-size: 60rpx;
  color: #999;
  line-height: 1;
}

.popup-content {
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.form-input {
  width: 100%;
  padding: 20rpx;
  background-color: #f9fafb;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  background-color: #f9fafb;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.popup-footer {
  display: flex;
  gap: 20rpx;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  padding: 24rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  border: none;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #6b7280;
}

.confirm-btn {
  background-color: #3b82f6;
  color: #fff;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
</style>
