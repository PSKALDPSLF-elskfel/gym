<template>
  <div class="create-training-plan">
    <a-page-header
      :title="isEdit ? '编辑训练计划' : '创建训练计划'"
      @back="handleBack"
    />

    <a-card :bordered="false">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
      >
        <!-- 基本信息 -->
        <a-divider orientation="left">基本信息</a-divider>

        <a-form-item label="选择学员" name="userId" required>
          <a-select
            v-model:value="formData.userId"
            placeholder="请选择学员"
            show-search
            :filter-option="filterStudent"
            @change="handleStudentChange"
            :disabled="isEdit"
          >
            <a-select-option v-for="student in students" :key="student.userId" :value="student.userId">
              <a-space>
                <a-avatar :src="getAvatarUrl(student.avatar)" :size="24">
                  {{ student.nickname?.charAt(0) }}
                </a-avatar>
                <span>{{ student.nickname }}</span>
                <span style="color: #999;">({{ student.phone }})</span>
              </a-space>
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="计划名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入计划名称" />
        </a-form-item>

        <a-form-item label="训练目标" name="goal">
          <a-select v-model:value="formData.goal" placeholder="请选择训练目标">
            <a-select-option value="减脂">减脂</a-select-option>
            <a-select-option value="增肌">增肌</a-select-option>
            <a-select-option value="塑形">塑形</a-select-option>
            <a-select-option value="康复">康复</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="训练周期" name="dateRange">
          <a-range-picker
            v-model:value="formData.dateRange"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </a-form-item>

        <a-form-item label="计划备注" name="remark">
          <a-textarea
            v-model:value="formData.remark"
            :rows="3"
            placeholder="请输入计划备注"
          />
        </a-form-item>

        <!-- 快捷操作 -->
        <a-form-item label="使用模板" :wrapper-col="{ span: 20 }">
          <a-space>
            <a-button @click="showTemplateModal">
              <template #icon><CopyOutlined /></template>
              从模板创建
            </a-button>
            <a-button @click="handleClearDetails" v-if="formData.details.length > 0">
              清空所有动作
            </a-button>
          </a-space>
        </a-form-item>

        <!-- 训练动作 -->
        <a-divider orientation="left">
          训练动作安排
          <a-button type="link" @click="addExercise">
            <template #icon><PlusOutlined /></template>
            添加动作
          </a-button>
        </a-divider>

        <a-empty v-if="formData.details.length === 0" description="请添加训练动作" />

        <!-- 动作列表 -->
        <div v-for="(detail, index) in formData.details" :key="index" class="exercise-item">
          <a-card size="small" :title="`动作 ${index + 1}`">
            <template #extra>
              <a-button type="link" danger size="small" @click="removeExercise(index)">
                删除
              </a-button>
            </template>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="星期" :label-col="{ span: 6 }">
                  <a-select v-model:value="detail.dayOfWeek" placeholder="选择星期">
                    <a-select-option :value="1">星期一</a-select-option>
                    <a-select-option :value="2">星期二</a-select-option>
                    <a-select-option :value="3">星期三</a-select-option>
                    <a-select-option :value="4">星期四</a-select-option>
                    <a-select-option :value="5">星期五</a-select-option>
                    <a-select-option :value="6">星期六</a-select-option>
                    <a-select-option :value="7">星期日</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="训练动作" :label-col="{ span: 6 }">
                  <a-select 
                    v-model:value="detail.actionId" 
                    placeholder="选择动作"
                    show-search
                    :filter-option="filterAction"
                  >
                    <a-select-option 
                      v-for="action in trainingActions" 
                      :key="action.id" 
                      :value="action.id"
                    >
                      {{ action.name }} ({{ action.category }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item label="组数" :label-col="{ span: 12 }">
                  <a-input-number v-model:value="detail.sets" :min="1" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="次数" :label-col="{ span: 12 }">
                  <a-input-number v-model:value="detail.reps" :min="1" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="重量(kg)" :label-col="{ span: 12 }">
                  <a-input-number v-model:value="detail.weight" :min="0" style="width: 100%" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="时长(分钟)" :label-col="{ span: 12 }">
                  <a-input-number v-model:value="detail.duration" :min="1" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="休息(秒)" :label-col="{ span: 12 }">
                  <a-input-number v-model:value="detail.restTime" :min="0" style="width: 100%" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-card>
        </div>

        <!-- 提交按钮 -->
        <a-form-item :wrapper-col="{ span: 16, offset: 4 }">
          <a-space>
            <a-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ isEdit ? '保存' : '创建' }}
            </a-button>
            <a-button @click="handleBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 模板选择弹窗 -->
    <a-modal
      v-model:open="templateModalVisible"
      title="选择模板"
      width="800px"
      @ok="handleApplyTemplate"
    >
      <a-table
        :columns="templateColumns"
        :data-source="templates"
        :row-selection="{ type: 'radio', selectedRowKeys: [selectedTemplateId], onChange: onTemplateSelect }"
        :pagination="false"
        :scroll="{ y: 400 }"
        rowKey="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'difficulty'">
            <a-tag :color="getDifficultyColor(record.difficulty)">
              {{ getDifficultyText(record.difficulty) }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined, CopyOutlined } from '@ant-design/icons-vue'
import { 
  createTrainingPlan, 
  updateTrainingPlan,
  getTrainingPlanDetail,
  getTrainingActions,
  getTemplates,
  getTemplateDetail
} from '@/api/trainingPlan'
import { getMyStudents } from '@/api/student'
import { getAvatarUrl } from '@/utils/fileUtils'
import { useUserStore } from '@/store/user'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const submitting = ref(false)
const isEdit = ref(false)

