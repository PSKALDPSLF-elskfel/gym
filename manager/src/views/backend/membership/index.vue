<template>
  <div class="membership-package-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <i class="fas fa-crown"></i>
        会员套餐管理
      </h2>
    </div>

    <a-card class="box-card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <a-space :size="[16, 16]" wrap>
          <a-input
            v-model:value="searchForm.name"
            placeholder="套餐名称"
            class="search-input"
            allow-clear
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <i class="fas fa-tag"></i>
            </template>
          </a-input>
          
          <a-select
            v-model:value="searchForm.memberType"
            placeholder="会员类型"
            class="search-select"
            allow-clear
          >
            <a-select-option :value="1">黄金会员</a-select-option>
            <a-select-option :value="2">铂金会员</a-select-option>
          </a-select>
          
          <a-select
            v-model:value="searchForm.status"
            placeholder="状态"
            class="search-select"
            allow-clear
          >
            <a-select-option :value="1">上架</a-select-option>
            <a-select-option :value="0">下架</a-select-option>
          </a-select>
          
          <a-button type="primary" @click="handleSearch">
            <template #icon>
              <i class="fas fa-search"></i>
            </template>
            搜索
          </a-button>
          
          <a-button @click="handleReset">
            <template #icon>
              <i class="fas fa-redo"></i>
            </template>
            重置
          </a-button>
        </a-space>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon>
              <i class="fas fa-plus"></i>
            </template>
            新增套餐
          </a-button>
        </a-space>
      </div>

      <!-- 套餐表格 -->
      <a-table
        :columns="columns"
        :data-source="packageList"
        :loading="loading"
        :pagination="pagination"
        :row-key="record => record.id"
        :scroll="{ x: 1400 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <!-- 会员类型 -->
          <template v-if="column.key === 'memberType'">
            <a-tag :color="record.memberType === 1 ? 'gold' : 'purple'">
              {{ record.memberTypeDisplayName }}
            </a-tag>
          </template>

          <!-- 价格 -->
          <template v-else-if="column.key === 'price'">
            <span class="price-text">¥{{ record.price }}</span>
          </template>

          <!-- 有效期 -->
          <template v-else-if="column.key === 'durationDays'">
            {{ formatDuration(record.durationDays) }}
          </template>

          <!-- 状态 -->
          <template v-else-if="column.key === 'status'">
            <a-switch
              :checked="record.status === 1"
              :loading="record.statusLoading"
              @change="handleStatusChange(record)"
            >
              <template #checkedChildren>上架</template>
              <template #unCheckedChildren>下架</template>
            </a-switch>
          </template>

          <!-- 创建时间 -->
          <template v-else-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>

          <!-- 操作 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                <i class="fas fa-edit"></i> 编辑
              </a-button>
              <a-button type="link" size="small" danger @click="handleDelete(record)">
                <i class="fas fa-trash"></i> 删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 编辑/新增对话框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="700"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="套餐名称" name="name">
          <a-input 
            v-model:value="formData.name" 
            placeholder="请输入套餐名称，如：黄金会员月卡"
          />
        </a-form-item>

        <a-form-item label="会员类型" name="memberType">
          <a-select v-model:value="formData.memberType" placeholder="请选择会员类型">
            <a-select-option :value="1">黄金会员</a-select-option>
            <a-select-option :value="2">铂金会员</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="有效天数" name="durationDays">
          <a-input-number 
            v-model:value="formData.durationDays" 
            :min="1"
            :max="3650"
            placeholder="请输入有效天数"
            style="width: 100%"
          />
          <div class="form-tip">建议：30天（月卡）、90天（季卡）、365天（年卡）</div>
        </a-form-item>

        <a-form-item label="价格" name="price">
          <a-input-number 
            v-model:value="formData.price" 
            :min="0.01"
            :max="99999.99"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          >
            <template #addonBefore>¥</template>
          </a-input-number>
        </a-form-item>

        <a-form-item label="套餐描述" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            placeholder="请输入套餐描述"
            :rows="3"
            :maxlength="500"
            show-count
          />
        </a-form-item>

        <a-form-item label="会员权益" name="benefits">
          <a-textarea 
            v-model:value="formData.benefits" 
            placeholder="请输入会员权益描述"
            :rows="3"
            :maxlength="500"
            show-count
          />
          <div class="form-tip">
            黄金会员：健身房自主训练权限+课程95折优惠<br>
            铂金会员：健身房自主训练权限+课程九折优惠
          </div>
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">上架</a-radio>
            <a-radio :value="0">下架</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  getMembershipPackagePage,
  createMembershipPackage,
  updateMembershipPackage,
  deleteMembershipPackage,
  updateMembershipPackageStatus
} from '@/api/MembershipPackageApi'

