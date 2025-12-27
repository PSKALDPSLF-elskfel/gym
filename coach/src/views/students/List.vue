<template>
  <div class="student-list">
    <a-card title="学员管理" :bordered="false">
      <!-- 搜索和筛选栏 -->
      <div class="search-bar">
        <a-space size="middle">
          <a-input-search
            v-model:value="searchParams.keyword"
            placeholder="搜索学员昵称或手机号"
            style="width: 260px"
            allow-clear
            @search="handleSearch"
          />
          <a-select
            v-model:value="searchParams.memberType"
            placeholder="会员类型"
            style="width: 150px"
            allow-clear
            @change="handleSearch"
          >
            <a-select-option :value="0">普通用户</a-select-option>
            <a-select-option :value="1">黄金会员</a-select-option>
            <a-select-option :value="2">铂金会员</a-select-option>
          </a-select>
          <a-button @click="handleReset">重置</a-button>
        </a-space>
      </div>

      <!-- 表格 -->
      <a-table
        class="student-table"
        :columns="columns"
        :data-source="students"
        :loading="loading"
        :pagination="pagination"
        :row-key="record => record.userId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="getAvatarUrl(record.avatar)" :size="40">
              {{ record.nickname?.charAt(0) }}
            </a-avatar>
          </template>
          
          <template v-if="column.key === 'memberType'">
            <a-tag :color="getMemberTypeColor(record.memberType)">
              {{ record.memberTypeName }}
            </a-tag>
            <a-tag v-if="!record.isMemberValid" color="red">已过期</a-tag>
          </template>
          
          <template v-if="column.key === 'trainingInfo'">
            <div>
              <div>计划数：{{ record.trainingPlanCount || 0 }}</div>
              <div v-if="record.latestPlanName" class="latest-plan">
                最近：{{ record.latestPlanName }}
              </div>
            </div>
          </template>
          
          <template v-if="column.key === 'remark'">
            <a-tooltip :title="record.coachRemark" placement="topLeft">
              <div class="remark-text">
                {{ record.coachRemark || '暂无备注' }}
              </div>
            </a-tooltip>
          </template>
          
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleViewDetail(record)">
                查看详情
              </a-button>
              <a-button type="link" size="small" @click="handleEditRemark(record)">
                修改备注
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 备注编辑弹窗 -->
    <a-modal
      v-model:open="remarkModalVisible"
      title="编辑学员备注"
      :confirm-loading="remarkSaving"
      @ok="handleSaveRemark"
    >
      <a-form :label-col="{ span: 4 }">
        <a-form-item label="学员">
          <span>{{ currentStudent?.nickname }}</span>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="remarkForm.remark"
            :rows="4"
            :maxlength="500"
            show-count
            placeholder="请输入备注内容..."
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getMyStudents, updateStudentRemark } from '@/api/student'
import { getAvatarUrl } from '@/utils/fileUtils'

const router = useRouter()
const loading = ref(false)
const students = ref([])

// 搜索参数
const searchParams = reactive({
  keyword: '',
  memberType: undefined
})

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`,
  pageSizeOptions: ['10', '20', '50', '100']
})

// 备注编辑
const remarkModalVisible = ref(false)
const remarkSaving = ref(false)
const currentStudent = ref(null)
const remarkForm = reactive({
  remark: ''
})

// 表格列配置
const columns = [
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    align: 'center'
  },
  {
    title: '学员昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    width: 120
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 130
  },
  {
    title: '会员类型',
    key: 'memberType',
    width: 140
  },
  {
    title: '训练信息',
    key: 'trainingInfo',
    width: 160
  },
  {
    title: '备注',
    key: 'remark',
    width: 180,
    ellipsis: true
  },
  {
    title: '加入时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 110,
    customRender: ({ text }) => text ? text.substring(0, 10) : '-'
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    align: 'center',
    fixed: 'right'
  }
]

// 获取会员类型颜色
const getMemberTypeColor = (type) => {
  const colorMap = {
    0: 'default',
    1: 'gold',
    2: 'blue'
  }
  return colorMap[type] || 'default'
}

// 加载学员列表
const loadStudents = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchParams
    }
    
    // 移除空值
    if (!params.keyword) delete params.keyword
    if (params.memberType === undefined) delete params.memberType
    
    const data = await getMyStudents(params)
    students.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载学员列表失败:', error)
    message.error('加载学员列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadStudents()
}

// 重置
const handleReset = () => {
  searchParams.keyword = ''
  searchParams.memberType = undefined
  pagination.current = 1
  loadStudents()
}

// 查看详情
const handleViewDetail = (record) => {
  router.push(`/students/${record.userId}`)
}

// 编辑备注
const handleEditRemark = (record) => {
  currentStudent.value = record
  remarkForm.remark = record.coachRemark || ''
  remarkModalVisible.value = true
}

// 保存备注
const handleSaveRemark = async () => {
  if (!currentStudent.value) return
  
  remarkSaving.value = true
  try {
    await updateStudentRemark({
      userId: currentStudent.value.userId,
      remark: remarkForm.remark
    })
    message.success('备注更新成功')
    remarkModalVisible.value = false
    loadStudents() // 刷新列表
  } catch (error) {
    console.error('更新备注失败:', error)
    message.error('更新备注失败')
  } finally {
    remarkSaving.value = false
  }
}

// 分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadStudents()
}

// 组件挂载
onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.student-list {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.student-table {
  margin-top: 20px;
}

.latest-plan {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.remark-text {
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
