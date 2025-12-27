<template>
  <a-layout style="min-height: 100vh">
    <!-- 侧边栏 -->
    <nav-sidebar></nav-sidebar>
    
    <!-- 主内容区 -->
    <a-layout>
      <!-- 顶部导航 -->
      <a-layout-header style="background: #fff; padding: 0 24px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1)">
        <nav-header></nav-header>
      </a-layout-header>
      
      <!-- 页面内容 -->
      <a-layout-content style="padding: 24px; background: #f0f2f5">
        <router-view />
      </a-layout-content>
      
      <!-- 页脚 -->
      <a-layout-footer style="text-align: center; background: #f0f2f5">
        <p style="margin: 0; color: #666; font-size: 12px">© 2025 健身房预约系统 | 教练管理端 v1.0</p>
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import NavSidebar from '@/components/layout/NavSidebar.vue'
import NavHeader from '@/components/layout/NavHeader.vue'
</script>

<style scoped>
</style>
