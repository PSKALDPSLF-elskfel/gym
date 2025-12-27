<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <a-card class="welcome-card">
      <template #title>
        <div class="welcome-header">
          <a-avatar :size="64" :src="avatarUrl">
            {{ userInfo?.name?.charAt(0) }}
          </a-avatar>
          <div class="welcome-info">
            <h2>欢迎回来, {{ userInfo?.name || userInfo?.username }}</h2>
            <p>{{ currentTime }}</p>
          </div>
        </div>
      </template>
      <div class="role-info">
        <a-tag>{{ roleLabel }}</a-tag>
      </div>
    </a-card>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <a-card class="stat-card">
        <a-statistic title="总用户数" :value="statistics.totalUsers" :prefix="'👥'" />
        <div class="stat-extra">
          <span class="trend-up">本月+{{ statistics.monthNewUsers || 0 }}</span>
        </div>
      </a-card>
      <a-card class="stat-card">
        <a-statistic title="总课程数" :value="statistics.totalCourses" :prefix="'📚'" />
        <div class="stat-extra">
          <span>满座率: {{ statistics.courseOccupancyRate || 0 }}%</span>
        </div>
      </a-card>
      <a-card class="stat-card">
        <a-statistic title="总预约数" :value="statistics.totalBookings" :prefix="'📅'" />
        <div class="stat-extra">
          <span class="trend-up">本周活跃: {{ statistics.todayActiveUsers || 0 }}</span>
        </div>
      </a-card>
      <a-card class="stat-card">
        <a-statistic title="会员数量" :value="statistics.memberCount" :prefix="'👑'" />
        <div class="stat-extra">
          <span>续费率: {{ statistics.renewalRate || 0 }}%</span>
        </div>
      </a-card>
      <a-card class="stat-card">
        <a-statistic title="教练数量" :value="statistics.coachCount" :prefix="'🏋️'" />
      </a-card>
      <a-card class="stat-card">
        <a-statistic title="运动记录" :value="statistics.totalWorkoutRecords" :prefix="'💪'" />
      </a-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 会员类型分布饼图 -->
      <a-card class="chart-card" title="会员类型分布">
        <div ref="memberChartRef" class="chart"></div>
      </a-card>

      <!-- 预约趋势折线图 -->
      <a-card class="chart-card" title="课程预约趋势（最近7天）">
        <template #extra>
          <a-button size="small" @click="exportReport('BOOKING_TREND')">导出</a-button>
        </template>
        <div ref="bookingChartRef" class="chart"></div>
      </a-card>

      <!-- 用户增长趋势 -->
      <a-card class="chart-card" title="用户增长趋势（最近30天）">
        <template #extra>
          <a-button size="small" @click="exportReport('USER_GROWTH')">导出</a-button>
        </template>
        <div ref="userGrowthChartRef" class="chart"></div>
      </a-card>

      <!-- 运动数据统计 -->
      <a-card class="chart-card" title="运动数据统计（最近30天）">
        <template #extra>
          <a-button size="small" @click="exportReport('WORKOUT_STATS')">导出</a-button>
        </template>
        <div ref="workoutChartRef" class="chart"></div>
      </a-card>

      <!-- 课程受欢迎度 -->
      <a-card class="chart-card" title="课程受欢迎度 Top 10">
        <div ref="coursePopularityChartRef" class="chart"></div>
      </a-card>

      <!-- 教练工作量 -->
      <a-card class="chart-card" title="教练工作量 Top 10">
        <div ref="coachWorkloadChartRef" class="chart"></div>
      </a-card>
    </div>

    <!-- 用户行为分析和运营数据 -->
    <div class="analysis-container">
      <a-card class="analysis-card" title="用户行为分析">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="平均每周运动次数">
            {{ statistics.userBehaviorAnalysis?.avgWeeklyWorkouts || 0 }} 次
          </a-descriptions-item>
          <a-descriptions-item label="平均运动时长">
            {{ statistics.userBehaviorAnalysis?.avgWorkoutDuration || 0 }} 分钟
          </a-descriptions-item>
          <a-descriptions-item label="高峰运动时段">
            {{ statistics.userBehaviorAnalysis?.peakWorkoutTime || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="7天留存率">
            {{ statistics.userBehaviorAnalysis?.retentionRate7Days || 0 }}%
          </a-descriptions-item>
          <a-descriptions-item label="30天留存率">
            {{ statistics.userBehaviorAnalysis?.retentionRate30Days || 0 }}%
          </a-descriptions-item>
          <a-descriptions-item label="活跃用户占比">
            {{ statistics.userBehaviorAnalysis?.activeUserRate || 0 }}%
          </a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card class="analysis-card" title="运营数据概览">
        <template #extra>
          <a-button type="primary" size="small" @click="exportReport('REVENUE')">导出收入报表</a-button>
        </template>
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="本月总收入">
            ¥{{ statistics.operationalOverview?.monthRevenue || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="本月会员充值">
            ¥{{ statistics.operationalOverview?.monthRecharge || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="课程预约转化率">
            {{ statistics.operationalOverview?.bookingConversionRate || 0 }}%
          </a-descriptions-item>
          <a-descriptions-item label="课程出席率">
            {{ statistics.operationalOverview?.courseAttendanceRate || 0 }}%
          </a-descriptions-item>
          <a-descriptions-item label="平均客单价">
            ¥{{ statistics.operationalOverview?.avgOrderValue || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="会员流失率">
            <span :class="{ 'text-danger': (statistics.operationalOverview?.churnRate || 0) > 15 }">
              {{ statistics.operationalOverview?.churnRate || 0 }}%
            </span>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboardStatistics, exportDashboardReport } from '@/api/dashboard'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 防抖函数
let resizeTimer = null
const debounceResize = () => {
  if (resizeTimer) clearTimeout(resizeTimer)
  resizeTimer = setTimeout(handleResize, 300)
}

// 角色标签
const roleLabel = computed(() => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'USER': '普通用户'
  }
  return roleMap[userInfo.value?.userType] || '未知角色'
})

const avatarUrl = computed(() => {
  return userInfo.value?.avatar;
})

// 当前时间
const currentTime = ref('')
let timeInterval = null // 保存定时器引用

const updateTime = () => {
  const now = new Date()
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric', 
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  }
  currentTime.value = now.toLocaleDateString('zh-CN', options)
}

// 统计数据
const statistics = ref({
  totalUsers: 0,
  totalCourses: 0,
  totalBookings: 0,
  memberCount: 0,
  memberTypeDistribution: [],
  bookingTrends: []
})

// 图表引用
const memberChartRef = ref(null)
const bookingChartRef = ref(null)
const userGrowthChartRef = ref(null)
const workoutChartRef = ref(null)
const coursePopularityChartRef = ref(null)
const coachWorkloadChartRef = ref(null)

let memberChart = null
let bookingChart = null
let userGrowthChart = null
let workoutChart = null
let coursePopularityChart = null
let coachWorkloadChart = null

// 获取统计数据
const fetchStatistics = () => {
  getDashboardStatistics(null, {
    onSuccess: (data) => {
      statistics.value = data
      // 数据加载完成后初始化图表
      nextTick(() => {
        initMemberChart()
        initBookingChart()
        initUserGrowthChart()
        initWorkoutChart()
        initCoursePopularityChart()
        initCoachWorkloadChart()
      })
    },
    onError: (error) => {
      console.error('获取统计数据失败:', error)
    }
  })
}

// 初始化会员类型分布饼图
const initMemberChart = () => {
  if (!memberChartRef.value) return

  // 销毁旧图表
  if (memberChart) {
    memberChart.dispose()
  }

  memberChart = echarts.init(memberChartRef.value)

  // 确保有数据才初始化图表
  const data = statistics.value.memberTypeDistribution && statistics.value.memberTypeDistribution.length > 0
    ? statistics.value.memberTypeDistribution.map(item => ({
        name: item.memberType,
        value: item.count
      }))
    : [{ name: '暂无数据', value: 1 }]

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '会员类型',
        type: 'pie',
        radius: '50%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  memberChart.setOption(option)
}

// 初始化预约趋势折线图
const initBookingChart = () => {
  if (!bookingChartRef.value) return

  // 销毁旧图表
  if (bookingChart) {
    bookingChart.dispose()
  }

  bookingChart = echarts.init(bookingChartRef.value)

  // 确保有数据
  const bookingData = statistics.value.bookingTrends || []
  const xAxisData = bookingData.length > 0 ? bookingData.map(item => item.date) : ['暂无数据']
  const seriesData = bookingData.length > 0 ? bookingData.map(item => item.count) : [0]

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '预约数量',
        type: 'line',
        data: seriesData,
        smooth: true,
        areaStyle: {
          color: 'rgba(102, 126, 234, 0.1)'
        },
        itemStyle: {
          color: '#667eea'
        },
        lineStyle: {
          color: '#667eea',
          width: 2
        }
      }
    ]
  }

  bookingChart.setOption(option)
}

// 窗口大小改变时重新调整图表大小
const handleResize = () => {
  // 使用 nextTick 确保 DOM 已更新
  nextTick(() => {
    if (memberChart) memberChart.resize()
    if (bookingChart) bookingChart.resize()
    if (userGrowthChart) userGrowthChart.resize()
    if (workoutChart) workoutChart.resize()
    if (coursePopularityChart) coursePopularityChart.resize()
    if (coachWorkloadChart) coachWorkloadChart.resize()
  })
}

// 初始化用户增长趋势图
const initUserGrowthChart = () => {
  if (!userGrowthChartRef.value) return
  
  if (userGrowthChart) userGrowthChart.dispose()
  userGrowthChart = echarts.init(userGrowthChartRef.value)
  
  const growthData = statistics.value.userGrowthTrends || []
  const xAxisData = growthData.length > 0 ? growthData.map(item => item.date) : ['暂无数据']
  const newUsersData = growthData.length > 0 ? growthData.map(item => item.newUsers) : [0]
  const totalUsersData = growthData.length > 0 ? growthData.map(item => item.totalUsers) : [0]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['新增用户', '累计用户']
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      boundaryGap: false
    },
    yAxis: [
      { type: 'value', name: '新增用户数', minInterval: 1 },
      { type: 'value', name: '累计用户数', minInterval: 1 }
    ],
    series: [
      {
        name: '新增用户',
        type: 'bar',
        data: newUsersData,
        itemStyle: { color: '#5470c6' }
      },
      {
        name: '累计用户',
        type: 'line',
        yAxisIndex: 1,
        data: totalUsersData,
        smooth: true,
        itemStyle: { color: '#91cc75' }
      }
    ]
  }
  
  userGrowthChart.setOption(option)
}

