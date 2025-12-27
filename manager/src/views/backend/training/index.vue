<template>
  <div class="training-action-management">
    <a-card title="动作库管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="动作名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入动作名称"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="动作分类">
          <a-select
            v-model:value="searchForm.category"
            placeholder="请选择分类"
            allow-clear
            style="width: 150px"
          >
            <a-select-option value="胸">胸</a-select-option>
            <a-select-option value="背">背</a-select-option>
            <a-select-option value="腿">腿</a-select-option>
            <a-select-option value="肩">肩</a-select-option>
            <a-select-option value="臂">臂</a-select-option>
            <a-select-option value="腹">腹</a-select-option>
            <a-select-option value="核心">核心</a-select-option>
            <a-select-option value="全身">全身</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="难度">
          <a-select
            v-model:value="searchForm.difficulty"
            placeholder="请选择难度"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">简单</a-select-option>
            <a-select-option :value="2">中等</a-select-option>
            <a-select-option :value="3">困难</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
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
          新增动作
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
          <template v-if="column.key === 'difficulty'">
            <a-tag :color="getDifficultyColor(record.difficulty)">
              {{ record.difficultyName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.status === 1 ? '启用' : '禁用' }}
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
      width="700px"
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
        <a-form-item label="动作名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入动作名称" />
        </a-form-item>
        <a-form-item label="动作分类" name="category">
          <a-select v-model:value="formData.category" placeholder="请选择分类">
            <a-select-option value="胸">胸</a-select-option>
            <a-select-option value="背">背</a-select-option>
            <a-select-option value="腿">腿</a-select-option>
            <a-select-option value="肩">肩</a-select-option>
            <a-select-option value="臂">臂</a-select-option>
            <a-select-option value="腹">腹</a-select-option>
            <a-select-option value="核心">核心</a-select-option>
            <a-select-option value="全身">全身</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="难度" name="difficulty">
          <a-select v-model:value="formData.difficulty" placeholder="请选择难度">
            <a-select-option :value="1">简单</a-select-option>
            <a-select-option :value="2">中等</a-select-option>
            <a-select-option :value="3">困难</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="目标肌群" name="targetMuscle">
          <a-input v-model:value="formData.targetMuscle" placeholder="请输入目标肌群" />
        </a-form-item>
        <a-form-item label="动作描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入动作描述"
            :rows="4"
          />
        </a-form-item>
        <a-form-item label="示范视频URL" name="videoUrl">
          <a-input v-model:value="formData.videoUrl" placeholder="请输入示范视频URL" />
        </a-form-item>
        <a-form-item label="示范图片URL" name="imageUrl">
          <a-input v-model:value="formData.imageUrl" placeholder="请输入示范图片URL" />
        </a-form-item>
        <a-form-item v-if="isEdit" label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
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
  getTrainingActionPage,
  createTrainingAction,
  updateTrainingAction,
  deleteTrainingAction
} from '@/api/TrainingActionApi'

// 搜索表单
const searchForm = reactive({
  name: '',
  category: undefined,
  difficulty: undefined,
  status: undefined
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
  showTotal: (total) => `共 ${total} 条`
})

// 表格列配置
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '动作名称', dataIndex: 'name', key: 'name' },
  { title: '分类', dataIndex: 'category', key: 'category', width: 100 },
  { title: '难度', dataIndex: 'difficulty', key: 'difficulty', width: 100 },
  { title: '目标肌群', dataIndex: 'targetMuscle', key: 'targetMuscle' },
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
  name: '',
  category: undefined,
  difficulty: undefined,
  targetMuscle: '',
  description: '',
  videoUrl: '',
  imageUrl: '',
  status: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入动作名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择动作分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ]
}

// 获取难度颜色
const getDifficultyColor = (difficulty) => {
  const colors = { 1: 'success', 2: 'warning', 3: 'error' }
  return colors[difficulty] || 'default'
}

// 获取动作列表
const fetchActions = () => {
  loading.value = true
  getTrainingActionPage(
    {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      category: searchForm.category,
      difficulty: searchForm.difficulty,
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
  fetchActions()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.category = undefined
  searchForm.difficulty = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchActions()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchActions()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增动作'
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑动作'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    category: record.category,
    difficulty: record.difficulty,
    targetMuscle: record.targetMuscle,
    description: record.description,
    videoUrl: record.videoUrl,
    imageUrl: record.imageUrl,
    status: record.status
  })
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除动作"${record.name}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteTrainingAction(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchActions()
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
      name: formData.name,
      category: formData.category,
      difficulty: formData.difficulty,
      targetMuscle: formData.targetMuscle,
      description: formData.description,
      videoUrl: formData.videoUrl,
      imageUrl: formData.imageUrl
    }
    
    if (isEdit.value) {
      params.status = formData.status
      updateTrainingAction(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchActions()
        },
        onError: () => {
          confirmLoading.value = false
        }
      })
    } else {
      createTrainingAction(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchActions()
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
  formData.name = ''
  formData.category = undefined
  formData.difficulty = undefined
  formData.targetMuscle = ''
  formData.description = ''
  formData.videoUrl = ''
  formData.imageUrl = ''
  formData.status = 1
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  fetchActions()
})
</script>

<style scoped lang="scss">
.training-action-management {
  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
