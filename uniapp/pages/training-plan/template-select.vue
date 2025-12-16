<template>
  <view class="template-select-container">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="header-content">
        <view class="title">选择训练方案模板</view>
        <view class="subtitle">基于专业模板快速创建您的训练计划</view>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="filter-item">
        <picker mode="selector" :range="goalOptions" range-key="label" @change="onGoalChange">
          <view class="filter-button">
            <text>{{ selectedGoal || '训练目标' }}</text>
            <text class="icon">▼</text>
          </view>
        </picker>
      </view>
      <view class="filter-item">
        <picker mode="selector" :range="difficultyOptions" range-key="label" @change="onDifficultyChange">
          <view class="filter-button">
            <text>{{ selectedDifficulty || '难度' }}</text>
            <text class="icon">▼</text>
          </view>
        </picker>
      </view>
      <view class="filter-item" @click="resetFilter">
        <view class="filter-button reset">
          <text>重置</text>
        </view>
      </view>
    </view>

    <!-- 模板列表 -->
    <view class="template-list">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>

      <view v-else-if="templates.length === 0" class="empty">
        <image src="/static/empty.png" class="empty-img" mode="aspectFit"></image>
        <text class="empty-text">暂无可用模板</text>
      </view>

      <view v-else>
        <view 
          v-for="template in templates" 
          :key="template.id" 
          class="template-card"
          @click="viewTemplate(template)"
        >
          <!-- 模板标签 -->
          <view class="template-header">
            <view class="template-title">{{ template.name }}</view>
            <view class="template-tags">
              <text class="tag tag-type" :class="{'system': !template.coachId, 'coach': template.coachId}">
                {{ template.coachId ? '教练' : '系统' }}
              </text>
              <text class="tag tag-difficulty" :class="'difficulty-' + template.difficulty">
                {{ getDifficultyText(template.difficulty) }}
              </text>
            </view>
          </view>

          <!-- 模板信息 -->
          <view class="template-info">
            <view class="info-row">
              <view class="info-item">
                <text class="label">目标：</text>
                <text class="value">{{ template.goal }}</text>
              </view>
              <view class="info-item">
                <text class="label">周期：</text>
                <text class="value">{{ template.durationDays }}天</text>
              </view>
            </view>
            <view class="info-row">
              <view class="info-item">
                <text class="label">动作数：</text>
                <text class="value">{{ template.totalExercises }}个</text>
              </view>
            </view>
          </view>

          <!-- 模板描述 -->
          <view class="template-desc" v-if="template.description">
            {{ template.description }}
          </view>

          <!-- 选择按钮 -->
          <view class="template-footer">
            <button class="select-btn" @click.stop="selectTemplate(template)">
              选择此模板
            </button>
          </view>
        </view>
      </view>
    </view>

    <!-- 分页 -->
    <view class="pagination" v-if="total > pageSize">
      <view class="page-info">第{{ currentPage }}/{{ totalPages }}页，共{{ total }}条</view>
      <view class="page-buttons">
        <button class="page-btn" :disabled="currentPage === 1" @click="prevPage">上一页</button>
        <button class="page-btn" :disabled="currentPage === totalPages" @click="nextPage">下一页</button>
      </view>
    </view>

    <!-- 模板详情弹窗 -->
    <view class="popup-mask" v-if="showDetailPopup" @click="closeDetail">
      <view class="popup-container" @click.stop>
        <view class="popup-header">
          <text class="popup-title">模板详情</text>
          <text class="popup-close" @click="closeDetail">×</text>
        </view>

        <scroll-view class="popup-content" scroll-y v-if="currentTemplate">
          <!-- 基本信息 -->
          <view class="detail-section">
            <view class="section-title">{{ currentTemplate.name }}</view>
            <view class="detail-info">
              <view class="detail-row">
                <text class="detail-label">训练目标：</text>
                <text class="detail-value">{{ currentTemplate.goal }}</text>
              </view>
              <view class="detail-row">
                <text class="detail-label">难度等级：</text>
                <text class="detail-value tag" :class="'difficulty-' + currentTemplate.difficulty">
                  {{ getDifficultyText(currentTemplate.difficulty) }}
                </text>
              </view>
              <view class="detail-row">
                <text class="detail-label">建议周期：</text>
                <text class="detail-value">{{ currentTemplate.durationDays }}天</text>
              </view>
              <view class="detail-row">
                <text class="detail-label">总动作数：</text>
                <text class="detail-value">{{ currentTemplate.totalExercises }}个</text>
              </view>
              <view class="detail-row" v-if="currentTemplate.description">
                <text class="detail-label">模板说明：</text>
                <text class="detail-value">{{ currentTemplate.description }}</text>
              </view>
            </view>
          </view>

          <!-- 训练动作明细 -->
          <view class="detail-section" v-if="templateDetails.length > 0">
            <view class="section-title">训练动作安排</view>
            <view class="exercise-list">
              <view v-for="(detail, index) in templateDetails" :key="index" class="exercise-item">
                <view class="exercise-header">
                  <text class="exercise-day">星期{{ getWeekText(detail.dayOfWeek) }}</text>
                  <text class="exercise-name">{{ detail.actionName }}</text>
                </view>
                <view class="exercise-params">
                  <text v-if="detail.sets">{{ detail.sets }}组</text>
                  <text v-if="detail.reps">×{{ detail.reps }}次</text>
                  <text v-if="detail.weight">{{ detail.weight }}kg</text>
                  <text v-if="detail.duration">{{ detail.duration }}分钟</text>
                  <text v-if="detail.restTime">休{{ detail.restTime }}秒</text>
                </view>
                <view class="exercise-desc" v-if="detail.description">
                  <text>{{ detail.description }}</text>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>

        <!-- 底部按钮 -->
        <view class="popup-footer">
          <button class="create-btn" @click="confirmCreate">使用此模板创建计划</button>
        </view>
      </view>
    </view>

    <!-- 创建计划名称输入弹窗 -->
    <view class="popup-mask" v-if="showNamePopup" @click="cancelCreate">
      <view class="name-popup" @click.stop>
        <view class="name-popup-title">创建训练计划</view>
        <view class="name-popup-content">
          <input 
            v-model="planName" 
            class="name-input" 
            placeholder="请输入计划名称"
            placeholder-style="color: #999"
          />
        </view>
        <view class="name-popup-footer">
          <button class="name-btn cancel" @click="cancelCreate">取消</button>
          <button class="name-btn confirm" @click="submitCreate">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getTemplatePage, getTemplateById, createPlanFromTemplate } from '@/apis/trainingPlan.js'
