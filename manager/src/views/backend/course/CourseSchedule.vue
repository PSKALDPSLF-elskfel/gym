<template>
  <div class="course-schedule-management">
    <a-card :bordered="false">
      <!-- 课程信息 -->
      <div class="course-info">
        <a-descriptions title="课程信息" :column="3" bordered>
          <a-descriptions-item label="课程名称">{{ courseInfo.name }}</a-descriptions-item>
          <a-descriptions-item label="课程时长">{{ courseInfo.duration }}分钟</a-descriptions-item>
          <a-descriptions-item label="最大人数">{{ courseInfo.maxParticipants }}人</a-descriptions-item>
          <a-descriptions-item label="课程价格">¥{{ courseInfo.price }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="courseInfo.status === 1 ? 'success' : 'default'">
              {{ courseInfo.statusDisplayName }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <a-divider />

      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">已取消</a-select-option>
            <a-select-option :value="2">已满</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="日期范围">
          <a-range-picker
            v-model:value="dateRange"
            format="YYYY-MM-DD"
            style="width: 240px"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            查询
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          新增排课
        </a-button>
        <a-button type="primary" @click="handleBatchAdd" style="margin-left: 8px">
          <template #icon><CalendarOutlined /></template>
          批量排课
        </a-button>
        <a-popconfirm
          title="确定要清除所有过期排课吗？"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleClearExpired"
        >
          <a-button danger style="margin-left: 8px">
            <template #icon><DeleteOutlined /></template>
            清除过期排课
          </a-button>
        </a-popconfirm>
        <a-button @click="handleBack" style="margin-left: 8px">
          <template #icon><ArrowLeftOutlined /></template>
          返回课程列表
        </a-button>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'timeRange'">
            <div>
              <div>{{ formatDateTime(record.startTime) }}</div>
              <div style="color: #999; font-size: 12px">至</div>
              <div>{{ formatDateTime(record.endTime) }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'participants'">
            <a-progress
              :percent="getParticipantsPercent(record)"
              :status="record.status === 2 ? 'exception' : 'normal'"
            >
              <template #format>
                {{ record.currentParticipants }}/{{ record.maxParticipants }}
              </template>
            </a-progress>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusDisplayName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'canBook'">
            <a-tag :color="record.canBook ? 'success' : 'default'">
              {{ record.canBook ? '可预约' : '不可预约' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-button
                v-if="record.status === 1"
                type="link"
                size="small"
                @click="handleToggleStatus(record, 0)"
              >
                取消
              </a-button>
              <a-button
                v-if="record.status === 0"
                type="link"
                size="small"
                @click="handleToggleStatus(record, 1)"
              >
                恢复
              </a-button>
              <a-popconfirm
                title="确定要删除这个排课吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" size="small" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑排课模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="600"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="开始时间" name="startTime">
          <a-date-picker
            v-model:value="formData.startTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="结束时间" name="endTime">
          <a-date-picker
            v-model:value="formData.endTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="最大参与人数" name="maxParticipants">
          <a-input-number
            v-model:value="formData.maxParticipants"
            :min="1"
            :max="1000"
            placeholder="请输入最大参与人数"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formData.status" placeholder="请选择状态">
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">已取消</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 批量排课模态框 -->
    <a-modal
      v-model:open="batchModalVisible"
      title="批量排课"
      :width="700"
      @ok="handleBatchModalOk"
      @cancel="handleBatchModalCancel"
    >
      <a-form
        ref="batchFormRef"
        :model="batchFormData"
        :rules="batchFormRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="排课周期" name="dateRange">
          <a-range-picker
            v-model:value="batchFormData.dateRange"
            format="YYYY-MM-DD"
            placeholder="['开始日期', '结束日期']"
            style="width: 100%"
          />
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            选择排课的起止日期范围
          </div>
        </a-form-item>
        
        <a-form-item label="重复星期" name="weekdays">
          <a-checkbox-group v-model:value="batchFormData.weekdays">
            <a-checkbox :value="1">周一</a-checkbox>
            <a-checkbox :value="2">周二</a-checkbox>
            <a-checkbox :value="3">周三</a-checkbox>
            <a-checkbox :value="4">周四</a-checkbox>
            <a-checkbox :value="5">周五</a-checkbox>
            <a-checkbox :value="6">周六</a-checkbox>
            <a-checkbox :value="0">周日</a-checkbox>
          </a-checkbox-group>
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            选择每周哪几天需要排课
          </div>
        </a-form-item>
        
        <a-form-item label="上课时间" name="timeRange">
          <a-time-range-picker
            v-model:value="batchFormData.timeRange"
            format="HH:mm"
            placeholder="['开始时间', '结束时间']"
            style="width: 100%"
          />
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            每次课程的上课时间段
          </div>
        </a-form-item>
        
        <a-form-item label="最大参与人数" name="maxParticipants">
          <a-input-number
            v-model:value="batchFormData.maxParticipants"
            :min="1"
            :max="1000"
            placeholder="请输入最大参与人数"
            style="width: 100%"
          />
        </a-form-item>
        
        <a-form-item label="预览">
          <div class="batch-preview">
            <div v-if="batchPreviewList.length > 0" class="preview-list">
              <div class="preview-count">
                将创建 <span style="color: #1890ff; font-weight: bold">{{ batchPreviewList.length }}</span> 个排课
              </div>
              <div class="preview-items">
                <div v-for="(item, index) in batchPreviewList.slice(0, 5)" :key="index" class="preview-item">
                  <i class="fa fa-calendar-alt"></i>
                  {{ item }}
                </div>
                <div v-if="batchPreviewList.length > 5" class="preview-more">
                  还有 {{ batchPreviewList.length - 5 }} 个排课...
                </div>
              </div>
            </div>
            <div v-else class="preview-empty">
              请选择排课周期、重复星期和上课时间
            </div>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  ArrowLeftOutlined,
  CalendarOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import {
  getCourseSchedulePage,
  createCourseSchedule,
  updateCourseSchedule,
  deleteCourseSchedule,
  updateCourseScheduleStatus,
  deleteExpiredSchedules
} from '@/api/courseSchedule'
import { getCourseById } from '@/api/CourseApi'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 课程ID
const courseId = ref(route.params.courseId)

// 课程信息
const courseInfo = ref({})

// 搜索表单
const searchForm = reactive({
  status: undefined
})

// 日期范围
const dateRange = ref([])

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`
})

// 表格列配置
const columns = [
  {
    title: '排课ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '时间范围',
    key: 'timeRange',
    width: 200
  },
  {
    title: '参与人数',
    key: 'participants',
    width: 150
  },
  {
    title: '剩余名额',
    dataIndex: 'remainingSlots',
    key: 'remainingSlots',
    width: 100
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '可预约',
    key: 'canBook',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right'
  }
]

// 模态框
const modalVisible = ref(false)
const modalTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  id: null,
  courseId: courseId.value,
  startTime: null,
  endTime: null,
  maxParticipants: null,
  status: 1
})

// 表单验证规则
const formRules = {
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  maxParticipants: [{ required: true, message: '请输入最大参与人数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 批量排课模态框
const batchModalVisible = ref(false)
const batchFormRef = ref()

// 批量排课表单数据
const batchFormData = reactive({
  dateRange: null,
  weekdays: [],
  timeRange: null,
  maxParticipants: null
})

// 批量排课表单验证规则
const batchFormRules = {
  dateRange: [{ required: true, message: '请选择排课周期', trigger: 'change' }],
  weekdays: [{ required: true, message: '请选择重复星期', trigger: 'change' }],
  timeRange: [{ required: true, message: '请选择上课时间', trigger: 'change' }],
  maxParticipants: [{ required: true, message: '请输入最大参与人数', trigger: 'blur' }]
}

// 批量排课预览列表
const batchPreviewList = computed(() => {
  if (!batchFormData.dateRange || !batchFormData.weekdays.length || !batchFormData.timeRange) {
    return []
  }
  
  const [startDate, endDate] = batchFormData.dateRange
  const [startTime, endTime] = batchFormData.timeRange
  const weekdays = batchFormData.weekdays
  
  const schedules = []
  let currentDate = dayjs(startDate)
  const end = dayjs(endDate)
  
  while (currentDate.isBefore(end) || currentDate.isSame(end, 'day')) {
    const dayOfWeek = currentDate.day()
    if (weekdays.includes(dayOfWeek)) {
      const scheduleStart = currentDate
        .hour(startTime.hour())
        .minute(startTime.minute())
        .second(0)
      const scheduleEnd = currentDate
        .hour(endTime.hour())
        .minute(endTime.minute())
        .second(0)
      
      schedules.push(
        `${scheduleStart.format('YYYY-MM-DD HH:mm')} - ${scheduleEnd.format('HH:mm')}`
      )
    }
    currentDate = currentDate.add(1, 'day')
  }
  
  return schedules
})

// 加载课程信息
const loadCourseInfo = () => {
  getCourseById(courseId.value, {
    onSuccess: (res) => {
      courseInfo.value = res
    }
  })
}

// 加载排课列表
const loadScheduleList = () => {
  loading.value = true

  const params = {
    current: pagination.current,
    size: pagination.pageSize,
    courseId: courseId.value,
    status: searchForm.status
  }

  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = formatLocalDate(dateRange.value[0])
    params.endDate = formatLocalDate(dateRange.value[1])
  }

  getCourseSchedulePage(params, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      pagination.total = res.total || 0
      loading.value = false
    },
    onError: () => {
      loading.value = false
    }
  })
}

// 本地日期格式化函数
const formatLocalDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  
  return `${year}-${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  return dateTime ? dayjs(dateTime).format('YYYY-MM-DD HH:mm') : '-'
}

// 获取参与人数百分比
const getParticipantsPercent = (record) => {
  if (!record.maxParticipants) return 0
  return Math.round((record.currentParticipants / record.maxParticipants) * 100)
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'default',
    1: 'success',
    2: 'error'
  }
  return colorMap[status] || 'default'
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadScheduleList()
}

// 重置
const handleReset = () => {
  searchForm.status = undefined
  dateRange.value = []
  pagination.current = 1
  loadScheduleList()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadScheduleList()
}

// 新增排课
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增排课'
  resetForm()
  modalVisible.value = true
}

// 批量排课
const handleBatchAdd = () => {
  resetBatchForm()
  batchModalVisible.value = true
}

// 编辑排课
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑排课'
  formData.id = record.id
  formData.courseId = record.courseId
  formData.startTime = record.startTime ? dayjs(record.startTime) : null
  formData.endTime = record.endTime ? dayjs(record.endTime) : null
  formData.maxParticipants = record.maxParticipants
  formData.status = record.status
  modalVisible.value = true
}

// 删除排课
const handleDelete = (record) => {
  deleteCourseSchedule(record.id, {
    successMsg: '删除成功',
    onSuccess: () => {
      loadScheduleList()
    }
  })
}

// 切换状态
const handleToggleStatus = (record, status) => {
  updateCourseScheduleStatus(record.id, status, {
    successMsg: status === 0 ? '取消成功' : '恢复成功',
    onSuccess: () => {
      loadScheduleList()
    }
  })
}

// 模态框确定
const handleModalOk = () => {
  formRef.value.validate().then(() => {
    const params = {
      courseId: formData.courseId,
      startTime: formData.startTime ? dayjs(formData.startTime).format('YYYY-MM-DD HH:mm:ss') : null,
      endTime: formData.endTime ? dayjs(formData.endTime).format('YYYY-MM-DD HH:mm:ss') : null,
      maxParticipants: formData.maxParticipants,
      status: formData.status
    }

    if (isEdit.value) {
      updateCourseSchedule(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          modalVisible.value = false
          loadScheduleList()
        }
      })
    } else {
      createCourseSchedule(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          modalVisible.value = false
          loadScheduleList()
        }
      })
    }
  })
}

