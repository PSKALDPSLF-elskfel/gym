<template>
  <view class="create-review-page">
    <mod-nav-bar title="发布评价"></mod-nav-bar>
    
    <view class="content">
      <!-- 教练信息 -->
      <view class="section coach-section">
        <text class="section-title">评价教练</text>
        <view class="coach-info">
          <text class="coach-name">{{ coachName || '教练' }}</text>
        </view>
      </view>

      <!-- 星级评分 -->
      <view class="section">
        <text class="section-title required">评分</text>
        <view class="rating-container">
          <view 
            v-for="i in 5" 
            :key="i" 
            class="star-item"
            @click="handleRatingChange(i)"
          >
            <text class="fa fa-star" :class="{ active: i <= formData.rating }"></text>
          </view>
          <text class="rating-text">{{ ratingLabel }}</text>
        </view>
      </view>

      <!-- 评价类型 -->
      <view class="section">
        <text class="section-title required">评价类型</text>
        <view class="radio-group">
          <view 
            class="radio-item"
            :class="{ active: formData.reviewType === 2 }"
            @click="formData.reviewType = 2"
          >
            <text class="fa" :class="formData.reviewType === 2 ? 'fa-check-circle' : 'fa-circle-o'"></text>
            <text>课程评价</text>
          </view>
          <view 
            class="radio-item"
            :class="{ active: formData.reviewType === 1 }"
            @click="formData.reviewType = 1"
          >
            <text class="fa" :class="formData.reviewType === 1 ? 'fa-check-circle' : 'fa-circle-o'"></text>
            <text>训练计划评价</text>
          </view>
        </view>
      </view>

      <!-- 评价标签 -->
      <view class="section">
        <text class="section-title">评价标签（可多选）</text>
        <view class="tag-container">
          <view 
            v-for="tag in availableTags" 
            :key="tag.id" 
            class="tag-item"
            :class="{ active: isTagSelected(tag.tagName) }"
            @click="toggleTag(tag.tagName)"
          >
            {{ tag.tagName }}
          </view>
        </view>
      </view>

      <!-- 评价内容 -->
      <view class="section">
        <text class="section-title">评价内容</text>
        <textarea 
          class="textarea" 
          v-model="formData.content"
          placeholder="请输入您的评价内容（选填）"
          maxlength="500"
          :show-count="true"
        ></textarea>
      </view>

      <!-- 上传图片 -->
      <view class="section">
        <text class="section-title">上传图片（选填）</text>
        <view class="image-upload">
          <view 
            v-for="(img, index) in formData.images" 
            :key="index" 
            class="image-item"
          >
            <image :src="img" mode="aspectFill"></image>
            <view class="delete-btn" @click="removeImage(index)">
              <text class="fa fa-times"></text>
            </view>
          </view>
          <view 
            v-if="formData.images.length < 3" 
            class="upload-btn"
            @click="chooseImage"
          >
            <text class="fa fa-plus"></text>
            <text class="upload-text">上传图片</text>
          </view>
        </view>
        <text class="hint">最多上传3张图片</text>
      </view>

      <!-- 匿名选项 -->
      <view class="section">
        <view class="checkbox-item" @click="formData.isAnonymous = formData.isAnonymous ? 0 : 1">
          <text class="fa" :class="formData.isAnonymous ? 'fa-check-square' : 'fa-square-o'"></text>
          <text>匿名评价</text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="button-group">
        <button class="submit-btn" @click="handleSubmit" :disabled="submitting">
          <text v-if="submitting" class="fa fa-spinner fa-spin"></text>
          {{ submitting ? '提交中...' : '发布评价' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createReview, getReviewTags } from '@/apis/coachReview.js'
import { uploadImage } from '@/apis/file.js'

// 页面参数
const coachId = ref(null)
const coachName = ref('')
const planId = ref(null)
const courseBookingId = ref(null)

// 表单数据
const formData = ref({
  coachId: null,
  planId: null,
  courseBookingId: null,
  reviewType: 2, // 1-训练计划评价，2-课程评价
  rating: 5,
  tagList: [],
  content: '',
  images: [],
  isAnonymous: 0
})

// 可选标签
const availableTags = ref([])

// 提交状态
const submitting = ref(false)

// 计算属性
const ratingLabel = computed(() => {
  const labels = ['', '很差', '较差', '一般', '满意', '非常满意']
  return labels[formData.value.rating] || ''
})

/**
 * 页面加载
 */
onLoad((options) => {
  if (options.coachId) {
    coachId.value = parseInt(options.coachId)
    formData.value.coachId = coachId.value
  }
  if (options.coachName) {
    coachName.value = decodeURIComponent(options.coachName)
  }
  if (options.planId) {
    planId.value = parseInt(options.planId)
    formData.value.planId = planId.value
    formData.value.reviewType = 1
  }
  if (options.courseBookingId) {
    courseBookingId.value = parseInt(options.courseBookingId)
    formData.value.courseBookingId = courseBookingId.value
    formData.value.reviewType = 2
  }
})

onMounted(() => {
  loadTags()
})

/**
 * 加载标签列表
 */
const loadTags = async () => {
  try {
    // 加载正面标签
    const tags = await getReviewTags({ tagType: 1 }, { showDefaultMsg: false })
    availableTags.value = tags || []
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

/**
 * 修改评分
 */
const handleRatingChange = (rating) => {
  formData.value.rating = rating
}

/**
 * 切换标签选择
 */
const toggleTag = (tagName) => {
  const index = formData.value.tagList.indexOf(tagName)
  if (index > -1) {
    formData.value.tagList.splice(index, 1)
  } else {
    if (formData.value.tagList.length < 5) {
      formData.value.tagList.push(tagName)
    } else {
      uni.showToast({ title: '最多选择5个标签', icon: 'none' })
    }
  }
}

/**
 * 判断标签是否选中
 */
const isTagSelected = (tagName) => {
  return formData.value.tagList.includes(tagName)
}

/**
 * 选择图片
 */
const chooseImage = () => {
  uni.chooseImage({
    count: 3 - formData.value.images.length,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      uni.showLoading({ title: '上传中...' })
      
      try {
        for (const filePath of res.tempFilePaths) {
          const url = await uploadImage(filePath)
          formData.value.images.push(url)
        }
      } catch (error) {
        console.error('上传失败:', error)
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  })
}

/**
 * 删除图片
 */
const removeImage = (index) => {
  formData.value.images.splice(index, 1)
}

/**
 * 提交评价
 */
const handleSubmit = async () => {
  // 验证
  if (!formData.value.rating) {
    uni.showToast({ title: '请选择评分', icon: 'none' })
    return
  }

  if (submitting.value) return
  
  submitting.value = true
  
  try {
    await createReview(formData.value)
    
    uni.showToast({ 
      title: '发布成功', 
      icon: 'success',
      duration: 2000
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 2000)
  } catch (error) {
    console.error('发布评价失败:', error)
    uni.showToast({ 
      title: error.message || '发布失败', 
      icon: 'none' 
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.create-review-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  padding: 32rpx;
}

.section {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;

  .section-title {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 24rpx;

    &.required::before {
      content: '*';
      color: #ff4d4f;
      margin-right: 8rpx;
    }
  }
}

.coach-section {
  .coach-info {
    .coach-name {
      font-size: 32rpx;
      color: #667eea;
      font-weight: bold;
    }
  }
}

.rating-container {
  display: flex;
  align-items: center;
  gap: 16rpx;

  .star-item {
    .fa {
      font-size: 64rpx;
      color: #e0e0e0;

      &.active {
        color: #fadb14;
      }
    }
  }

  .rating-text {
    font-size: 28rpx;
    color: #ff6b00;
    font-weight: bold;
    margin-left: 16rpx;
  }
}

.radio-group, .checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.radio-item, .checkbox-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 28rpx;
  color: #333;

  .fa {
    font-size: 36rpx;
    color: #999;
  }

  &.active .fa {
    color: #667eea;
  }
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .tag-item {
    padding: 12rpx 24rpx;
    background: #f5f5f5;
    color: #666;
    font-size: 26rpx;
    border-radius: 32rpx;
    border: 2rpx solid transparent;

    &.active {
      background: #f0f5ff;
      color: #667eea;
      border-color: #667eea;
    }
  }
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  line-height: 1.6;
}

.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .image-item {
    position: relative;
    width: 200rpx;
    height: 200rpx;
    border-radius: 12rpx;
    overflow: hidden;

    image {
      width: 100%;
      height: 100%;
    }

    .delete-btn {
      position: absolute;
      top: 8rpx;
      right: 8rpx;
      width: 48rpx;
      height: 48rpx;
      background: rgba(0, 0, 0, 0.6);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      .fa {
        color: #fff;
        font-size: 24rpx;
      }
    }
  }

  .upload-btn {
    width: 200rpx;
    height: 200rpx;
    background: #f5f5f5;
    border: 2rpx dashed #d9d9d9;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12rpx;

    .fa {
      font-size: 48rpx;
      color: #999;
    }

    .upload-text {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.hint {
  display: block;
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #999;
}

.button-group {
  margin-top: 48rpx;

  .submit-btn {
    width: 100%;
    height: 88rpx;
    background: #667eea;
    color: #fff;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;

    &:disabled {
      opacity: 0.6;
    }

    &::after {
      border: none;
    }

    .fa {
      font-size: 28rpx;
    }
  }
}
</style>
