<template>
  <div class="dashboard-container">
    <!-- 欢迎信息 -->
    <a-card class="welcome-card" :bordered="false">
      <div class="welcome-content">
        <div>
          <h2>你好，{{ coachName }}教练 👋</h2>
          <p style="color: #666; margin-top: 8px">{{ currentDate }} | {{ greeting }}</p>
        </div>
      </div>
    </a-card>

    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <!-- 统计卡片 -->
      <a-col :xs="24" :sm="12" :md="6">
        <a-card hoverable class="stat-card">
          <template #cover>
            <div style="height: 80px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center">
              <TeamOutlined style="font-size: 32px; color: white" />
            </div>
          </template>
          <a-statistic title="我的学员" :value="stats.studentCount" suffix="人" :loading="loading" />
          <router-link to="/students" style="color: #1890ff">查看详情 →</router-link>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card hoverable class="stat-card">
          <template #cover>
            <div style="height: 80px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); display: flex; align-items: center; justify-content: center">
              <FileTextOutlined style="font-size: 32px; color: white" />
            </div>
          </template>
          <a-statistic title="训练方案" :value="stats.planCount" suffix="个" :loading="loading" />
          <router-link to="/training-plans" style="color: #1890ff">查看详情 →</router-link>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card hoverable class="stat-card">
          <template #cover>
            <div style="height: 80px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); display: flex; align-items: center; justify-content: center">
              <CalendarOutlined style="font-size: 32px; color: white" />
            </div>
          </template>
          <a-statistic title="本周课程" :value="stats.weekCourseCount" suffix="节" :loading="loading" />
          <router-link to="/courses" style="color: #1890ff">查看详情 →</router-link>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card hoverable class="stat-card">
          <template #cover>
            <div style="height: 80px; background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); display: flex; align-items: center; justify-content: center">
              <StarOutlined style="font-size: 32px; color: white" />
            </div>
          </template>
          <a-statistic title="平均评分" :value="stats.avgRating" suffix="分" :precision="1" :loading="loading" />
          <router-link to="/reviews" style="color: #1890ff">查看详情 →</router-link>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快捷操作 -->
    <a-row style="margin-top: 16px">
      <a-col :xs="24">
        <a-card title="快捷操作" :bordered="false">
          <a-space wrap :size="[8, 8]">
            <a-button type="primary" @click="$router.push('/schedule')">
              <template #icon>
                <ClockCircleOutlined />
              </template>
              我的排班
            </a-button>
            <a-button @click="$router.push('/schedule/request')">
              <template #icon>
                <FormOutlined />
              </template>
              排班申请
            </a-button>
            <a-button @click="$router.push('/schedule/statistics')">
              <template #icon>
                <BarChartOutlined />
              </template>
              排班统计
            </a-button>
            <a-button @click="$router.push('/schedule/attendance')">
              <template #icon>
                <CheckCircleOutlined />
              </template>
              上下班打卡
            </a-button>
            <a-button @click="$router.push('/training-plans/create')">
              <template #icon>
                <FileTextOutlined />
              </template>
              创建训练方案
            </a-button>
            <a-button @click="$router.push('/students')">
              <template #icon>
                <TeamOutlined />
              </template>
              学员管理
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>

    <!-- 今日课程和待办事项 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <a-col :xs="24" :lg="16">
        <a-card title="今日课程安排" :bordered="false">
          <template #extra>
            <router-link to="/courses">查看全部</router-link>
          </template>
          <a-spin :spinning="coursesLoading">
            <a-list v-if="todayCourses.length > 0" :data-source="todayCourses" size="small">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <span style="font-weight: 500">{{ item.courseName }}</span>
                      <a-tag :color="getCourseStatusColor(item.status)" style="margin-left: 8px">
                        {{ getCourseStatusText(item.status) }}
                      </a-tag>
                    </template>
                    <template #description>
                      <div style="display: flex; gap: 16px; flex-wrap: wrap">
                        <span><ClockCircleOutlined /> {{ item.startTime }} - {{ item.endTime }}</span>
                        <span><EnvironmentOutlined /> {{ item.location || '未指定地点' }}</span>
                        <span><TeamOutlined /> {{ item.currentCount || 0 }}/{{ item.maxCapacity }} 人</span>
                      </div>
                    </template>
                  </a-list-item-meta>
                  <template #actions>
                    <a-button type="link" size="small" @click="viewCourseDetail(item.id)">查看详情</a-button>
                  </template>
                </a-list-item>
              </template>
            </a-list>
            <a-empty v-else description="今日暂无课程安排" />
          </a-spin>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <a-card title="待办事项" :bordered="false">
          <a-list :data-source="todoList" size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-badge :color="item.color" :text="item.text" />
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- 最新评价和数据趋势 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <a-col :xs="24" :lg="12">
        <a-card title="最新学员评价" :bordered="false">
          <template #extra>
            <router-link to="/reviews">查看全部</router-link>
          </template>
          <a-spin :spinning="reviewsLoading">
            <a-list v-if="recentReviews.length > 0" :data-source="recentReviews" size="small">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <div style="display: flex; justify-content: space-between; align-items: center">
                        <span>{{ item.userName }}</span>
                        <a-rate :value="item.rating" disabled style="font-size: 14px" />
                      </div>
                    </template>
                    <template #description>
                      <div style="color: #666">{{ item.comment }}</div>
                      <div style="color: #999; font-size: 12px; margin-top: 4px">
                        {{ formatDate(item.createTime) }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
            <a-empty v-else description="暂无评价" />
          </a-spin>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="本周课程趋势" :bordered="false">
          <div style="height: 250px; display: flex; align-items: center; justify-content: center">
            <div v-if="weekCourseData.length > 0" style="width: 100%; height: 100%">
              <div class="chart-container">
                <div v-for="(item, index) in weekCourseData" :key="index" class="chart-bar-group">
                  <div class="chart-bar" :style="{ height: (item.count / maxCourseCount * 100) + '%' }">
                    <span class="chart-value">{{ item.count }}</span>
                  </div>
                  <div class="chart-label">{{ item.day }}</div>
                </div>
              </div>
            </div>
            <a-empty v-else description="暂无数据" />
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  TeamOutlined, 
  FileTextOutlined, 
  CalendarOutlined, 
  StarOutlined,
  ClockCircleOutlined,
  FormOutlined,
  BarChartOutlined,
  CheckCircleOutlined,
  EnvironmentOutlined
} from '@ant-design/icons-vue'
import { getDashboardData, getCoachInfo } from '@/api/coach'
import { getMyCourses } from '@/api/course'
import { getReceivedReviews } from '@/api/review'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const router = useRouter()

