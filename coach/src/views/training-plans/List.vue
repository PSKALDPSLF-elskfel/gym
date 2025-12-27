<template>
  <a-card title="训练方案管理" :bordered="false">
    <template #extra>
      <a-space>
        <a-button @click="router.push('/training-plans/templates')">
          模板管理
        </a-button>
        <a-button type="primary" @click="handleCreate">
          创建方案
        </a-button>
      </a-space>
    </template>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <a-space>
        <a-input-search
          v-model:value="searchParams.keyword"
          placeholder="搜索学员或计划名称"
          style="width: 250px"
          allow-clear
          @search="handleSearch"
        />
        <a-select
          v-model:value="searchParams.goal"
          placeholder="训练目标"
          style="width: 150px"
          allow-clear
          @change="handleSearch"
        >
          <a-select-option value="减脂">减脂</a-select-option>
          <a-select-option value="增肌">增肌</a-select-option>
          <a-select-option value="塑形">塑形</a-select-option>
          <a-select-option value="康复">康复</a-select-option>
        </a-select>
        <a-select
          v-model:value="searchParams.status"
          placeholder="状态"
          style="width: 120px"
          allow-clear
          @change="handleSearch"
        >
          <a-select-option :value="1">进行中</a-select-option>
          <a-select-option :value="0">已结束</a-select-option>
        </a-select>
        <a-button @click="handleReset">重置</a-button>
      </a-space>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="plans"
      :loading="loading"
      :pagination="pagination"
      :row-key="record => record.id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'studentName'">
          <a-space>
            <a-avatar :src="record.userAvatar" :size="32">
              {{ record.userNickname?.charAt(0) || '学' }}
            </a-avatar>
            <span>{{ record.userNickname || '未知学员' }}</span>
          </a-space>
        </template>

        <template v-if="column.key === 'goal'">
          <a-tag color="blue">{{ record.goal }}</a-tag>
        </template>

        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">
            {{ record.status === 1 ? '进行中' : '已结束' }}
          </a-tag>
        </template>

        <template v-if="column.key === 'period'">
          {{ record.startDate }} ~ {{ record.endDate }}
        </template>

        <template v-if="column.key === 'progress'">
          <a-progress :percent="record.progress || 0" :size="'small'" />
        </template>

        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleViewDetail(record)">
              查看详情
            </a-button>
            <a-button 
              type="link" 
              size="small" 
              @click="handleEdit(record)"
              v-if="record.status === 1"
            >
              编辑
            </a-button>
            <a-popconfirm
              title="确定删除此训练计划吗?"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </a-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getTrainingPlans, deleteTrainingPlan, getTrainingPlanProgress } from '@/api/trainingPlan'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const plans = ref([])

// 搜索参数
const searchParams = reactive({
  keyword: '',
  goal: undefined,
  status: undefined
})

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 表格列
const columns = [
  { title: '计划名称', dataIndex: 'name', key: 'name', width: 180 },
  { title: '学员', key: 'studentName', width: 150 },
  { title: '训练目标', key: 'goal', width: 100 },
  { title: '训练周期', key: 'period', width: 220 },
  { title: '进度', key: 'progress', width: 120 },
  { title: '状态', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180, 
    customRender: ({ text }) => text ? text.substring(0, 16).replace('T', ' ') : '-' },
  { title: '操作', key: 'action', width: 200, fixed: 'right' }
]

// 加载训练计划列表
const loadPlans = async () => {
  loading.value = true
  try {
    // 获取教练ID
    const coachId = userStore.user?.coachId
    if (!coachId) {
      message.error('未找到教练信息，请重新登录')
      loading.value = false
      return
    }
    
    const params = {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      coachId: coachId,
      ...searchParams
    }
    
    if (!params.keyword) delete params.keyword
    if (params.goal === undefined) delete params.goal
    if (params.status === undefined) delete params.status
    
    const res = await getTrainingPlans(params)
    plans.value = res.records || []
    pagination.total = res.total || 0
    
    // 计算每个计划的进度
    for (const plan of plans.value) {
      try {
        const progressRes = await getTrainingPlanProgress(plan.id)
        plan.progress = Math.round(progressRes || 0)
      } catch (error) {
        console.error('获取进度失败:', error)
        plan.progress = 0
      }
    }
  } catch (error) {
    console.error('加载训练计划列表失败:', error)
    message.error('加载训练计划列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadPlans()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.goal = undefined
  searchParams.status = undefined
  pagination.current = 1
  loadPlans()
}

// 创建方案
const handleCreate = () => {
  router.push('/training-plans/create')
}

// 查看详情
const handleViewDetail = (record) => {
  router.push(`/training-plans/${record.id}`)
}

// 编辑
const handleEdit = (record) => {
  router.push(`/training-plans/create?id=${record.id}`)
}

// 删除
const handleDelete = async (id) => {
  try {
    await deleteTrainingPlan(id)
    message.success('删除成功')
    loadPlans()
  } catch (error) {
    message.error('删除失败')
  }
}

// 分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadPlans()
}

// 初始化
onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}
</style>
