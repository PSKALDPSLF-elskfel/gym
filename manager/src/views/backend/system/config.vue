<template>
  <div class="sys-config-management">
    <a-card title="系统参数配置" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="配置键">
          <a-input
            v-model:value="searchForm.configKey"
            placeholder="请输入配置键"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="配置分组">
          <a-select
            v-model:value="searchForm.configGroup"
            placeholder="请选择配置分组"
            allow-clear
            style="width: 150px"
          >
            <a-select-option v-for="group in configGroups" :key="group" :value="group">
              {{ group }}
            </a-select-option>
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
          新增配置
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
          <template v-if="column.key === 'isSystem'">
            <a-tag :color="record.isSystem === 1 ? 'blue' : 'default'">
              {{ record.isSystem === 1 ? '系统内置' : '自定义' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button 
                type="link" 
                size="small" 
                danger 
                :disabled="record.isSystem === 1"
                @click="handleDelete(record)"
              >
                删除
              </a-button>
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
        <a-form-item label="配置键" name="configKey">
          <a-input 
            v-model:value="formData.configKey" 
            placeholder="请输入配置键" 
            :disabled="isEdit"
          />
        </a-form-item>
        <a-form-item label="配置值" name="configValue">
          <a-textarea
            v-model:value="formData.configValue"
            placeholder="请输入配置值"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="配置描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入配置描述"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="配置分组" name="configGroup">
          <a-input v-model:value="formData.configGroup" placeholder="请输入配置分组" />
        </a-form-item>
        <a-form-item v-if="isEdit" label="状态" name="status">
          <a-radio-group v-model:value="formData.status" :disabled="formData.isSystem === 1">
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
  getSysConfigPage,
  createSysConfig,
  updateSysConfig,
  deleteSysConfig,
  getAllConfigGroups
} from '@/api/SysConfigApi'

// 搜索表单
const searchForm = reactive({
  configKey: '',
  configGroup: undefined,
  status: undefined
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const configGroups = ref([])

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
  { title: '配置键', dataIndex: 'configKey', key: 'configKey' },
  { title: '配置值', dataIndex: 'configValue', key: 'configValue', ellipsis: true },
  { title: '配置描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '配置分组', dataIndex: 'configGroup', key: 'configGroup', width: 120 },
  { title: '类型', dataIndex: 'isSystem', key: 'isSystem', width: 100 },
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
  configKey: '',
  configValue: '',
  description: '',
  configGroup: '',
  isSystem: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  configKey: [
    { required: true, message: '请输入配置键', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '请输入配置值', trigger: 'blur' }
  ]
}

// 获取配置分组
const fetchConfigGroups = () => {
  getAllConfigGroups({
    onSuccess: (res) => {
      configGroups.value = res || []
    }
  })
}

// 获取配置列表
const fetchConfigs = () => {
  loading.value = true
  getSysConfigPage(
    {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      configKey: searchForm.configKey || undefined,
      configGroup: searchForm.configGroup,
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
  fetchConfigs()
}

// 重置
const handleReset = () => {
  searchForm.configKey = ''
  searchForm.configGroup = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchConfigs()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchConfigs()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增配置'
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑配置'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    configKey: record.configKey,
    configValue: record.configValue,
    description: record.description,
    configGroup: record.configGroup,
    isSystem: record.isSystem,
    status: record.status
  })
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  if (record.isSystem === 1) {
    message.warning('系统内置配置不允许删除')
    return
  }
  
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除配置"${record.configKey}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteSysConfig(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchConfigs()
          fetchConfigGroups()
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
      configValue: formData.configValue,
      description: formData.description,
      configGroup: formData.configGroup
    }
    
    if (isEdit.value) {
      params.status = formData.status
      updateSysConfig(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchConfigs()
          fetchConfigGroups()
        },
        onError: () => {
          confirmLoading.value = false
        }
      })
    } else {
      params.configKey = formData.configKey
      createSysConfig(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          confirmLoading.value = false
          modalVisible.value = false
          fetchConfigs()
          fetchConfigGroups()
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
  formData.configKey = ''
  formData.configValue = ''
  formData.description = ''
  formData.configGroup = ''
  formData.isSystem = 0
  formData.status = 1
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  fetchConfigs()
  fetchConfigGroups()
})
</script>

<style scoped lang="scss">
.sys-config-management {
  .search-form {
    margin-bottom: 20px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