// 模态框取消
const handleModalCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.courseId = courseId.value
  formData.startTime = null
  formData.endTime = null
  formData.maxParticipants = courseInfo.value.maxParticipants || null
  formData.status = 1
  formRef.value?.resetFields()
}

// 批量排课模态框确定
const handleBatchModalOk = () => {
  batchFormRef.value.validate().then(() => {
    const [startDate, endDate] = batchFormData.dateRange
    const [startTime, endTime] = batchFormData.timeRange
    const weekdays = batchFormData.weekdays
    
    const schedules = []
    let currentDate = dayjs(startDate)
    const end = dayjs(endDate)
    
    while (currentDate.isBefore(end) || currentDate.isSame(end, 'day')) {
      const dayOfWeek = currentDate.day()
      if (weekdays.includes(dayOfWeek)) {
        const scheduleStart = currentDate
          .hour(startTime.hour())
          .minute(startTime.minute())
          .second(0)
        const scheduleEnd = currentDate
          .hour(endTime.hour())
          .minute(endTime.minute())
          .second(0)
        
        schedules.push({
          courseId: courseId.value,
          startTime: scheduleStart.format('YYYY-MM-DD HH:mm:ss'),
          endTime: scheduleEnd.format('YYYY-MM-DD HH:mm:ss'),
          maxParticipants: batchFormData.maxParticipants,
          status: 1
        })
      }
      currentDate = currentDate.add(1, 'day')
    }
    
    // 批量创建排课
    let successCount = 0
    let failCount = 0
    const totalCount = schedules.length
    
    const createNext = (index) => {
      if (index >= schedules.length) {
        batchModalVisible.value = false
        message.success(`批量排课完成！成功${successCount}个，失败${failCount}个`)
        loadScheduleList()
        return
      }
      
      createCourseSchedule(schedules[index], {
        showDefaultMsg: false,
        onSuccess: () => {
          successCount++
          createNext(index + 1)
        },
        onError: () => {
          failCount++
          createNext(index + 1)
        }
      })
    }
    
    createNext(0)
  })
}

