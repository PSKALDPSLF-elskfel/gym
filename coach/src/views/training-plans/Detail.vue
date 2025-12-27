<template>
  <div class="training-plan-detail">
    <a-page-header
      :title="planData?.name || '训练计划详情'"
      @back="handleBack"
    >
      <template #extra>
        <a-space>
          <a-button @click="handleRefresh">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
          <a-button 
            type="primary" 
            @click="handleEdit"
            v-if="planData?.status === 1"
          >
            编辑计划
          </a-button>
        </a-space>
      </template>

      <a-descriptions :column="3" bordered size="small">
        <a-descriptions-item label="学员">
          <a-space>
            <a-avatar :src="planData?.userAvatar" :size="32">
              {{ planData?.userNickname?.charAt(0) || '学' }}
            </a-avatar>
            <span>{{ planData?.userNickname || '未知学员' }}</span>
          </a-space>
        </a-descriptions-item>
        <a-descriptions-item label="训练目标">
          <a-tag color="blue">{{ planData?.goal }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="planData?.status === 1 ? 'success' : 'default'">
            {{ planData?.status === 1 ? '进行中' : '已结束' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="训练周期" :span="2">
          {{ planData?.startDate }} ~ {{ planData?.endDate }}
        </a-descriptions-item>
        <a-descriptions-item label="完成进度">
          <a-progress 
            :percent="Math.round(progress)" 
            :size="'small'"
            :status="progress === 100 ? 'success' : 'active'"
          />
        </a-descriptions-item>
        <a-descriptions-item label="计划说明" :span="3">
          {{ planData?.description || '暂无说明' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-page-header>

    <a-card title="训练内容" :bordered="false" style="margin-top: 16px;">
      <a-spin :spinning="loading">
        <a-collapse 
          v-model:activeKey="activeKeys" 
          :bordered="false"
          accordion
        >
          <a-collapse-panel
            v-for="(dayDetails, day) in groupedDetails"
            :key="day"
            :header="`第 ${day} 天训练`"
          >
            <template #extra>
              <a-tag :color="getDayCompletionColor(dayDetails)">
                已完成 {{ getCompletedCount(dayDetails) }}/{{ dayDetails.length }}
              </a-tag>
            </template>

            <a-timeline>
              <a-timeline-item
                v-for="detail in dayDetails"
                :key="detail.id"
                :color="detail.isCompleted === 1 ? 'green' : 'gray'"
              >
                <template #dot>
                  <CheckCircleOutlined v-if="detail.isCompleted === 1" style="font-size: 16px;" />
                  <ClockCircleOutlined v-else style="font-size: 16px;" />
                </template>

                <a-card size="small" :bordered="true" class="detail-card">
                  <div class="detail-header">
                    <div class="action-info">
                      <h4>{{ detail.actionName }}</h4>
                      <a-space size="small">
                        <a-tag>{{ detail.targetSets }} 组</a-tag>
                        <a-tag>{{ detail.targetReps }} 次/组</a-tag>
                        <a-tag v-if="detail.targetWeight">{{ detail.targetWeight }} kg</a-tag>
                        <a-tag v-if="detail.restTime">休息 {{ detail.restTime }}s</a-tag>
                      </a-space>
                    </div>
                    <div class="action-status">
                      <a-tag v-if="detail.isCompleted === 1" color="success">
                        <CheckOutlined /> 已完成
                      </a-tag>
                      <a-tag v-else color="default">
                        待完成
                      </a-tag>
                    </div>
                  </div>

                  <!-- 实际完成情况 -->
                  <div v-if="detail.isCompleted === 1" class="completion-info">
                    <a-divider style="margin: 12px 0;" />
                    <a-descriptions size="small" :column="4" bordered>
                      <a-descriptions-item label="实际组数">
                        {{ detail.actualSets || '-' }}
                      </a-descriptions-item>
                      <a-descriptions-item label="实际次数">
                        {{ detail.actualReps || '-' }}
                      </a-descriptions-item>
                      <a-descriptions-item label="完成时间" :span="2">
                        {{ formatDateTime(detail.completionTime) }}
                      </a-descriptions-item>
                      <a-descriptions-item 
                        v-if="detail.executionNote" 
                        label="执行备注" 
                        :span="4"
                      >
                        {{ detail.executionNote }}
                      </a-descriptions-item>
                    </a-descriptions>
                  </div>

                  <!-- 动作说明 -->
                  <div v-if="detail.actionDescription" class="action-description">
                    <a-divider style="margin: 12px 0;" />
                    <div class="description-label">动作说明：</div>
                    <div class="description-content">{{ detail.actionDescription }}</div>
                  </div>

                  <!-- 动作示范 -->
                  <div v-if="detail.actionVideoUrl || detail.actionImageUrl" class="action-media">
                    <a-divider style="margin: 12px 0;" />
                    <a-space>
                      <a-button 
                        v-if="detail.actionVideoUrl" 
                        type="link" 
                        size="small"
                        @click="handleViewVideo(detail.actionVideoUrl)"
                      >
                        <PlayCircleOutlined /> 查看视频示范
                      </a-button>
                      <a-button 
                        v-if="detail.actionImageUrl" 
                        type="link" 
                        size="small"
                        @click="handleViewImage(detail.actionImageUrl)"
                      >
                        <PictureOutlined /> 查看图片示范
                      </a-button>
                    </a-space>
                  </div>
                </a-card>
              </a-timeline-item>
            </a-timeline>
          </a-collapse-panel>
        </a-collapse>

        <a-empty v-if="!planData?.details || planData.details.length === 0" 
          description="暂无训练内容" 
          style="padding: 40px 0;"
        />
      </a-spin>
    </a-card>

    <!-- 训练统计 -->
    <a-card title="训练统计" :bordered="false" style="margin-top: 16px;">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic
            title="总训练天数"
            :value="statistics.totalDays"
            suffix="天"
          >
            <template #prefix>
              <CalendarOutlined />
            </template>
          </a-statistic>
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="总训练动作"
            :value="statistics.totalActions"
            suffix="个"
          >
            <template #prefix>
              <ThunderboltOutlined />
            </template>
          </a-statistic>
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="已完成动作"
            :value="statistics.completedActions"
            suffix="个"
            :value-style="{ color: '#3f8600' }"
          >
            <template #prefix>
              <CheckCircleOutlined />
            </template>
          </a-statistic>
        </a-col>
        <a-col :span="6">
          <a-statistic
            title="完成率"
            :value="statistics.completionRate"
            suffix="%"
            :precision="1"
            :value-style="{ color: statistics.completionRate >= 80 ? '#3f8600' : '#cf1322' }"
          >
            <template #prefix>
              <RiseOutlined v-if="statistics.completionRate >= 80" />
              <FallOutlined v-else />
            </template>
          </a-statistic>
        </a-col>
      </a-row>
    </a-card>

    <!-- 视频预览Modal -->
    <a-modal
      v-model:visible="videoModalVisible"
      title="动作视频示范"
      :footer="null"
      width="800px"
      centered
    >
      <video
        v-if="currentVideoUrl"
        :src="currentVideoUrl"
        controls
        style="width: 100%; max-height: 500px;"
      ></video>
    </a-modal>

    <!-- 图片预览Modal -->
    <a-modal
      v-model:visible="imageModalVisible"
      title="动作图片示范"
      :footer="null"
      width="800px"
      centered
    >
      <img
        v-if="currentImageUrl"
        :src="currentImageUrl"
        style="width: 100%;"
        alt="动作示范"
      />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ReloadOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CheckOutlined,
  PlayCircleOutlined,
  PictureOutlined,
  CalendarOutlined,
  ThunderboltOutlined,
  RiseOutlined,
  FallOutlined
} from '@ant-design/icons-vue'
import { getTrainingPlanDetail, getTrainingPlanProgress } from '@/api/trainingPlan'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const planData = ref(null)
const progress = ref(0)
const activeKeys = ref(['1'])

// Modal相关
const videoModalVisible = ref(false)
const imageModalVisible = ref(false)
const currentVideoUrl = ref('')
const currentImageUrl = ref('')

// 按天分组的训练内容
const groupedDetails = computed(() => {
  if (!planData.value?.details) return {}
  
  const groups = {}
  planData.value.details.forEach(detail => {
    const day = detail.dayNumber || 1
    if (!groups[day]) {
      groups[day] = []
    }
    groups[day].push(detail)
  })
  
  // 按dayNumber排序
  return Object.keys(groups)
    .sort((a, b) => Number(a) - Number(b))
    .reduce((acc, key) => {
      acc[key] = groups[key]
      return acc
    }, {})
})

// 训练统计
const statistics = computed(() => {
  const details = planData.value?.details || []
  const totalActions = details.length
  const completedActions = details.filter(d => d.isCompleted === 1).length
  const totalDays = Object.keys(groupedDetails.value).length
  const completionRate = totalActions > 0 ? (completedActions / totalActions) * 100 : 0
  
  return {
    totalDays,
    totalActions,
    completedActions,
    completionRate
  }
})

// 获取某天的完成情况颜色
const getDayCompletionColor = (dayDetails) => {
  const completed = getCompletedCount(dayDetails)
  const total = dayDetails.length
  const rate = (completed / total) * 100
  
  if (rate === 100) return 'success'
  if (rate >= 50) return 'processing'
  if (rate > 0) return 'warning'
  return 'default'
}

// 获取某天已完成的数量
const getCompletedCount = (dayDetails) => {
  return dayDetails.filter(d => d.isCompleted === 1).length
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.substring(0, 16).replace('T', ' ')
}

// 加载训练计划详情
const loadPlanDetail = async () => {
  loading.value = true
  try {
    const planId = route.params.id
    if (!planId) {
      message.error('缺少计划ID')
      router.back()
      return
    }
    
    // 获取计划详情
    const planRes = await getTrainingPlanDetail(planId)
    planData.value = planRes
    
    // 获取进度
    try {
      const progressRes = await getTrainingPlanProgress(planId)
      progress.value = progressRes || 0
    } catch (error) {
      console.error('获取进度失败:', error)
      progress.value = 0
    }
    
    // 默认展开第一天
    const firstDay = Object.keys(groupedDetails.value)[0]
    if (firstDay) {
      activeKeys.value = [firstDay]
    }
  } catch (error) {
    console.error('加载训练计划详情失败:', error)
    message.error('加载训练计划详情失败')
  } finally {
    loading.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 刷新
const handleRefresh = () => {
  loadPlanDetail()
}

// 编辑
const handleEdit = () => {
  router.push(`/training-plans/create?id=${route.params.id}`)
}

// 查看视频
const handleViewVideo = (url) => {
  currentVideoUrl.value = url
  videoModalVisible.value = true
}

// 查看图片
const handleViewImage = (url) => {
  currentImageUrl.value = url
  imageModalVisible.value = true
}

// 初始化
onMounted(() => {
  loadPlanDetail()
})
</script>

<style scoped>
.training-plan-detail {
  padding: 16px;
}

.detail-card {
  margin-bottom: 12px;
  background: #fafafa;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.action-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.completion-info {
  margin-top: 8px;
}

.action-description {
  margin-top: 8px;
}

.description-label {
  font-weight: 600;
  margin-bottom: 4px;
  color: rgba(0, 0, 0, 0.85);
}

.description-content {
  color: rgba(0, 0, 0, 0.65);
  line-height: 1.6;
  padding: 8px;
  background: #fff;
  border-radius: 4px;
}

.action-media {
  margin-top: 8px;
}

:deep(.ant-collapse-borderless) {
  background: transparent;
}

:deep(.ant-collapse-borderless > .ant-collapse-item) {
  border-bottom: 1px solid #d9d9d9;
  margin-bottom: 16px;
}

:deep(.ant-timeline-item-content) {
  margin-left: 24px;
}
</style>
