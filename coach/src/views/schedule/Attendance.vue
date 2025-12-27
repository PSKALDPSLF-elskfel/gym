<template>
  <div>
    <a-card title="上下班打卡" :bordered="false">
      <!-- 打卡状态 -->
      <a-row style="margin-bottom: 24px;">
        <a-col :span="24">
          <div class="attendance-status">
            <a-avatar :size="64" :icon="attendanceStatus.icon" :style="{ backgroundColor: attendanceStatus.color }" />
            <div class="status-info">
              <h2>{{ attendanceStatus.text }}</h2>
              <p v-if="attendanceStatus.time">{{ attendanceStatus.timeLabel }}: {{ attendanceStatus.time }}</p>
            </div>
          </div>
        </a-col>
      </a-row>
      
      <!-- 打卡按钮 -->
      <a-row style="margin-bottom: 24px;">
        <a-col :span="24" style="text-align: center;">
          <a-button 
            type="primary" 
            size="large" 
            :loading="checking"
            @click="handleAttendance"
            :disabled="attendanceStatus.disabled"
          >
            {{ attendanceStatus.buttonText }}
          </a-button>
        </a-col>
      </a-row>
      
      <!-- 今日排班信息 -->
      <a-row style="margin-bottom: 24px;">
        <a-col :span="24">
          <a-card title="今日排班" size="small">
            <a-empty v-if="!todaySchedules.length" description="今日无排班" />
            <div v-else>
              <div 
                v-for="schedule in todaySchedules" 
                :key="schedule.id"
                class="schedule-item"
              >
                <a-descriptions :column="1" size="small">
                  <a-descriptions-item label="时间">
                    {{ formatTime(schedule.startTime) }} - {{ formatTime(schedule.endTime) }}
                  </a-descriptions-item>
                  <a-descriptions-item label="地点">
                    {{ schedule.location }}
                  </a-descriptions-item>
                  <a-descriptions-item label="类型">
                    <a-tag :color="getWorkTypeColor(schedule.workType)">
                      {{ getWorkTypeName(schedule.workType) }}
                    </a-tag>
                  </a-descriptions-item>
                </a-descriptions>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
      
      <!-- 打卡记录 -->
      <a-row>
        <a-col :span="24">
          <a-card title="近期打卡记录" size="small">
            <a-table 
              :dataSource="attendanceRecords" 
              :columns="columns" 
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
              size="small"
              :scroll="{ x: 'max-content' }"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'checkInTime'">
                  {{ record.checkInTime || '-' }}
                </template>
                <template v-else-if="column.dataIndex === 'checkOutTime'">
                  {{ record.checkOutTime || '-' }}
                </template>
                <template v-else-if="column.dataIndex === 'status'">
                  <a-tag :color="getAttendanceStatusColor(record.status)">
                    {{ getAttendanceStatusName(record.status) }}
                  </a-tag>
                </template>
              </template>
            </a-table>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/store/user'
import { getTodaySchedule, checkIn, checkOut, getAttendanceRecords } from '@/api/schedule'

// 用户存储
const userStore = useUserStore()

// 打卡状态
const checking = ref(false)

// 今日排班
const todaySchedules = ref([])

// 打卡记录
const attendanceRecords = ref([])
const loading = ref(false)

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
})

// 表格列定义
const columns = [
  {
    title: '日期',
    dataIndex: 'workDate',
    key: 'workDate'
  },
  {
    title: '上班时间',
    dataIndex: 'checkInTime',
    key: 'checkInTime'
  },
  {
    title: '下班时间',
    dataIndex: 'checkOutTime',
    key: 'checkOutTime'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  }
]

// 计算属性：打卡状态
const attendanceStatus = computed(() => {
  // 检查是否有今日排班
  if (!todaySchedules.value.length) {
    return {
      icon: 'ExclamationCircleOutlined',
      color: '#faad14',
      text: '今日无排班',
      buttonText: '无法打卡',
      disabled: true
    }
  }
  
  // 检查是否有未完成的打卡记录
  // 这里简化处理，实际应该检查具体的打卡状态
  const now = new Date()
  const hour = now.getHours()
  
  if (hour < 12) {
    // 上午时段，默认显示上班打卡
    return {
      icon: 'LoginOutlined',
      color: '#52c41a',
      text: '等待上班打卡',
      buttonText: '上班打卡',
      disabled: false,
      action: 'checkIn'
    }
  } else {
    // 下午时段，默认显示下班打卡
    return {
      icon: 'LogoutOutlined',
      color: '#1890ff',
      text: '等待下班打卡',
      buttonText: '下班打卡',
      disabled: false,
      action: 'checkOut'
    }
  }
})

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.substring(0, 5)
}

