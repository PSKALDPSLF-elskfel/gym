<template>
  <div class="course-detail-container">
    <a-page-header
      title="课程详情"
      @back="() => $router.back()"
    >
      <template #extra>
        <a-button @click="goToAttendance" type="primary" :disabled="!courseDetail || courseDetail.status !== 1">
          签到管理
        </a-button>
      </template>
    </a-page-header>

    <a-spin :spinning="loading">
      <div v-if="courseDetail" class="detail-content">
        <!-- 课程基本信息 -->
        <a-card title="课程信息" class="info-card">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="课程名称">
              {{ courseDetail.courseName }}
            </a-descriptions-item>
            <a-descriptions-item label="课程分类">
              {{ courseDetail.categoryName || '未分类' }}
            </a-descriptions-item>
            <a-descriptions-item label="上课日期">
              {{ formatScheduleDate(courseDetail) }}
            </a-descriptions-item>
            <a-descriptions-item label="上课时间">
              {{ formatScheduleTime(courseDetail.startTime, courseDetail.endTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="上课地点">
              {{ courseDetail.location || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="课程状态">
              <a-tag :color="getStatusColor(courseDetail.status)">
                {{ getStatusText(courseDetail.status) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="最大人数">
              {{ courseDetail.maxParticipants }}
            </a-descriptions-item>
            <a-descriptions-item label="当前人数">
              {{ courseDetail.currentParticipants }}
            </a-descriptions-item>
            <a-descriptions-item label="余额" :span="2">
              <a-progress
                :percent="calculateProgress(courseDetail.currentParticipants, courseDetail.maxParticipants)"
                :format="() => `${courseDetail.currentParticipants}/${courseDetail.maxParticipants}`"
                :status="courseDetail.currentParticipants >= courseDetail.maxParticipants ? 'success' : 'active'"
              />
            </a-descriptions-item>
            <a-descriptions-item label="课程描述" :span="2">
              {{ courseDetail.description || '-' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 预约学员列表 -->
        <a-card title="预约学员" class="bookings-card">
          <a-table
            :columns="bookingColumns"
            :data-source="bookingList"
            :loading="bookingLoading"
            :pagination="false"
            :row-key="record => record.id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'userInfo'">
                <div class="user-info">
                  <div class="user-name">{{ record.userNickname || record.nickname || '未知' }}</div>
                  <div class="user-phone">{{ record.userPhone || record.phone || '-' }}</div>
                </div>
              </template>
              <template v-else-if="column.key === 'bookingTime'">
                {{ formatDateTime(record.createTime) }}
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getBookingStatusColor(record.status)">
                  {{ getBookingStatusText(record.status) }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>

        <!-- 课程统计 -->
        <a-card title="课程统计" class="stats-card">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-statistic title="总预约人数" :value="stats.totalBookings" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="已签到" :value="stats.checkedIn" suffix="人" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="未签到" :value="stats.notCheckedIn" suffix="人" />
            </a-col>
            <a-col :span="6">
              <a-statistic
                title="签到率"
                :value="stats.checkInRate"
                suffix="%"
                :precision="1"
              />
            </a-col>
          </a-row>
        </a-card>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getCourseDetail, getCourseBookings } from '@/api/course'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const scheduleId = computed(() => route.params.id)

// 课程详情
const courseDetail = ref(null)
const loading = ref(false)

// 预约列表
const bookingList = ref([])
const bookingLoading = ref(false)

// 预约表格列
const bookingColumns = [
  {
    title: '序号',
    customRender: ({ index }) => index + 1,
    width: 60
  },
  {
    title: '学员信息',
    key: 'userInfo',
    width: 200
  },
  {
    title: '预约时间',
    key: 'bookingTime',
    width: 180
  },
  {
    title: '预约状态',
    key: 'status',
    width: 100
  },
  {
    title: '备注',
    dataIndex: 'remark',
    customRender: ({ text }) => text || '-'
  }
]

// 统计数据
const stats = reactive({
  totalBookings: 0,
  checkedIn: 0,
  notCheckedIn: 0,
  checkInRate: 0
})

// 加载课程详情
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const data = await getCourseDetail(scheduleId.value)
    courseDetail.value = data
  } catch (error) {
    console.error('加载课程详情失败:', error)
    message.error('加载课程详情失败')
  } finally {
    loading.value = false
  }
}

// 加载预约列表
const loadBookings = async () => {
  try {
    bookingLoading.value = true
    const data = await getCourseBookings(scheduleId.value)
    bookingList.value = data || []
    calculateStats()
  } catch (error) {
    console.error('加载预约列表失败:', error)
    message.error('加载预约列表失败')
  } finally {
    bookingLoading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  const validBookings = bookingList.value.filter(b => b.status === 1) // 有效预约
  stats.totalBookings = validBookings.length
  stats.checkedIn = validBookings.filter(b => b.checkInTime).length
  stats.notCheckedIn = stats.totalBookings - stats.checkedIn
  stats.checkInRate = stats.totalBookings > 0 ? (stats.checkedIn / stats.totalBookings) * 100 : 0
}

// 前往签到管理
const goToAttendance = () => {
  router.push(`/courses/${scheduleId.value}/attendance`)
}

// 格式化排课日期
const formatScheduleDate = (detail) => {
  if (!detail) return '-'
  if (detail.scheduleDate) {
    return dayjs(detail.scheduleDate).format('YYYY-MM-DD')
  }
  return dayjs(detail.startTime).format('YYYY-MM-DD')
}

// 格式化排课时间
const formatScheduleTime = (startTime, endTime) => {
  if (!startTime || !endTime) return '-'
  const start = dayjs(startTime).format('HH:mm')
  const end = dayjs(endTime).format('HH:mm')
  return `${start} - ${end}`
}

// 格式化日期
const formatDate = (dateStr) => {
  return dayjs(dateStr).format('YYYY-MM-DD')
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

// 计算进度
const calculateProgress = (current, max) => {
  if (!max) return 0
  return Math.round((current / max) * 100)
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'error',    // 已取消
    1: 'success',  // 正常
    2: 'warning'   // 已满额
  }
  return colorMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '已取消',
    1: '正常',
    2: '已满额'
  }
  return textMap[status] || '未知'
}

// 获取预约状态颜色
const getBookingStatusColor = (status) => {
  const colorMap = {
    0: 'error',     // 已取消
    1: 'processing', // 已预约
    2: 'success'    // 已完成
  }
  return colorMap[status] || 'default'
}

// 获取预约状态文本
const getBookingStatusText = (status) => {
  const textMap = {
    0: '已取消',
    1: '已预约',
    2: '已完成'
  }
  return textMap[status] || '未知'
}

// 组件挂载
onMounted(() => {
  loadCourseDetail()
  loadBookings()
})
</script>

<style scoped>
.course-detail-container {
  padding: 24px;
}

.detail-content {
  margin-top: 16px;
}

.info-card,
.bookings-card,
.stats-card {
  margin-bottom: 24px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 4px;
}

.user-phone {
  color: #999;
  font-size: 12px;
}
</style>
