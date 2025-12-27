<template>
  <div class="statistics-container">
    <a-card title="评价统计概览" :bordered="false">
      <a-spin :spinning="loading">
        <!-- 核心统计指标 -->
        <a-row :gutter="16" style="margin-bottom: 24px">
          <a-col :span="6">
            <a-card :bordered="false" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <a-statistic
                title="总评价数"
                :value="statistics.totalReviews"
                :value-style="{ color: '#fff', fontSize: '32px' }"
                style="color: #fff"
              >
                <template #prefix>
                  <StarOutlined style="color: #fff" />
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card :bordered="false" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <a-statistic
                title="平均评分"
                :value="statistics.averageRating"
                :precision="2"
                :value-style="{ color: '#fff', fontSize: '32px' }"
                style="color: #fff"
              >
                <template #suffix>
                  <span style="font-size: 16px; color: rgba(255,255,255,0.8)">/ 5.0</span>
                </template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card :bordered="false" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <a-statistic
                title="回复率"
                :value="statistics.replyRate"
                :precision="1"
                suffix="%"
                :value-style="{ color: '#fff', fontSize: '32px' }"
                style="color: #fff"
              />
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card :bordered="false" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              <a-statistic
                title="五星好评"
                :value="statistics.rating5Count"
                :value-style="{ color: '#fff', fontSize: '32px' }"
                style="color: '#fff'"
              >
                <template #suffix>
                  <span style="font-size: 16px; color: rgba(255,255,255,0.8)">
                    / {{ statistics.totalReviews }}
                  </span>
                </template>
              </a-statistic>
            </a-card>
          </a-col>
        </a-row>

        <!-- 星级分布 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-card title="星级分布" :bordered="false">
              <div class="rating-distribution">
                <div class="rating-item" v-for="i in 5" :key="i">
                  <div class="rating-label">
                    <a-rate :value="6 - i" disabled style="font-size: 14px" />
                  </div>
                  <div class="rating-bar">
                    <a-progress
                      :percent="getRatingPercent(6 - i)"
                      :stroke-color="getRatingColor(6 - i)"
                      :show-info="false"
                    />
                  </div>
                  <div class="rating-count">
                    {{ getRatingCount(6 - i) }}
                  </div>
                </div>
              </div>
            </a-card>
          </a-col>

          <a-col :span="12">
            <a-card title="评价趋势" :bordered="false">
              <div style="height: 300px; display: flex; align-items: center; justify-content: center">
                <a-empty description="评价趋势图表功能开发中" />
              </div>
            </a-card>
          </a-col>
        </a-row>

        <!-- 最新评价 -->
        <a-row :gutter="16" style="margin-top: 16px">
          <a-col :span="24">
            <a-card title="最新评价" :bordered="false">
              <template #extra>
                <a @click="$router.push('/reviews/list')">查看全部 →</a>
              </template>
              
              <a-list
                :data-source="recentReviews"
                :loading="recentLoading"
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
                          <a-rate :value="item.rating" disabled style="font-size: 14px" />
                          <span style="color: #999; font-size: 12px">
                            {{ formatTime(item.createTime) }}
                          </span>
                        </a-space>
                      </template>
                      <template #description>
                        <div>{{ item.content }}</div>
                      </template>
                    </a-list-item-meta>
                    <template #actions>
                      <a v-if="!item.reply" @click="showReplyModal(item)">回复</a>
                      <span v-else style="color: #52c41a">已回复</span>
                    </template>
                  </a-list-item>
                </template>
              </a-list>

              <a-empty v-if="!recentLoading && recentReviews.length === 0" description="暂无评价" />
            </a-card>
          </a-col>
        </a-row>
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
        <a-form-item label="回复内容">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { StarOutlined } from '@ant-design/icons-vue'
import {
  getReceivedReviews,
  replyReview,
  getMyStatistics
} from '@/api/review'
import dayjs from 'dayjs'

// 统计数据
const loading = ref(false)
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

// 最新评价
const recentReviews = ref([])
const recentLoading = ref(false)

// 回复对话框
const replyModalVisible = ref(false)
const replyLoading = ref(false)
const currentReview = ref({})
const replyForm = reactive({
  reply: ''
})

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    const res = await getMyStatistics()
    Object.assign(statistics, res)
  } catch (error) {
    message.error('加载统计数据失败')
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载最新评价
const loadRecentReviews = async () => {
  recentLoading.value = true
  try {
    const res = await getReceivedReviews({ pageNum: 1, pageSize: 5 })
    recentReviews.value = res.records
  } catch (error) {
    console.error('加载最新评价失败:', error)
  } finally {
    recentLoading.value = false
  }
}

// 获取星级数量
const getRatingCount = (rating) => {
  const key = `rating${rating}Count`
  return statistics[key] || 0
}

// 获取星级百分比
const getRatingPercent = (rating) => {
  if (statistics.totalReviews === 0) return 0
  return Math.round((getRatingCount(rating) / statistics.totalReviews) * 100)
}

// 获取星级颜色
const getRatingColor = (rating) => {
  const colors = {
    5: '#faad14',
    4: '#52c41a',
    3: '#1890ff',
    2: '#ff7a45',
    1: '#f5222d'
  }
  return colors[rating] || '#d9d9d9'
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
    loadRecentReviews()
    loadStatistics()
  } catch (error) {
    message.error(error.message || '回复失败')
  } finally {
    replyLoading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 页面加载
onMounted(() => {
  loadStatistics()
  loadRecentReviews()
})
</script>

<style scoped>
.statistics-container {
  padding: 24px;
}

.rating-distribution {
  padding: 16px 0;
}

.rating-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.rating-label {
  width: 120px;
  flex-shrink: 0;
}

.rating-bar {
  flex: 1;
  margin: 0 16px;
}

.rating-count {
  width: 60px;
  text-align: right;
  font-weight: bold;
  color: #262626;
}

:deep(.ant-card) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ant-statistic-title) {
  margin-bottom: 8px;
}
</style>
