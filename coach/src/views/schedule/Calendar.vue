<template>
  <div>
    <a-card title="我的排班" :bordered="false">
      <template #extra>
        <a-button type="primary" @click="showScheduleModal">添加排班</a-button>
      </template>
      
      <!-- 月份选择器 -->
      <a-row style="margin-bottom: 20px;">
        <a-col :span="24">
          <a-space>
            <a-button @click="prevMonth">上个月</a-button>
            <a-typography-title :level="4" style="margin: 0;">{{ currentYear }}年{{ currentMonth }}月</a-typography-title>
            <a-button @click="nextMonth">下个月</a-button>
          </a-space>
        </a-col>
      </a-row>
      
      <!-- 排班日历 -->
      <div class="schedule-calendar">
        <div class="calendar-header">
          <div class="week-day" v-for="day in weekDays" :key="day">{{ day }}</div>
        </div>
        <div class="calendar-body">
          <div 
            class="calendar-cell" 
            v-for="(day, index) in calendarDays" 
            :key="index"
            :class="{ 'empty': !day.date, 'today': day.isToday }"
          >
            <div class="cell-header" v-if="day.date">
              <span class="date-number">{{ day.date.getDate() }}</span>
              <span v-if="day.isToday" class="today-tag">今天</span>
            </div>
            <div class="cell-content" v-if="day.date">
              <div 
                class="schedule-item" 
                v-for="schedule in day.schedules" 
                :key="schedule.id"
                :class="`work-type-${schedule.workType}`"
                @click="editSchedule(schedule)"
              >
                <div class="schedule-time">{{ formatTime(schedule.startTime) }}-{{ formatTime(schedule.endTime) }}</div>
                <div class="schedule-location">{{ schedule.location }}</div>
                <div class="schedule-type">{{ getWorkTypeName(schedule.workType) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-card>
    
    <!-- 添加/编辑排班模态框 -->
    <a-modal 
      v-model:open="scheduleModalVisible" 
      :title="editingSchedule ? '编辑排班' : '添加排班'"
      @ok="saveSchedule"
      @cancel="closeScheduleModal"
      :confirmLoading="saving"
    >
      <a-form :model="scheduleForm" layout="vertical">
        <a-form-item label="工作日期">
          <a-date-picker 
            v-model:value="scheduleForm.workDate" 
            style="width: 100%" 
            :disabled-date="disabledDate"
          />
        </a-form-item>
        <a-form-item label="开始时间">
          <a-time-picker 
            v-model:value="scheduleForm.startTime" 
            format="HH:mm"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="结束时间">
          <a-time-picker 
            v-model:value="scheduleForm.endTime" 
            format="HH:mm"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="工作类型">
          <a-select v-model:value="scheduleForm.workType">
            <a-select-option value="NORMAL">正常排班</a-select-option>
            <a-select-option value="OVERTIME">加班</a-select-option>
            <a-select-option value="SHIFT">轮班</a-select-option>
            <a-select-option value="HOLIDAY">休息</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="工作地点">
          <a-input v-model:value="scheduleForm.location" placeholder="请输入工作地点" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="scheduleForm.remark" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useUserStore } from '@/store/user'
import { getMonthlySchedule, createSchedule, updateSchedule } from '@/api/schedule'

// 用户存储
const userStore = useUserStore()

// 当前日期状态
const currentDate = new Date()
const currentYear = ref(currentDate.getFullYear())
const currentMonth = ref(currentDate.getMonth() + 1)

// 星期几
const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 排班数据
const schedules = ref([])

// 模态框状态
const scheduleModalVisible = ref(false)
const editingSchedule = ref(null)
const saving = ref(false)

// 排班表单
const scheduleForm = reactive({
  workDate: null,
  startTime: null,
  endTime: null,
  workType: 'NORMAL',
  location: '',
  remark: ''
})

// 计算属性：日历天数
const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  
  // 获取当月第一天是星期几（0表示星期日）
  const firstDay = new Date(year, month - 1, 1).getDay()
  // 调整为周一为第一天（0表示周一）
  const firstDayAdjusted = firstDay === 0 ? 6 : firstDay - 1
  
  // 获取当月总天数
  const daysInMonth = new Date(year, month, 0).getDate()
  
  // 构建日历数组
  const days = []
  
  // 添加前一个月的空白天数
  for (let i = 0; i < firstDayAdjusted; i++) {
    days.push({ date: null })
  }
  
  // 添加当月的天数
  const today = new Date()
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month - 1, i)
    const isToday = date.toDateString() === today.toDateString()
    
    // 获取当天的排班
    const daySchedules = schedules.value.filter(schedule => {
      const scheduleDate = new Date(schedule.workDate)
      return scheduleDate.toDateString() === date.toDateString()
    })
    
    days.push({
      date: date,
      isToday: isToday,
      schedules: daySchedules
    })
  }
  
  return days
})

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  // 假设时间格式为 HH:mm:ss，只取前5位 HH:mm
  return timeStr.substring(0, 5)
}

// 获取工作类型名称
const getWorkTypeName = (workType) => {
  const workTypeMap = {
    'NORMAL': '正常排班',
    'OVERTIME': '加班',
    'SHIFT': '轮班',
    'HOLIDAY': '休息'
  }
  return workTypeMap[workType] || workType
}

// 上个月
const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentYear.value--
    currentMonth.value = 12
  } else {
    currentMonth.value--
  }
  loadSchedules()
}

