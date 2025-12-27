<template>
  <div class="membership-records-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">会员购买记录</h2>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <a-form layout="inline">
        <a-form-item label="用户ID">
          <a-input 
            v-model:value="searchForm.userId" 
            placeholder="请输入用户ID"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="会员类型">
          <a-select 
            v-model:value="searchForm.memberType" 
            placeholder="请选择会员类型"
            allow-clear
            style="width: 150px"
          >
            <a-select-option :value="1">黄金会员</a-select-option>
            <a-select-option :value="2">铂金会员</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select 
            v-model:value="searchForm.status" 
            placeholder="请选择状态"
            allow-clear
            style="width: 150px"
          >
            <a-select-option :value="0">已过期</a-select-option>
            <a-select-option :value="1">使用中</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <i class="fa fa-search"></i> 查询
          </a-button>
          <a-button style="margin-left: 10px" @click="handleReset">
            <i class="fa fa-refresh"></i> 重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <!-- 用户信息 -->
        <template #userInfo="{ record }">
          <div class="user-info">
            <!-- <div class="user-id">ID: {{ record.userId }}</div> -->
            <div class="user-nickname">{{ record.userNickname || '未设置昵称' }}</div>
          </div>
        </template>

        <!-- 套餐信息 -->
        <template #packageInfo="{ record }">
          <div class="package-info">
            <div class="package-name">{{ record.packageName }}</div>
            <div class="package-price">¥{{ record.price }}</div>
          </div>
        </template>

        <!-- 会员类型 -->
        <template #memberType="{ record }">
          <a-tag :color="record.memberType === 1 ? 'gold' : 'blue'">
            {{ record.memberTypeName }}
          </a-tag>
        </template>

        <!-- 有效期 -->
        <template #validity="{ record }">
          <div class="validity-info">
            <div class="date-range">
              {{ formatDate(record.startTime) }} 至 {{ formatDate(record.endTime) }}
            </div>
            <div class="remaining-days" :class="{ 'expired': record.expired }">
              {{ record.expired ? '已过期' : `剩余${record.remainingDays}天` }}
            </div>
          </div>
        </template>

        <!-- 状态 -->
        <template #status="{ record }">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">
            {{ record.statusName }}
          </a-tag>
        </template>

        <!-- 购买时间 -->
        <template #purchaseTime="{ record }">
          {{ formatDateTime(record.purchaseTime) }}
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getUserMembershipPage } from '@/api/UserMembershipApi'
import { message } from 'ant-design-vue'

// 搜索表单
const searchForm = reactive({
  userId: null,
  memberType: null,
  status: null
})

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
  showTotal: (total) => `共 ${total} 条记录`
})

// 表格列配置
const columns = [
  {
    title: '记录ID',
    dataIndex: 'id',
    key: 'id',
    width: 100
  },
  {
    title: '用户信息',
    key: 'userInfo',
    slots: { customRender: 'userInfo' },
    width: 180
  },
  {
    title: '套餐信息',
    key: 'packageInfo',
    slots: { customRender: 'packageInfo' },
    width: 200
  },
  {
    title: '会员类型',
    key: 'memberType',
    slots: { customRender: 'memberType' },
    width: 120
  },
  {
    title: '有效期',
    key: 'validity',
    slots: { customRender: 'validity' },
    width: 250
  },
  {
    title: '状态',
    key: 'status',
    slots: { customRender: 'status' },
    width: 100
  },
  {
    title: '购买时间',
    key: 'purchaseTime',
    slots: { customRender: 'purchaseTime' },
    width: 180
  }
]

// 加载数据
const loadData = () => {
  loading.value = true

  const params = {
    current: pagination.current,
    size: pagination.pageSize,
    userId: searchForm.userId || null,
    memberType: searchForm.memberType,
    status: searchForm.status
  }

  getUserMembershipPage(params, {
    onSuccess: (res) => {
      tableData.value = res.records || []
      pagination.total = res.total || 0
      loading.value = false
    },
    onError: (err) => {
      loading.value = false
      message.error('加载数据失败')
    }
  })
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.userId = null
  searchForm.memberType = null
  searchForm.status = null
  pagination.current = 1
  loadData()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 格式化日期
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 页面加载
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.membership-records-page {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  background: #fff;
  padding: 20px 24px;
  margin-bottom: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.search-form {
  background: #fff;
  padding: 24px;
  margin-bottom: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.table-container {
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

/* 用户信息样式 */
.user-info .user-id {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.user-info .user-nickname {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 套餐信息样式 */
.package-info .package-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.package-info .package-price {
  font-size: 14px;
  color: #f5222d;
  font-weight: bold;
}

/* 有效期样式 */
.validity-info .date-range {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.validity-info .remaining-days {
  font-size: 12px;
  color: #52c41a;
  font-weight: 500;
}

.validity-info .remaining-days.expired {
  color: #999;
}
</style>
