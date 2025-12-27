<template>
  <a-layout-sider theme="dark" :width="200" :collapsed="collapsed" @collapse="handleCollapse">
    <div class="logo" style="color: white; text-align: center; padding: 16px; font-weight: bold; font-size: 16px">
      {{ collapsed ? 'GM' : '健身管理' }}
    </div>
    
    <a-menu 
      theme="dark" 
      mode="inline" 
      :selected-keys="selectedKeys" 
      @click="handleMenuClick"
      v-model:open-keys="openKeys"
    >
      <template v-for="route in menuRoutes" :key="route.fullPath">
        <!-- 如果有子菜单，则渲染为SubMenu -->
        <a-sub-menu 
          v-if="route.children && route.children.length > 0" 
          :key="route.fullPath"
        >
          <template #title>
            <component v-if="route.meta.icon" :is="resolveIcon(route.meta.icon)" />
            <span>{{ route.meta.title }}</span>
          </template>
          <a-menu-item 
            v-for="child in route.children" 
            :key="child.fullPath"
          >
            <component v-if="child.meta.icon" :is="resolveIcon(child.meta.icon)" />
            <router-link :to="child.fullPath">{{ child.meta.title }}</router-link>
          </a-menu-item>
        </a-sub-menu>
        
        <!-- 否则渲染为普通菜单项 -->
        <a-menu-item v-else :key="route.fullPath">
          <template #icon>
            <component v-if="route.meta.icon" :is="resolveIcon(route.meta.icon)" />
          </template>
          <router-link :to="route.fullPath">{{ route.meta.title }}</router-link>
        </a-menu-item>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import * as AntdIcons from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const openKeys = ref([])

// 获取菜单路由
const menuRoutes = computed(() => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
  if (!layoutRoute || !layoutRoute.children) return []
  
  // 过滤掉隐藏的菜单项，并构建完整的路由树
  const filteredRoutes = layoutRoute.children
    .filter(r => !r.meta?.hideInMenu)
    .map(child => ({
      ...child,
      // 确保路径是完整路径
      fullPath: child.path.startsWith('/') ? child.path : `/${child.path}`,
      // 处理子路由
      children: child.children 
        ? child.children.filter(c => !c.meta?.hideInMenu).map(c => ({
            ...c,
            fullPath: child.path.startsWith('/') 
              ? `${child.path}${c.path.startsWith('/') ? c.path : `/${c.path}`}` 
              : `/${child.path}${c.path.startsWith('/') ? c.path : `/${c.path}`}`
          }))
        : []
    }))
    
  return filteredRoutes
})

// 获取当前选中的菜单
const selectedKeys = computed(() => {
  const path = route.path
  // 使用完整路径匹配
  const parent = menuRoutes.value.find(r => {
    const routePath = r.fullPath
    return path === routePath || path.startsWith(routePath + '/')
  })
  return parent ? [parent.fullPath] : []
})

// 监听路由变化，自动展开包含当前路由的子菜单
watch(
  () => route.path,
  (newPath) => {
    // 查找包含当前路径的父级菜单
    const parentRoute = menuRoutes.value.find(r => {
      if (r.children && r.children.length > 0) {
        return r.children.some(child => newPath.startsWith(child.fullPath))
      }
      return false
    })
    
    if (parentRoute) {
      openKeys.value = [parentRoute.fullPath]
    }
  },
  { immediate: true }
)

const handleCollapse = (collapsed) => {
  // 处理菜单收起/展开
}

const handleMenuClick = (e) => {
  // 菜单点击处理
}

const resolveIcon = (iconName) => {
  if (!iconName) return AntdIcons.HomeOutlined
  // 处理 icon 名称，例如 'HomeOutlined' -> HomeOutlined
  const iconComponent = AntdIcons[iconName]
  return iconComponent || AntdIcons.HomeOutlined
}
</script>

<style scoped>
.logo {
  color: white;
  text-align: center;
  padding: 16px;
  font-weight: bold;
  font-size: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.ant-menu) {
  background: transparent;
}

:deep(.ant-menu-item) {
  margin-top: 8px;
}

:deep(.ant-menu-item a) {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
}

:deep(.ant-menu-item-selected a) {
  color: #1890ff;
}
</style>