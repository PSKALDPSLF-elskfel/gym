<template>
  <div class="login-container">
    <a-card style="width: 400px; margin: 100px auto">
      <template #title>
        <div style="text-align: center; font-size: 24px; font-weight: bold">
          健身房教练管理
        </div>
      </template>

      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
        autocomplete="off"
        @finish="handleLogin"
      >
        <a-form-item name="username" label="用户名">
          <a-input
            v-model:value="formData.username"
            placeholder="请输入用户名"
            autocomplete="username"
            :prefix-icon="h(UserOutlined)"
          />
        </a-form-item>

        <a-form-item name="password" label="密码">
          <a-input-password
            v-model:value="formData.password"
            placeholder="请输入密码"
            autocomplete="current-password"
            :prefix-icon="h(LockOutlined)"
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            block
            :loading="loading"
          >
            登录
          </a-button>
        </a-form-item>

        <a-form-item>
          <div style="text-align: center">
            还没有账号？
            <a @click="goToRegister">立即注册</a>
          </div>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { h, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const handleLogin = async () => {
  try {
    loading.value = true
    
    console.log('开始登录，请求数据：', formData)
    
    // 调用后端登录接口
    const response = await request.post('/user/login', {
      username: formData.username,
      password: formData.password
    })
    
    console.log('登录响应：', response)
    
    // 后端返回结构：{ userInfo: {...}, token: "...", roleType: "ADMIN" }
    if (response && response.token) {
      const userInfo = response.userInfo
      
      // 保存token
      userStore.setToken(response.token)
      
      // 获取教练信息
      try {
        const coachResponse = await request.get('/coaches/info')
        console.log('教练信息：', coachResponse)
        
        // 保存用户信息（包含coachId）
        userStore.setUser({
          id: userInfo.id,
          username: userInfo.username,
          userType: userInfo.userType,
          name: userInfo.nickname || userInfo.username,
          avatar: userInfo.avatar,
          email: userInfo.email,
          coachId: coachResponse.id  // 保存教练ID
        })
      } catch (coachError) {
        console.error('获取教练信息失败:', coachError)
        // 即使获取教练信息失败，也保存用户信息
        userStore.setUser({
          id: userInfo.id,
          username: userInfo.username,
          userType: userInfo.userType,
          name: userInfo.nickname || userInfo.username,
          avatar: userInfo.avatar,
          email: userInfo.email
        })
      }
      
      message.success('登录成功')
      router.push('/dashboard')
    } else {
      message.error('登录失败：未获取到Token')
    }
  } catch (error) {
    console.error('登录失败详情:', error)
    console.error('错误响应:', error.response)
    const errorMsg = error.response?.data?.msg || error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
