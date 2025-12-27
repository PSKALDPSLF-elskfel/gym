<template>
  <div class="equipment-management">
    <a-card title="器材管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="器材名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入器材名称"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="器材类型">
          <a-select
            v-model:value="searchForm.category"
            placeholder="请选择器材类型"
            allow-clear
            style="width: 150px"
          >
            <a-select-option value="有氧">有氧器材</a-select-option>
            <a-select-option value="力量">力量器材</a-select-option>
            <a-select-option value="自由重量">自由重量</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="0">故障</a-select-option>
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="2">维护中</a-select-option>
            <a-select-option :value="3">报废</a-select-option>
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
          新增器材
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
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'currentUser'">
            <span v-if="record.currentUserId">
              {{ record.currentUserName || '使用中' }}
            </span>
            <span v-else style="color: #999">-</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这个器材吗？"
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

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="800"
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
        <a-form-item label="器材名称" name="name">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入器材名称"
            :maxlength="100"
          />
        </a-form-item>
        <a-form-item label="器材编号" name="code">
          <a-input
            v-model:value="formData.code"
            placeholder="请输入器材编号"
            :maxlength="50"
          />
        </a-form-item>
        <a-form-item label="器材类型" name="category">
          <a-select
            v-model:value="formData.category"
            placeholder="请选择器材类型"
          >
            <a-select-option value="有氧">有氧器材</a-select-option>
            <a-select-option value="力量">力量器材</a-select-option>
            <a-select-option value="自由重量">自由重量</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="品牌">
          <a-input
            v-model:value="formData.brand"
            placeholder="请输入品牌"
            :maxlength="100"
          />
        </a-form-item>
        <a-form-item label="型号">
          <a-input
            v-model:value="formData.model"
            placeholder="请输入型号"
            :maxlength="100"
          />
        </a-form-item>
        <a-form-item label="位置">
          <a-input
            v-model:value="formData.location"
            placeholder="请输入器材位置"
            :maxlength="100"
          />
        </a-form-item>
        <a-form-item label="购买日期">
          <a-date-picker
            v-model:value="formData.purchaseDate"
            style="width: 100%"
            placeholder="请选择购买日期"
          />
        </a-form-item>
        <a-form-item label="保修到期日">
          <a-date-picker
            v-model:value="formData.warrantyExpire"
            style="width: 100%"
            placeholder="请选择保修到期日"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select
            v-model:value="formData.status"
            placeholder="请选择状态"
          >
            <a-select-option :value="0">故障</a-select-option>
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="2">维护中</a-select-option>
            <a-select-option :value="3">报废</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注"
            :rows="4"
            :maxlength="500"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import {
  getEquipmentPage,
  createEquipment,
  updateEquipment,
  deleteEquipment
} from '@/api/EquipmentApi'

// 搜索表单
const searchForm = reactive({
  name: '',
  category: undefined,
  status: undefined
})

// 表格列定义
const columns = [
  {
    title: '器材编号',
    dataIndex: 'code',
    key: 'code',
    width: 120
  },
  {
    title: '器材名称',
    dataIndex: 'name',
    key: 'name',
    width: 150
  },
  {
    title: '器材类型',
    dataIndex: 'category',
    key: 'category',
    width: 100
  },
  {
    title: '品牌',
    dataIndex: 'brand',
    key: 'brand',
    width: 100
  },
  {
    title: '型号',
    dataIndex: 'model',
    key: 'model',
    width: 100
  },
  {
    title: '位置',
    dataIndex: 'location',
    key: 'location',
    width: 100
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '当前使用者',
    dataIndex: 'currentUserId',
    key: 'currentUser',
    width: 120
  },
  {
    title: '购买日期',
    dataIndex: 'purchaseDate',
    key: 'purchaseDate',
    width: 120
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 150
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

// 对话框相关
const modalVisible = ref(false)
const modalTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  name: '',
  code: '',
  category: undefined,
  brand: '',
  model: '',
  location: '',
  purchaseDate: null,
  warrantyExpire: null,
  status: 1,
  remark: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入器材名称', trigger: 'blur' },
    { max: 100, message: '器材名称长度不能超过100个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入器材编号', trigger: 'blur' },
    { max: 50, message: '器材编号长度不能超过50个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择器材类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'error',    // 故障
    1: 'success',  // 正常
    2: 'warning',  // 维护中
    3: 'default'   // 报废
  }
  return colorMap[status] || 'default'
}

// 获取器材列表
const fetchEquipments = () => {
  loading.value = true
  getEquipmentPage(
    {
      current: pagination.current,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      category: searchForm.category || undefined,
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
  fetchEquipments()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.category = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchEquipments()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchEquipments()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增器材'
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑器材'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    code: record.code,
    category: record.category,
    brand: record.brand,
    model: record.model,
    location: record.location,
    purchaseDate: record.purchaseDate ? dayjs(record.purchaseDate) : null,
    warrantyExpire: record.warrantyExpire ? dayjs(record.warrantyExpire) : null,
    status: record.status,
    remark: record.remark
  })
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  deleteEquipment(record.id, {
    successMsg: '删除成功',
    onSuccess: () => {
      fetchEquipments()
    }
  })
}

// 对话框确定
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    
    const params = {
      name: formData.name,
      code: formData.code,
      category: formData.category,
      brand: formData.brand,
      model: formData.model,
      location: formData.location,
      purchaseDate: formData.purchaseDate ? formData.purchaseDate.format('YYYY-MM-DD') : null,
      warrantyExpire: formData.warrantyExpire ? formData.warrantyExpire.format('YYYY-MM-DD') : null,
      status: formData.status,
      remark: formData.remark
    }
    
    if (isEdit.value) {
      updateEquipment(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          modalVisible.value = false
          fetchEquipments()
        }
      })
    } else {
      createEquipment(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          modalVisible.value = false
          fetchEquipments()
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
  formData.code = ''
  formData.category = undefined
  formData.brand = ''
  formData.model = ''
  formData.location = ''
  formData.purchaseDate = null
  formData.warrantyExpire = null
  formData.status = 1
  formData.remark = ''
  formRef.value?.clearValidate()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchEquipments()
})
</script>

<style scoped lang="scss">
.equipment-management {
  .search-form {
    margin-bottom: 16px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
