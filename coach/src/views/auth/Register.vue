<template>
  <div class="register-container">
    <a-card style="width: 500px; margin: 50px auto">
      <template #title>
        <div style="text-align: center; font-size: 24px; font-weight: bold">
          教练注册
        </div>
      </template>

      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
        autocomplete="off"
        @finish="handleRegister"
      >
        <a-form-item name="username" label="用户名">
          <a-input
            v-model:value="formData.username"
            placeholder="请输入用户名(3-50个字符,仅支持字母、数字、下划线)"
            autocomplete="username"
            :prefix-icon="h(UserOutlined)"
          />
        </a-form-item>

        <a-form-item name="email" label="邮箱">
          <a-input
            v-model:value="formData.email"
            placeholder="请输入邮箱"
            autocomplete="email"
            :prefix-icon="h(MailOutlined)"
          />
        </a-form-item>

        <a-form-item name="phone" label="手机号">
          <a-input
            v-model:value="formData.phone"
            placeholder="请输入手机号"
            autocomplete="tel"
            :prefix-icon="h(PhoneOutlined)"
          />
        </a-form-item>

        <a-form-item name="nickname" label="昵称">
          <a-input
            v-model:value="formData.nickname"
            placeholder="请输入昵称(选填)"
            :prefix-icon="h(UserOutlined)"
          />
        </a-form-item>

        <a-form-item name="password" label="密码">
          <a-input-password
            v-model:value="formData.password"
            placeholder="请输入密码(6-50个字符)"
            autocomplete="new-password"
            :prefix-icon="h(LockOutlined)"
          />
        </a-form-item>

        <a-form-item name="confirmPassword" label="确认密码">
          <a-input-password
            v-model:value="formData.confirmPassword"
            placeholder="请再次输入密码"
            autocomplete="new-password"
            :prefix-icon="h(LockOutlined)"
          />
        </a-form-item>

        <a-form-item name="userType" label="账户类型">
          <a-select v-model:value="formData.userType" placeholder="请选择账户类型">
            <a-select-option value="COACH">教练</a-select-option>
            <a-select-option value="ADMIN">管理员</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            block
            :loading="loading"
          >
            注册
          </a-button>
        </a-form-item>

        <a-form-item>
          <div style="text-align: center">
            已有账号？
            <a @click="goToLogin">立即登录</a>
          </div>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { h, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  username: '',
  email: '',
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  userType: 'COACH'
})

const validateUsername = async (rule, value) => {
  if (!value) {
    return Promise.reject('请输入用户名')
  }
  if (value.length < 3 || value.length > 50) {
    return Promise.reject('用户名长度必须在3到50个字符之间')
  }
  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    return Promise.reject('用户名只能包含字母、数字和下划线')
  }
  return Promise.resolve()
}

const validateEmail = async (rule, value) => {
  if (!value) {
    return Promise.reject('请输入邮箱')
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(value)) {
    return Promise.reject('邮箱格式不正确')
  }
  return Promise.resolve()
}

const validatePhone = async (rule, value) => {
  if (!value) {
    return Promise.reject('请输入手机号')
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    return Promise.reject('手机号格式不正确')
  }
  return Promise.resolve()
}

const validatePassword = async (rule, value) => {
  if (!value) {
    return Promise.reject('请输入密码')
  }
  if (value.length < 6 || value.length > 50) {
    return Promise.reject('密码长度必须在6到50个字符之间')
  }
  return Promise.resolve()
}

const validateConfirmPassword = async (rule, value) => {
  if (!value) {
    return Promise.reject('请再次输入密码')
  }
  if (value !== formData.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const rules = {
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  userType: [{ required: true, message: '请选择账户类型', trigger: 'change' }]
}

const handleRegister = async () => {
  try {
    loading.value = true
    
    console.log('开始注册，请求数据：', formData)
    
    // 调用后端注册接口
    const response = await request.post('/user/add', {
      username: formData.username,
      email: formData.email,
      phone: formData.phone,
      nickname: formData.nickname || formData.username,
      password: formData.password,
      confirmPassword: formData.confirmPassword,
      userType: formData.userType
    })
    
    console.log('注册响应：', response)
    
    if (response) {
      message.success('注册成功！请登录')
      // 跳转到登录页面
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      message.error('注册失败：未知错误')
    }
  } catch (error) {
    console.error('注册失败详情:', error)
    console.error('错误响应:', error.response)
    const errorMsg = error.response?.data?.msg || error.response?.data?.message || error.message || '注册失败，请检查输入信息'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
