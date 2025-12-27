<template>
  <div style="display: flex; justify-content: space-between; align-items: center">
    <!-- 左侧：面包屑 -->
    <a-breadcrumb style="font-size: 14px">
      <a-breadcrumb-item href="/">
        <HomeOutlined /> 首页
      </a-breadcrumb-item>
      <a-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
        {{ item.title }}
      </a-breadcrumb-item>
    </a-breadcrumb>

    <!-- 右侧：用户菜单 -->
    <a-dropdown>
      <template #overlay>
        <a-menu @click="handleMenuClick">
          <a-menu-item key="profile">
            <UserOutlined /> 个人信息
          </a-menu-item>
          <a-menu-item key="password">
            <LockOutlined /> 修改密码
          </a-menu-item>
          <a-menu-divider />
          <a-menu-item key="logout">
            <LogoutOutlined /> 退出登录
          </a-menu-item>
        </a-menu>
      </template>
      <a-button type="text" style="display: flex; align-items: center; gap: 8px;">
        <a-avatar 
          :src="userStore.user?.avatar" 
          :icon="!userStore.user?.avatar ? h(UserOutlined) : undefined"
          :size="32"
        />
        {{ userStore.user?.name || '教练' }}
        <DownOutlined />
      </a-button>
    </a-dropdown>
  </div>
</template>

<script setup>
import { computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { message } from 'ant-design-vue'
import { 
  HomeOutlined, 
  UserOutlined, 
  LockOutlined, 
  LogoutOutlined,
  DownOutlined 
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const breadcrumbs = computed(() => {
  const matched = route.matched
  return matched
    .filter(record => record.meta?.title && record.path !== '/')
    .map(record => ({
      title: record.meta.title,
      path: record.path
    }))
})

const handleMenuClick = ({ key }) => {
  switch (key) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/change-password')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  message.success('已登出')
}
</script>

<style scoped>
:deep(.ant-dropdown-trigger) {
  cursor: pointer;
}

:deep(.ant-btn) {
  height: auto !important;
  padding: 4px 12px !important;
}
</style>
