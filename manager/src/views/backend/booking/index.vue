<template>
  <div class="booking-management">
    <a-card title="预约管理" :bordered="false">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="姓名">
          <a-input 
            v-model:value="searchForm.nickname" 
            placeholder="请输入姓名"
            allow-clear
            style="width: 150px"
          />
        </a-form-item>
        
        <a-form-item label="课程名称">
          <a-input 
            v-model:value="searchForm.courseName" 
            placeholder="请输入课程名称"
            allow-clear
            style="width: 150px"
          />
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select 
            v-model:value="searchForm.status" 
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="0">已取消</a-select-option>
            <a-select-option :value="1">已预约</a-select-option>
            <a-select-option :value="2">已完成</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="预约日期">
          <a-range-picker 
            v-model:value="dateRange"
            format="YYYY-MM-DD"
            style="width: 240px"
          />
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <i class="fa fa-search"></i> 搜索
          </a-button>
          <a-button style="margin-left: 10px" @click="handleReset">
            <i class="fa fa-refresh"></i> 重置
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
        :scroll="{ x: 1500 }"
      >
        <!-- 用户信息 -->
        <template #userInfo="{ record }">
          <div class="user-info">
            <a-avatar :src="record.userAvatar" :size="32">
              {{ record.userNickname?.charAt(0) }}
            </a-avatar>
            <div class="user-detail">
              <div>{{ record.userNickname }}</div>
              <div class="text-secondary">{{ record.userPhone }}</div>
            </div>
          </div>
        </template>

        <!-- 会员类型 -->
        <template #memberType="{ record }">
          <a-tag v-if="record.userMemberType === 0" color="default">
            普通用户
          </a-tag>
          <a-tag v-else-if="record.userMemberType === 1" color="gold">
            黄金会员
          </a-tag>
          <a-tag v-else-if="record.userMemberType === 2" color="purple">
            铂金会员
          </a-tag>
        </template>

        <!-- 课程信息 -->
        <template #courseInfo="{ record }">
          <div class="course-info">
            <div class="course-name">{{ record.courseName }}</div>
            <div class="text-secondary">时长: {{ record.courseDuration }}分钟</div>
          </div>
        </template>

        <!-- 课程时间 -->
        <template #scheduleTime="{ record }">
          <div>
            <div>{{ formatDateTime(record.scheduleStartTime) }}</div>
            <div class="text-secondary">至 {{ formatTime(record.scheduleEndTime) }}</div>
          </div>
        </template>

        <!-- 价格信息 -->
        <template #priceInfo="{ record }">
          <div>
            <div>实付: ¥{{ record.actualPrice }}</div>
            <div v-if="record.savedAmount > 0" class="text-success">
              节省: ¥{{ record.savedAmount }}
            </div>
            <div v-else class="text-secondary">
              原价: ¥{{ record.originalPrice }}
            </div>
          </div>
        </template>

        <!-- 状态 -->
        <template #status="{ record }">
          <a-tag v-if="record.status === 0" color="default">
            已取消
          </a-tag>
          <a-tag v-else-if="record.status === 1" color="success">
            已预约
          </a-tag>
          <a-tag v-else-if="record.status === 2" color="blue">
            已完成
          </a-tag>
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <a-button type="link" size="small" @click="handleViewDetail(record)">
              <i class="fa fa-eye"></i> 详情
            </a-button>
            <a-button 
              v-if="record.status === 1"
              type="link" 
              size="small" 
              danger
              @click="handleCancel(record)"
            >
              <i class="fa fa-times"></i> 取消
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 预约详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="预约详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions v-if="currentDetail" :column="2" bordered>
        <a-descriptions-item label="预约ID">
          {{ currentDetail.id }}
        </a-descriptions-item>
        <a-descriptions-item label="预约时间">
          {{ formatDateTime(currentDetail.bookingTime) }}
        </a-descriptions-item>
        
        <a-descriptions-item label="用户昵称">
          {{ currentDetail.userNickname }}
        </a-descriptions-item>
        <a-descriptions-item label="手机号">
          {{ currentDetail.userPhone }}
        </a-descriptions-item>
        
        <a-descriptions-item label="会员类型">
          <a-tag v-if="currentDetail.userMemberType === 0" color="default">
            普通用户
          </a-tag>
          <a-tag v-else-if="currentDetail.userMemberType === 1" color="gold">
            黄金会员
          </a-tag>
          <a-tag v-else-if="currentDetail.userMemberType === 2" color="purple">
            铂金会员
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="预约状态">
          <a-tag v-if="currentDetail.status === 0" color="default">
            已取消
          </a-tag>
          <a-tag v-else-if="currentDetail.status === 1" color="success">
            已预约
          </a-tag>
          <a-tag v-else-if="currentDetail.status === 2" color="blue">
            已完成
          </a-tag>
        </a-descriptions-item>
        
        <a-descriptions-item label="课程名称" :span="2">
          {{ currentDetail.courseName }}
        </a-descriptions-item>
        
        <a-descriptions-item label="课程时长">
          {{ currentDetail.courseDuration }}分钟
        </a-descriptions-item>
        <a-descriptions-item label="排课ID">
          {{ currentDetail.scheduleId }}
        </a-descriptions-item>
        
        <a-descriptions-item label="开始时间">
          {{ formatDateTime(currentDetail.scheduleStartTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="结束时间">
          {{ formatDateTime(currentDetail.scheduleEndTime) }}
        </a-descriptions-item>
        
        <a-descriptions-item label="原价">
          ¥{{ currentDetail.originalPrice }}
        </a-descriptions-item>
        <a-descriptions-item label="实付价格">
          ¥{{ currentDetail.actualPrice }}
        </a-descriptions-item>
        
        <a-descriptions-item label="折扣率">
          {{ (currentDetail.discountRate * 100).toFixed(0) }}%
        </a-descriptions-item>
        <a-descriptions-item label="节省金额">
          <span :class="currentDetail.savedAmount > 0 ? 'text-success' : ''">
            ¥{{ currentDetail.savedAmount }}
          </span>
        </a-descriptions-item>
        
        <a-descriptions-item v-if="currentDetail.cancelTime" label="取消时间" :span="2">
          {{ formatDateTime(currentDetail.cancelTime) }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { getBookingPage, cancelBooking } from '@/api/CourseBookingApi';

// 搜索表单
const searchForm = reactive({
  nickname: '',
  courseName: '',
  status: null
});

// 日期范围
const dateRange = ref([]);

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
    title: '预约ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left'
  },
  {
    title: '用户信息',
    key: 'userInfo',
    width: 180,
    slots: { customRender: 'userInfo' }
  },
  {
    title: '会员类型',
    key: 'memberType',
    width: 100,
    slots: { customRender: 'memberType' }
  },
  {
    title: '课程信息',
    key: 'courseInfo',
    width: 150,
    slots: { customRender: 'courseInfo' }
  },
  {
    title: '课程时间',
    key: 'scheduleTime',
    width: 180,
    slots: { customRender: 'scheduleTime' }
  },
  {
    title: '价格信息',
    key: 'priceInfo',
    width: 120,
    slots: { customRender: 'priceInfo' }
  },
  {
    title: '预约时间',
    dataIndex: 'bookingTime',
    width: 160,
    customRender: ({ text }) => formatDateTime(text)
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    slots: { customRender: 'status' }
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
    slots: { customRender: 'action' }
  }
];

// 详情弹窗
const detailVisible = ref(false);
const currentDetail = ref(null);

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

// 格式化时间
const formatTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  if (isNaN(date.getTime())) return '-';
  
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${hours}:${minutes}`;
};

// 格式化本地日期
const formatLocalDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

// 查询预约列表
const fetchBookings = () => {
  loading.value = true;
  
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    nickname: searchForm.nickname || null,
    courseName: searchForm.courseName || null,
    status: searchForm.status !== undefined ? searchForm.status : null
  };
  
  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = formatLocalDate(dateRange.value[0]);
    params.endDate = formatLocalDate(dateRange.value[1]);
  }
  
  getBookingPage(params, {
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
  fetchBookings();
};

// 重置
const handleReset = () => {
  searchForm.nickname = '';
  searchForm.courseName = '';
  searchForm.status = null;
  dateRange.value = [];
  currentPage.value = 1;
  fetchBookings();
};

// 表格变化
const handleTableChange = (pag) => {
  currentPage.value = pag.current;
  pageSize.value = pag.pageSize;
  fetchBookings();
};

// 查看详情
const handleViewDetail = (record) => {
  currentDetail.value = record;
  detailVisible.value = true;
};

// 取消预约
const handleCancel = (record) => {
  Modal.confirm({
    title: '确认取消',
    content: `确定要取消用户"${record.userNickname}"的预约吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      cancelBooking(record.id, {
        successMsg: '预约已取消',
        onSuccess: () => {
          fetchBookings();
        }
      });
    }
  });
};

// 初始化
onMounted(() => {
  fetchBookings();
});
</script>

<style scoped lang="scss">
.booking-management {
  .search-form {
    margin-bottom: 20px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .user-detail {
      font-size: 12px;
    }
  }

  .course-info {
    .course-name {
      font-weight: 500;
      margin-bottom: 4px;
    }
  }

  .text-secondary {
    color: #999;
    font-size: 12px;
  }

  .text-success {
    color: #52c41a;
    font-weight: 500;
  }
}
</style>
