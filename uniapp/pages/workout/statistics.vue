<template>
  <view class="page-wrap">
    <mod-nav-bar title="运动统计"></mod-nav-bar>
    
    <view class="page-content">
      <!-- 汇总数据卡片 -->
      <view class="summary-section">
        <view class="stat-card">
          <text class="stat-value">{{ safeNumber(summary.totalWorkouts, 0) }}</text>
          <text class="stat-label">运动次数</text>
          <text class="fa fa-trophy stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ safeNumber(summary.totalDuration, 0) }}</text>
          <text class="stat-label">总时长(分)</text>
          <text class="fa fa-clock-o stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ safeNumber(summary.totalCalories, 0) }}</text>
          <text class="stat-label">总热量(千卡)</text>
          <text class="fa fa-fire stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ safeNumber(summary.streakDays, 0) }}</text>
          <text class="stat-label">连续天数</text>
          <text class="fa fa-calendar-check-o stat-icon"></text>
        </view>
      </view>
      
      <!-- 运动分类占比 -->
      <view class="chart-section">
        <view class="section-header">
          <text class="section-title">运动类型占比</text>
        </view>
        <view class="percentage-bars">
          <view class="percentage-item">
            <view class="item-header">
              <text class="item-label">有氧运动</text>
              <text class="item-value">{{ safeNumber(summary.cardioPercentage, 0).toFixed(1) }}%</text>
            </view>
            <view class="progress-bar">
              <view class="progress-fill cardio" :style="{ width: safeNumber(summary.cardioPercentage, 0) + '%' }"></view>
            </view>
          </view>
          
          <view class="percentage-item">
            <view class="item-header">
              <text class="item-label">力量训练</text>
              <text class="item-value">{{ safeNumber(summary.strengthPercentage, 0).toFixed(1) }}%</text>
            </view>
            <view class="progress-bar">
              <view class="progress-fill strength" :style="{ width: safeNumber(summary.strengthPercentage, 0) + '%' }"></view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 日期选择器 -->
      <view class="date-filter">
        <view class="filter-tabs">
          <view 
            v-for="tab in dateTabs" 
            :key="tab.value"
            class="tab-item"
            :class="{ active: activeTab === tab.value }"
            @click="switchTab(tab.value)"
          >
            {{ tab.label }}
          </view>
        </view>
      </view>
      
      <!-- 每日数据趋势图 -->
      <view class="chart-section">
        <view class="section-header">
          <text class="section-title">运动时长趋势</text>
        </view>
        <view v-if="dailyStats.length === 0" class="empty-chart">
          <text class="fa fa-line-chart empty-icon"></text>
          <text class="empty-text">暂无运动数据</text>
          <text class="empty-hint">请先添加运动记录</text>
        </view>
        <view v-else class="chart-wrapper">
          <view class="chart-container">
            <qiun-data-charts 
              :key="`duration-chart-${activeTab}`"
              type="line"
              :opts="durationChartOpts"
              :chartData="durationChartData"
              :canvas2d="false"
              :tapLegend="false"
            />
          </view>
        </view>
      </view>
      
      <!-- 热量消耗趋势图 -->
      <view class="chart-section">
        <view class="section-header">
          <text class="section-title">热量消耗趋势</text>
        </view>
        <view v-if="dailyStats.length === 0" class="empty-chart">
          <text class="fa fa-line-chart empty-icon"></text>
          <text class="empty-text">暂无运动数据</text>
          <text class="empty-hint">请先添加运动记录</text>
        </view>
        <view v-else class="chart-wrapper">
          <view class="chart-container">
            <qiun-data-charts 
              :key="`calories-chart-${activeTab}`"
              type="line"
              :opts="caloriesChartOpts"
              :chartData="caloriesChartData"
              :canvas2d="false"
              :tapLegend="false"
            />
          </view>
        </view>
      </view>
      
      <!-- 本周/本月统计 -->
      <view class="period-stats">
        <view class="period-card">
          <text class="period-label">本周运动</text>
          <text class="period-value">{{ safeNumber(summary.weekWorkouts, 0) }}次</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">本月运动</text>
          <text class="period-value">{{ safeNumber(summary.monthWorkouts, 0) }}次</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">平均时长</text>
          <text class="period-value">{{ safeNumber(summary.avgDuration, 0) }}分</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">运动天数</text>
          <text class="period-value">{{ safeNumber(summary.workoutDays, 0) }}天</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getWorkoutStatisticsSummary, getWorkoutDailyStats } from '@/apis/workout.js'