// 下个月
const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentYear.value++
    currentMonth.value = 1
  } else {
    currentMonth.value++
  }
  loadSchedules()
}

// 显示排班模态框
const showScheduleModal = () => {
  editingSchedule.value = null
  // 初始化表单默认值
  scheduleForm.workDate = dayjs()
  scheduleForm.startTime = dayjs().hour(9).minute(0)
  scheduleForm.endTime = dayjs().hour(17).minute(0)
  scheduleForm.workType = 'NORMAL'
  scheduleForm.location = ''
  scheduleForm.remark = ''
  scheduleModalVisible.value = true
}

// 编辑排班
const editSchedule = (schedule) => {
  editingSchedule.value = schedule
  // 填充表单数据
  scheduleForm.workDate = dayjs(schedule.workDate)
  scheduleForm.startTime = dayjs(`2000-01-01 ${schedule.startTime}`)
  scheduleForm.endTime = dayjs(`2000-01-01 ${schedule.endTime}`)
  scheduleForm.workType = schedule.workType
  scheduleForm.location = schedule.location
  scheduleForm.remark = schedule.remark
  scheduleModalVisible.value = true
}

// 关闭排班模态框
const closeScheduleModal = () => {
  scheduleModalVisible.value = false
  editingSchedule.value = null
}

// 保存排班
const saveSchedule = async () => {
  // 表单验证
  if (!scheduleForm.workDate) {
    message.warning('请选择工作日期')
    return
  }
  
  if (!scheduleForm.startTime) {
    message.warning('请选择开始时间')
    return
  }
  
  if (!scheduleForm.endTime) {
    message.warning('请选择结束时间')
    return
  }
  
  // 结束时间必须晚于开始时间
  if (scheduleForm.startTime >= scheduleForm.endTime) {
    message.warning('结束时间必须晚于开始时间')
    return
  }
  
  try {
    saving.value = true
    
    // 构造请求数据
    const requestData = {
      coachId: userStore.user?.coachId || 1,
      workDate: scheduleForm.workDate.format('YYYY-MM-DD'),
      startTime: scheduleForm.startTime.format('HH:mm:ss'),
      endTime: scheduleForm.endTime.format('HH:mm:ss'),
      workType: scheduleForm.workType,
      location: scheduleForm.location,
      remark: scheduleForm.remark
    }
    
    if (editingSchedule.value) {
      // 更新排班
      await updateSchedule(editingSchedule.value.id, requestData)
      message.success('排班更新成功')
    } else {
      // 创建排班
      await createSchedule(requestData)
      message.success('排班添加成功')
    }
    
    closeScheduleModal()
    loadSchedules()
  } catch (error) {
    message.error('保存排班失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

// 禁用日期（这里可以根据需求设置）
const disabledDate = (current) => {
  // 不禁用任何日期，允许添加历史排班
  return false
}

// 加载排班数据
const loadSchedules = async () => {
  try {
    const response = await getMonthlySchedule({
      coachId: userStore.user?.coachId || 1,
      year: currentYear.value,
      month: currentMonth.value
    })
    schedules.value = response
  } catch (error) {
    message.error('加载排班数据失败: ' + error.message)
    // 使用模拟数据以便开发
    schedules.value = [
      {
        id: 1,
        coachId: userStore.user?.coachId || 1,
        workDate: new Date(currentYear.value, currentMonth.value - 1, 5),
        startTime: '09:00:00',
        endTime: '17:00:00',
        workType: 'NORMAL',
        location: '主训练区',
        status: 1,
        remark: '日常排班'
      },
      {
        id: 2,
        coachId: userStore.user?.coachId || 1,
        workDate: new Date(currentYear.value, currentMonth.value - 1, 10),
        startTime: '14:00:00',
        endTime: '22:00:00',
        workType: 'OVERTIME',
        location: 'VIP训练区',
        status: 1,
        remark: '加班'
      }
    ]
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadSchedules()
})
</script>

<style scoped>
.schedule-calendar {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.week-day {
  padding: 12px 0;
  text-align: center;
  font-weight: bold;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: minmax(120px, auto);
}

.calendar-cell {
  border-right: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  min-height: 120px;
  padding: 4px;
}

.calendar-cell:nth-child(7n) {
  border-right: none;
}

.calendar-cell.empty {
  background-color: #fafafa;
}

.cell-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px;
  border-bottom: 1px dashed #f0f0f0;
}

.date-number {
  font-weight: bold;
}

.today-tag {
  background-color: #1890ff;
  color: white;
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 2px;
}

.cell-content {
  padding: 4px 0;
}

.schedule-item {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 2px;
  padding: 4px;
  margin-bottom: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.schedule-item:hover {
  background-color: #bae7ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.schedule-item.work-type-OVERTIME {
  background-color: #fffbe6;
  border-color: #ffe58f;
}

.schedule-item.work-type-OVERTIME:hover {
  background-color: #fff1b8;
}

.schedule-item.work-type-HOLIDAY {
  background-color: #fff2f0;
  border-color: #ffccc7;
}

.schedule-item.work-type-HOLIDAY:hover {
  background-color: #ffccc7;
}

.schedule-time {
  font-weight: bold;
}

.schedule-location {
  color: #666;
  margin-top: 2px;
}

.schedule-type {
  color: #999;
  font-size: 11px;
  margin-top: 2px;
}
</style>