// 学员列表
const students = ref([])
const trainingActions = ref([])
const templates = ref([])
const selectedTemplateId = ref()
const templateModalVisible = ref(false)

// 表单数据
const formData = reactive({
  userId: undefined,
  name: '',
  goal: undefined,
  dateRange: [],
  remark: '',
  details: []
})

// 表单验证规则
const formRules = {
  userId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  goal: [{ required: true, message: '请选择训练目标', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择训练周期', trigger: 'change' }]
}

// 模板表格列
const templateColumns = [
  { title: '模板名称', dataIndex: 'name', key: 'name', width: 200 },
  { title: '训练目标', dataIndex: 'goal', key: 'goal', width: 100 },
  { title: '难度', key: 'difficulty', width: 100 },
  { title: '周期', dataIndex: 'durationDays', key: 'durationDays', width: 100, customRender: ({ text }) => `${text} 天` },
  { title: '动作数', dataIndex: 'totalExercises', key: 'totalExercises', width: 100 }
]

// 加载学员列表
const loadStudents = async () => {
  try {
    const data = await getMyStudents({ pageSize: 1000 })
    students.value = data.records || []
  } catch (error) {
    console.error('加载学员列表失败:', error)
  }
}

// 加载训练动作
const loadTrainingActions = async () => {
  try {
    const res = await getTrainingActions({ pageSize: 100 })
    trainingActions.value = res.records || []
  } catch (error) {
    console.error('加载训练动作失败:', error)
  }
}

// 加载模板
const loadTemplates = async () => {
  try {
    const coachId = userStore.user?.coachId
    if (!coachId) return
    
    const res = await getTemplates({ pageSize: 100, coachId: coachId })
    templates.value = res.records || []
  } catch (error) {
    console.error('加载模板失败:', error)
  }
}

// 禁用过去的日期
const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

// 学员过滤
const filterStudent = (input, option) => {
  return option.children[0].children[1].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 动作过滤
const filterAction = (input, option) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 学员变化
const handleStudentChange = (userId) => {
  const student = students.value.find(s => s.userId === userId)
  if (student && !isEdit.value) {
    formData.name = `${student.nickname}的训练计划`
  }
}

// 添加训练动作
const addExercise = () => {
  formData.details.push({
    dayOfWeek: 1,
    actionId: undefined,
    sets: 3,
    reps: 12,
    weight: 0,
    duration: null,
    restTime: 60
  })
}

// 删除训练动作
const removeExercise = (index) => {
  formData.details.splice(index, 1)
}

// 清空所有动作
const handleClearDetails = () => {
  formData.details = []
}

// 显示模板弹窗
const showTemplateModal = () => {
  templateModalVisible.value = true
}

// 选择模板
const onTemplateSelect = (selectedRowKeys) => {
  selectedTemplateId.value = selectedRowKeys[0]
}

// 应用模板
const handleApplyTemplate = async () => {
  if (!selectedTemplateId.value) {
    message.warning('请选择一个模板')
    return
  }

  try {
    const template = await getTemplateDetail(selectedTemplateId.value)
    formData.goal = template.goal
    formData.details = template.details.map(d => ({
      dayOfWeek: d.dayOfWeek,
      actionId: d.actionId,
      sets: d.sets,
      reps: d.reps,
      weight: d.weight,
      duration: d.duration,
      restTime: d.restTime
    }))
    templateModalVisible.value = false
    message.success('模板应用成功')
  } catch (error) {
    message.error('应用模板失败')
  }
}

// 难度颜色
const getDifficultyColor = (difficulty) => {
  const colors = { 1: 'green', 2: 'orange', 3: 'red' }
  return colors[difficulty] || 'default'
}

// 难度文本
const getDifficultyText = (difficulty) => {
  const texts = { 1: '初级', 2: '中级', 3: '高级' }
  return texts[difficulty] || '未知'
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (formData.details.length === 0) {
      message.warning('请至少添加一个训练动作')
      return
    }

    submitting.value = true

    const data = {
      userId: formData.userId,
      coachId: userStore.user?.coachId,
      name: formData.name,
      goal: formData.goal,
      startDate: formData.dateRange[0].format('YYYY-MM-DD'),
      endDate: formData.dateRange[1].format('YYYY-MM-DD'),
      remark: formData.remark,
      details: formData.details.map((d, index) => ({
        ...d,
        sortOrder: index + 1
      }))
    }

    let res
    if (isEdit.value) {
      res = await updateTrainingPlan(route.query.id, data)
    } else {
      res = await createTrainingPlan(data)
    }

    message.success(isEdit.value ? '更新成功' : '创建成功')
    router.back()
  } catch (error) {
    console.error('提交失败:', error)
    message.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 初始化
onMounted(async () => {
  await loadStudents()
  await loadTrainingActions()
  await loadTemplates()

  // 如果是编辑模式
  if (route.query.id) {
    isEdit.value = true
    try {
      const plan = await getTrainingPlanDetail(route.query.id)
      formData.userId = plan.userId
      formData.name = plan.name
      formData.goal = plan.goal
      formData.dateRange = [dayjs(plan.startDate), dayjs(plan.endDate)]
      formData.remark = plan.remark
      formData.details = plan.details || []
    } catch (error) {
      message.error('加载计划详情失败')
    }
  }
  
  // 如果是从学员详情页创建
  if (route.query.userId) {
    formData.userId = parseInt(route.query.userId)
    handleStudentChange(formData.userId)
  }
})
</script>

<style scoped>
.create-training-plan {
  padding: 20px;
}

.exercise-item {
  margin-bottom: 16px;
}
</style>
