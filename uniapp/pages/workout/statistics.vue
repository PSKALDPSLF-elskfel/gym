<template>
  <view class="page-wrap">
    <mod-nav-bar title="运动统计"></mod-nav-bar>
    
    <view class="page-content">
      <!-- 汇总数据卡片 -->
      <view class="summary-section">
        <view class="stat-card">
          <text class="stat-value">{{ summary.totalWorkouts || 0 }}</text>
          <text class="stat-label">运动次数</text>
          <text class="fa fa-trophy stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ summary.totalDuration || 0 }}</text>
          <text class="stat-label">总时长(分)</text>
          <text class="fa fa-clock-o stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ summary.totalCalories || 0 }}</text>
          <text class="stat-label">总热量(千卡)</text>
          <text class="fa fa-fire stat-icon"></text>
        </view>
        
        <view class="stat-card">
          <text class="stat-value">{{ summary.streakDays || 0 }}</text>
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
              <text class="item-value">{{ summary.cardioPercentage || 0 }}%</text>
            </view>
            <view class="progress-bar">
              <view class="progress-fill cardio" :style="{ width: (summary.cardioPercentage || 0) + '%' }"></view>
            </view>
          </view>
          
          <view class="percentage-item">
            <view class="item-header">
              <text class="item-label">力量训练</text>
              <text class="item-value">{{ summary.strengthPercentage || 0 }}%</text>
            </view>
            <view class="progress-bar">
              <view class="progress-fill strength" :style="{ width: (summary.strengthPercentage || 0) + '%' }"></view>
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
        <view class="chart-container">
          <qiun-data-charts 
            type="line"
            :opts="durationChartOpts"
            :chartData="durationChartData"
            :canvas2d="true"
          />
        </view>
      </view>
      
      <!-- 热量消耗趋势图 -->
      <view class="chart-section">
        <view class="section-header">
          <text class="section-title">热量消耗趋势</text>
        </view>
        <view class="chart-container">
          <qiun-data-charts 
            type="line"
            :opts="caloriesChartOpts"
            :chartData="caloriesChartData"
            :canvas2d="true"
          />
        </view>
      </view>
      
      <!-- 本周/本月统计 -->
      <view class="period-stats">
        <view class="period-card">
          <text class="period-label">本周运动</text>
          <text class="period-value">{{ summary.weekWorkouts || 0 }}次</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">本月运动</text>
          <text class="period-value">{{ summary.monthWorkouts || 0 }}次</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">平均时长</text>
          <text class="period-value">{{ summary.avgDuration || 0 }}分</text>
        </view>
        
        <view class="period-card">
          <text class="period-label">运动天数</text>
          <text class="period-value">{{ summary.workoutDays || 0 }}天</text>
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
  padding: [15, 15, 0, 5],
  legend: {
    show: false
  },
  xAxis: {
    disableGrid: true,
    fontSize: 10
  },
  yAxis: {
    data: [
      { min: 0 }
    ],
    fontSize: 10
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
  padding: [15, 15, 0, 5],
  legend: {
    show: false
  },
  xAxis: {
    disableGrid: true,
    fontSize: 10
  },
  yAxis: {
    data: [
      { min: 0 }
    ],
    fontSize: 10
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
        data: dailyStats.value.map(item => item.totalDuration || 0)
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
        data: dailyStats.value.map(item => item.totalCalories || 0)
      }
    ]
  }
})

/**
 * 获取汇总统计
 */
const fetchSummary = () => {
  getWorkoutStatisticsSummary({
    showDefaultMsg: false,
    onSuccess: (data) => {
      summary.value = data || {}
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
  
  getWorkoutDailyStats({
    startDate: startDateStr,
    endDate: endDate
  }, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      dailyStats.value = data || []
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
  const date = new Date(dateStr.replace(/-/g, '/'))
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
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
  
  .chart-container {
    width: 100%;
    height: 400rpx;
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