import { getCurrentUser } from '@/utils/auth.js'

export default {
  components: {},
  data() {
    return {
      loading: false,
      templates: [],
      currentTemplate: null,
      templateDetails: [],
      planName: '',
      showDetailPopup: false,
      showNamePopup: false,
      
      // 筛选条件
      goalOptions: [
        { label: '全部目标', value: '' },
        { label: '减脂', value: '减脂' },
        { label: '增肌', value: '增肌' },
        { label: '塑形', value: '塑形' },
        { label: '康复', value: '康复' }
      ],
      difficultyOptions: [
        { label: '全部难度', value: '' },
        { label: '初级', value: 1 },
        { label: '中级', value: 2 },
        { label: '高级', value: 3 }
      ],
      selectedGoal: '',
      selectedDifficulty: '',
      
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  
  onLoad() {
    this.loadTemplates()
  },
  
  methods: {
    // 加载模板列表
    async loadTemplates() {
      this.loading = true
      try {
        const params = {
          currentPage: this.currentPage,
          pageSize: this.pageSize,
          status: 1  // 只查询启用的模板
        }
        
        if (this.selectedGoal) {
          params.goal = this.selectedGoal
        }
        if (this.selectedDifficulty) {
          params.difficulty = this.selectedDifficulty
        }
        
        const res = await getTemplatePage(params)
        this.templates = res.records || []
        this.total = res.total || 0
      } catch (err) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    
    // 目标筛选
    onGoalChange(e) {
      this.selectedGoal = this.goalOptions[e.detail.value].value
      this.currentPage = 1
      this.loadTemplates()
    },
    
    // 难度筛选
    onDifficultyChange(e) {
      this.selectedDifficulty = this.difficultyOptions[e.detail.value].value
      this.currentPage = 1
      this.loadTemplates()
    },
    
    // 重置筛选
    resetFilter() {
      this.selectedGoal = ''
      this.selectedDifficulty = ''
      this.currentPage = 1
      this.loadTemplates()
    },
    
    // 查看模板详情
    async viewTemplate(template) {
      try {
        const res = await getTemplateById(template.id)
        this.currentTemplate = res
        this.templateDetails = res.details || []
        this.showDetailPopup = true
      } catch (err) {
        uni.showToast({ title: '加载详情失败', icon: 'none' })
      }
    },
    
    // 关闭详情
    closeDetail() {
      this.showDetailPopup = false
    },
    
    // 选择模板
    selectTemplate(template) {
      this.currentTemplate = template
      this.planName = template.name + ' - 我的计划'
      this.showNamePopup = true
    },
    
    // 确认创建
    confirmCreate() {
      this.showDetailPopup = false
      this.planName = this.currentTemplate.name + ' - 我的计划'
      this.showNamePopup = true
    },
    
    // 取消创建
    cancelCreate() {
      this.showNamePopup = false
      this.planName = ''
    },
    
    // 提交创建
    async submitCreate() {
      if (!this.planName.trim()) {
        uni.showToast({ title: '请输入计划名称', icon: 'none' })
        return
      }
      
      const userInfo = getCurrentUser()
      if (!userInfo || !userInfo.id) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      
      try {
        uni.showLoading({ title: '创建中...' })
        
        const res = await createPlanFromTemplate(
          this.currentTemplate.id,
          userInfo.id,
          this.planName
        )
        
        uni.hideLoading()
        uni.showToast({ title: '创建成功', icon: 'success' })
        this.showNamePopup = false
        
        // 跳转到计划详情
        setTimeout(() => {
          uni.navigateTo({
            url: `/pages/training-plan/detail?id=${res.id}`
          })
        }, 1500)
      } catch (err) {
        uni.hideLoading()
        uni.showToast({ title: err?.message || '创建失败', icon: 'none' })
      }
    },
    
    // 上一页
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadTemplates()
      }
    },
    
    // 下一页
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadTemplates()
      }
    },
    
    // 获取难度文本
    getDifficultyText(difficulty) {
      const map = { 1: '初级', 2: '中级', 3: '高级' }
      return map[difficulty] || '未知'
    },
    
    // 获取星期文本
    getWeekText(day) {
      const map = { 1: '一', 2: '二', 3: '三', 4: '四', 5: '五', 6: '六', 7: '日' }
      return map[day] || ''
    }
  }
}
</script>

