<template>
  <a-card :bordered="false" title="训练计划模板">
    <!-- 搜索和操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <a-space>
        <a-input-search
          v-model:value="searchParams.keyword"
          placeholder="搜索模板名称"
          style="width: 250px"
          @search="loadTemplates"
        />
        <a-select
          v-model:value="searchParams.goal"
          placeholder="训练目标"
          style="width: 150px"
          allowClear
          @change="loadTemplates"
        >
          <a-select-option value="减脂">减脂</a-select-option>
          <a-select-option value="增肌">增肌</a-select-option>
          <a-select-option value="塑形">塑形</a-select-option>
          <a-select-option value="康复">康复</a-select-option>
        </a-select>
        <a-select
          v-model:value="searchParams.difficulty"
          placeholder="难度"
          style="width: 120px"
          allowClear
          @change="loadTemplates"
        >
          <a-select-option :value="1">初级</a-select-option>
          <a-select-option :value="2">中级</a-select-option>
          <a-select-option :value="3">高级</a-select-option>
        </a-select>
        <a-button @click="resetSearch">重置</a-button>
      </a-space>
      <a-button type="primary" @click="showCreateModal">
        <template #icon><PlusOutlined /></template>
        创建模板
      </a-button>
    </div>

    <!-- 模板列表 -->
    <a-table
      :columns="columns"
      :data-source="templates"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      rowKey="id"
    >
      <template #bodyCell="{ column, record }">
        <!-- 难度标签 -->
        <template v-if="column.key === 'difficulty'">
          <a-tag :color="getDifficultyColor(record.difficulty)">
            {{ getDifficultyText(record.difficulty) }}
          </a-tag>
        </template>

        <!-- 目标标签 -->
        <template v-if="column.key === 'goal'">
          <a-tag color="blue">{{ record.goal }}</a-tag>
        </template>

        <!-- 类型标签 -->
        <template v-if="column.key === 'type'">
          <a-tag :color="record.coachId ? 'green' : 'orange'">
            {{ record.coachId ? '个人模板' : '系统模板' }}
          </a-tag>
        </template>

        <!-- 状态 -->
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>

        <!-- 操作 -->
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="viewTemplate(record)">
              查看
            </a-button>
            <a-button 
              type="link" 
              size="small" 
              @click="editTemplate(record)"
              v-if="record.coachId"
            >
              编辑
            </a-button>
            <a-popconfirm
              title="确定删除此模板吗？"
              @confirm="handleDelete(record.id)"
              v-if="record.coachId"
            >
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 创建/编辑模板弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑模板' : '创建模板'"
      width="900px"
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
        <a-form-item label="模板名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入模板名称" />
        </a-form-item>

        <a-form-item label="训练目标" name="goal">
          <a-select v-model:value="formData.goal" placeholder="请选择训练目标">
            <a-select-option value="减脂">减脂</a-select-option>
            <a-select-option value="增肌">增肌</a-select-option>
            <a-select-option value="塑形">塑形</a-select-option>
            <a-select-option value="康复">康复</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="难度" name="difficulty">
          <a-radio-group v-model:value="formData.difficulty">
            <a-radio :value="1">初级</a-radio>
            <a-radio :value="2">中级</a-radio>
            <a-radio :value="3">高级</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="建议周期（天）" name="durationDays">
          <a-input-number 
            v-model:value="formData.durationDays" 
            :min="1" 
            :max="365"
            style="width: 200px"
          />
        </a-form-item>

        <a-form-item label="模板说明" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            :rows="4"
            placeholder="请输入模板说明"
          />
        </a-form-item>

        <a-divider>训练动作安排</a-divider>

        <a-form-item :wrapper-col="{ span: 24 }">
          <a-button type="dashed" block @click="addExercise">
            <template #icon><PlusOutlined /></template>
            添加训练动作
          </a-button>
        </a-form-item>

        <!-- 训练动作列表 -->
        <div v-for="(detail, index) in formData.details" :key="index" class="exercise-item">
          <a-card size="small" :title="`动作 ${index + 1}`">
            <template #extra>
              <a-button type="link" danger size="small" @click="removeExercise(index)">
                删除
              </a-button>
            </template>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="星期" :label-col="{ span: 8 }">
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
                <a-form-item label="训练动作" :label-col="{ span: 8 }">
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

            <a-form-item label="说明" :label-col="{ span: 4 }">
              <a-input v-model:value="detail.description" placeholder="动作说明" />
            </a-form-item>
          </a-card>
        </div>
      </a-form>
    </a-modal>

    <!-- 查看模板详情弹窗 -->
    <a-modal
      v-model:open="viewModalVisible"
      title="模板详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentTemplate">
        <a-descriptions-item label="模板名称">{{ currentTemplate.name }}</a-descriptions-item>
        <a-descriptions-item label="训练目标">{{ currentTemplate.goal }}</a-descriptions-item>
        <a-descriptions-item label="难度">
          <a-tag :color="getDifficultyColor(currentTemplate.difficulty)">
            {{ getDifficultyText(currentTemplate.difficulty) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="建议周期">{{ currentTemplate.durationDays }} 天</a-descriptions-item>
        <a-descriptions-item label="总动作数">{{ currentTemplate.totalExercises }} 个</a-descriptions-item>
        <a-descriptions-item label="类型">
          <a-tag :color="currentTemplate.coachId ? 'green' : 'orange'">
            {{ currentTemplate.coachId ? '个人模板' : '系统模板' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="模板说明" :span="2">
          {{ currentTemplate.description || '暂无说明' }}
        </a-descriptions-item>
      </a-descriptions>

      <a-divider>训练动作安排</a-divider>

      <a-table
        :columns="detailColumns"
        :data-source="currentTemplate?.details || []"
        :pagination="false"
        rowKey="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'dayOfWeek'">
            星期{{ ['一', '二', '三', '四', '五', '六', '日'][record.dayOfWeek - 1] }}
          </template>
        </template>
      </a-table>
    </a-modal>
  </a-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { 
  getTemplates, 
  getCoachTemplates,
  createTemplate, 
  updateTemplate, 
  deleteTemplate,
  getTemplateDetail,
  getTrainingActions
} from '@/api/trainingPlan'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
// 获取教练ID - 优先使用coachId,如果不存在则使用userId
const coachId = computed(() => {
  const user = userStore.user
  if (!user) return null
  // 优先使用coachId(教练表的ID),否则使用userId(用户表的ID) 
  return user.coachId || user.id
})

// 表格列定义
const columns = [
  { title: '模板名称', dataIndex: 'name', key: 'name', width: 200 },
  { title: '训练目标', dataIndex: 'goal', key: 'goal', width: 100 },
  { title: '难度', dataIndex: 'difficulty', key: 'difficulty', width: 100 },
  { title: '建议周期', dataIndex: 'durationDays', key: 'durationDays', width: 100, customRender: ({ text }) => `${text} 天` },
  { title: '总动作数', dataIndex: 'totalExercises', key: 'totalExercises', width: 100 },
  { title: '类型', key: 'type', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 150 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' }
]

// 详情表格列
const detailColumns = [
  { title: '星期', dataIndex: 'dayOfWeek', key: 'dayOfWeek', width: 80 },
  { title: '动作名称', dataIndex: 'actionName', key: 'actionName', width: 150 },
  { title: '组数', dataIndex: 'sets', key: 'sets', width: 60 },
  { title: '次数', dataIndex: 'reps', key: 'reps', width: 60 },
  { title: '重量(kg)', dataIndex: 'weight', key: 'weight', width: 80 },
  { title: '时长(分钟)', dataIndex: 'duration', key: 'duration', width: 100 },
  { title: '休息(秒)', dataIndex: 'restTime', key: 'restTime', width: 80 },
  { title: '说明', dataIndex: 'description', key: 'description', ellipsis: true }
]

// 数据
const loading = ref(false)
const templates = ref([])
const trainingActions = ref([])
const currentTemplate = ref(null)

// 搜索参数
const searchParams = reactive({
  keyword: '',
  goal: undefined,
  difficulty: undefined
})

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 弹窗
const modalVisible = ref(false)
const viewModalVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  name: '',
  goal: undefined,
  difficulty: 1,
  durationDays: 28,
  description: '',
  details: []
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  goal: [{ required: true, message: '请选择训练目标', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
}

// 加载模板列表
const loadTemplates = async () => {
  try {
    loading.value = true
    const params = {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      coachId: coachId.value,  // 使用.value获取computed的值
      ...searchParams
    }
    const res = await getTemplates(params)
    templates.value = res.records
    pagination.total = res.total
  } catch (error) {
    message.error('加载模板列表失败')
  } finally {
    loading.value = false
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

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadTemplates()
}

// 重置搜索
const resetSearch = () => {
  searchParams.keyword = ''
  searchParams.goal = undefined
  searchParams.difficulty = undefined
  pagination.current = 1
  loadTemplates()
}

// 显示创建弹窗
const showCreateModal = () => {
  isEdit.value = false
  resetForm()
  modalVisible.value = true
}

// 查看模板
const viewTemplate = async (record) => {
  try {
    const res = await getTemplateDetail(record.id)
    currentTemplate.value = res
    viewModalVisible.value = true
  } catch (error) {
    message.error('加载模板详情失败')
  }
}

// 编辑模板
const editTemplate = async (record) => {
  try {
    const res = await getTemplateDetail(record.id)
    isEdit.value = true
    currentTemplate.value = res
    Object.assign(formData, {
      id: res.id,
      name: res.name,
      goal: res.goal,
      difficulty: res.difficulty,
      durationDays: res.durationDays,
      description: res.description,
      details: res.details || []
    })
    modalVisible.value = true
  } catch (error) {
    message.error('加载模板详情失败')
  }
}

// 删除模板
const handleDelete = async (id) => {
  try {
    message.success('删除成功')
    await deleteTemplate(id)
    loadTemplates()
  } catch (error) {
    message.error('删除失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (formData.details.length === 0) {
      message.warning('请至少添加一个训练动作')
      return
    }

    const data = {
      name: formData.name,
      goal: formData.goal,
      difficulty: formData.difficulty,
      durationDays: formData.durationDays,
      description: formData.description,
      details: formData.details.map((d, index) => ({
        ...d,
        sortOrder: index + 1
      }))
    }

    let res
    if (isEdit.value) {
      res = await updateTemplate(formData.id, data)
    } else {
      res = await createTemplate(coachId.value, data)  // 使用.value获取computed的值
    }

    message.success(isEdit.value ? '更新成功' : '创建成功')
    modalVisible.value = false
    loadTemplates()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 取消
const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.goal = undefined
  formData.difficulty = 1
  formData.durationDays = 28
  formData.description = ''
  formData.details = []
  formRef.value?.resetFields()
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
    restTime: 60,
    description: ''
  })
}

// 删除训练动作
const removeExercise = (index) => {
  formData.details.splice(index, 1)
}

// 过滤动作
const filterAction = (input, option) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 初始化
onMounted(() => {
  loadTemplates()
  loadTrainingActions()
})
</script>

<style scoped>
.exercise-item {
  margin-bottom: 16px;
}

.mb-4 {
  margin-bottom: 16px;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}
</style>