// 搜索表单
const searchForm = reactive({
  name: '',
  memberType: undefined,
  status: undefined
})

// 表格数据
const packageList = ref([])
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
  {
    title: '套餐ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '套餐名称',
    dataIndex: 'name',
    key: 'name',
    width: 180
  },
  {
    title: '会员类型',
    key: 'memberType',
    width: 120,
    align: 'center'
  },
  {
    title: '有效期',
    key: 'durationDays',
    width: 100,
    align: 'center'
  },
  {
    title: '价格',
    key: 'price',
    width: 120,
    align: 'center'
  },
  {
    title: '套餐描述',
    dataIndex: 'description',
    key: 'description',
    width: 200,
    ellipsis: true
  },
  {
    title: '会员权益',
    dataIndex: 'benefits',
    key: 'benefits',
    width: 200,
    ellipsis: true
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
    align: 'center'
  }
]

// 对话框相关
const modalVisible = ref(false)
const modalTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  name: '',
  memberType: undefined,
  durationDays: undefined,
  price: undefined,
  description: '',
  benefits: '',
  status: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入套餐名称', trigger: 'blur' },
    { max: 100, message: '套餐名称不能超过100个字符', trigger: 'blur' }
  ],
  memberType: [
    { required: true, message: '请选择会员类型', trigger: 'change' }
  ],
  durationDays: [
    { required: true, message: '请输入有效天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 3650, message: '有效天数必须在1-3650之间', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, max: 99999.99, message: '价格必须在0.01-99999.99之间', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '套餐描述不能超过500个字符', trigger: 'blur' }
  ],
  benefits: [
    { max: 500, message: '会员权益描述不能超过500个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载套餐列表
const loadPackageList = () => {
  loading.value = true
  
  getMembershipPackagePage(
    {
      current: pagination.current,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      memberType: searchForm.memberType,
      status: searchForm.status
    },
    {
      onSuccess: (res) => {
        packageList.value = res.records || []
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
  loadPackageList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.memberType = undefined
  searchForm.status = undefined
  pagination.current = 1
  loadPackageList()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadPackageList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增套餐'
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑套餐'
  
  formData.id = record.id
  formData.name = record.name
  formData.memberType = record.memberType
  formData.durationDays = record.durationDays
  formData.price = record.price
  formData.description = record.description
  formData.benefits = record.benefits
  formData.status = record.status
  
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除套餐"${record.name}"吗？此操作不可恢复。`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteMembershipPackage(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          loadPackageList()
        }
      })
    }
  })
}

// 状态切换
const handleStatusChange = (record) => {
  const newStatus = record.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'
  
  Modal.confirm({
    title: '确认操作',
    content: `确定要${statusText}套餐"${record.name}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      record.statusLoading = true
      updateMembershipPackageStatus(record.id, newStatus, {
        successMsg: `${statusText}成功`,
        onSuccess: () => {
          record.status = newStatus
          record.statusLoading = false
        },
        onError: () => {
          record.statusLoading = false
        }
      })
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate().then(() => {
    const apiMethod = isEdit.value ? updateMembershipPackage : createMembershipPackage
    const params = {
      name: formData.name,
      memberType: formData.memberType,
      durationDays: formData.durationDays,
      price: formData.price,
      description: formData.description,
      benefits: formData.benefits,
      status: formData.status
    }
    
    if (isEdit.value) {
      apiMethod(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          modalVisible.value = false
          loadPackageList()
        }
      })
    } else {
      apiMethod(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          modalVisible.value = false
          loadPackageList()
        }
      })
    }
  })
}

// 取消
const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.memberType = undefined
  formData.durationDays = undefined
  formData.price = undefined
  formData.description = ''
  formData.benefits = ''
  formData.status = 1
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 格式化有效期
const formatDuration = (days) => {
  if (days >= 365) {
    return `${Math.floor(days / 365)}年`
  } else if (days >= 30) {
    return `${Math.floor(days / 30)}个月`
  } else {
    return `${days}天`
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 页面加载时获取数据
onMounted(() => {
  loadPackageList()
})
</script>

<style scoped>
.membership-package-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title i {
  color: #667eea;
}

.box-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 150px;
}

.action-bar {
  margin-bottom: 16px;
}

.price-text {
  color: #f5222d;
  font-weight: 600;
  font-size: 16px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  line-height: 1.5;
}
</style>
