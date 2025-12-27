<template>
  <div class="user-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <i class="fas fa-users"></i>
        用户管理
      </h2>
    </div>

    <a-card class="box-card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <a-space :size="[16, 16]" wrap>
          <a-input
            v-model:value="searchForm.username"
            placeholder="用户名"
            class="search-input"
            allow-clear
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <i class="fas fa-user"></i>
            </template>
          </a-input>
          
          <a-input
            v-model:value="searchForm.email"
            placeholder="邮箱"
            class="search-input"
            allow-clear
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <i class="fas fa-envelope"></i>
            </template>
          </a-input>
          
          <a-select
            v-model:value="searchForm.userType"
            placeholder="用户类型"
            class="search-select"
            allow-clear
          >
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="USER">普通用户</a-select-option>
          </a-select>
          
          <a-select
            v-model:value="searchForm.status"
            placeholder="状态"
            class="search-select"
            allow-clear
          >
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
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
            新增用户
          </a-button>
          
        </a-space>
      </div>

      <!-- 用户表格 -->
      <a-table
        :columns="columns"
        :data-source="userList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        :row-key="record => record.id"
        :scroll="{ x: 1200 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <!-- 头像 -->
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="getAvatarUrl(record.avatar)" :size="40">
              {{ record.nickname?.charAt(0) || record.username?.charAt(0) }}
            </a-avatar>
          </template>

          <!-- 会员类型 -->
          <template v-else-if="column.key === 'memberType'">
            <a-tag :color="getMemberTypeColor(record.memberType)">
              {{ getMemberTypeLabel(record.memberType) }}
            </a-tag>
          </template>

          <!-- 用户类型 -->
          <template v-else-if="column.key === 'userType'">
            <a-tag :color="record.userType === 'ADMIN' ? 'red' : 'green'">
              {{ getUserTypeLabel(record.userType) }}
            </a-tag>
          </template>

          <!-- 状态 -->
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusLabel(record.status) }}
            </a-tag>
          </template>

          <!-- 创建时间 -->
          <template v-else-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>

          <!-- 操作 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleView(record)">
                <i class="fas fa-eye"></i> 查看
              </a-button>
              <a-button 
                type="link" 
                size="small" 
                danger
                :disabled="record.id === currentUserId"
                @click="handleDelete(record)"
              >
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
      :width="600"
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
        <a-form-item label="用户名" name="username">
          <a-input 
            v-model:value="formData.username" 
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </a-form-item>

        <a-form-item label="昵称" name="nickname">
          <a-input v-model:value="formData.nickname" placeholder="请输入昵称" />
        </a-form-item>

        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>

        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="用户类型" name="userType">
          <a-select v-model:value="formData.userType" placeholder="请选择用户类型">
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="USER">普通用户</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password 
            v-model:value="formData.password" 
            placeholder="请输入密码"
          />
        </a-form-item>

        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password 
            v-model:value="formData.confirmPassword" 
            placeholder="请再次输入密码"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useUserStore } from '@/store/user'
import { getUserPage, register, deleteUser } from '@/api/user'

const userStore = useUserStore()


// 当前登录用户ID
const currentUserId = computed(() => userStore.userId)

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  userType: undefined,
  status: undefined
})

// 表格数据
const userList = ref([])
const loading = ref(false)
const selectedRowKeys = ref([])

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
    title: '头像',
    key: 'avatar',
    width: 80,
    align: 'center'
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 120
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    width: 100
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 180
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
    width: 100,
    align: 'center'
  },
  {
    title: '用户类型',
    key: 'userType',
    width: 100,
    align: 'center'
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
    width: 200,
    fixed: 'right',
    align: 'center'
  }
]

// 行选择配置
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys) => {
    selectedRowKeys.value = keys
  },
  getCheckboxProps: (record) => ({
    disabled: record.id === currentUserId.value
  })
}))

// 对话框相关
const modalVisible = ref(false)
const modalTitle = ref('新增用户')
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  userType: undefined,
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' }
  ]
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      currentPage: pagination.current,
      size: pagination.pageSize,
      username: searchForm.username || undefined,
      email: searchForm.email || undefined,
      userType: searchForm.userType || undefined,
      status: searchForm.status || undefined
    }
    
    const res = await getUserPage(params)
    userList.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    message.error(error.message || '获取用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchUserList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.email = ''
  searchForm.userType = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchUserList()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchUserList()
}

// 新增
const handleAdd = () => {
  modalTitle.value = '新增用户'
  isEdit.value = false
  resetForm()
  modalVisible.value = true
}