// 统计数据
const stats = ref({
  studentCount: 0,
  planCount: 0,
  weekCourseCount: 0,
  avgRating: 0
})

// 加载中状态
const loading = ref(true)
const coursesLoading = ref(false)
const reviewsLoading = ref(false)

// 教练信息
const coachName = ref('教练')

// 今日课程
const todayCourses = ref([])

// 最新评价
const recentReviews = ref([])

// 本周课程数据
const weekCourseData = ref([])

// 当前日期和问候语
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好，开始元气满满的一天！'
  if (hour < 18) return '下午好，继续加油！'
  return '晚上好，辛苦了！'
})

// 待办事项
const todoList = computed(() => {
  const list = []
  const todayCount = todayCourses.value.length
  const unrepliedReviews = recentReviews.value.filter(r => !r.reply).length
  
  if (todayCount > 0) {
    list.push({ text: `今日有 ${todayCount} 节课程`, color: 'blue' })
  }
  if (unrepliedReviews > 0) {
    list.push({ text: `${unrepliedReviews} 条评价待回复`, color: 'orange' })
  }
  if (list.length === 0) {
    list.push({ text: '暂无待办事项', color: 'green' })
  }
  return list
})

// 计算图表最大值
const maxCourseCount = computed(() => {
  const max = Math.max(...weekCourseData.value.map(d => d.count), 1)
  return max
})

