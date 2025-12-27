<template>
  <div class="course-list-container">
    <a-card :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <a-space :size="16">
          <a-range-picker
            v-model:value="searchParams.dateRange"
            format="YYYY-MM-DD"
            :placeholder="['开始日期', '结束日期']"
            @change="handleSearch"
          />
          <a-select
            v-model:value="searchParams.status"
            placeholder="课程状态"
            style="width: 150px"
            allowClear
            @change="handleSearch"
          >
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">已取消</a-select-option>
            <a-select-option :value="2">已满额</a-select-option>
          </a-select>
          <a-button type="primary" :icon="h(SearchOutlined)" @click="handleSearch">
            搜索
          </a-button>
          <a-button :icon="h(ReloadOutlined)" @click="handleReset">
            重置
          </a-button>
        </a-space>
      </div>

      <!-- 课程视图 -->
      <a-tabs v-model:activeKey="viewMode" class="view-tabs" @change="handleViewChange">
        <a-tab-pane key="courses" tab="我的课程">
          <a-table
            :columns="courseColumns"
            :data-source="managedCourseList"
            :loading="managedLoading"
            :pagination="false"
            :row-key="record => record.id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'courseInfo'">
                <div class="course-info">
                  <div class="course-name">{{ record.name }}</div>
                  <div class="course-category">{{ record.categoryName || '未分类' }}</div>
                </div>
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getCourseStatusColor(record.status)">
                  {{ getCourseStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'price'">
                ¥{{ record.price ? record.price.toFixed(2) : '0.00' }}
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="viewCourseSchedules(record.id)">
                    查看排课
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="list" tab="排课列表">
          <a-table
            :columns="columns"
            :data-source="courseList"
            :loading="loading"
            :pagination="pagination"
            @change="handleTableChange"
            :row-key="record => record.id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'courseInfo'">
                <div class="course-info">
                  <div class="course-name">{{ record.courseName }}</div>
                  <div class="course-category">{{ record.categoryName || '未分类' }}</div>
                </div>
              </template>
              <template v-else-if="column.key === 'scheduleTime'">
                <div class="schedule-time">
                  <div>{{ formatScheduleDate(record) }}</div>
                  <div class="time-range">
                    {{ formatScheduleTime(record.startTime, record.endTime) }}
                  </div>
                </div>
              </template>
              <template v-else-if="column.key === 'participants'">
                <a-progress
                  :percent="calculateProgress(record.currentParticipants, record.maxParticipants)"
                  :format="() => `${record.currentParticipants}/${record.maxParticipants}`"
                  :status="record.currentParticipants >= record.maxParticipants ? 'success' : 'active'"
                />
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getScheduleStatusColor(record.status)">
                  {{ getScheduleStatusText(record.status) }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="viewDetail(record.id)">
                    详情
                  </a-button>
                  <a-button
                    type="link"
                    size="small"
                    @click="goToAttendance(record.id)"
                    :disabled="record.status !== 1"
                  >
                    签到管理
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="calendar" tab="日历视图">
          <a-calendar
            :fullscreen="false"
            @select="onCalendarSelect"
          >
            <template #dateCellRender="{ current }">
              <div class="calendar-events">
                <div
                  v-for="course in getCoursesForDate(current)"
                  :key="course.id"
                  class="calendar-event"
                  @click="viewDetail(course.id)"
                >
                  <div class="event-time">{{ formatEventTime(course.startTime) }}</div>
                  <div class="event-name">{{ course.courseName }}</div>
                </div>
              </div>
            </template>
          </a-calendar>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getMyCourses, getMyManagedCourses } from '@/api/course'
import dayjs from 'dayjs'

const router = useRouter()

// 视图模式
const viewMode = ref('courses')

// 我负责的课程列表
const managedCourseList = ref([])
const managedLoading = ref(false)

// 搜索参数
const searchParams = reactive({
  dateRange: [],
  status: undefined
})

// 课程表格列定义
const courseColumns = [
  {
    title: '课程信息',
    key: 'courseInfo',
    width: 200
  },
  {
    title: '课程时长',
    dataIndex: 'duration',
    key: 'duration',
    width: 100,
    customRender: ({ text }) => text ? `${text}分钟` : '-'
  },
  {
    title: '最大人数',
    dataIndex: 'maxParticipants',
    key: 'maxParticipants',
    width: 100
  },
  {
    title: '价格',
    key: 'price',
    width: 100
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 150
  }
]

// 排课表格列定义
const columns = [
  {
    title: '课程信息',
    key: 'courseInfo',
    width: 200
  },
  {
    title: '上课时间',
    key: 'scheduleTime',
    width: 180
  },
  {
    title: '上课地点',
    dataIndex: 'location',
    key: 'location',
    width: 150
  },
  {
    title: '参与人数',
    key: 'participants',
    width: 180
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 180
  }
]

// 课程列表数据
const courseList = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`
})

// 加载我负责的课程列表
const loadManagedCourses = async () => {
  try {
    managedLoading.value = true
    const data = await getMyManagedCourses()
    managedCourseList.value = data || []
  } catch (error) {
    console.error('加载课程列表失败:', error)
    message.error('加载课程列表失败')
  } finally {
    managedLoading.value = false
  }
}

// 加载排课列表
const loadCourses = async () => {
  try {
    loading.value = true
    const params = {
      current: pagination.current,
      size: pagination.pageSize,
      status: searchParams.status
    }
    
    // 添加日期范围
    if (searchParams.dateRange && searchParams.dateRange.length === 2) {
      params.startDate = searchParams.dateRange[0].format('YYYY-MM-DD')
      params.endDate = searchParams.dateRange[1].format('YYYY-MM-DD')
    }
    
    const data = await getMyCourses(params)
    courseList.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载课程列表失败:', error)
    message.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

// 视图切换
const handleViewChange = (key) => {
  if (key === 'courses') {
    loadManagedCourses()
  } else if (key === 'list') {
    loadCourses()
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadCourses()
}

// 重置
const handleReset = () => {
  searchParams.dateRange = []
  searchParams.status = undefined
  pagination.current = 1
  loadCourses()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadCourses()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/courses/${id}`)
}