// 批量排课模态框取消
const handleBatchModalCancel = () => {
  batchModalVisible.value = false
  resetBatchForm()
}

// 重置批量排课表单
const resetBatchForm = () => {
  batchFormData.dateRange = null
  batchFormData.weekdays = []
  batchFormData.timeRange = null
  batchFormData.maxParticipants = courseInfo.value.maxParticipants || null
  batchFormRef.value?.resetFields()
}

// 清除过期排课
const handleClearExpired = () => {
  deleteExpiredSchedules({
    showDefaultMsg: false,
    onSuccess: (count) => {
      if (count > 0) {
        message.success(`成功清除 ${count} 个过期排课`)
        loadScheduleList()
      } else {
        message.info('没有过期的排课需要清除')
      }
    },
    onError: () => {
      message.error('清除过期排课失败')
    }
  })
}

// 返回课程列表
const handleBack = () => {
  router.push('/back/course')
}

// 页面加载
onMounted(() => {
  loadCourseInfo()
  loadScheduleList()
})
</script>

<style scoped lang="scss">
.course-schedule-management {
  padding: 24px;

  .course-info {
    margin-bottom: 16px;
  }

  .search-form {
    margin-bottom: 16px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}

.batch-preview {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 16px;
  min-height: 100px;
  
  .preview-count {
    font-size: 14px;
    margin-bottom: 12px;
    color: #333;
  }
  
  .preview-items {
    max-height: 200px;
    overflow-y: auto;
    
    .preview-item {
      padding: 8px 12px;
      background-color: #fff;
      border-radius: 4px;
      margin-bottom: 8px;
      font-size: 13px;
      color: #666;
      display: flex;
      align-items: center;
      gap: 8px;
      
      .fa {
        color: #1890ff;
      }
    }
    
    .preview-more {
      padding: 8px 12px;
      text-align: center;
      color: #999;
      font-size: 12px;
    }
  }
  
  .preview-empty {
    text-align: center;
    color: #999;
    padding: 40px 0;
    font-size: 14px;
  }
}
</style>