<style scoped>
.template-select-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20rpx;
}

/* 顶部标题 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx 30rpx;
}

.header-content {
  color: #fff;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.subtitle {
  font-size: 26rpx;
  opacity: 0.9;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  padding: 20rpx 30rpx;
  background-color: #fff;
  gap: 20rpx;
}

.filter-item {
  flex: 1;
}

.filter-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.filter-button.reset {
  background-color: #667eea;
  color: #fff;
  justify-content: center;
}

.icon {
  font-size: 24rpx;
  margin-left: 10rpx;
}

/* 模板列表 */
.template-list {
  padding: 20rpx 30rpx;
}

.loading, .empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-img {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  display: block;
  font-size: 28rpx;
}

/* 模板卡片 */
.template-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.template-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.template-tags {
  display: flex;
  gap: 10rpx;
}

.tag {
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
  font-size: 22rpx;
}

.tag-type.system {
  background-color: #fef3e7;
  color: #f39c12;
}

.tag-type.coach {
  background-color: #e8f5e9;
  color: #4caf50;
}

.tag-difficulty.difficulty-1 {
  background-color: #e8f5e9;
  color: #4caf50;
}

.tag-difficulty.difficulty-2 {
  background-color: #fff3e0;
  color: #ff9800;
}

.tag-difficulty.difficulty-3 {
  background-color: #ffebee;
  color: #f44336;
}

/* 模板信息 */
.template-info {
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 12rpx;
  gap: 30rpx;
}

.info-item {
  display: flex;
  font-size: 26rpx;
}

.label {
  color: #999;
}

.value {
  color: #333;
  font-weight: 500;
}

/* 模板描述 */
.template-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

/* 选择按钮 */
.template-footer {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.select-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;
}

/* 分页 */
.pagination {
  padding: 30rpx;
  text-align: center;
}

.page-info {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.page-buttons {
  display: flex;
  justify-content: center;
  gap: 20rpx;
}

.page-btn {
  padding: 16rpx 40rpx;
  background-color: #fff;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  font-size: 26rpx;
}

.page-btn[disabled] {
  opacity: 0.5;
}

/* 弹窗遮罩层 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 999;
}

/* 弹窗样式 */
.popup-container {
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 80vh;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
}

.popup-close {
  font-size: 50rpx;
  color: #999;
  line-height: 1;
}

.popup-content {
  flex: 1;
  padding: 30rpx;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
}

.detail-info {
  background-color: #f9f9f9;
  padding: 20rpx;
  border-radius: 12rpx;
}

.detail-row {
  display: flex;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  color: #666;
  min-width: 150rpx;
}

.detail-value {
  flex: 1;
  color: #333;
}

/* 训练动作列表 */
.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.exercise-item {
  background-color: #f9f9f9;
  padding: 20rpx;
  border-radius: 12rpx;
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.exercise-day {
  font-size: 24rpx;
  color: #667eea;
  background-color: #e8eaf6;
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
}

.exercise-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.exercise-params {
  display: flex;
  gap: 20rpx;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.exercise-desc {
  font-size: 24rpx;
  color: #999;
  line-height: 1.5;
}

.popup-footer {
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.create-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;
}

/* 名称输入弹窗 */
.popup-mask .name-popup {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  width: 600rpx;
  margin: auto;
}

.name-popup-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
}

.name-popup-content {
  margin-bottom: 30rpx;
}

.name-input {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.name-popup-footer {
  display: flex;
  gap: 20rpx;
}

.name-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;
}

.name-btn.cancel {
  background-color: #f5f5f5;
  color: #666;
}

.name-btn.confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
</style>
