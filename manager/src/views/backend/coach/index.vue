<template>
  <div class="coach-management">
    <a-card title="教练管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="姓名">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入姓名"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="专业领域">
          <a-input
            v-model:value="searchForm.specialty"
            placeholder="请输入专业领域"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">在职</a-select-option>
            <a-select-option :value="0">离职</a-select-option>
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

      <!-- 操作按钮 -->
      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          新增教练
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
          <template v-if="column.key === 'avatar'">
            <a-avatar v-if="record.avatar" :src="record.avatar" :size="40" />
            <a-avatar v-else :size="40">{{ record.nickname ? record.nickname.charAt(0) : 'U' }}</a-avatar>
          </template>
          <template v-else-if="column.key === 'rating'">
            <a-rate :value="record.rating" disabled allow-half />
            <span style="margin-left: 8px">{{ record.rating }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :confirm-loading="confirmLoading"
      width="600px"
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
        <a-form-item v-if="!isEdit" label="选择用户" name="userId">
          <a-select
            v-model:value="formData.userId"
            placeholder="请选择用户"
            show-search
            :filter-option="filterUserOption"
          >
            <a-select-option v-for="user in availableUsers" :key="user.id" :value="user.id">
              {{ user.nickname || user.username }} ({{ user.username }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item v-else label="教练姓名">
          <a-input :value="formData.nickname || formData.username" disabled />
        </a-form-item>
        <a-form-item label="专业领域" name="specialty">
          <a-input v-model:value="formData.specialty" placeholder="请输入专业领域" />
        </a-form-item>
        <a-form-item label="资质证书" name="certificate">
          <a-textarea
            v-model:value="formData.certificate"
            placeholder="请输入资质证书信息（JSON格式）"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="个人简介" name="introduction">
          <a-textarea
            v-model:value="formData.introduction"
            placeholder="请输入个人简介"
            :rows="4"
          />
        </a-form-item>
        <a-form-item v-if="isEdit" label="平均评分" name="rating">
          <a-input-number
            v-model:value="formData.rating"
            :min="0"
            :max="5"
            :step="0.1"
            :precision="1"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item v-if="isEdit" label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">在职</a-radio>
            <a-radio :value="0">离职</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import {
  getCoachPage,
  createCoach,
  updateCoach,
  deleteCoach
} from '@/api/CoachApi'
import { getUserPage } from '@/api/user'

// 搜索表单
const searchForm = reactive({
  name: '',
  specialty: '',
  status: undefined
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const availableUsers = ref([])

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
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '头像', dataIndex: 'avatar', key: 'avatar', width: 80 },
  { title: '姓名', dataIndex: 'nickname', key: 'nickname' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '专业领域', dataIndex: 'specialty', key: 'specialty' },
  { title: '评分', dataIndex: 'rating', key: 'rating', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 150, fixed: 'right' }
]

// 对话框
const modalVisible = ref(false)
const modalTitle = ref('')
const confirmLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  id: null,
  userId: null,
  nickname: '',
  username: '',
  specialty: '',
  certificate: '',
  introduction: '',
  rating: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  userId: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ]
}

// 用户筛选
const filterUserOption = (input, option) => {
  return option.children[0].children.toLowerCase().includes(input.toLowerCase())
}

// 获取可用用户列表
const fetchAvailableUsers = () => {
  getUserPage(
    {
      current: 1,
      size: 100
    },
    {
      onSuccess: (res) => {
        availableUsers.value = res.records || []
      }
    }
  )
}

// 获取教练列表
const fetchCoaches = () => {
  loading.value = true
  getCoachPage(
    {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      specialty: searchForm.specialty || undefined,
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
  fetchCoaches()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.specialty = ''
  searchForm.status = undefined
  pagination.current = 1
  fetchCoaches()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchCoaches()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增教练'
  resetForm()
  fetchAvailableUsers()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑教练'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    userId: record.userId,
    nickname: record.nickname,
    username: record.username,
    specialty: record.specialty,
    certificate: record.certificate,
    introduction: record.introduction,
    rating: record.rating,
    status: record.status
  })
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除教练"${record.nickname}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteCoach(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchCoaches()
        }
      })
    }
  })
}

// 对话框确定
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    confirmLoading.value = true
    
    const params = {
      specialty: formData.specialty,
      certificate: formData.certificate,
      introduction: formData.introduction
    }
    
    if (isEdit.value) {
      params.rating = formData.rating
      params.status = formData.status
      updateCoach(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchCoaches()
        },
        onError: () => {
          confirmLoading.value = false
        }
      })
    } else {
      params.userId = formData.userId
      createCoach(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchCoaches()
        },
        onError: () => {
          confirmLoading.value = false
        }
      })
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 对话框取消
const handleModalCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.userId = null
  formData.nickname = ''
  formData.username = ''
  formData.specialty = ''
  formData.certificate = ''
  formData.introduction = ''
  formData.rating = 0
  formData.status = 1
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  fetchCoaches()
})
</script>

<style scoped lang="scss">
.coach-management {
  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