// 获取工作类型名称
const getWorkTypeName = (workType) => {
  const workTypeMap = {
    'NORMAL': '正常排班',
    'OVERTIME': '加班',
    'SHIFT': '轮班',
    'HOLIDAY': '休息'
  }
  return workTypeMap[workType] || workType
}

// 获取工作类型颜色
const getWorkTypeColor = (workType) => {
  const colorMap = {
    'NORMAL': 'blue',
    'OVERTIME': 'green',
    'SHIFT': 'orange',
    'HOLIDAY': 'red'
  }
  return colorMap[workType] || 'default'
}

// 获取打卡状态名称
const getAttendanceStatusName = (status) => {
  const statusMap = {
    0: '未打卡',
    1: '已上班',
    2: '已下班'
  }
  return statusMap[status] || '未知'
}

// 获取打卡状态颜色
const getAttendanceStatusColor = (status) => {
  const colorMap = {
    0: 'default',
    1: 'gold',
    2: 'green'
  }
  return colorMap[status] || 'default'
}

// 处理打卡
const handleAttendance = async () => {
  if (!todaySchedules.value.length) {
    message.warning('今日无排班，无法打卡')
    return
  }
  
  // 获取第一个排班作为打卡对象
  const schedule = todaySchedules.value[0]
  
  try {
    checking.value = true
    
    if (attendanceStatus.value.action === 'checkIn') {
      // 上班打卡
      await checkIn(userStore.user?.id || 1, {
        scheduleId: schedule.id,
        location: schedule.location,
        remark: '正常上班打卡'
      })
      message.success('上班打卡成功')
    } else {
      // 下班打卡
      await checkOut(userStore.user?.id || 1, {
        scheduleId: schedule.id,
        location: schedule.location,
        remark: '正常下班打卡'
      })
      message.success('下班打卡成功')
    }
    
    // 重新加载数据
    loadTodaySchedules()
    loadAttendanceRecords()
  } catch (error) {
    message.error('打卡失败: ' + error.message)
  } finally {
    checking.value = false
  }
}

// 加载今日排班
const loadTodaySchedules = async () => {
  try {
    const response = await getTodaySchedule({
      coachId: userStore.user?.coachId || 1
    })
    todaySchedules.value = response
  } catch (error) {
    message.error('加载今日排班失败: ' + error.message)
    // 使用模拟数据以便开发
    todaySchedules.value = [
      {
        id: 1,
        coachId: userStore.user?.coachId || 1,
        workDate: new Date().toISOString().split('T')[0],
        startTime: '09:00:00',
        endTime: '17:00:00',
        workType: 'NORMAL',
        location: '主训练区',
        status: 1,
        remark: '日常排班'
      }
    ]
  }
}

// 加载打卡记录
const loadAttendanceRecords = async () => {
  try {
    loading.value = true
    
    const response = await getAttendanceRecords({
      coachId: userStore.user?.coachId || 1,
      page: pagination.current,
      size: pagination.pageSize
    })
    
    attendanceRecords.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    message.error('加载打卡记录失败: ' + error.message)
    // 使用模拟数据以便开发
    attendanceRecords.value = [
      {
        id: 1,
        scheduleId: 1,
        workDate: '2025-12-10',
        checkInTime: '2025-12-10 08:55:23',
        checkOutTime: '2025-12-10 17:05:42',
        status: 2
      },
      {
        id: 2,
        scheduleId: 2,
        workDate: '2025-12-09',
        checkInTime: '2025-12-09 09:02:15',
        checkOutTime: '2025-12-09 17:10:30',
        status: 2
      }
    ]
    pagination.total = 2
  } finally {
    loading.value = false
  }
}

// 表格分页改变事件
const handleTableChange = (pager) => {
  pagination.current = pager.current
  pagination.pageSize = pager.pageSize
  loadAttendanceRecords()
}

// 组件挂载时加载数据
onMounted(() => {
  loadTodaySchedules()
  loadAttendanceRecords()
})
</script>

<style scoped>
.attendance-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.status-info h2 {
  margin: 0 0 8px 0;
}

.status-info p {
  margin: 0;
  color: #666;
}

.schedule-item {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #f0f0f0;
}

.schedule-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}
</style>