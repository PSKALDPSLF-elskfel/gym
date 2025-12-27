<template>
  <view class="page-wrap">
    <mod-nav-bar title="添加运动记录"></mod-nav-bar>
    
    <view class="page-content">
      <view class="form-container">
        <!-- 运动类型选择 -->
        <view class="form-section">
          <view class="section-title">基本信息</view>
          
          <view class="form-item required">
            <text class="label">运动类型</text>
            <picker 
              mode="selector" 
              :range="workoutTypes" 
              range-key="name"
              :value="formData.workoutTypeIndex"
              @change="onWorkoutTypeChange"
            >
              <view class="picker-value">
                {{ selectedWorkoutType ? selectedWorkoutType.name : '请选择运动类型' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
          
          <view class="form-item required">
            <text class="label">运动日期</text>
            <picker 
              mode="date" 
              :value="formData.workoutDate"
              @change="onDateChange"
            >
              <view class="picker-value">
                {{ formData.workoutDate || '请选择日期' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
          
          <view class="form-item required">
            <text class="label">开始时间</text>
            <picker 
              mode="time" 
              :value="formData.startTime"
              @change="onStartTimeChange"
            >
              <view class="picker-value">
                {{ formData.startTime || '请选择时间' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
          
          <view class="form-item">
            <text class="label">结束时间</text>
            <picker 
              mode="time" 
              :value="formData.endTime"
              @change="onEndTimeChange"
            >
              <view class="picker-value">
                {{ formData.endTime || '请选择时间' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
          
          <view class="form-item required">
            <text class="label">时长(分钟)</text>
            <input 
              class="input" 
              type="number" 
              v-model="formData.duration" 
              placeholder="请输入运动时长"
            />
          </view>
          
          <view class="form-item">
            <text class="label">强度</text>
            <picker 
              mode="selector" 
              :range="intensityOptions" 
              range-key="label"
              :value="formData.intensityIndex"
              @change="onIntensityChange"
            >
              <view class="picker-value">
                {{ selectedIntensity ? selectedIntensity.label : '请选择强度' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
        </view>
        
        <!-- 运动数据 -->
        <view class="form-section">
          <view class="section-title">运动数据</view>
          
          <view class="form-item">
            <text class="label">消耗热量(千卡)</text>
            <input 
              class="input" 
              type="number" 
              v-model="formData.calories" 
              placeholder="请输入消耗热量"
            />
          </view>
          
          <view v-if="isCardio" class="form-item">
            <text class="label">距离(公里)</text>
            <input 
              class="input" 
              type="digit" 
              v-model="formData.distance" 
              placeholder="请输入运动距离"
            />
          </view>
          
          <view v-if="isCardio" class="form-item">
            <text class="label">步数</text>
            <input 
              class="input" 
              type="number" 
              v-model="formData.steps" 
              placeholder="请输入步数"
            />
          </view>
          
          <view class="form-item">
            <text class="label">平均心率</text>
            <input 
              class="input" 
              type="number" 
              v-model="formData.heartRateAvg" 
              placeholder="请输入平均心率"
            />
          </view>
          
          <view class="form-item">
            <text class="label">最大心率</text>
            <input 
              class="input" 
              type="number" 
              v-model="formData.heartRateMax" 
              placeholder="请输入最大心率"
            />
          </view>
        </view>
        
        <!-- 运动感受 -->
        <view class="form-section">
          <view class="section-title">运动感受</view>
          
          <view class="form-item">
            <text class="label">感受</text>
            <picker 
              mode="selector" 
              :range="feelingOptions" 
              range-key="label"
              :value="formData.feelingIndex"
              @change="onFeelingChange"
            >
              <view class="picker-value">
                {{ selectedFeeling ? selectedFeeling.label : '请选择感受' }}
                <text class="fa fa-chevron-right"></text>
              </view>
            </picker>
          </view>
          
          <view class="form-item">
            <text class="label">天气</text>
            <input 
              class="input" 
              v-model="formData.weather" 
              placeholder="如：晴天、阴天"
            />
          </view>
          
          <view class="form-item">
            <text class="label">位置</text>
            <input 
              class="input" 
              v-model="formData.location" 
              placeholder="如：健身房跑步机"
            />
          </view>
          
          <view class="form-item full">
            <text class="label">备注</text>
            <textarea 
              class="textarea" 
              v-model="formData.note" 
              placeholder="记录一下今天的运动感受..."
              maxlength="200"
            />
          </view>
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <view class="submit-section">
        <button class="submit-btn" @click="handleSubmit">保存记录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getWorkoutTypeList, createWorkoutRecord } from '@/apis/workout.js'

// 表单数据
const formData = ref({
  workoutTypeIndex: -1,
  workoutTypeId: null,
  workoutDate: '',
  startTime: '',
  endTime: '',
  duration: '',
  intensityIndex: -1,
  intensity: '',
  calories: '',
  distance: '',
  steps: '',
  heartRateAvg: '',
  heartRateMax: '',
  feelingIndex: -1,
  feeling: '',
  weather: '',
  location: '',
  note: ''
})

// 运动类型列表
const workoutTypes = ref([])

// 强度选项
const intensityOptions = [
  { value: 'LOW', label: '低强度' },
  { value: 'MEDIUM', label: '中强度' },
  { value: 'HIGH', label: '高强度' }
]

// 感受选项
const feelingOptions = [
  { value: 'GREAT', label: '非常好' },
  { value: 'GOOD', label: '良好' },
  { value: 'NORMAL', label: '一般' },
  { value: 'TIRED', label: '疲惫' },
  { value: 'BAD', label: '不佳' }
]

// 计算属性
const selectedWorkoutType = computed(() => {
  if (formData.value.workoutTypeIndex >= 0) {
    return workoutTypes.value[formData.value.workoutTypeIndex]
  }
  return null
})

const selectedIntensity = computed(() => {
  if (formData.value.intensityIndex >= 0) {
    return intensityOptions[formData.value.intensityIndex]
  }
  return null
})

const selectedFeeling = computed(() => {
  if (formData.value.feelingIndex >= 0) {
    return feelingOptions[formData.value.feelingIndex]
  }
  return null
})

// 是否是有氧运动
const isCardio = computed(() => {
  return selectedWorkoutType.value?.category === 'CARDIO'
})

/**
 * 获取运动类型列表
 */
const fetchWorkoutTypes = () => {
  getWorkoutTypeList(null, {
    showDefaultMsg: false,
    onSuccess: (data) => {
      workoutTypes.value = data || []
    }
  })
}

/**
 * 运动类型改变
 */
const onWorkoutTypeChange = (e) => {
  const index = e.detail.value
  formData.value.workoutTypeIndex = index
  formData.value.workoutTypeId = workoutTypes.value[index].id
}

/**
 * 日期改变
 */
const onDateChange = (e) => {
  formData.value.workoutDate = e.detail.value
}

/**
 * 开始时间改变
 */
const onStartTimeChange = (e) => {
  formData.value.startTime = e.detail.value
}

/**
 * 结束时间改变
 */
const onEndTimeChange = (e) => {
  formData.value.endTime = e.detail.value
}

/**
 * 强度改变
 */
const onIntensityChange = (e) => {
  const index = e.detail.value
  formData.value.intensityIndex = index
  formData.value.intensity = intensityOptions[index].value
}

/**
 * 感受改变
 */
const onFeelingChange = (e) => {
  const index = e.detail.value
  formData.value.feelingIndex = index
  formData.value.feeling = feelingOptions[index].value
}

/**
 * 提交表单
 */
const handleSubmit = () => {
  // 验证必填项
  if (!formData.value.workoutTypeId) {
    uni.showToast({ title: '请选择运动类型', icon: 'none' })
    return
  }
  if (!formData.value.workoutDate) {
    uni.showToast({ title: '请选择运动日期', icon: 'none' })
    return
  }
  if (!formData.value.startTime) {
    uni.showToast({ title: '请选择开始时间', icon: 'none' })
    return
  }
  if (!formData.value.duration) {
    uni.showToast({ title: '请输入运动时长', icon: 'none' })
    return
  }
  
  // 构建请求数据
  const requestData = {
    workoutTypeId: formData.value.workoutTypeId,
    workoutDate: formData.value.workoutDate,
    startTime: `${formData.value.workoutDate} ${formData.value.startTime}:00`,
    endTime: formData.value.endTime ? `${formData.value.workoutDate} ${formData.value.endTime}:00` : null,
    duration: parseInt(formData.value.duration),
    intensity: formData.value.intensity || null,
    calories: formData.value.calories ? parseInt(formData.value.calories) : null,
    distance: formData.value.distance ? parseFloat(formData.value.distance) : null,
    steps: formData.value.steps ? parseInt(formData.value.steps) : null,
    heartRateAvg: formData.value.heartRateAvg ? parseInt(formData.value.heartRateAvg) : null,
    heartRateMax: formData.value.heartRateMax ? parseInt(formData.value.heartRateMax) : null,
    note: formData.value.note || null,
    feeling: formData.value.feeling || null,
    weather: formData.value.weather || null,
    location: formData.value.location || null,
    isCompleted: 1,
    source: 'MANUAL'
  }
  
  createWorkoutRecord(requestData, {
    successMsg: '运动记录添加成功',
    onSuccess: () => {
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  })
}

// 页面加载
onMounted(() => {
  // 设置默认日期为今天
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  formData.value.workoutDate = `${year}-${month}-${day}`
  
  // 获取运动类型列表
  fetchWorkoutTypes()
})
</script>

<style lang="scss" scoped>
.page-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 160rpx;
}

.form-container {
  padding: 30rpx;
}

.form-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  &.full {
    flex-direction: column;
    align-items: flex-start;
  }
  
  &.required .label::before {
    content: '*';
    color: #ff6b35;
    margin-right: 4rpx;
  }
  
  .label {
    width: 180rpx;
    font-size: 28rpx;
    color: #333;
    flex-shrink: 0;
  }
  
  .input,
  .picker-value {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }
  
  .input {
    text-align: right;
    
    &::placeholder {
      color: #999;
    }
  }
  
  .picker-value {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #333;
    
    .fa {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .textarea {
    width: 100%;
    min-height: 200rpx;
    font-size: 28rpx;
    color: #333;
    margin-top: 20rpx;
    padding: 20rpx;
    background: #f8f8f8;
    border-radius: 8rpx;
    
    &::placeholder {
      color: #999;
    }
  }
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx;
  background: #fff;
  border-top: 1rpx solid #e8e8e8;
  
  .submit-btn {
    width: 100%;
    height: 88rpx;
    background: #ff6b35;
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 44rpx;
    border: none;
    
    &::after {
      border: none;
    }
  }
}
</style>