// 加载统计数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    const data = await getDashboardData()
    // 响应拦截器已经返回了data.data，所以这里直接使用data
    if (data) {
      stats.value = {
        studentCount: data.studentCount || 0,
        planCount: data.planCount || 0,
        weekCourseCount: data.weekCourseCount || 0,
        avgRating: data.avgRating || 0
      }
    }
  } catch (error) {
    console.error('加载首页数据失败:', error)
    message.error('加载统计数据失败，请刷新重试')
    // 设置默认值，避免显示undefined
    stats.value = {
      studentCount: 0,
      planCount: 0,
      weekCourseCount: 0,
      avgRating: 0
    }
  } finally {
    loading.value = false
  }
}

// 获取课程状态颜色
const getCourseStatusColor = (status) => {
  const statusMap = {
    0: 'default',  // 未开始
    1: 'processing',  // 进行中
    2: 'success',  // 已结束
    3: 'error'  // 已取消
  }
  return statusMap[status] || 'default'
}

// 获取课程状态文本
const getCourseStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '进行中',
    2: '已结束',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateStr) => {
  return dayjs(dateStr).format('MM-DD HH:mm')
}

// 查看课程详情
const viewCourseDetail = (id) => {
  router.push(`/courses/${id}`)
}

// 加载教练信息
const loadCoachInfo = async () => {
  try {
    const data = await getCoachInfo()
    coachName.value = data.name || '教练'
  } catch (error) {
    console.error('加载教练信息失败:', error)
  }
}

// 加载今日课程
const loadTodayCourses = async () => {
  try {
    coursesLoading.value = true
    const today = dayjs().format('YYYY-MM-DD')
    const data = await getMyCourses({
      startDate: today,
      endDate: today,
      currentPage: 1,
      pageSize: 5
    })
    todayCourses.value = data.records || []
  } catch (error) {
    console.error('加载今日课程失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

// 加载最新评价
const loadRecentReviews = async () => {
  try {
    reviewsLoading.value = true
    const data = await getReceivedReviews({
      pageNum: 1,
      pageSize: 5
    })
    recentReviews.value = data.records || []
  } catch (error) {
    console.error('加载评价失败:', error)
  } finally {
    reviewsLoading.value = false
  }
}

// 加载本周课程趋势
const loadWeekCourseData = async () => {
  try {
    const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const startOfWeek = dayjs().startOf('week').add(1, 'day')
    
    const promises = weekDays.map(async (day, index) => {
      const date = startOfWeek.add(index, 'day').format('YYYY-MM-DD')
      try {
        const data = await getMyCourses({
          startDate: date,
          endDate: date,
          currentPage: 1,
          pageSize: 100
        })
        return {
          day,
          count: data.total || 0
        }
      } catch (error) {
        return { day, count: 0 }
      }
    })
    
    weekCourseData.value = await Promise.all(promises)
  } catch (error) {
    console.error('加载本周课程数据失败:', error)
  }
}

onMounted(() => {
  loadDashboardData()
  loadCoachInfo()
  loadTodayCourses()
  loadRecentReviews()
  loadWeekCourseData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-bottom: 0;
}

.welcome-card :deep(.ant-card-body) {
  padding: 24px;
}

.welcome-content h2 {
  color: white;
  margin: 0;
  font-size: 24px;
}

.stat-card {
  height: 100%;
}

:deep(.ant-card) {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

:deep(.ant-card:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.ant-statistic-title) {
  margin-top: 16px;
}

:deep(.ant-list-item) {
  padding: 12px 0;
}

:deep(.ant-badge) {
  width: 100%;
}

.chart-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  padding: 20px 10px 40px;
}

.chart-bar-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.chart-bar {
  position: relative;
  width: 40px;
  background: linear-gradient(to top, #1890ff, #40a9ff);
  border-radius: 4px 4px 0 0;
  min-height: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
}

.chart-bar:hover {
  opacity: 0.8;
}

.chart-value {
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.chart-label {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
  white-space: nowrap;
}
</style>