// 汇总数据
const summary = ref({
  totalWorkouts: 0,
  totalDuration: 0,
  totalCalories: 0,
  totalDistance: 0,
  avgDuration: 0,
  workoutDays: 0,
  streakDays: 0,
  weekWorkouts: 0,
  monthWorkouts: 0,
  cardioPercentage: 0,
  strengthPercentage: 0
})

// 每日统计数据
const dailyStats = ref([])

// 日期筛选
const activeTab = ref('week')
const dateTabs = [
  { label: '近7天', value: 'week' },
  { label: '近30天', value: 'month' },
  { label: '近90天', value: 'quarter' }
]

// 时长图表配置
const durationChartOpts = ref({
  color: ['#4facfe'],
  padding: [15, 15, 0, 15],
  enableScroll: false,
  legend: {
    show: false
  },
  xAxis: {
    disableGrid: true,
    fontSize: 10
  },
  yAxis: {
    min: 0,
    fontSize: 10,
    gridType: 'dash'
  },
  extra: {
    line: {
      type: 'curve',
      width: 2,
      activeType: 'hollow'
    }
  }
})

// 热量图表配置
const caloriesChartOpts = ref({
  color: ['#fa709a'],
  padding: [15, 15, 0, 15],
  enableScroll: false,
  legend: {
    show: false
  },
  xAxis: {
    disableGrid: true,
    fontSize: 10
  },
  yAxis: {
    min: 0,
    fontSize: 10,
    gridType: 'dash'
  },
  extra: {
    line: {
      type: 'curve',
      width: 2,
      activeType: 'hollow'
    }
  }
})

// 时长图表数据
const durationChartData = computed(() => {
  if (!dailyStats.value || dailyStats.value.length === 0) {
    return {
      categories: [],
      series: [
        {
          name: '运动时长',
          data: []
        }
      ]
    }
  }
  
  return {
    categories: dailyStats.value.map(item => formatChartDate(item.statDate)),
    series: [
      {
        name: '运动时长(分钟)',
        data: dailyStats.value.map(item => safeNumber(item.totalDuration, 0))
      }
    ]
  }
})

// 热量图表数据
const caloriesChartData = computed(() => {
  if (!dailyStats.value || dailyStats.value.length === 0) {
    return {
      categories: [],
      series: [
        {
          name: '消耗热量',
          data: []
        }
      ]
    }
  }
  
  return {
    categories: dailyStats.value.map(item => formatChartDate(item.statDate)),
    series: [
      {
        name: '消耗热量(千卡)',
        data: dailyStats.value.map(item => safeNumber(item.totalCalories, 0))
      }
    ]
  }
})

/**
 * 安全转换数字
 */
const safeNumber = (value, defaultValue = 0) => {
  if (value === null || value === undefined || value === '') {
    return defaultValue
  }
  const num = Number(value)
  return isNaN(num) ? defaultValue : num
}

/**
 * 获取汇总统计
 */
