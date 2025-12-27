<template>
  <div class="attendance-container">
    <a-page-header
      title="签到管理"
      :sub-title="courseInfo"
      @back="() => $router.back()"
    />

    <a-spin :spinning="loading">
      <div class="attendance-content">
        <!-- 统计卡片 -->
        <a-row :gutter="16" class="stats-row">
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="总预约人数"
                :value="stats.totalBookings"
                :value-style="{ color: '#3f8600' }"
              >
                <template #prefix>
                  <team-outlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="已签到"
                :value="stats.checkedIn"
                :value-style="{ color: '#1890ff' }"
              >
                <template #prefix>
                  <check-circle-outlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="未签到"
                :value="stats.notCheckedIn"
                :value-style="{ color: '#cf1322' }"
              >
                <template #prefix>
                  <close-circle-outlined />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card>
              <a-statistic
                title="签到率"
                :value="stats.checkInRate"
                suffix="%"
                :precision="1"
              />
            </a-card>
          </a-col>
        </a-row>

        <!-- 学员列表 -->
        <a-card title="学员签到" class="student-list-card">
          <template #extra>
            <a-space>
              <a-input-search
                v-model:value="searchKeyword"
                placeholder="搜索学员姓名/手机号"
                style="width: 250px"
                @search="handleSearch"
              />
              <a-radio-group v-model:value="filterStatus" @change="handleFilterChange">
                <a-radio-button value="all">全部</a-radio-button>
                <a-radio-button value="checked">已签到</a-radio-button>
                <a-radio-button value="unchecked">未签到</a-radio-button>
              </a-radio-group>
            </a-space>
          </template>

          <a-table
            :columns="columns"
            :data-source="filteredStudents"
            :loading="tableLoading"
            :pagination="pagination"
            :row-key="record => record.id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'index'">
                {{ record.index }}
              </template>
              <template v-else-if="column.key === 'userInfo'">
                <div class="user-info">
                  <a-avatar :size="40" :src="getAvatarUrl(record.userAvatar || record.avatar)">
                    {{ (record.userNickname || record.nickname || '未知').charAt(0) }}
                  </a-avatar>
                  <div class="user-details">
                    <div class="user-name">{{ record.userNickname || record.nickname || '未知' }}</div>
                    <div class="user-phone">{{ record.userPhone || record.phone || '-' }}</div>
                  </div>
                </div>
              </template>
              <template v-else-if="column.key === 'bookingTime'">
                {{ formatDateTime(record.createTime) }}
              </template>
              <template v-else-if="column.key === 'checkInStatus'">
                <a-tag v-if="record.checkInTime" color="success">
                  <check-circle-outlined /> 已签到
                </a-tag>
                <a-tag v-else color="default">
                  <clock-circle-outlined /> 未签到
                </a-tag>
              </template>
              <template v-else-if="column.key === 'checkInTime'">
                {{ record.checkInTime ? formatDateTime(record.checkInTime) : '-' }}
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button
                  v-if="!record.checkInTime"
                  type="primary"
                  size="small"
                  @click="handleCheckIn(record)"
                >
                  签到
                </a-button>
                <a-tag v-else color="success">已签到</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  TeamOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons-vue'
import { getCourseDetail, getCourseBookings, signInStudent } from '@/api/course'
import { getAvatarUrl } from '@/utils/fileUtils'
import dayjs from 'dayjs'

const route = useRoute()
const scheduleId = computed(() => route.params.id)

// 课程信息
const courseInfo = ref('')
const loading = ref(false)

// 学员列表
const studentList = ref([])
const tableLoading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('all')

// 表格列
const columns = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    align: 'center'
  },
  {
    title: '学员信息',
    key: 'userInfo',
    width: 250
  },
  {
    title: '预约时间',
    key: 'bookingTime',
    width: 180
  },
  {
    title: '签到状态',
    key: 'checkInStatus',
    width: 120,
    align: 'center'
  },
  {
    title: '签到时间',
    key: 'checkInTime',
    width: 180
  },
  {
    title: '备注',
    dataIndex: 'remark',
    customRender: ({ text }) => text || '-'
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 100,
    align: 'center'
  }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 统计数据
const stats = reactive({
  totalBookings: 0,
  checkedIn: 0,
  notCheckedIn: 0,
  checkInRate: 0
})

// 过滤后的学员列表
const filteredStudents = computed(() => {
  let result = studentList.value

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(student => {
      const nickname = student.userNickname || student.nickname || ''
      const phone = student.userPhone || student.phone || ''
      return (
        nickname.toLowerCase().includes(keyword) ||
        phone.includes(keyword)
      )
    })
  }

  // 按状态过滤
  if (filterStatus.value === 'checked') {
    result = result.filter(student => student.checkInTime)
  } else if (filterStatus.value === 'unchecked') {
    result = result.filter(student => !student.checkInTime)
  }

  // 添加序号
  return result.map((item, index) => ({
    ...item,
    index: index + 1
  }))
})

// 加载课程信息
const loadCourseInfo = async () => {
  try {
    const data = await getCourseDetail(scheduleId.value)
    // 处理日期和时间格式
    let dateStr = ''
    let timeStr = ''
    
    if (data.scheduleDate) {
      dateStr = dayjs(data.scheduleDate).format('YYYY-MM-DD')
    } else {
      dateStr = dayjs(data.startTime).format('YYYY-MM-DD')
    }
    
    const startTime = dayjs(data.startTime).format('HH:mm')
    const endTime = dayjs(data.endTime).format('HH:mm')
    timeStr = `${startTime}-${endTime}`
    
    courseInfo.value = `${data.courseName} - ${dateStr} ${timeStr}`
  } catch (error) {
    console.error('加载课程信息失败:', error)
  }
}

// 加载学员列表
const loadStudents = async () => {
  try {
    loading.value = true
    tableLoading.value = true
    const data = await getCourseBookings(scheduleId.value)
    // 只显示有效预约
    studentList.value = (data || []).filter(item => item.status === 1)
    calculateStats()
  } catch (error) {
    console.error('加载学员列表失败:', error)
    message.error('加载学员列表失败')
  } finally {
    loading.value = false
    tableLoading.value = false
  }
}

// 计算统计数据
const calculateStats = () => {
  stats.totalBookings = studentList.value.length
  stats.checkedIn = studentList.value.filter(s => s.checkInTime).length
  stats.notCheckedIn = stats.totalBookings - stats.checkedIn
  stats.checkInRate = stats.totalBookings > 0
    ? (stats.checkedIn / stats.totalBookings) * 100
    : 0
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
}

// 过滤变化
const handleFilterChange = () => {
  pagination.current = 1
}

// 签到
const handleCheckIn = (record) => {
  const studentName = record.userNickname || record.nickname || '未知学员'
  Modal.confirm({
    title: '确认签到',
    content: `确认为学员 ${studentName} 签到吗？`,
    onOk: async () => {
      try {
        await signInStudent(scheduleId.value, record.id)
        message.success('签到成功')
        // 重新加载数据
        await loadStudents()
      } catch (error) {
        console.error('签到失败:', error)
        message.error('签到失败')
      }
    }
  })
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

// 组件挂载
onMounted(() => {
  loadCourseInfo()
  loadStudents()
})
</script>

<style scoped>
.attendance-container {
  padding: 24px;
}

.attendance-content {
  margin-top: 16px;
}

.stats-row {
  margin-bottom: 24px;
}

.student-list-card {
  margin-top: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
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
