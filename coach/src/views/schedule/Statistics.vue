<template>
  <div>
    <a-card title="排班统计" :bordered="false">
      <!-- 月份选择器 -->
      <a-row style="margin-bottom: 20px;">
        <a-col :span="24">
          <a-space>
            <a-select v-model:value="selectedYear" style="width: 100px;" @change="loadStatistics">
              <a-select-option v-for="year in availableYears" :key="year" :value="year">
                {{ year }}年
              </a-select-option>
            </a-select>
            <a-select v-model:value="selectedMonth" style="width: 100px;" @change="loadStatistics">
              <a-select-option v-for="month in 12" :key="month" :value="month">
                {{ month }}月
              </a-select-option>
            </a-select>
          </a-space>
        </a-col>
      </a-row>
      
      <!-- 统计数据概览 -->
      <a-row :gutter="[16, 16]" style="margin-bottom: 24px;">
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="总排班天数" :value="statistics.totalDays" suffix="天" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="正常排班" :value="statistics.normalDays" suffix="天" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="加班天数" :value="statistics.overtimeDays" suffix="天" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="休息天数" :value="statistics.holidayDays" suffix="天" />
          </a-card>
        </a-col>
      </a-row>
      
      <a-row :gutter="[16, 16]" style="margin-bottom: 24px;">
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="总工作时长" :value="statistics.totalHours" suffix="小时" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="平均每日工时" :value="statistics.avgDailyHours" :precision="1" suffix="小时" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="加班时长" :value="statistics.overtimeHours" suffix="小时" />
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card hoverable>
            <a-statistic title="出勤率" :value="statistics.attendanceRate" :precision="1" suffix="%" />
          </a-card>
        </a-col>
      </a-row>
      
      <!-- 图表展示 -->
      <a-row :gutter="[16, 16]">
        <a-col :span="24">
          <a-card title="月度排班趋势" :bordered="false">
            <div ref="chartContainer" style="height: 400px;"></div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/store/user'
import { getMonthlyStatistics } from '@/api/schedule'

// 用户存储
const userStore = useUserStore()

// 当前日期
const currentDate = new Date()
const currentYear = currentDate.getFullYear()
const currentMonth = currentDate.getMonth() + 1

// 可选年份（最近5年）
const availableYears = Array.from({ length: 5 }, (_, i) => currentYear - i)

// 选择的年月
const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)

// 统计数据
const statistics = reactive({
  totalDays: 0,
  normalDays: 0,
  overtimeDays: 0,
  holidayDays: 0,
  totalHours: 0,
  avgDailyHours: 0,
  overtimeHours: 0,
  attendanceRate: 0
})

// 图表实例
let chartInstance = null
const chartContainer = ref(null)

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getMonthlyStatistics({
      coachId: userStore.user?.coachId || 1,
      year: selectedYear.value,
      month: selectedMonth.value
    })
    
    // 更新统计数据
    Object.assign(statistics, response)
    
    // 更新图表
    updateChart()
  } catch (error) {
    message.error('加载统计数据失败: ' + error.message)
    // 使用模拟数据以便开发
    Object.assign(statistics, {
      totalDays: 22,
      normalDays: 18,
      overtimeDays: 2,
      holidayDays: 2,
      totalHours: 160,
      avgDailyHours: 7.3,
      overtimeHours: 8,
      attendanceRate: 95.5
    })
    
    // 更新图表
    updateChart()
  }
}

// 初始化图表
const initChart = () => {
  if (chartContainer.value) {
    chartInstance = echarts.init(chartContainer.value)
    updateChart()
  }
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['排班天数', '工作时长']
    },
    xAxis: {
      type: 'category',
      data: ['第1周', '第2周', '第3周', '第4周', '第5周']
    },
    yAxis: [
      {
        type: 'value',
        name: '天数'
      },
      {
        type: 'value',
        name: '小时'
      }
    ],
    series: [
      {
        name: '排班天数',
        type: 'bar',
        data: [5, 6, 5, 4, 2],
        itemStyle: {
          color: '#1890ff'
        }
      },
      {
        name: '工作时长',
        type: 'line',
        yAxisIndex: 1,
        data: [40, 48, 40, 32, 16],
        itemStyle: {
          color: '#52c41a'
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

// 窗口大小改变时重置图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 组件挂载时初始化
onMounted(() => {
  loadStatistics()
  initChart()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
// onUnmounted(() => {
//   window.removeEventListener('resize', handleResize)
//   if (chartInstance) {
//     chartInstance.dispose()
//   }
// })

// 监听年月变化
watch([selectedYear, selectedMonth], () => {
  loadStatistics()
})
</script>

<style scoped>
/* 可以在这里添加自定义样式 */
</style>