const fetchSummary = () => {
  getWorkoutStatisticsSummary({
    showDefaultMsg: false,
    onSuccess: (data) => {
      console.log('运动统计汇总数据:', data)
      if (!data) {
        console.warn('未获取到汇总统计数据')
        summary.value = {
          totalWorkouts: 0,
          totalDuration: 0,
          totalCalories: 0,
          totalDistance: 0,
          avgDuration: 0,
          workoutDays: 0,
          streakDays: 0,
          weekWorkouts: 0,
          monthWorkouts: 0,
          cardioPercentage: 0,
          strengthPercentage: 0
        }
        return
      }
      
      // 安全转换所有数值字段
      summary.value = {
        totalWorkouts: safeNumber(data.totalWorkouts, 0),
        totalDuration: safeNumber(data.totalDuration, 0),
        totalCalories: safeNumber(data.totalCalories, 0),
        totalDistance: safeNumber(data.totalDistance, 0),
        avgDuration: safeNumber(data.avgDuration, 0),
        workoutDays: safeNumber(data.workoutDays, 0),
        streakDays: safeNumber(data.streakDays, 0),
        weekWorkouts: safeNumber(data.weekWorkouts, 0),
        monthWorkouts: safeNumber(data.monthWorkouts, 0),
        cardioPercentage: safeNumber(data.cardioPercentage, 0),
        strengthPercentage: safeNumber(data.strengthPercentage, 0)
      }
      console.log('处理后的汇总数据:', summary.value)
    },
    onError: (error) => {
      console.error('获取汇总统计失败:', error)
    }
  })
}

/**
 * 获取每日统计
 */
const fetchDailyStats = () => {
  const today = new Date()
  const endYear = today.getFullYear()
  const endMonth = String(today.getMonth() + 1).padStart(2, '0')
  const endDay = String(today.getDate()).padStart(2, '0')
  const endDate = `${endYear}-${endMonth}-${endDay}`
  
  let days = 7
  if (activeTab.value === 'month') {
    days = 30
  } else if (activeTab.value === 'quarter') {
    days = 90
  }
  
  const startDate = new Date(today.getTime() - days * 24 * 60 * 60 * 1000)
  const startYear = startDate.getFullYear()
  const startMonth = String(startDate.getMonth() + 1).padStart(2, '0')
  const startDay = String(startDate.getDate()).padStart(2, '0')
  const startDateStr = `${startYear}-${startMonth}-${startDay}`
  
  console.log('查询每日统计，日期范围:', startDateStr, '至', endDate)
  
  getWorkoutDailyStats({
    startDate: startDateStr,
    endDate: endDate
  }, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      console.log('每日统计数据:', data)
      if (!data || !Array.isArray(data)) {
        console.warn('未获取到每日统计数据')
        dailyStats.value = []
        return
      }
      
      // 安全转换每日统计数据
      console.log('=== 开始处理每日统计数据 ===')
      console.log('原始数据类型:', typeof data)
      console.log('是否为数组:', Array.isArray(data))
      console.log('数据长度:', data.length)
      
      dailyStats.value = data.map((item, index) => {
        console.log(`--- 第${index + 1}条数据 ---`)
        console.log('原始item:', JSON.stringify(item))
        console.log('statDate类型:', typeof item.statDate)
        console.log('totalDuration值:', item.totalDuration, '类型:', typeof item.totalDuration)
        console.log('totalCalories值:', item.totalCalories, '类型:', typeof item.totalCalories)
        
        const processed = {
          statDate: item.statDate,
          totalDuration: safeNumber(item.totalDuration, 0),
          totalCalories: safeNumber(item.totalCalories, 0),
          totalDistance: safeNumber(item.totalDistance, 0),
          workoutCount: safeNumber(item.workoutCount, 0),
          totalSteps: safeNumber(item.totalSteps, 0),
          cardioDuration: safeNumber(item.cardioDuration, 0),
          strengthDuration: safeNumber(item.strengthDuration, 0),
          flexibilityDuration: safeNumber(item.flexibilityDuration, 0)
        }
        console.log('处理后的数据:', JSON.stringify(processed))
        return processed
      })
      
      console.log('=== 处理完成 ===')
      console.log('dailyStats.value:', dailyStats.value)
      console.log('图表数据 - 时长:', durationChartData.value)
      console.log('图表数据 - 热量:', caloriesChartData.value)
    },
    onError: (error) => {
      console.error('获取每日统计失败:', error)
    }
  })
}

/**
 * 切换日期范围
 */
const switchTab = (tab) => {
  activeTab.value = tab
  fetchDailyStats()
}

