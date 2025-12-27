<template>
  <div class="maintenance-management">
    <a-card title="器材维护记录" :bordered="false">
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
        <a-form-item label="维护类型">
          <a-select
            v-model:value="searchForm.maintenanceType"
            placeholder="请选择维护类型"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">保养</a-select-option>
            <a-select-option :value="2">维修</a-select-option>
            <a-select-option :value="3">检查</a-select-option>
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
          新增维护记录
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
          <template v-if="column.key === 'maintenanceType'">
            <a-tag :color="getTypeColor(record.maintenanceType)">
              {{ record.maintenanceTypeName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'cost'">
            <span v-if="record.cost">¥{{ record.cost }}</span>
            <span v-else style="color: #999">-</span>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增维护记录对话框 -->
    <a-modal
      v-model:open="modalVisible"
      title="新增维护记录"
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
        <a-form-item label="器材" name="equipmentId">
          <a-select
            v-model:value="formData.equipmentId"
            placeholder="请选择器材"
            show-search
            :filter-option="filterEquipmentOption"
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
        <a-form-item label="维护类型" name="maintenanceType">
          <a-select
            v-model:value="formData.maintenanceType"
            placeholder="请选择维护类型"
          >
            <a-select-option :value="1">保养</a-select-option>
            <a-select-option :value="2">维修</a-select-option>
            <a-select-option :value="3">检查</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维护时间" name="maintenanceDate">
          <a-date-picker
            v-model:value="formData.maintenanceDate"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            placeholder="请选择维护时间"
          />
        </a-form-item>
        <a-form-item label="维护内容" name="content">
          <a-textarea
            v-model:value="formData.content"
            placeholder="请输入维护内容"
            :rows="4"
            :maxlength="500"
          />
        </a-form-item>
        <a-form-item label="费用">
          <a-input-number
            v-model:value="formData.cost"
            placeholder="请输入费用"
            :min="0"
            :max="99999.99"
            :precision="2"
            style="width: 100%"
            addon-before="¥"
          />
        </a-form-item>
        <a-form-item label="维护人员">
          <a-input
            v-model:value="formData.operator"
            placeholder="请输入维护人员"
            :maxlength="50"
          />
        </a-form-item>
        <a-form-item label="下次维护日期">
          <a-date-picker
            v-model:value="formData.nextMaintenanceDate"
            style="width: 100%"
            placeholder="请选择下次维护日期"
          />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注"
            :rows="3"
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
  getMaintenancePage,
  createMaintenance
} from '@/api/EquipmentApi'
import {
  getEquipmentPage
} from '@/api/EquipmentApi'

// 器材列表
const equipmentList = ref([])

// 搜索表单
const searchForm = reactive({
  equipmentId: undefined,
  maintenanceType: undefined
})

// 表格列定义
const columns = [
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
    title: '维护类型',
    dataIndex: 'maintenanceType',
    key: 'maintenanceType',
    width: 100
  },
  {
    title: '维护时间',
    dataIndex: 'maintenanceDate',
    key: 'maintenanceDate',
    width: 180
  },
  {
    title: '维护内容',
    dataIndex: 'content',
    key: 'content',
    width: 200,
    ellipsis: true
  },
  {
    title: '费用',
    dataIndex: 'cost',
    key: 'cost',
    width: 100
  },
  {
    title: '维护人员',
    dataIndex: 'operator',
    key: 'operator',
    width: 100
  },
  {
    title: '下次维护日期',
    dataIndex: 'nextMaintenanceDate',
    key: 'nextMaintenanceDate',
    width: 120
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

// 对话框相关
const modalVisible = ref(false)
const formRef = ref(null)
const formData = reactive({
  equipmentId: undefined,
  maintenanceType: undefined,
  maintenanceDate: null,
  content: '',
  cost: null,
  operator: '',
  nextMaintenanceDate: null,
  remark: ''
})

// 表单验证规则
const formRules = {
  equipmentId: [
    { required: true, message: '请选择器材', trigger: 'change' }
  ],
  maintenanceType: [
    { required: true, message: '请选择维护类型', trigger: 'change' }
  ],
  maintenanceDate: [
    { required: true, message: '请选择维护时间', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入维护内容', trigger: 'blur' }
  ]
}

// 获取维护类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    1: 'blue',      // 保养
    2: 'orange',    // 维修
    3: 'green'      // 检查
  }
  return colorMap[type] || 'default'
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
      size: 1000,
      status: 1 // 只查询正常状态的器材
    },
    {
      onSuccess: (res) => {
        equipmentList.value = res.records || []
      }
    }
  )
}

// 获取维护记录列表
const fetchMaintenanceRecords = () => {
  loading.value = true
  getMaintenancePage(
    {
      current: pagination.current,
      size: pagination.pageSize,
      equipmentId: searchForm.equipmentId || undefined,
      maintenanceType: searchForm.maintenanceType || undefined
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
  fetchMaintenanceRecords()
}

// 重置
const handleReset = () => {
  searchForm.equipmentId = undefined
  searchForm.maintenanceType = undefined
  pagination.current = 1
  fetchMaintenanceRecords()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchMaintenanceRecords()
}

// 新增
const handleAdd = () => {
  resetForm()
  modalVisible.value = true
}

// 对话框确定
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    
    const params = {
      equipmentId: formData.equipmentId,
      maintenanceType: formData.maintenanceType,
      maintenanceDate: formData.maintenanceDate.format('YYYY-MM-DD HH:mm:ss'),
      content: formData.content,
      cost: formData.cost,
      operator: formData.operator,
      nextMaintenanceDate: formData.nextMaintenanceDate ? formData.nextMaintenanceDate.format('YYYY-MM-DD') : null,
      remark: formData.remark
    }
    
    createMaintenance(params, {
      successMsg: '创建成功',
      onSuccess: () => {
        modalVisible.value = false
        fetchMaintenanceRecords()
      }
    })
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
  formData.equipmentId = undefined
  formData.maintenanceType = undefined
  formData.maintenanceDate = null
  formData.content = ''
  formData.cost = null
  formData.operator = ''
  formData.nextMaintenanceDate = null
  formData.remark = ''
  formRef.value?.clearValidate()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchEquipmentList()
  fetchMaintenanceRecords()
})
</script>

<style scoped lang="scss">
.maintenance-management {
  .search-form {
    margin-bottom: 16px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
