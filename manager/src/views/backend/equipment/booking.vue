<template>
  <div class="booking-management">
    <a-card title="器材预约记录" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="器材">
          <a-select
            v-model:value="searchForm.equipmentId"
            placeholder="请选择器材"
            allow-clear
            show-search
            :filter-option="filterEquipmentOption"
            style="width: 200px"
          >
            <a-select-option
              v-for="item in equipmentList"
              :key="item.id"
              :value="item.id"
            >
              {{ item.name }} ({{ item.code }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户">
          <a-input
            v-model:value="searchForm.userId"
            placeholder="请输入用户ID"
            allow-clear
            style="width: 150px"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="0">已取消</a-select-option>
            <a-select-option :value="1">预约中</a-select-option>
            <a-select-option :value="2">使用中</a-select-option>
            <a-select-option :value="3">已完成</a-select-option>
            <a-select-option :value="4">超时未使用</a-select-option>
          </a-select>
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

      <!-- 统计信息 -->
      <a-row :gutter="16" class="stats-row">
        <a-col :span="6">
          <a-statistic title="总预约数" :value="stats.total" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="使用中" :value="stats.using" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="已完成" :value="stats.completed" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="已取消" :value="stats.cancelled" />
        </a-col>
      </a-row>

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
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'duration'">
            <span v-if="record.actualStartTime && record.actualEndTime">
              {{ calculateDuration(record.actualStartTime, record.actualEndTime) }} 分钟
            </span>
            <span v-else-if="record.startTime && record.endTime" style="color: #999">
              预计 {{ calculateDuration(record.startTime, record.endTime) }} 分钟
            </span>
            <span v-else>-</span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import {
  getEquipmentBookingPage,
  getEquipmentPage
} from '@/api/EquipmentApi'

// 器材列表
const equipmentList = ref([])

// 搜索表单
const searchForm = reactive({
  equipmentId: undefined,
  userId: '',
  status: undefined
})

// 表格列定义
const columns = [
  {
    title: '预约ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '用户昵称',
    dataIndex: 'userName',
    key: 'userName',
    width: 120
  },
  {
    title: '器材名称',
    dataIndex: 'equipmentName',
    key: 'equipmentName',
    width: 150
  },
  {
    title: '器材编号',
    dataIndex: 'equipmentCode',
    key: 'equipmentCode',
    width: 120
  },
  {
    title: '预约开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    width: 180
  },
  {
    title: '预约结束时间',
    dataIndex: 'endTime',
    key: 'endTime',
    width: 180
  },
  {
    title: '实际使用时长',
    key: 'duration',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  }
]

// 表格数据
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 统计数据
const stats = computed(() => {
  const data = {
    total: tableData.value.length,
    using: 0,
    completed: 0,
    cancelled: 0
  }
  
  tableData.value.forEach(item => {
    if (item.status === 2) data.using++
    if (item.status === 3) data.completed++
    if (item.status === 0) data.cancelled++
  })
  
  return data
})

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'default',  // 已取消
    1: 'blue',     // 预约中
    2: 'processing', // 使用中
    3: 'success',  // 已完成
    4: 'error'     // 超时未使用
  }
  return colorMap[status] || 'default'
}

// 计算时长（分钟）
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return 0
  const start = dayjs(startTime)
  const end = dayjs(endTime)
  return end.diff(start, 'minute')
}

// 器材筛选
const filterEquipmentOption = (input, option) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 获取器材列表
const fetchEquipmentList = () => {
  getEquipmentPage(
    {
      current: 1,
      size: 1000
    },
    {
      onSuccess: (res) => {
        equipmentList.value = res.records || []
      }
    }
  )
}

// 获取预约记录列表
const fetchBookingRecords = () => {
  loading.value = true
  getEquipmentBookingPage(
    {
      current: pagination.current,
      size: pagination.pageSize,
      equipmentId: searchForm.equipmentId || undefined,
      userId: searchForm.userId || undefined,
      status: searchForm.status
    },
    {
      onSuccess: (res) => {
        tableData.value = res.records || []
        pagination.total = res.total || 0
        loading.value = false
      },
      onError: () => {
        loading.value = false
      }
    }
  )
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchBookingRecords()
}

// 重置
const handleReset = () => {
  searchForm.equipmentId = undefined
  searchForm.userId = ''
  searchForm.status = undefined
  pagination.current = 1
  fetchBookingRecords()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchBookingRecords()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchEquipmentList()
  fetchBookingRecords()
})
</script>

<style scoped lang="scss">
.booking-management {
  .search-form {
    margin-bottom: 16px;
  }

  .stats-row {
    margin-bottom: 24px;
    padding: 20px;
    background: #fafafa;
    border-radius: 4px;
  }
}
</style>
