<template>
  <div class="reviews-list-container">
    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :span="6">
        <a-card :bordered="false">
          <a-statistic
            title="总评价数"
            :value="statistics.totalReviews"
            :value-style="{ color: '#3f8600' }"
          >
            <template #prefix>
              <StarOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false">
          <a-statistic
            title="平均评分"
            :value="statistics.averageRating"
            :precision="2"
            :value-style="{ color: '#cf1322' }"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">/ 5.0</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false">
          <a-statistic
            title="五星好评"
            :value="statistics.rating5Count"
            :value-style="{ color: '#faad14' }"
          >
            <template #suffix>
              <span style="font-size: 14px; color: #999">
                / {{ statistics.totalReviews }}
              </span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false">
          <a-statistic
            title="回复率"
            :value="statistics.replyRate"
            :precision="1"
            suffix="%"
            :value-style="{ color: '#1890ff' }"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 评价列表 -->
    <a-card title="学员评价" :bordered="false">
      <!-- 筛选和排序 -->
      <template #extra>
        <a-space>
          <a-select
            v-model:value="filterRating"
            placeholder="筛选评分"
            style="width: 120px"
            allowClear
            @change="handleFilterChange"
          >
            <a-select-option :value="5">⭐⭐⭐⭐⭐</a-select-option>
            <a-select-option :value="4">⭐⭐⭐⭐</a-select-option>
            <a-select-option :value="3">⭐⭐⭐</a-select-option>
            <a-select-option :value="2">⭐⭐</a-select-option>
            <a-select-option :value="1">⭐</a-select-option>
          </a-select>
          <a-button @click="loadReviews">刷新</a-button>
        </a-space>
      </template>

      <a-spin :spinning="loading">
        <a-list
          :data-source="reviews"
          :pagination="pagination"
          @change="handlePageChange"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #avatar>
                  <a-avatar :src="item.userAvatar || undefined">
                    {{ item.userNickname?.charAt(0) }}
                  </a-avatar>
                </template>
                <template #title>
                  <a-space>
                    <span>{{ item.userNickname }}</span>
                    <a-rate :value="item.rating" disabled />
                    <span style="color: #999">
                      {{ formatTime(item.createTime) }}
                    </span>
                  </a-space>
                </template>
                <template #description>
                  <div style="margin-bottom: 8px">
                    <a-tag
                      v-for="tag in item.tagList"
                      :key="tag"
                      color="blue"
                    >
                      {{ tag }}
                    </a-tag>
                  </div>
                  <div style="margin-bottom: 8px">{{ item.content }}</div>
                  
                  <!-- 评价图片 -->
                  <div v-if="item.images && item.images.length" style="margin-bottom: 8px">
                    <a-image-preview-group>
                      <a-image
                        v-for="(img, index) in item.images"
                        :key="index"
                        :src="img"
                        :width="100"
                        style="margin-right: 8px"
                      />
                    </a-image-preview-group>
                  </div>

                  <!-- 教练回复 -->
                  <div v-if="item.reply" class="reply-box">
                    <div style="font-weight: bold; margin-bottom: 4px">
                      <MessageOutlined /> 我的回复：
                    </div>
                    <div>{{ item.reply }}</div>
                    <div style="color: #999; font-size: 12px; margin-top: 4px">
                      {{ formatTime(item.replyTime) }}
                    </div>
                  </div>
                </template>
              </a-list-item-meta>

              <template #actions>
                <a-space>
                  <span>
                    <LikeOutlined /> {{ item.helpfulCount || 0 }}
                  </span>
                  <a
                    v-if="!item.reply"
                    @click="showReplyModal(item)"
                  >
                    回复
                  </a>
                  <a @click="showDetailModal(item)">详情</a>
                </a-space>
              </template>
            </a-list-item>
          </template>
        </a-list>

        <a-empty v-if="!loading && reviews.length === 0" description="暂无评价" />
      </a-spin>
    </a-card>

    <!-- 回复对话框 -->
    <a-modal
      v-model:open="replyModalVisible"
      title="回复评价"
      @ok="handleReplySubmit"
      :confirm-loading="replyLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="评价内容">
          <div style="padding: 12px; background: #f5f5f5; border-radius: 4px">
            <div style="margin-bottom: 8px">
              <a-rate :value="currentReview.rating" disabled />
            </div>
            <div>{{ currentReview.content }}</div>
          </div>
        </a-form-item>
        <a-form-item
          label="回复内容"
          :rules="[{ required: true, message: '请输入回复内容' }]"
        >
          <a-textarea
            v-model:value="replyForm.reply"
            :rows="4"
            placeholder="请输入您的回复内容，最多300字"
            :maxlength="300"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情对话框 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="评价详情"
      :footer="null"
      width="600px"
    >
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="评价用户">
          <a-space>
            <a-avatar :src="currentReview.userAvatar || undefined">
              {{ currentReview.userNickname?.charAt(0) }}
            </a-avatar>
            <span>{{ currentReview.userNickname }}</span>
          </a-space>
        </a-descriptions-item>
        <a-descriptions-item label="评价类型">
          {{ currentReview.reviewTypeDesc }}
        </a-descriptions-item>
        <a-descriptions-item label="评分">
          <a-rate :value="currentReview.rating" disabled />
        </a-descriptions-item>
        <a-descriptions-item label="标签">
          <a-tag
            v-for="tag in currentReview.tagList"
            :key="tag"
            color="blue"
          >
            {{ tag }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="评价内容">
          {{ currentReview.content }}
        </a-descriptions-item>
        <a-descriptions-item label="评价图片" v-if="currentReview.images?.length">
          <a-image-preview-group>
            <a-image
              v-for="(img, index) in currentReview.images"
              :key="index"
              :src="img"
              :width="100"
              style="margin-right: 8px"
            />
          </a-image-preview-group>
        </a-descriptions-item>
        <a-descriptions-item label="点赞数">
          {{ currentReview.helpfulCount || 0 }}
        </a-descriptions-item>
        <a-descriptions-item label="评价时间">
          {{ formatTime(currentReview.createTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="回复内容" v-if="currentReview.reply">
          {{ currentReview.reply }}
        </a-descriptions-item>
        <a-descriptions-item label="回复时间" v-if="currentReview.replyTime">
          {{ formatTime(currentReview.replyTime) }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  StarOutlined,
  MessageOutlined,
  LikeOutlined
} from '@ant-design/icons-vue'
import {
  getReceivedReviews,
  replyReview,
  getMyStatistics
} from '@/api/review'
import dayjs from 'dayjs'

// 统计数据
const statistics = reactive({
  totalReviews: 0,
  averageRating: 0,
  rating5Count: 0,
  rating4Count: 0,
  rating3Count: 0,
  rating2Count: 0,
  rating1Count: 0,
  replyRate: 0
})

// 评价列表
const reviews = ref([])
const loading = ref(false)
const filterRating = ref(undefined)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 回复对话框
const replyModalVisible = ref(false)
const replyLoading = ref(false)
const currentReview = ref({})
const replyForm = reactive({
  reply: ''
})

// 详情对话框
const detailModalVisible = ref(false)

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getMyStatistics()
    Object.assign(statistics, res)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载评价列表
const loadReviews = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    }
    if (filterRating.value) {
      params.rating = filterRating.value
    }

    const res = await getReceivedReviews(params)
    reviews.value = res.records
    pagination.total = res.total
  } catch (error) {
    message.error('加载评价列表失败')
    console.error('加载评价列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  pagination.current = 1
  loadReviews()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page.current
  pagination.pageSize = page.pageSize
  loadReviews()
}

// 显示回复对话框
const showReplyModal = (review) => {
  currentReview.value = review
  replyForm.reply = ''
  replyModalVisible.value = true
}

// 提交回复
const handleReplySubmit = async () => {
  if (!replyForm.reply.trim()) {
    message.warning('请输入回复内容')
    return
  }

  replyLoading.value = true
  try {
    await replyReview(currentReview.value.id, { reply: replyForm.reply })
    message.success('回复成功')
    replyModalVisible.value = false
    loadReviews()
    loadStatistics()
  } catch (error) {
    message.error(error.message || '回复失败')
  } finally {
    replyLoading.value = false
  }
}

// 显示详情对话框
const showDetailModal = (review) => {
  currentReview.value = review
  detailModalVisible.value = true
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 页面加载
onMounted(() => {
  loadStatistics()
  loadReviews()
})
</script>

<style scoped>
.reviews-list-container {
  padding: 24px;
}

.reply-box {
  margin-top: 12px;
  padding: 12px;
  background: #f0f2f5;
  border-radius: 4px;
  border-left: 3px solid #1890ff;
}

:deep(.ant-list-item) {
  padding: 16px;
}

:deep(.ant-list-item-meta-description) {
  color: #262626;
}
</style>
