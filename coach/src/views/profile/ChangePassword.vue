<template>
  <div class="change-password-container">
    <a-card title="修改密码" :bordered="false">
      <a-alert
        message="温馨提示"
        description="修改密码后需要重新登录"
        type="info"
        show-icon
        style="margin-bottom: 24px"
      />
      
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <a-form-item label="旧密码" name="oldPassword">
          <a-input-password
            v-model:value="formState.oldPassword"
            placeholder="请输入旧密码"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="新密码" name="newPassword">
          <a-input-password
            v-model:value="formState.newPassword"
            placeholder="请输入新密码（6-20个字符）"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item label="确认新密码" name="confirmPassword">
          <a-input-password
            v-model:value="formState.confirmPassword"
            placeholder="请再次输入新密码"
            autocomplete="off"
          />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading">
              确认修改
            </a-button>
            <a-button @click="handleReset">
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { changePassword } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const formState = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateNewPassword = async (rule, value) => {
  if (!value) {
    return Promise.reject('请输入新密码')
  }
  if (value.length < 6 || value.length > 20) {
    return Promise.reject('新密码长度必须在6-20个字符之间')
  }
  if (value === formState.oldPassword) {
    return Promise.reject('新密码不能与旧密码相同')
  }
  return Promise.resolve()
}

const validateConfirmPassword = async (rule, value) => {
  if (!value) {
    return Promise.reject('请再次输入新密码')
  }
  if (value !== formState.newPassword) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    loading.value = true
    
    // 响应拦截器已经返回了 data 字段，成功时直接返回数据，失败时抛出异常
    await changePassword(
      formState.oldPassword,
      formState.newPassword
    )
    
    message.success('密码修改成功，请重新登录')
    // 延迟后退出登录
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error) {
    console.error('修改密码失败:', error)
    message.error(error.message || '密码修改失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.change-password-container {
  padding: 24px;
}

:deep(.ant-card) {
  max-width: 600px;
  margin: 0 auto;
}
</style>
