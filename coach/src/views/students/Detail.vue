<template>
  <div class="student-detail">
    <!-- 返回按钮 -->
    <a-page-header
      title="学员详情"
      @back="handleBack"
    />

    <a-spin :spinning="loading">
      <div v-if="studentDetail">
        <!-- 基本信息卡片 -->
        <a-card class="info-card" title="基本信息" :bordered="false">
          <a-row :gutter="24">
            <a-col :span="4">
              <a-avatar :src="getAvatarUrl(studentDetail.avatar)" :size="80">
                {{ studentDetail.nickname?.charAt(0) }}
              </a-avatar>
            </a-col>
            <a-col :span="20">
              <a-descriptions :column="2">
                <a-descriptions-item label="昵称">
                  {{ studentDetail.nickname }}
                </a-descriptions-item>
                <a-descriptions-item label="手机号">
                  {{ studentDetail.phone }}
                </a-descriptions-item>
                <a-descriptions-item label="会员类型">
                  <a-tag :color="getMemberTypeColor(studentDetail.memberType)">
                    {{ studentDetail.memberTypeName }}
                  </a-tag>
                  <a-tag v-if="!studentDetail.isMemberValid" color="red">已过期</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="会员到期时间">
                  {{ studentDetail.memberExpireTime ? formatDate(studentDetail.memberExpireTime) : '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="加入时间">
                  {{ formatDate(studentDetail.createTime) }}
                </a-descriptions-item>
                <a-descriptions-item label="训练计划">
                  共 {{ studentDetail.totalTrainingPlans }} 个，进行中 {{ studentDetail.activeTrainingPlans }} 个
                </a-descriptions-item>
              </a-descriptions>
            </a-col>
          </a-row>
          
          <!-- 教练备注 -->
          <a-divider />
          <div class="remark-section">
            <div class="remark-header">
              <span class="remark-title">教练备注</span>
              <a-button type="link" size="small" @click="handleEditRemark">
                编辑备注
              </a-button>
            </div>
            <div class="remark-content">
              {{ studentDetail.coachRemark || '暂无备注' }}
            </div>
          </div>
        </a-card>

        <!-- 最新体测数据 -->
        <a-card class="info-card" title="最新体测数据" :bordered="false">
          <div v-if="studentDetail.latestBodyTest">
            <a-row :gutter="16">
              <a-col :span="6">
                <a-statistic title="体重" :value="studentDetail.latestBodyTest.weight" suffix="kg" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="BMI" :value="studentDetail.latestBodyTest.bmi" :precision="1" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="体脂率" :value="studentDetail.latestBodyTest.bodyFat" suffix="%" :precision="1" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="肌肉量" :value="studentDetail.latestBodyTest.muscleMass" suffix="kg" :precision="1" />
              </a-col>
            </a-row>
            <a-divider />
            <div>
              <strong>测试时间：</strong>{{ formatDateTime(studentDetail.latestBodyTest.testTime) }}
            </div>
            <div v-if="studentDetail.latestBodyTest.remark" style="margin-top: 8px">
              <strong>备注：</strong>{{ studentDetail.latestBodyTest.remark }}
            </div>
          </div>
          <a-empty v-else description="暂无体测数据" />
        </a-card>

        <!-- 体测历史 -->
        <a-card class="info-card" :bordered="false">
          <template #title>
            <span>体测历史</span>
            <a-button type="link" size="small" @click="showBodyTestHistory = !showBodyTestHistory">
              {{ showBodyTestHistory ? '收起' : '展开' }}
            </a-button>
          </template>
          
          <div v-if="showBodyTestHistory">
            <a-timeline v-if="studentDetail.bodyTestHistory && studentDetail.bodyTestHistory.length > 0">
              <a-timeline-item v-for="test in studentDetail.bodyTestHistory" :key="test.id">
                <div class="timeline-content">
                  <div class="timeline-time">{{ formatDateTime(test.testTime) }}</div>
                  <a-space :size="16">
                    <span>体重: {{ test.weight }}kg</span>
                    <span>BMI: {{ test.bmi }}</span>
                    <span>体脂率: {{ test.bodyFat }}%</span>
                    <span>肌肉量: {{ test.muscleMass }}kg</span>
                  </a-space>
                  <div v-if="test.remark" class="timeline-remark">{{ test.remark }}</div>
                </div>
              </a-timeline-item>
            </a-timeline>
            <a-empty v-else description="暂无体测历史" />
          </div>
        </a-card>

        <!-- 训练计划 -->
        <a-card class="info-card" :bordered="false">
          <template #title>
            <span>训练计划</span>
          </template>
          <template #extra>
            <a-button type="primary" @click="handleCreatePlan">
              <template #icon><PlusOutlined /></template>
              创建训练计划
            </a-button>
          </template>
                  
          <a-list
            v-if="studentDetail.trainingPlans && studentDetail.trainingPlans.length > 0"
            :data-source="studentDetail.trainingPlans"
            :pagination="false"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <a-space>
                      <span>{{ item.name }}</span>
                      <a-tag :color="item.status === 1 ? 'green' : 'default'">
                        {{ item.status === 1 ? '进行中' : '已结束' }}
                      </a-tag>
                    </a-space>
                  </template>
                  <template #description>
                    <div>
                      <span>目标:{{ item.goal }}</span>
                      <a-divider type="vertical" />
                      <span>周期:{{ item.startDate }} ~ {{ item.endDate }}</span>
                    </div>
                    <div v-if="item.remark" style="margin-top: 4px; color: #999;">
                      {{ item.remark }}
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <a @click="handleViewPlan(item)">查看详情</a>
                </template>
              </a-list-item>
            </template>
          </a-list>
          <a-empty v-else description="暂无训练计划" />
        </a-card>
      </div>
    </a-spin>

    <!-- 备注编辑弹窗 -->
    <a-modal
      v-model:open="remarkModalVisible"
      title="编辑学员备注"
      :confirm-loading="remarkSaving"
      @ok="handleSaveRemark"
    >
      <a-form :label-col="{ span: 4 }">
        <a-form-item label="学员">
          <span>{{ studentDetail?.nickname }}</span>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="remarkForm.remark"
            :rows="4"
            :maxlength="500"
            show-count
            placeholder="请输入备注内容..."
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getStudentDetail, updateStudentRemark } from '@/api/student'
import { getAvatarUrl } from '@/utils/fileUtils'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const studentDetail = ref(null)
const showBodyTestHistory = ref(false)

// 备注编辑
const remarkModalVisible = ref(false)
const remarkSaving = ref(false)
const remarkForm = reactive({
  remark: ''
})

// 获取会员类型颜色
const getMemberTypeColor = (type) => {
  const colorMap = {
    0: 'default',
    1: 'gold',
    2: 'blue'
  }
  return colorMap[type] || 'default'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

// 加载学员详情
const loadStudentDetail = async () => {
  loading.value = true
  try {
    const userId = route.params.id
    const data = await getStudentDetail(userId)
    studentDetail.value = data
  } catch (error) {
    console.error('加载学员详情失败:', error)
    message.error('加载学员详情失败')
  } finally {
    loading.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 编辑备注
const handleEditRemark = () => {
  remarkForm.remark = studentDetail.value?.coachRemark || ''
  remarkModalVisible.value = true
}

// 保存备注
const handleSaveRemark = async () => {
  remarkSaving.value = true
  try {
    await updateStudentRemark({
      userId: studentDetail.value.userId,
      remark: remarkForm.remark
    })
    message.success('备注更新成功')
    remarkModalVisible.value = false
    loadStudentDetail() // 刷新数据
  } catch (error) {
    console.error('更新备注失败:', error)
    message.error('更新备注失败')
  } finally {
    remarkSaving.value = false
  }
}

// 创建训练计划
const handleCreatePlan = () => {
  router.push(`/training-plans/create?userId=${studentDetail.value.userId}`)
}

// 查看训练计划详情
const handleViewPlan = (plan) => {
  router.push(`/training-plans/${plan.id}`)
}

// 组件挂载
onMounted(() => {
  loadStudentDetail()
})
</script>

<style scoped>
.student-detail {
  padding: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.remark-section {
  margin-top: 16px;
}

.remark-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.remark-title {
  font-weight: 500;
  font-size: 14px;
}

.remark-content {
  padding: 12px;
  background-color: #fafafa;
  border-radius: 4px;
  min-height: 60px;
  color: #666;
}

.timeline-content {
  padding: 8px 0;
}

.timeline-time {
  font-weight: 500;
  margin-bottom: 8px;
}

.timeline-remark {
  margin-top: 8px;
  color: #999;
  font-size: 12px;
}

:deep(.ant-statistic-title) {
  font-size: 14px;
}

:deep(.ant-statistic-content) {
  font-size: 24px;
}
</style>