// 初始化运动数据统计图
const initWorkoutChart = () => {
  if (!workoutChartRef.value) return
  
  if (workoutChart) workoutChart.dispose()
  workoutChart = echarts.init(workoutChartRef.value)
  
  const workoutData = statistics.value.workoutStatistics || []
  const xAxisData = workoutData.length > 0 ? workoutData.map(item => item.date) : ['暂无数据']
  const workoutCountData = workoutData.length > 0 ? workoutData.map(item => item.workoutCount) : [0]
  const durationData = workoutData.length > 0 ? workoutData.map(item => item.totalDuration) : [0]
  const caloriesData = workoutData.length > 0 ? workoutData.map(item => item.totalCalories) : [0]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['运动次数', '总时长(分钟)', '总热量(千卡)']
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '运动次数',
        type: 'bar',
        data: workoutCountData,
        itemStyle: { color: '#fac858' }
      },
      {
        name: '总时长(分钟)',
        type: 'line',
        data: durationData,
        smooth: true,
        itemStyle: { color: '#ee6666' }
      },
      {
        name: '总热量(千卡)',
        type: 'line',
        data: caloriesData,
        smooth: true,
        itemStyle: { color: '#73c0de' }
      }
    ]
  }
  
  workoutChart.setOption(option)
}

// 初始化课程受欢迎度图
const initCoursePopularityChart = () => {
  if (!coursePopularityChartRef.value) return
  
  if (coursePopularityChart) coursePopularityChart.dispose()
  coursePopularityChart = echarts.init(coursePopularityChartRef.value)
  
  const popularityData = statistics.value.coursePopularities || []
  const yAxisData = popularityData.length > 0 ? popularityData.map(item => item.courseName) : ['暂无数据']
  const seriesData = popularityData.length > 0 ? popularityData.map(item => item.bookingCount) : [0]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: yAxisData
    },
    series: [
      {
        name: '预约次数',
        type: 'bar',
        data: seriesData,
        itemStyle: { color: '#9a60b4' }
      }
    ]
  }
  
  coursePopularityChart.setOption(option)
}

