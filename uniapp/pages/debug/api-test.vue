<template>
  <view class="container">
    <view class="header">
      <text class="title">API诊断工具</text>
      <text class="subtitle">调试API连接问题</text>
    </view>

    <!-- 配置显示 -->
    <view class="section">
      <view class="section-title">📋 当前配置</view>
      <view class="config-item">
        <text class="config-label">API地址:</text>
        <text class="config-value">{{ baseUrl }}</text>
      </view>
      <view class="config-item">
        <text class="config-label">登录状态:</text>
        <text class="config-value" :style="{ color: isLoggedIn ? '#4CAF50' : '#f56c6c' }">
          {{ isLoggedIn ? '已登录' : '未登录' }}
        </text>
      </view>
      <view class="config-item">
        <text class="config-label">Token:</text>
        <text class="config-value token">{{ token || '无' }}</text>
      </view>
    </view>

    <!-- 测试按钮 -->
    <view class="section">
      <view class="section-title">🧪 API测试</view>
      
      <button class="test-btn" @click="testHealthCheck">
        <text>1️⃣ 后端健康检查</text>
      </button>

      <button class="test-btn" @click="testGetCurrentUser">
        <text>2️⃣ 获取当前用户</text>
      </button>

      <button class="test-btn" @click="testGetUnreadCount">
        <text>3️⃣ 获取未读通知数量</text>
      </button>

      <button class="test-btn" @click="testGetNotifications">
        <text>4️⃣ 获取通知列表</text>
      </button>

      <button class="test-btn" @click="clearLogs">
        <text>🗑️ 清空日志</text>
      </button>
    </view>

    <!-- 日志输出 -->
    <view class="section">
      <view class="section-title">📝 测试日志</view>
      <scroll-view class="log-container" scroll-y="true">
        <view 
          v-for="(log, index) in logs" 
          :key="index"
          :class="['log-item', log.type]"
        >
          <text class="log-time">{{ log.time }}</text>
          <text class="log-message">{{ log.message }}</text>
        </view>
        <view v-if="logs.length === 0" class="log-empty">
          暂无日志
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user.js'
import { getCurrentUser } from '@/apis/user.js'
import { getUnreadCount, getMyNotifications } from '@/apis/notification.js'

const userStore = useUserStore()
const logs = ref([])

const baseUrl = computed(() => {
  // 获取request.js中的baseURL
  return 'http://localhost:8080/api'
})

const isLoggedIn = computed(() => userStore.isLoggedIn)
const token = computed(() => userStore.token ? userStore.token.substring(0, 20) + '...' : '')

// 添加日志
function addLog(message, type = 'info') {
  const time = new Date().toLocaleTimeString('zh-CN')
  logs.value.push({
    time,
    message,
    type
  })
  
  // 自动滚动到底部
  setTimeout(() => {
    const scrollView = document.querySelector('.log-container')
    if (scrollView) {
      scrollView.scrollTop = scrollView.scrollHeight
    }
  }, 100)
}

// 清空日志
function clearLogs() {
  logs.value = []
  addLog('日志已清空', 'info')
}

// 测试1: 后端健康检查
async function testHealthCheck() {
  addLog('开始测试: 后端健康检查...', 'info')
  
  try {
    const response = await uni.request({
      url: `${baseUrl.value}/user/current`,
      method: 'GET',
      timeout: 5000
    })
    
    addLog(`HTTP状态码: ${response[1].statusCode}`, 'success')
    
    if (response[1].statusCode === 401) {
      addLog('✅ 后端服务正常运行！(返回401是因为未登录)', 'success')
    } else if (response[1].statusCode === 200) {
      addLog('✅ 后端服务正常运行！', 'success')
    } else {
      addLog(`⚠️ 返回状态码: ${response[1].statusCode}`, 'warning')
    }
  } catch (error) {
    addLog(`❌ 连接失败: ${error.message || error}`, 'error')
    addLog(`请检查: 后端是否启动在 ${baseUrl.value}`, 'error')
  }
}

// 测试2: 获取当前用户
async function testGetCurrentUser() {
  if (!userStore.isLoggedIn) {
    addLog('❌ 未登录，请先登录!', 'error')
    return
  }

  addLog('开始测试: 获取当前用户...', 'info')
  
  try {
    const user = await getCurrentUser({ showDefaultMsg: false })
    addLog(`✅ 获取用户成功: ${user.username}`, 'success')
  } catch (error) {
    addLog(`❌ 获取用户失败: ${error.message}`, 'error')
  }
}

// 测试3: 获取未读通知数量
async function testGetUnreadCount() {
  if (!userStore.isLoggedIn) {
    addLog('❌ 未登录，请先登录!', 'error')
    return
  }

  addLog('开始测试: 获取未读通知数量...', 'info')
  
  try {
    const count = await getUnreadCount({ showDefaultMsg: false })
    addLog(`✅ 获取成功: 未读通知${count}条`, 'success')
  } catch (error) {
    addLog(`❌ 获取失败: ${error.message}`, 'error')
    addLog(`完整错误: ${JSON.stringify(error)}`, 'error')
  }
}

// 测试4: 获取通知列表
async function testGetNotifications() {
  if (!userStore.isLoggedIn) {
    addLog('❌ 未登录，请先登录!', 'error')
    return
  }

  addLog('开始测试: 获取通知列表...', 'info')
  
  try {
    const result = await getMyNotifications({
      current: 1,
      size: 10
    }, { showDefaultMsg: false })
    
    addLog(`✅ 获取成功: 共${result.total}条通知`, 'success')
    addLog(`当前返回${result.records?.length || 0}条记录`, 'info')
  } catch (error) {
    addLog(`❌ 获取失败: ${error.message}`, 'error')
    addLog(`完整错误: ${JSON.stringify(error)}`, 'error')
  }
}

onMounted(() => {
  addLog('API诊断工具已启动', 'info')
  addLog(`当前API基地址: ${baseUrl.value}`, 'info')
})
</script>

<style scoped lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;

  .title {
    display: block;
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  .subtitle {
    display: block;
    font-size: 14px;
    opacity: 0.9;
  }
}

.section {
  background: white;
  margin: 15px;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .section-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 12px;
    padding-bottom: 10px;
    border-bottom: 2px solid #667eea;
  }
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;

  &:last-child {
    border-bottom: none;
  }

  .config-label {
    font-weight: 500;
    color: #666;
  }

  .config-value {
    color: #333;
    word-break: break-all;
    flex: 1;
    margin-left: 10px;
    text-align: right;
    font-size: 12px;

    &.token {
      font-family: monospace;
    }
  }
}

.test-btn {
  width: 100%;
  height: 45px;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: bold;

  &:active {
    opacity: 0.8;
  }

  text {
    color: white;
  }
}

.log-container {
  height: 300px;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  font-size: 12px;
  font-family: monospace;
}

.log-item {
  display: flex;
  gap: 8px;
  padding: 6px 8px;
  margin-bottom: 4px;
  border-radius: 4px;
  word-break: break-all;

  .log-time {
    color: #999;
    min-width: 75px;
    white-space: nowrap;
  }

  .log-message {
    flex: 1;
  }

  &.info {
    background: #e3f2fd;
    color: #1976d2;
  }

  &.success {
    background: #e8f5e9;
    color: #388e3c;
  }

  &.warning {
    background: #fff3e0;
    color: #f57c00;
  }

  &.error {
    background: #ffebee;
    color: #d32f2f;
  }
}

.log-empty {
  text-align: center;
  color: #999;
  padding: 40px 10px;
}
</style>
