<template>
  <div class="profile-container">
    <a-card title="个人信息" :bordered="false">
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <!-- 头像上传区域 -->
        <a-form-item label="头像" name="avatar">
          <a-upload
            name="file"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
            :before-upload="beforeAvatarUpload"
            :custom-request="handleAvatarUpload"
          >
            <img v-if="avatarUrl" :src="avatarUrl" alt="avatar" class="avatar-img" />
            <div v-else class="upload-placeholder">
              <LoadingOutlined v-if="avatarUploading" />
              <PlusOutlined v-else />
              <div class="upload-text">上传头像</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-divider />

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="用户类型" name="userType">
              <a-input v-model:value="formState.userTypeText" disabled />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="昵称" name="nickname">
              <a-input v-model:value="formState.nickname" placeholder="请输入昵称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formState.phone" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider />

        <a-form-item label="专业领域" name="specialty">
          <a-input v-model:value="formState.specialty" placeholder="请输入专业领域" />
        </a-form-item>

        <a-form-item label="个人简介" name="introduction">
          <a-textarea
            v-model:value="formState.introduction"
            placeholder="请输入个人简介"
            :rows="4"
          />
        </a-form-item>

        <a-form-item label="资质证书" name="certificate">
          <a-textarea
            v-model:value="formState.certificate"
            placeholder="请输入资质证书信息"
            :rows="3"
          />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading">
              保存修改
            </a-button>
            <a-button @click="loadUserInfo">
              取消
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getCoachInfo, updateCoachInfo } from '@/api/coach'
import { updateUser } from '@/api/user'
import { uploadImage } from '@/api/file'
import { useUserStore } from '@/store/user'
import { getAvatarUrl } from '@/utils/fileUtils'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons-vue'

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const avatarUploading = ref(false)

const formState = reactive({
  id: null,
  userId: null,
  username: '',
  userTypeText: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  specialty: '',
  introduction: '',
  certificate: ''
})

// 计算属性：头像 URL
const avatarUrl = computed(() => {
  if (!formState.avatar) return ''
  return getAvatarUrl(formState.avatar)
})

const rules = {
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ]
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过2MB！')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = async ({ file }) => {
  try {
    avatarUploading.value = true
    console.log('开始上传头像:', file.name)
    
    // 响应拦截器已经返回了 data 字段
    const filePath = await uploadImage(file)
    console.log('头像上传成功，路径:', filePath)
    
    // 后端返回的是完整的文件路径
    formState.avatar = filePath
    message.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    message.error('头像上传失败')
  } finally {
    avatarUploading.value = false
  }
}

const userTypeMap = {
  'ADMIN': '管理员',
  'MEMBER': '会员',
  'COACH': '教练'
}

const loadUserInfo = async () => {
  try {
    // 响应拦截器已经返回了 data 字段
    const data = await getCoachInfo()
    console.log('loadUserInfo - data:', data)
    formState.id = data.id
    formState.userId = data.userId
    formState.username = data.username
    formState.userTypeText = userTypeMap[data.userType] || data.userType
    formState.nickname = data.nickname || ''
    formState.phone = data.phone || ''
    formState.email = data.email || ''
    formState.avatar = data.avatar || ''
    formState.specialty = data.specialty || ''
    formState.introduction = data.introduction || ''
    formState.certificate = data.certificate || ''
  } catch (error) {
    console.error('获取个人信息失败:', error)
    message.error('获取个人信息失败')
  }
}

const handleSubmit = async () => {
  try {
    loading.value = true
    
    // 更新用户基本信息
    const userUpdateData = {
      nickname: formState.nickname,
      phone: formState.phone,
      email: formState.email,
      avatar: formState.avatar
    }
    
    await updateUser(formState.userId, userUpdateData)

    // 更新教练信息
    const coachUpdateData = {
      specialty: formState.specialty,
      introduction: formState.introduction,
      certificate: formState.certificate
    }
    
    await updateCoachInfo(coachUpdateData)
    
    message.success('个人信息更新成功')
    // 更新本地存储的用户信息
    if (userStore.user) {
      userStore.setUser({
        ...userStore.user,
        name: formState.nickname || formState.username,
        avatar: formState.avatar
      })
    }
    await loadUserInfo()
  } catch (error) {
    console.error('更新个人信息失败:', error)
    message.error(error.message || '更新个人信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
}

:deep(.ant-card) {
  max-width: 900px;
  margin: 0 auto;
}

.avatar-uploader :deep(.ant-upload) {
  width: 128px;
  height: 128px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #999;
}

.upload-placeholder .anticon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #666;
}
</style>