// 初始化教练工作量图
const initCoachWorkloadChart = () => {
  if (!coachWorkloadChartRef.value) return
  
  if (coachWorkloadChart) coachWorkloadChart.dispose()
  coachWorkloadChart = echarts.init(coachWorkloadChartRef.value)
  
  const workloadData = statistics.value.coachWorkloads || []
  const xAxisData = workloadData.length > 0 ? workloadData.map(item => item.coachName) : ['暂无数据']
  const courseCountData = workloadData.length > 0 ? workloadData.map(item => item.courseCount) : [0]
  const studentCountData = workloadData.length > 0 ? workloadData.map(item => item.studentCount) : [0]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['课程数量', '学员数量']
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLabel: { rotate: 30 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '课程数量',
        type: 'bar',
        data: courseCountData,
        itemStyle: { color: '#fc8452' }
      },
      {
        name: '学员数量',
        type: 'bar',
        data: studentCountData,
        itemStyle: { color: '#5470c6' }
      }
    ]
  }
  
  coachWorkloadChart.setOption(option)
}

// 导出报表
const exportReport = async (reportType) => {
  try {
    const response = await exportDashboardReport({ reportType })
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    const date = new Date().toISOString().split('T')[0]
    link.download = `${reportType}_${date}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    message.success('报表导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    message.error('报表导出失败')
  }
}

onMounted(() => {
  updateTime()
  // 每分钟更新一次时间
  timeInterval = setInterval(updateTime, 60000)
  
  // 获取统计数据
  fetchStatistics()

  // 监听窗口大小变化（使用防抖）
  window.addEventListener('resize', debounceResize)
})

onUnmounted(() => {
  // 清除定时器
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }

  // 清除 resize 防抖定时器
  if (resizeTimer) {
    clearTimeout(resizeTimer)
    resizeTimer = null
  }

  // 销毁图表
  if (memberChart) memberChart.dispose()
  if (bookingChart) bookingChart.dispose()
  if (userGrowthChart) userGrowthChart.dispose()
  if (workoutChart) workoutChart.dispose()
  if (coursePopularityChart) coursePopularityChart.dispose()
  if (coachWorkloadChart) coachWorkloadChart.dispose()

  // 移除窗口大小监听
  window.removeEventListener('resize', debounceResize)
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  
  .welcome-header {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .welcome-info {
      h2 {
        margin: 0;
        font-size: 24px;
        color: #333;
      }
      
      p {
        margin: 5px 0 0;
        color: #666;
        font-size: 14px;
      }
    }
  }
  
  .role-info {
    margin-top: 10px;
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
  
  .stat-card {
    text-align: center;
    position: relative;
    
    :deep(.ant-statistic-title) {
      font-size: 14px;
      color: #666;
    }
    
    :deep(.ant-statistic-content) {
      font-size: 24px;
      font-weight: bold;
      color: #667eea;
    }

    .stat-extra {
      margin-top: 10px;
      font-size: 12px;
      color: #999;

      .trend-up {
        color: #52c41a;
      }

      .trend-down {
        color: #f5222d;
      }
    }
  }
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
  
  .chart-card {
    min-height: 450px;
    display: flex;
    flex-direction: column;
    
    .chart {
      flex: 1;
      width: 100%;
      min-height: 400px;
    }
  }
}

.analysis-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 20px;

  .analysis-card {
    :deep(.ant-descriptions-item-label) {
      font-weight: 500;
      background-color: #fafafa;
    }
  }
}

.text-danger {
  color: #f5222d;
  font-weight: bold;
}

@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .charts-container {
    grid-template-columns: 1fr;
  }

  .analysis-container {
    grid-template-columns: 1fr;
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style> 