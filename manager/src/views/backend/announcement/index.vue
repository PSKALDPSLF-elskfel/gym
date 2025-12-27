<template>
  <div class="announcement-management">
    <a-card title="公告管理" :bordered="false">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="标题">
          <a-input 
            v-model:value="searchForm.title" 
            placeholder="请输入标题"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select 
            v-model:value="searchForm.status" 
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="0">禁用</a-select-option>
            <a-select-option :value="1">启用</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <i class="fa fa-search"></i> 搜索
          </a-button>
          <a-button style="margin-left: 10px" @click="handleReset">
            <i class="fa fa-refresh"></i> 重置
          </a-button>
          <a-button type="primary" style="margin-left: 10px" @click="handleAdd">
            <i class="fa fa-plus"></i> 新增公告
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
        <!-- 状态 -->
        <template #status="{ record }">
          <a-tag v-if="record.status === 0" color="default">禁用</a-tag>
          <a-tag v-else-if="record.status === 1" color="success">启用</a-tag>
        </template>

        <!-- 内容 -->
        <template #content="{ record }">
          <div class="content-preview">
            {{ record.content }}
          </div>
        </template>

        <!-- 创建时间 -->
        <template #createTime="{ record }">
          {{ formatDateTime(record.createTime) }}
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <a-button type="link" size="small" @click="handleEdit(record)">
              <i class="fa fa-edit"></i> 编辑
            </a-button>
            <a-button 
              type="link" 
              size="small" 
              @click="handleToggleStatus(record)"
            >
              <i class="fa fa-toggle-on"></i> {{ record.status === 1 ? '禁用' : '启用' }}
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
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="700px"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        :model="formData"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="标题" required>
          <a-input 
            v-model:value="formData.title" 
            placeholder="请输入公告标题"
            :maxlength="200"
          />
        </a-form-item>

        <a-form-item label="内容" required>
          <a-textarea 
            v-model:value="formData.content" 
            placeholder="请输入公告内容"
            :rows="6"
            :maxlength="1000"
            show-count
          />
        </a-form-item>

        <a-form-item label="状态">
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
import { ref, reactive, computed, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { 
  getAnnouncementPage, 
  createAnnouncement, 
  updateAnnouncement, 
  deleteAnnouncement,
  updateAnnouncementStatus 
} from '@/api/AnnouncementApi';

// 搜索表单
const searchForm = reactive({
  title: '',
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
    title: '标题',
    dataIndex: 'title',
    width: 200
  },
  {
    title: '内容',
    key: 'content',
    width: 300,
    slots: { customRender: 'content' }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    slots: { customRender: 'status' }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    slots: { customRender: 'createTime' }
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
    fixed: 'right',
    slots: { customRender: 'action' }
  }
];

// 弹窗相关
const modalVisible = ref(false);
const modalTitle = ref('');
const isEdit = ref(false);
const formData = reactive({
  id: null,
  title: '',
  content: '',
  status: 1
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

// 查询公告列表
const fetchAnnouncements = () => {
  loading.value = true;
  
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    title: searchForm.title || null,
    status: searchForm.status !== undefined ? searchForm.status : null
  };
  
  getAnnouncementPage(params, {
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
  fetchAnnouncements();
};

// 重置
const handleReset = () => {
  searchForm.title = '';
  searchForm.status = null;
  currentPage.value = 1;
  fetchAnnouncements();
};

// 表格变化
const handleTableChange = (pag) => {
  currentPage.value = pag.current;
  pageSize.value = pag.pageSize;
  fetchAnnouncements();
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  modalTitle.value = '新增公告';
  
  // 重置表单数据
  Object.assign(formData, {
    id: null,
    title: '',
    content: '',
    status: 1
  });
  
  modalVisible.value = true;
};

// 编辑
const handleEdit = (record) => {
  isEdit.value = true;
  modalTitle.value = '编辑公告';
  
  // 使用Object.assign确保响应式更新
  Object.assign(formData, {
    id: record.id,
    title: record.title,
    content: record.content,
    status: record.status
  });
  
  modalVisible.value = true;
};

// 提交
const handleSubmit = () => {
  if (!formData.title) {
    message.error('请输入公告标题');
    return;
  }
  if (!formData.content) {
    message.error('请输入公告内容');
    return;
  }

  const params = {
    title: formData.title,
    content: formData.content,
    status: formData.status
  };

  if (isEdit.value) {
    updateAnnouncement(formData.id, params, {
      successMsg: '更新成功',
      onSuccess: () => {
        modalVisible.value = false;
        fetchAnnouncements();
      }
    });
  } else {
    createAnnouncement(params, {
      successMsg: '创建成功',
      onSuccess: () => {
        modalVisible.value = false;
        fetchAnnouncements();
      }
    });
  }
};

// 取消
const handleCancel = () => {
  modalVisible.value = false;
};

// 切换状态
const handleToggleStatus = (record) => {
  const newStatus = record.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? '启用' : '禁用';
  
  Modal.confirm({
    title: '确认操作',
    content: `确定要${statusText}该公告吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      updateAnnouncementStatus(record.id, newStatus, {
        successMsg: `${statusText}成功`,
        onSuccess: () => {
          fetchAnnouncements();
        }
      });
    }
  });
};

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除公告"${record.title}"吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteAnnouncement(record.id, {
        successMsg: '删除成功',
        onSuccess: () => {
          fetchAnnouncements();
        }
      });
    }
  });
};

// 初始化
onMounted(() => {
  fetchAnnouncements();
});
</script>

<style scoped lang="scss">
.announcement-management {
  .search-form {
    margin-bottom: 20px;
  }

  .content-preview {
    max-width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