// 查看用户详情
const handleView = (record) => {
  Modal.info({
    title: '用户详情',
    width: 600,
    content: () => {
      return [
        `用户名: ${record.username}`,
        `昵称: ${record.nickname || '-'}`,
        `邮箱: ${record.email}`,
        `手机号: ${record.phone || '-'}`,
        `会员类型: ${getMemberTypeLabel(record.memberType)}`,
        `用户类型: ${getUserTypeLabel(record.userType)}`,
        `状态: ${getStatusLabel(record.status)}`,
        `创建时间: ${formatDate(record.createTime)}`
      ].join('\n')
    },
    okText: '关闭'
  })
}

// 删除用户
const handleDelete = (record) => {
  // 不能删除当前登录用户
  if (record.id === currentUserId.value) {
    message.warning('不能删除当前登录用户')
    return
  }

  // 不能删除管理员
  if (record.userType === 'ADMIN') {
    message.warning('不能删除管理员账号')
    return
  }

  Modal.confirm({
    title: '确认删除',
    content: `确定要删除用户 "${record.username}" 吗？此操作不可恢复！`,
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      try {
        await deleteUser(record.id)
        message.success('删除成功')
        fetchUserList()
      } catch (error) {
        message.error(error.message || '删除失败')
        console.error(error)
      }
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    // 验证两次密码是否一致
    if (formData.password !== formData.confirmPassword) {
      message.error('两次输入的密码不一致')
      return
    }
    
    // 新增用户
    await register(formData)
    message.success('新增成功')
    
    modalVisible.value = false
    fetchUserList()
  } catch (error) {
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error(error.message || '新增失败')
    console.error(error)
  }
}

// 取消
const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.username = ''
  formData.nickname = ''
  formData.email = ''
  formData.phone = ''
  formData.userType = undefined
  formData.password = ''
  formData.confirmPassword = ''
  formRef.value?.clearValidate()
}

// 获取头像URL
const getAvatarUrl = (avatar) => {
  return avatar 
}

// 获取用户类型标签
const getUserTypeLabel = (userType) => {
  const typeMap = {
    'ADMIN': '管理员',
    'USER': '普通用户'
  }
  return typeMap[userType] || '未知'
}

// 获取状态标签
const getStatusLabel = (status) => {
  if (status === 1) return '正常'
  if (status === 0) return '禁用'
  return '未知'
}

// 获取状态颜色
const getStatusColor = (status) => {
  if (status === 1) return 'green'
  if (status === 0) return 'red'
  return 'default'
}

// 获取会员类型标签
const getMemberTypeLabel = (memberType) => {
  if (memberType === 1) return '黄金会员'
  if (memberType === 2) return '铂金会员'
  return '普通用户'
}

// 获取会员类型颜色
const getMemberTypeColor = (memberType) => {
  if (memberType === 1) return 'gold'
  if (memberType === 2) return 'purple'
  return 'default'
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

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style lang="scss" scoped>
.user-management {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
    
    .page-title {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #1a1a1a;
      display: flex;
      align-items: center;
      gap: 12px;
      
      i {
        color: #1890ff;
        font-size: 26px;
      }
    }
  }

  .box-card {
    overflow: hidden;
    
    .search-bar {
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #f0f0f0;
      
      .search-input {
        width: 200px;
        min-width: 150px;
      }
      
      .search-select {
        width: 150px;
        min-width: 120px;
      }
    }

    .action-bar {
      margin-bottom: 20px;
    }

    :deep(.ant-table) {
      .ant-table-cell {
        padding: 12px 8px;
      }
      
      // 表格容器响应式处理
      .ant-table-container {
        overflow-x: auto;
      }
      
      // 固定列阴影优化
      .ant-table-cell-fix-right {
        background: #fff;
      }
    }
  }
}

// 响应式适配
@media (max-width: 1200px) {
  .user-management {
    .box-card {
      .search-bar {
        .search-input,
        .search-select {
          width: 180px;
          min-width: 140px;
        }
      }
    }
  }
}

@media (max-width: 992px) {
  .user-management {
    .box-card {
      .search-bar {
        .search-input,
        .search-select {
          width: 160px;
          min-width: 140px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-management {
    padding: 10px;
    
    .page-header {
      .page-title {
        font-size: 20px;
        
        i {
          font-size: 22px;
        }
      }
    }
    
    .box-card {
      .search-bar {
        .search-input,
        .search-select,
        .ant-btn {
          width: 100%;
        }
      }
    }
  }
}

@media (max-width: 576px) {
  .user-management {
    padding: 8px;
    
    .box-card {
      :deep(.ant-card-body) {
        padding: 12px;
      }
      
      .search-bar {
        padding-bottom: 12px;
        margin-bottom: 12px;
      }
      
      .action-bar {
        margin-bottom: 12px;
        
        .ant-btn {
          width: 100%;
        }
      }
    }
  }
}
</style> 