/**
 * 格式化图表日期
 */
const formatChartDate = (dateStr) => {
  if (!dateStr) return ''
  
  // 如果是对象，转换为字符串
  if (typeof dateStr === 'object') {
    if (dateStr.year && dateStr.monthValue && dateStr.dayOfMonth) {
      // LocalDate对象格式 {year: 2024, monthValue: 12, dayOfMonth: 24}
      const month = String(dateStr.monthValue).padStart(2, '0')
      const day = String(dateStr.dayOfMonth).padStart(2, '0')
      return `${month}-${day}`
    }
    dateStr = String(dateStr)
  }
  
  // 处理 yyyy-MM-dd 格式的字符串
  const parts = String(dateStr).split('-')
  if (parts.length === 3) {
    const month = parseInt(parts[1])
    const day = parseInt(parts[2])
    return `${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  }
  
  // 兜底处理
  try {
    const date = new Date(String(dateStr).replace(/-/g, '/'))
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}-${day}`
  } catch (e) {
    console.error('日期格式化失败:', dateStr, e)
    return ''
  }
}

// 页面加载
onMounted(() => {
  fetchSummary()
  fetchDailyStats()
})
</script>

<style lang="scss" scoped>
.page-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 60rpx;
}

.summary-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 30rpx;
  
  .stat-card {
    position: relative;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16rpx;
    padding: 30rpx;
    min-height: 160rpx;
    overflow: hidden;
    
    &:nth-child(2) {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &:nth-child(3) {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    &:nth-child(4) {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    }
    
    .stat-value {
      display: block;
      font-size: 48rpx;
      font-weight: bold;
      color: #fff;
      margin-bottom: 10rpx;
    }
    
    .stat-label {
      display: block;
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.9);
    }
    
    .stat-icon {
      position: absolute;
      right: 20rpx;
      bottom: 20rpx;
      font-size: 80rpx;
      color: rgba(255, 255, 255, 0.2);
    }
  }
}

.chart-section {
  background: #fff;
  margin: 0 30rpx 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  
  .section-header {
    margin-bottom: 30rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
  
  .percentage-bars {
    .percentage-item {
      margin-bottom: 30rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .item-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 12rpx;
        
        .item-label {
          font-size: 28rpx;
          color: #666;
        }
        
        .item-value {
          font-size: 28rpx;
          font-weight: bold;
          color: #333;
        }
      }
      
      .progress-bar {
        height: 16rpx;
        background: #f0f0f0;
        border-radius: 8rpx;
        overflow: hidden;
        
        .progress-fill {
          height: 100%;
          border-radius: 8rpx;
          transition: width 0.3s;
          
          &.cardio {
            background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
          }
          
          &.strength {
            background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
          }
        }
      }
    }
  }
  
  .chart-wrapper {
    width: 100%;
    height: 500rpx;
    position: relative;
    flex-shrink: 0;
  }
  
  .chart-container {
    width: 100%;
    height: 100%;
  }
  
  .empty-chart {
    width: 100%;
    height: 500rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .empty-icon {
      font-size: 120rpx;
      color: #ddd;
      margin-bottom: 20rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
      margin-bottom: 10rpx;
    }
    
    .empty-hint {
      font-size: 24rpx;
      color: #ccc;
    }
  }
}

.date-filter {
  padding: 0 30rpx 30rpx;
  
  .filter-tabs {
    display: flex;
    background: #fff;
    border-radius: 12rpx;
    padding: 8rpx;
    
    .tab-item {
      flex: 1;
      text-align: center;
      padding: 16rpx;
      font-size: 28rpx;
      color: #666;
      border-radius: 8rpx;
      transition: all 0.3s;
      
      &.active {
        background: #ff6b35;
        color: #fff;
        font-weight: bold;
      }
    }
  }
}

.period-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 0 30rpx;
  
  .period-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    text-align: center;
    
    .period-label {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 16rpx;
    }
    
    .period-value {
      display: block;
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
    }
  }
}
</style>



