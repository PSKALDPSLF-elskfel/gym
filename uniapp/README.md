# Uniapp 基础框架

基于 Vue3 项目搭建的 Uniapp 基础框架，包含完整的请求封装、状态管理和工具函数。

## 📁 项目结构

```
uniapp/
├── api/              # API 接口
│   └── user.js      # 用户相关接口
├── config/          # 配置文件
│   └── site.js      # 站点配置
├── pages/           # 页面
│   ├── auth/        # 认证相关页面
│   │   └── login.vue
│   ├── index/       # 首页
│   └── my/          # 我的页面
├── store/           # 状态管理
│   └── user.js      # 用户状态
├── utils/           # 工具函数
│   ├── auth.js      # 认证工具
│   ├── dateUtils.js # 日期工具
│   └── request.js   # 请求封装
└── main.js          # 入口文件
```

## 🚀 快速开始

### 1. Request 请求封装

基于 `uni.request` 封装，支持自动添加 Token、统一错误处理、Toast 提示等。

```javascript
import request from '@/utils/request.js'

// GET 请求
request.get('/user/page', { page: 1, size: 10 })

// POST 请求
request.post('/user/login', { username, password }, {
  successMsg: '登录成功',
  onSuccess: (res) => {
    console.log('登录成功', res)
  }
})

// PUT 请求
request.put('/user/1', { name: '张三' })

// DELETE 请求
request.delete('/user/1', {
  successMsg: '删除成功'
})
```

#### 配置选项

- `showDefaultMsg`: 是否显示默认提示（默认 true）
- `successMsg`: 自定义成功提示
- `errorMsg`: 自定义错误提示
- `onSuccess`: 成功回调函数
- `onError`: 错误回调函数

### 2. Store 状态管理

使用 Pinia 进行状态管理，目前已封装用户状态。

```javascript
import { useUserStore } from '@/store/user.js'

const userStore = useUserStore()

// 获取登录状态
console.log(userStore.isLoggedIn)

// 获取用户信息
console.log(userStore.userInfo)
console.log(userStore.displayName)
console.log(userStore.userType)

// 登录
await userStore.login({ username, password })

// 退出
await userStore.logout()

// 更新用户信息
userStore.updateUserInfo({ nickname: '新昵称' })
```

### 3. API 接口

所有接口统一管理在 `api` 目录下。

```javascript
import { login, getCurrentUser, updateUser } from '@/apis/user.js'

// 登录
const res = await login({ username, password })

// 获取当前用户
const user = await getCurrentUser()

// 更新用户
await updateUser(userId, { name: '张三' })
```

### 4. 认证工具

```javascript
import { requireAuth, safeLogout, getCurrentUser } from '@/utils/auth.js'

// 检查登录状态
if (requireAuth()) {
  // 已登录，执行业务逻辑
}

// 安全退出
await safeLogout()

// 获取当前用户
const user = getCurrentUser()
```

### 5. 日期工具

```javascript
import DateUtils from '@/utils/dateUtils.js'

// 格式化日期
DateUtils.format(new Date(), 'YYYY-MM-DD')  // 2025-01-01
DateUtils.formatDateTime(new Date())         // 2025-01-01 12:00:00
DateUtils.formatDate(new Date())             // 2025-01-01
```

## 📝 使用示例

### 登录功能

```vue
<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user.js'

const loginForm = ref({
  username: '',
  password: ''
})

const userStore = useUserStore()

const handleLogin = async () => {
  await userStore.login(loginForm.value)
  
  uni.showToast({
    title: '登录成功'
  })
  
  uni.switchTab({
    url: '/pages/my/my'
  })
}
</script>
```

### 接口调用

```vue
<script setup>
import { ref } from 'vue'
import { getUserPage } from '@/apis/user.js'

const userList = ref([])

const loadUsers = async () => {
  const res = await getUserPage({
    currentPage: 1,
    size: 10
  })
  
  userList.value = res.records
}
</script>
```

## 🔧 配置说明

### API 基础地址配置

在 `config/site.js` 中配置：

```javascript
// H5端使用代理
// #ifdef H5
baseURL: '/api'
// #endif

// 小程序/App端使用完整地址
// #ifndef H5
baseURL: 'http://localhost:8888/api'  // 开发环境
// #endif
```

### Pinia 初始化

已在 `main.js` 中初始化：

```javascript
import { createPinia } from 'pinia'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()
  app.use(pinia)
  
  return { app }
}
```

## 📌 注意事项

1. **Token 自动携带**：request 会自动从 userStore 读取 token 并添加到请求头
2. **401 自动处理**：收到 401 状态码会自动清除登录信息并跳转登录页
3. **平台差异**：H5 和小程序的 API 地址需要分别配置
4. **状态持久化**：使用 `uni.storage` 持久化存储用户信息

## 🎯 核心特性

✅ 完整的请求封装（支持拦截器、自动 Token、错误处理）  
✅ Pinia 状态管理（用户状态、持久化存储）  
✅ 统一的 API 管理  
✅ 认证工具（登录检查、安全退出）  
✅ 日期格式化工具  
✅ 简洁的 UI 设计  

## 📖 扩展开发

### 添加新的 API 模块

1. 在 `api` 目录创建新文件，如 `article.js`
2. 导入 request 并定义接口函数
3. 在页面中导入使用

### 添加新的 Store

1. 在 `store` 目录创建新文件，如 `app.js`
2. 使用 `defineStore` 定义状态
3. 在页面中使用 `useXxxStore()` 调用