// 前往签到管理
const goToAttendance = (id) => {
  router.push(`/courses/${id}/attendance`)
}

// 格式化排课日期
const formatScheduleDate = (record) => {
  if (record.scheduleDate) {
    return dayjs(record.scheduleDate).format('YYYY-MM-DD')
  }
  // 如果没有scheduleDate,使用startTime提取日期
  return dayjs(record.startTime).format('YYYY-MM-DD')
}

// 格式化排课时间
const formatScheduleTime = (startTime, endTime) => {
  const start = dayjs(startTime).format('HH:mm')
  const end = dayjs(endTime).format('HH:mm')
  return `${start} - ${end}`
}

// 格式化日期
const formatDate = (dateStr) => {
  return dayjs(dateStr).format('YYYY-MM-DD')
}

// 计算进度
const calculateProgress = (current, max) => {
  if (!max) return 0
  return Math.round((current / max) * 100)
}

// 获取课程状态颜色
const getCourseStatusColor = (status) => {
  const colorMap = {
    0: 'default',  // 下架
    1: 'success'   // 上架
  }
  return colorMap[status] || 'default'
}

// 获取课程状态文本
const getCourseStatusText = (status) => {
  const textMap = {
    0: '下架',
    1: '上架'
  }
  return textMap[status] || '未知'
}

// 获取排课状态颜色
const getScheduleStatusColor = (status) => {
  const colorMap = {
    0: 'error',    // 已取消
    1: 'success',  // 正常
    2: 'warning'   // 已满额
  }
  return colorMap[status] || 'default'
}

// 获取排课状态文本
const getScheduleStatusText = (status) => {
  const textMap = {
    0: '已取消',
    1: '正常',
    2: '已满额'
  }
  return textMap[status] || '未知'
}

// 查看课程排课
const viewCourseSchedules = (courseId) => {
  // 切换到排课列表视图，并筛选该课程
  viewMode.value = 'list'
  // 这里可以根据需要添加课程ID筛选逻辑
  loadCourses()
}

// 日历选择
const onCalendarSelect = (date) => {
  console.log('选择日期:', date.format('YYYY-MM-DD'))
}

// 获取指定日期的课程
const getCoursesForDate = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  return courseList.value.filter(course => {
    // 优先使用scheduleDate,如果没有则使用startTime
    const courseDate = course.scheduleDate 
      ? dayjs(course.scheduleDate).format('YYYY-MM-DD')
      : dayjs(course.startTime).format('YYYY-MM-DD')
    return courseDate === dateStr
  })
}

// 格式化日历事件时间
const formatEventTime = (dateTime) => {
  return dayjs(dateTime).format('HH:mm')
}

// 组件挂载
onMounted(() => {
  loadManagedCourses()
})
</script>

<style scoped>
.course-list-container {
  padding: 24px;
}

.search-bar {
  margin-bottom: 24px;
}

.view-tabs {
  margin-top: 16px;
}

.course-info {
  display: flex;
  flex-direction: column;
}

.course-name {
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 4px;
}

.course-category {
  color: #999;
  font-size: 12px;
}

.schedule-time {
  display: flex;
  flex-direction: column;
}

.time-range {
  color: #666;
  font-size: 12px;
  margin-top: 4px;
}

.calendar-events {
  padding: 2px;
  overflow: hidden;
}

.calendar-event {
  background: #e6f7ff;
  border-left: 3px solid #1890ff;
  padding: 2px 4px;
  margin-bottom: 2px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.calendar-event:hover {
  background: #bae7ff;
}

.event-time {
  color: #666;
  font-size: 11px;
}

.event-name {
  color: #333;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
