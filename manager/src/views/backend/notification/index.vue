<template>
  <div class="notification-management">
    <a-card title="通知管理" :bordered="false">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="通知类型">
          <a-select 
            v-model:value="searchForm.notificationType" 
            placeholder="请选择通知类型"
            allow-clear
            style="width: 150px"
          >
            <a-select-option value="SYSTEM">系统通知</a-select-option>
            <a-select-option value="BOOKING">预约提醒</a-select-option>
            <a-select-option value="COURSE">课程相关</a-select-option>
            <a-select-option value="EQUIPMENT">器材相关</a-select-option>
            <a-select-option value="MEMBERSHIP">会员相关</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="目标用户类型">
          <a-select 
            v-model:value="searchForm.targetUserType" 
            placeholder="请选择目标用户类型"
            allow-clear
            style="width: 150px"
          >
            <a-select-option value="USER">小程序用户</a-select-option>
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="COACH">教练</a-select-option>
            <a-select-option value="ALL">全部</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="状态">
          <a-select 
            v-model:value="searchForm.status" 
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="0">已删除</a-select-option>
            <a-select-option :value="1">已发送</a-select-option>
            <a-select-option :value="2">草稿</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <i class="fa fa-search"></i> 搜索
          </a-button>
          <a-button style="margin-left: 10px" @click="handleReset">
            <i class="fa fa-refresh"></i> 重置
          </a-button>
          <a-button type="primary" style="margin-left: 10px" @click="handleSendNotification">
            <i class="fa fa-send"></i> 发送通知
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <!-- 通知类型 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'notificationType'">
            <a-tag :color="getTypeColor(record.notificationType)">
              {{ record.notificationTypeDesc || record.notificationType }}
            </a-tag>
          </template>

          <!-- 目标用户类型 -->
          <template v-else-if="column.key === 'targetUserType'">
            <a-tag :color="getTargetTypeColor(record.targetUserType)">
              {{ getTargetTypeText(record.targetUserType) }}
            </a-tag>
          </template>

          <!-- 优先级 -->
          <template v-else-if="column.key === 'priority'">
            <a-tag :color="getPriorityColor(record.priority)">
              {{ record.priorityDesc || getPriorityText(record.priority) }}
            </a-tag>
          </template>

          <!-- 状态 -->
          <template v-else-if="column.key === 'status'">
            <a-tag v-if="record.status === 0" color="default">已删除</a-tag>
            <a-tag v-else-if="record.status === 1" color="success">已发送</a-tag>
            <a-tag v-else-if="record.status === 2" color="warning">草稿</a-tag>
          </template>

          <!-- 内容预览 -->
          <template v-else-if="column.key === 'content'">
            <div class="content-preview" :title="record.content">
              {{ record.content }}
            </div>
          </template>

          <!-- 发送时间 -->
          <template v-else-if="column.key === 'sendTime'">
            {{ formatDateTime(record.sendTime) }}
          </template>

          <!-- 操作 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleViewDetail(record)">
                <i class="fa fa-eye"></i> 详情
              </a-button>
              <a-button 
                type="link" 
                size="small" 
                danger
                @click="handleDelete(record)"
              >
                <i class="fa fa-trash"></i> 删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 发送通知弹窗 -->
    <a-modal
      v-model:open="sendModalVisible"
      title="发送通知"
      width="800px"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        :model="formData"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 19 }"
      >
        <a-form-item label="通知类型" required>
          <a-select 
            v-model:value="formData.notificationType" 
            placeholder="请选择通知类型"
          >
            <a-select-option value="SYSTEM">系统通知</a-select-option>
            <a-select-option value="BOOKING">预约提醒</a-select-option>
            <a-select-option value="COURSE">课程相关</a-select-option>
            <a-select-option value="EQUIPMENT">器材相关</a-select-option>
            <a-select-option value="MEMBERSHIP">会员相关</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="通知标题" required>
          <a-input 
            v-model:value="formData.title" 
            placeholder="请输入通知标题"
            :maxlength="200"
          />
        </a-form-item>

        <a-form-item label="通知内容" required>
          <a-textarea 
            v-model:value="formData.content" 
            placeholder="请输入通知内容"
            :rows="6"
            :maxlength="1000"
            show-count
          />
        </a-form-item>

        <a-form-item label="目标用户类型" required>
          <a-select 
            v-model:value="formData.targetUserType" 
            placeholder="请选择目标用户类型"
            @change="handleTargetTypeChange"
          >
            <a-select-option value="USER">小程序用户</a-select-option>
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="COACH">教练</a-select-option>
            <a-select-option value="ALL">全部</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="优先级" required>
          <a-radio-group v-model:value="formData.priority">
            <a-radio :value="0">低</a-radio>
            <a-radio :value="1">中</a-radio>
            <a-radio :value="2">高</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="通知图标">
          <a-input 
            v-model:value="formData.icon" 
            placeholder="请输入通知图标URL（可选）"
          />
        </a-form-item>

        <a-form-item label="关联业务ID">
          <a-input 
            v-model:value="formData.relatedId" 
            placeholder="请输入关联业务ID（可选）"
          />
        </a-form-item>

        <a-form-item label="关联业务类型">
          <a-select 
            v-model:value="formData.relatedType" 
            placeholder="请选择关联业务类型（可选）"
            allow-clear
          >
            <a-select-option value="COURSE">课程</a-select-option>
            <a-select-option value="BOOKING">预约</a-select-option>
            <a-select-option value="EQUIPMENT">器材</a-select-option>
            <a-select-option value="MEMBERSHIP">会员</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="是否需要已读">
          <a-radio-group v-model:value="formData.isReadRequired">
            <a-radio :value="1">是</a-radio>
            <a-radio :value="0">否</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="备注">
          <a-textarea 
            v-model:value="formData.remark" 
            placeholder="请输入备注（可选）"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="通知详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="1" bordered v-if="currentRecord">
        <a-descriptions-item label="通知ID">
          {{ currentRecord.id }}
        </a-descriptions-item>
        <a-descriptions-item label="通知类型">
          <a-tag :color="getTypeColor(currentRecord.notificationType)">
            {{ currentRecord.notificationTypeDesc || currentRecord.notificationType }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="通知标题">
          {{ currentRecord.title }}
        </a-descriptions-item>
        <a-descriptions-item label="通知内容">
          {{ currentRecord.content }}
        </a-descriptions-item>
        <a-descriptions-item label="目标用户类型">
          <a-tag :color="getTargetTypeColor(currentRecord.targetUserType)">
            {{ getTargetTypeText(currentRecord.targetUserType) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="目标用户ID">
          {{ currentRecord.targetUserId || '全部用户' }}
        </a-descriptions-item>
        <a-descriptions-item label="优先级">
          <a-tag :color="getPriorityColor(currentRecord.priority)">
            {{ currentRecord.priorityDesc || getPriorityText(currentRecord.priority) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag v-if="currentRecord.status === 0" color="default">已删除</a-tag>
          <a-tag v-else-if="currentRecord.status === 1" color="success">已发送</a-tag>
          <a-tag v-else-if="currentRecord.status === 2" color="warning">草稿</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="关联业务ID">
          {{ currentRecord.relatedId || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="关联业务类型">
          {{ currentRecord.relatedType || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="通知图标">
          {{ currentRecord.icon || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="是否需要已读">
          {{ currentRecord.isReadRequired === 1 ? '是' : '否' }}
        </a-descriptions-item>
        <a-descriptions-item label="发送时间">
          {{ formatDateTime(currentRecord.sendTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="定时发送时间">
          {{ formatDateTime(currentRecord.scheduledTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="备注">
          {{ currentRecord.remark || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formatDateTime(currentRecord.createTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="更新时间">
          {{ formatDateTime(currentRecord.updateTime) }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { 
  getNotificationList,
  sendNotification,
  deleteNotification
} from '@/api/NotificationApi';

// 搜索表单
const searchForm = reactive({
  notificationType: null,
  targetUserType: null,
  status: null
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页配置
const pagination = computed(() => ({
  current: currentPage.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
}));

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80
  },
  {
    title: '通知类型',
    key: 'notificationType',
    width: 120
  },
  {
    title: '标题',
    dataIndex: 'title',
    width: 180
  },
  {
    title: '内容',
    key: 'content',
    width: 250
  },
  {
    title: '目标用户',
    key: 'targetUserType',
    width: 120
  },
  {
    title: '优先级',
    key: 'priority',
    width: 100
  },
  {
    title: '状态',
    key: 'status',
    width: 100
  },
  {
    title: '发送时间',
    key: 'sendTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right'
  }
];

// 弹窗相关
const sendModalVisible = ref(false);
const detailModalVisible = ref(false);
const currentRecord = ref(null);

const formData = reactive({
  notificationType: 'SYSTEM',
  title: '',
  content: '',
  targetUserType: 'ALL',
  targetUserId: null,
  relatedId: '',
  relatedType: null,
  icon: '',
  priority: 1,
  isReadRequired: 1,
  remark: ''
});

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  if (isNaN(date.getTime())) return '-';
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 获取通知类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    'SYSTEM': 'blue',
    'BOOKING': 'green',
    'COURSE': 'orange',
    'EQUIPMENT': 'purple',
    'MEMBERSHIP': 'red'
  };
  return colorMap[type] || 'default';
};

// 获取目标用户类型颜色
const getTargetTypeColor = (type) => {
  const colorMap = {
    'USER': 'blue',
    'ADMIN': 'red',
    'COACH': 'green',
    'ALL': 'purple'
  };
  return colorMap[type] || 'default';
};

// 获取目标用户类型文本
const getTargetTypeText = (type) => {
  const textMap = {
    'USER': '小程序用户',
    'ADMIN': '管理员',
    'COACH': '教练',
    'ALL': '全部'
  };
  return textMap[type] || type;
};

// 获取优先级颜色
const getPriorityColor = (priority) => {
  const colorMap = {
    0: 'default',
    1: 'warning',
    2: 'error'
  };
  return colorMap[priority] || 'default';
};

// 获取优先级文本
const getPriorityText = (priority) => {
  const textMap = {
    0: '低',
    1: '中',
    2: '高'
  };
  return textMap[priority] || '-';
};

// 查询通知列表
const fetchNotifications = () => {
  loading.value = true;
  
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    notificationType: searchForm.notificationType || null,
    targetUserType: searchForm.targetUserType || null,
    status: searchForm.status !== undefined ? searchForm.status : null
  };
  
  getNotificationList(params, {
    onSuccess: (res) => {
      tableData.value = res.records || [];
      total.value = res.total || 0;
      loading.value = false;
    },
    onError: () => {
      loading.value = false;
    }
  });
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchNotifications();
};

// 重置
const handleReset = () => {
  searchForm.notificationType = null;
  searchForm.targetUserType = null;
  searchForm.status = null;
  currentPage.value = 1;
  fetchNotifications();
};

// 表格变化
const handleTableChange = (pag) => {
  currentPage.value = pag.current;
  pageSize.value = pag.pageSize;
  fetchNotifications();
};

// 发送通知
const handleSendNotification = () => {
  // 重置表单数据
  Object.assign(formData, {
    notificationType: 'SYSTEM',
    title: '',
    content: '',
    targetUserType: 'ALL',
    targetUserId: null,
    relatedId: '',
    relatedType: null,
    icon: '',
    priority: 1,
    isReadRequired: 1,
    remark: ''
  });
  
  sendModalVisible.value = true;
};

// 目标用户类型变化
const handleTargetTypeChange = (value) => {
  // 如果选择全部，清空目标用户ID
  if (value === 'ALL') {
    formData.targetUserId = null;
  }
};

// 提交
const handleSubmit = () => {
  if (!formData.notificationType) {
    message.error('请选择通知类型');
    return;
  }
  if (!formData.title) {
    message.error('请输入通知标题');
    return;
  }
  if (!formData.content) {
    message.error('请输入通知内容');
    return;
  }
  if (!formData.targetUserType) {
    message.error('请选择目标用户类型');
    return;
  }

  const params = {
    notificationType: formData.notificationType,
    title: formData.title,
    content: formData.content,
    targetUserType: formData.targetUserType,
    targetUserId: formData.targetUserId || null,
    relatedId: formData.relatedId || null,
    relatedType: formData.relatedType || null,
    icon: formData.icon || null,
    priority: formData.priority,
    isReadRequired: formData.isReadRequired,
    remark: formData.remark || null
  };

  sendNotification(params, {
    successMsg: '发送成功',
    onSuccess: () => {
      sendModalVisible.value = false;
      fetchNotifications();
    }
  });
};

// 取消
const handleCancel = () => {
  sendModalVisible.value = false;
};

// 查看详情
const handleViewDetail = (record) => {
  currentRecord.value = record;
  detailModalVisible.value = true;
};

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除通知"${record.title}"吗？删除后将无法恢复。`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteNotification(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchNotifications();
        }
      });
    }
  });
};

// 初始化
onMounted(() => {
  fetchNotifications();
});
</script>

<style scoped lang="scss">
.notification-management {
  .search-form {
    margin-bottom: 20px;
  }

  .content-preview {
    max-width: 250px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
