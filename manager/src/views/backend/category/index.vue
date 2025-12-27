<template>
  <div class="course-category-management">
    <a-card title="课程分类管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="分类名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入分类名称"
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
          新增分类
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
          <template v-if="column.key === 'icon'">
            <a-image v-if="record.icon" :src="record.icon" :width="50" />
            <span v-else>-</span>
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
        <a-form-item label="分类名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="分类描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入分类描述"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="分类图标" name="icon">
          <a-input v-model:value="formData.icon" placeholder="请输入图标URL" />
        </a-form-item>
        <a-form-item label="排序" name="sortOrder">
          <a-input-number
            v-model:value="formData.sortOrder"
            :min="0"
            :max="999"
            placeholder="请输入排序"
            style="width: 100%"
          />
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
  getCourseCategoryPage,
  createCourseCategory,
  updateCourseCategory,
  deleteCourseCategory
} from '@/api/CourseCategoryApi'

// 搜索表单
const searchForm = reactive({
  name: '',
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
  { title: '分类名称', dataIndex: 'name', key: 'name' },
  { title: '分类描述', dataIndex: 'description', key: 'description' },
  { title: '分类图标', dataIndex: 'icon', key: 'icon', width: 100 },
  { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 80 },
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
  description: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 获取分类列表
const fetchCategories = () => {
  loading.value = true
  getCourseCategoryPage(
    {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
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
  fetchCategories()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = undefined
  pagination.current = 1
  fetchCategories()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchCategories()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增分类'
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑分类'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    description: record.description,
    icon: record.icon,
    sortOrder: record.sortOrder,
    status: record.status
  })
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除分类"${record.name}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteCourseCategory(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchCategories()
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
      description: formData.description,
      icon: formData.icon,
      sortOrder: formData.sortOrder
    }
    
    if (isEdit.value) {
      params.status = formData.status
      updateCourseCategory(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchCategories()
        },
        onError: () => {
          confirmLoading.value = false
        }
      })
    } else {
      createCourseCategory(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchCategories()
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
  formData.description = ''
  formData.icon = ''
  formData.sortOrder = 0
  formData.status = 1
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped lang="scss">
.course-category-management {
  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
