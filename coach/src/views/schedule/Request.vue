<template>
  <div>
    <a-card :bordered="false">
      <template #title>
        <a-space>
          <span>排班申请</span>
        </a-space>
      </template>
      <template #extra>
        <a-button type="primary" @click="showRequestModal">提交申请</a-button>
      </template>
      
      <!-- 申请记录表格 -->
      <a-table 
        :dataSource="requests" 
        :columns="columns" 
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'requestType'">
            <a-tag :color="getRequestTypeColor(record.requestType)">
              {{ getRequestTypeName(record.requestType) }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusName(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'targetDate'">
            {{ record.targetDate }}
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ record.createTime }}
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewRequest(record)">查看</a-button>
              <a-button 
                v-if="record.status === 0" 
                type="link" 
                size="small" 
                @click="cancelRequest(record)"
                danger
              >
                取消
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 提交申请模态框 -->
    <a-modal 
      v-model:open="requestModalVisible" 
      title="提交排班申请"
      @ok="submitRequest"
      @cancel="closeRequestModal"
      :confirmLoading="submitting"
    >
      <a-form :model="requestForm" layout="vertical">
        <a-form-item label="申请类型" required>
          <a-select v-model:value="requestForm.requestType" @change="onRequestTypeChange">
            <a-select-option :value="1">调休申请</a-select-option>
            <a-select-option :value="2">加班申请</a-select-option>
            <a-select-option :value="3">换班申请</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="目标日期" required>
          <a-date-picker 
            v-model:value="requestForm.targetDate" 
            style="width: 100%" 
            :disabled-date="disabledDate"
          />
        </a-form-item>
        
        <a-form-item 
          v-if="requestForm.requestType === 3" 
          label="换班对象" 
          required
        >
          <a-select 
            v-model:value="requestForm.exchangeWithCoachId" 
            placeholder="请选择换班对象"
            @change="onExchangeCoachChange"
          >
            <a-select-option v-for="coach in coaches" :key="coach.id" :value="coach.id">
              {{ coach.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item 
          v-if="requestForm.requestType === 3" 
          label="被交换排班" 
          required
        >
          <a-select 
            v-model:value="requestForm.exchangeScheduleId" 
            placeholder="请选择被交换的排班"
            :disabled="!requestForm.exchangeWithCoachId"
          >
            <a-select-option v-for="schedule in exchangeSchedules" :key="schedule.id" :value="schedule.id">
              {{ formatDate(schedule.workDate) }} {{ formatTime(schedule.startTime) }}-{{ formatTime(schedule.endTime) }} {{ schedule.location }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="申请理由" required>
          <a-textarea 
            v-model:value="requestForm.reason" 
            placeholder="请输入申请理由" 
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="附件">
          <a-upload
            v-model:file-list="fileList"
            name="file"
            :multiple="false"
            :before-upload="beforeUpload"
            :max-count="1"
          >
            <a-button>
              <upload-outlined></upload-outlined>
              点击上传
            </a-button>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 查看申请详情模态框 -->
    <a-modal 
      v-model:open="detailModalVisible" 
      title="申请详情"
      @cancel="closeDetailModal"
      :footer="null"
    >
      <a-descriptions bordered size="small" :column="1">
        <a-descriptions-item label="申请类型">
          <a-tag :color="getRequestTypeColor(detailData.requestType)">
            {{ getRequestTypeName(detailData.requestType) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="目标日期">
          {{ detailData.targetDate }}
        </a-descriptions-item>
        <a-descriptions-item v-if="detailData.requestType === 3" label="换班对象">
          {{ detailData.exchangeCoachName }}
        </a-descriptions-item>
        <a-descriptions-item v-if="detailData.requestType === 3" label="被交换排班">
          {{ detailData.exchangeScheduleInfo }}
        </a-descriptions-item>
        <a-descriptions-item label="申请理由">
          {{ detailData.reason }}
        </a-descriptions-item>
        <a-descriptions-item label="申请状态">
          <a-tag :color="getStatusColor(detailData.status)">
            {{ getStatusName(detailData.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item v-if="detailData.status !== 0" label="审批意见">
          {{ detailData.approveRemark || '无' }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ detailData.createTime }}
        </a-descriptions-item>
        <a-descriptions-item v-if="detailData.status !== 0" label="审批时间">
          {{ detailData.approveTime || '无' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/store/user'
import { getScheduleRequests, submitScheduleRequest, cancelScheduleRequest } from '@/api/schedule'

// 用户存储
const userStore = useUserStore()

// 申请记录数据
const requests = ref([])
const loading = ref(false)
const submitting = ref(false)

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
    title: '申请类型',
    dataIndex: 'requestType',
    key: 'requestType'
  },
  {
    title: '目标日期',
    dataIndex: 'targetDate',
    key: 'targetDate'
  },
  {
    title: '申请理由',
    dataIndex: 'reason',
    key: 'reason'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime'
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action'
  }
]

// 模态框状态
const requestModalVisible = ref(false)
const detailModalVisible = ref(false)

// 申请表单
const requestForm = reactive({
  requestType: 1,
  targetDate: null,
  exchangeWithCoachId: undefined,
  exchangeScheduleId: undefined,
  reason: '',
  attachmentUrl: ''
})

// 文件上传
const fileList = ref([])

// 模拟数据：教练列表和可交换排班
const coaches = ref([
  { id: 1, name: '张教练' },
  { id: 2, name: '李教练' },
  { id: 3, name: '王教练' }
])

const exchangeSchedules = ref([
  { 
    id: 101, 
    workDate: '2025-12-20', 
    startTime: '09:00:00', 
    endTime: '17:00:00', 
    location: '主训练区' 
  },
  { 
    id: 102, 
    workDate: '2025-12-21', 
    startTime: '14:00:00', 
    endTime: '22:00:00', 
    location: 'VIP训练区' 
  }
])

// 详情数据
const detailData = ref({})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.substring(0, 5)
}

// 获取申请类型名称
const getRequestTypeName = (type) => {
  const typeMap = {
    1: '调休申请',
    2: '加班申请',
    3: '换班申请'
  }
  return typeMap[type] || '未知'
}

// 获取申请类型颜色
const getRequestTypeColor = (type) => {
  const colorMap = {
    1: 'blue',
    2: 'green',
    3: 'orange'
  }
  return colorMap[type] || 'default'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '待审批',
    1: '已通过',
    2: '已拒绝',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'gold',
    1: 'green',
    2: 'red',
    3: 'gray'
  }
  return colorMap[status] || 'default'
}

// 禁用日期（不允许选择过去日期）
const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

// 申请类型改变事件
const onRequestTypeChange = (value) => {
  // 清空换班相关字段
  if (value !== 3) {
    requestForm.exchangeWithCoachId = undefined
    requestForm.exchangeScheduleId = undefined
  }
}

// 换班对象改变事件
const onExchangeCoachChange = (value) => {
  // 清空被交换排班
  requestForm.exchangeScheduleId = undefined
  // 这里可以调用API获取该教练的排班信息
}

// 文件上传前检查
const beforeUpload = (file) => {
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('文件大小不能超过 2MB!')
  }
  return isLt2M || Upload.LIST_IGNORE
}

// 显示申请模态框
const showRequestModal = () => {
  // 重置表单
  requestForm.requestType = 1
  requestForm.targetDate = null
  requestForm.exchangeWithCoachId = undefined
  requestForm.exchangeScheduleId = undefined
  requestForm.reason = ''
  requestForm.attachmentUrl = ''
  fileList.value = []
  requestModalVisible.value = true
}

// 关闭申请模态框
const closeRequestModal = () => {
  requestModalVisible.value = false
}

// 显示详情模态框
const viewRequest = (record) => {
  detailData.value = record
  detailModalVisible.value = true
}

// 关闭详情模态框
const closeDetailModal = () => {
  detailModalVisible.value = false
}

// 提交申请
const submitRequest = async () => {
  if (!requestForm.targetDate) {
    message.warning('请选择目标日期')
    return
  }
  
  if (!requestForm.reason) {
    message.warning('请输入申请理由')
    return
  }
  
  if (requestForm.requestType === 3) {
    if (!requestForm.exchangeWithCoachId) {
      message.warning('请选择换班对象')
      return
    }
    if (!requestForm.exchangeScheduleId) {
      message.warning('请选择被交换的排班')
      return
    }
  }
  
  try {
    submitting.value = true
    
    // 构造提交数据
    const requestData = {
      requestType: requestForm.requestType,
      targetDate: requestForm.targetDate.format('YYYY-MM-DD'),
      reason: requestForm.reason
    }
    
    if (requestForm.requestType === 3) {
      requestData.exchangeWithCoachId = requestForm.exchangeWithCoachId
      requestData.exchangeScheduleId = requestForm.exchangeScheduleId
    }
    
    // 调用API提交申请
    await submitScheduleRequest(userStore.user?.id || 1, requestData)
    
    message.success('申请提交成功')
    closeRequestModal()
    loadRequests()
  } catch (error) {
    message.error('提交申请失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

// 取消申请
const cancelRequest = async (record) => {
  try {
    // 调用API取消申请
    await cancelScheduleRequest(record.id)
    
    message.success('申请已取消')
    loadRequests()
  } catch (error) {
    message.error('取消申请失败: ' + error.message)
  }
}

// 表格分页改变事件
const handleTableChange = (pager) => {
  pagination.current = pager.current
  pagination.pageSize = pager.pageSize
  loadRequests()
}

// 加载申请记录
const loadRequests = async () => {
  try {
    loading.value = true
    
    const response = await getScheduleRequests({
      coachId: userStore.user?.coachId || 1,
      page: pagination.current,
      size: pagination.pageSize
    })
    
    requests.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    message.error('加载申请记录失败: ' + error.message)
    // 使用模拟数据以便开发
    requests.value = [
      {
        id: 1,
        coachId: userStore.user?.coachId || 1,
        requestType: 1,
        targetDate: '2025-12-20',
        reason: '身体不适需要休息',
        status: 0,
        createTime: '2025-12-10 14:30:00'
      },
      {
        id: 2,
        coachId: userStore.user?.coachId || 1,
        requestType: 2,
        targetDate: '2025-12-25',
        reason: '圣诞节加班',
        status: 1,
        approveRemark: '同意加班申请',
        approveTime: '2025-12-11 09:15:00',
        createTime: '2025-12-08 10:20:00'
      }
    ]
    pagination.total = 2
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadRequests()
})
</script>

<style scoped>
/* 可以在这里添加自定义样式 */
</style>