<template>
  <div class="sidebar-container" :class="{ 'is-collapsed': isCollapsed }">
    <div class="logo">
      <img :src="siteConfig.admin.logo.icon" alt="Logo" class="logo-icon" />
      <span class="logo-text" v-show="!isCollapsed">{{ siteConfig.admin.logo.text }}</span>
    </div>
    <div class="menu-wrapper">
      <a-menu 
        v-model:selectedKeys="selectedKeys" 
        :inline-collapsed="isCollapsed" 
        mode="inline" 
        class="sidebar-menu"
       
      >
        <!-- 固定菜单项 -->
        <a-menu-item key="/back/dashboard" @click="handleMenuClick('/back/dashboard')">
          <template #icon>
            <HomeOutlined />
          </template>
          <span>首页</span>
        </a-menu-item>
        
        <a-menu-item key="/back/user" @click="handleMenuClick('/back/user')">
          <template #icon>
            <UserOutlined />
          </template>
          <span>用户管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/membership" @click="handleMenuClick('/back/membership')">
          <template #icon>
            <CrownOutlined />
          </template>
          <span>会员套餐管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/membership-records" @click="handleMenuClick('/back/membership-records')">
          <template #icon>
            <FileTextOutlined />
          </template>
          <span>会员购买记录</span>
        </a-menu-item>
        
        <a-menu-item key="/back/course" @click="handleMenuClick('/back/course')">
          <template #icon>
            <BookOutlined />
          </template>
          <span>课程管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/booking" @click="handleMenuClick('/back/booking')">
          <template #icon>
            <ScheduleOutlined />
          </template>
          <span>预约管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/announcement" @click="handleMenuClick('/back/announcement')">
          <template #icon>
            <NotificationOutlined />
          </template>
          <span>公告管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/notification" @click="handleMenuClick('/back/notification')">
          <template #icon>
            <BellOutlined />
          </template>
          <span>通知管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/coach" @click="handleMenuClick('/back/coach')">
          <template #icon>
            <TeamOutlined />
          </template>
          <span>教练管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/category" @click="handleMenuClick('/back/category')">
          <template #icon>
            <FolderOutlined />
          </template>
          <span>课程分类管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/training-action" @click="handleMenuClick('/back/training-action')">
          <template #icon>
            <FireOutlined />
          </template>
          <span>动作库管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/system-config" @click="handleMenuClick('/back/system-config')">
          <template #icon>
            <SettingOutlined />
          </template>
          <span>系统参数配置</span>
        </a-menu-item>
        
        <a-menu-item key="/back/config-settings" @click="handleMenuClick('/back/config-settings')">
          <template #icon>
            <ControlOutlined />
          </template>
          <span>配置设置</span>
        </a-menu-item>
        
        <a-menu-item key="/back/equipment" @click="handleMenuClick('/back/equipment')">
          <template #icon>
            <ShopOutlined />
          </template>
          <span>器材管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/equipment-maintenance" @click="handleMenuClick('/back/equipment-maintenance')">
          <template #icon>
            <ToolOutlined />
          </template>
          <span>维护记录管理</span>
        </a-menu-item>
        
        <a-menu-item key="/back/equipment-booking" @click="handleMenuClick('/back/equipment-booking')">
          <template #icon>
            <CalendarOutlined />
          </template>
          <span>器材预约记录</span>
        </a-menu-item>
        
        <a-menu-item key="/back/profile" @click="handleMenuClick('/back/profile')">
          <template #icon>
            <UserOutlined />
          </template>
          <span>个人信息</span>
        </a-menu-item>
      </a-menu>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import siteConfig from '@/config/site'
import { HomeOutlined, UserOutlined, CrownOutlined, FileTextOutlined, BookOutlined, ScheduleOutlined, NotificationOutlined, BellOutlined, TeamOutlined, FolderOutlined, FireOutlined, SettingOutlined, ControlOutlined, ShopOutlined, ToolOutlined, CalendarOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const isCollapsed = computed(() => appStore.sidebarCollapsed)

// 当前激活的菜单 - 初始化时也要处理课程时间安排页面
const getInitialSelectedKey = () => {
  if (route.path.startsWith('/back/course/schedule/')) {
    return ['/back/course']
  }
  return [route.path]
}

const selectedKeys = ref(getInitialSelectedKey())

// 监听路由变化更新选中的菜单
watch(() => route.path, (newPath) => {
  // 如果是课程时间安排页面，高亮课程管理菜单
  if (newPath.startsWith('/back/course/schedule/')) {
    selectedKeys.value = ['/back/course']
  } else {
    selectedKeys.value = [newPath]
  }
})

// 处理菜单点击
const handleMenuClick = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%; 
  min-height: 100vh;
  background:rgb(255, 255, 255);
  display: flex;
  flex-direction: column;
  width: 220px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.is-collapsed {
    width: 64px;
    
    .logo {
      padding: 0;
      justify-content: center;
      
      .logo-icon {
        margin: 0;
      }
    }

    :deep(.ant-menu) {
      // 只隐藏文字，不隐藏图标
      .ant-menu-submenu-title > span:not(.anticon),
      .ant-menu-item > span:not(.anticon) {
        opacity: 0;
        transition: opacity 0.2s;
      }
    }
  }
  
  .logo {
    height: 60px;
    flex-shrink: 0;
    line-height: 60px;
    text-align: center;
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    display: flex;
    align-items: center;
    padding: 0 16px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    .logo-icon {
      width: 32px;
      height: 32px;
      margin-right: 8px;
      object-fit: contain;
      transition: margin 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
    
    .logo-text {
      color:rgb(0, 0, 0);
      font-size: 18px;
      font-weight: 600;
      white-space: nowrap;
      opacity: 1;
      transition: opacity 0.2s;
    }
  }

  .menu-wrapper {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
    width: 100%;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.2);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }

  :deep(.sidebar-menu) {
    border: none;
    width: 100% !important;
  }
}
</style> 