<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-header">
        <h1>{{ siteName }}</h1>
    
      </div>

      <div class="auth-content">
        <router-view />
      </div>

      <div class="auth-footer">
        <p>{{ copyRight }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import siteConfig from '@/config/site'

// 使用统一的站点配置
const siteName = siteConfig.name
const siteDescription = siteConfig.description
const copyRight = siteConfig.copyright.text
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('@/assets/bg.jpeg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.auth-layout::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.auth-container {
  max-width: 400px;
  width: 100%;
  margin: 0 auto;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 2;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.auth-header p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.auth-content {
  margin-bottom: 2rem;
}

.auth-footer {
  text-align: center;
  color: #999;
  font-size: 0.8rem;
}
</style>
