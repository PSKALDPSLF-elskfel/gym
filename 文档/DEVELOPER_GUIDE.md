# 🎯 开发者快速参考

快速查找常用代码片段和配置说明。

## 📋 目录

- [快速命令](#快速命令)
- [文件创建模板](#文件创建模板)
- [常用 API 调用](#常用-api-调用)
- [组件使用示例](#组件使用示例)
- [路由添加步骤](#路由添加步骤)
- [常见问题解决](#常见问题解决)

---

## 🚀 快速命令

```bash
# 安装依赖
npm install

# 开发
npm run dev

# 构建
npm run build

# 预览
npm run preview

# 安装新包
npm install <package-name>

# 查看依赖
npm list
```

---

## 📝 文件创建模板

### Vue 页面组件模板

```vue
<template>
  <a-card title="页面标题" :bordered="false">
    <!-- 搜索栏 -->
    <template #extra>
      <a-space>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索..."
          style="width: 200px"
          @search="handleSearch"
        />
      </a-space>
    </template>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="tableData"
      :loading="loading"
      :pagination="pagination"
      rowKey="id"
      @change="handleTableChange"
    />
  </a-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const loading = ref(false)
const searchText = ref('')
const tableData = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  {
    title: '列1',
    dataIndex: 'field1',
    key: 'field1'
  }
]

const handleSearch = () => {
  message.info('搜索')
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
}
</script>

<style scoped>
</style>
```

### 新 API 接口文件模板

```javascript
import request from '@/utils/request'

// 列表查询
export const getList = (params) => {
  return request.get('/api/path', { params })
}

// 详情查询
export const getDetail = (id) => {
  return request.get(`/api/path/${id}`)
}

// 创建
export const create = (data) => {
  return request.post('/api/path', data)
}

// 更新
export const update = (id, data) => {
  return request.put(`/api/path/${id}`, data)
}

// 删除
export const delete = (id) => {
  return request.delete(`/api/path/${id}`)
}
```

---

## 📡 常用 API 调用

### 获取学员列表

```javascript
import { getMyStudents } from '@/api/student'

const students = await getMyStudents({
  page: 1,
  pageSize: 10
})
```

### 获取训练方案

```javascript
import { getTrainingPlans } from '@/api/trainingPlan'

const plans = await getTrainingPlans({
  userId: 1,
  page: 1
})
```

### 获取课程列表

```javascript
import { getMyCourses } from '@/api/course'

const courses = await getMyCourses({
  startDate: '2025-01-01',
  endDate: '2025-01-31'
})
```

### 教练登录

```javascript
import { login } from '@/api/coach'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const result = await login('username', 'password')
userStore.setToken(result.token)
userStore.setUser(result.user)
```

---

## 🎨 组件使用示例

### 表格组件

```vue
<a-table
  :columns="columns"
  :data-source="data"
  :loading="loading"
  :pagination="pagination"
  rowKey="id"
  @change="onChange"
>
  <!-- 自定义列 -->
  <template #bodyCell="{ column, record }">
    <template v-if="column.key === 'action'">
      <a-space>
        <a-button type="link" size="small" @click="edit(record)">
          编辑
        </a-button>
        <a-button type="link" danger size="small" @click="delete(record)">
          删除
        </a-button>
      </a-space>
    </template>
  </template>
</a-table>
```

### 表单组件

```vue
<a-form
  ref="formRef"
  :model="formData"
  :rules="rules"
  layout="vertical"
  @finish="onFinish"
>
  <a-form-item name="username" label="用户名">
    <a-input v-model:value="formData.username" />
  </a-form-item>

  <a-form-item>
    <a-button type="primary" html-type="submit">
      提交
    </a-button>
  </a-form-item>
</a-form>
```

### 模态框

```vue
<a-modal
  v-model:visible="visible"
  title="标题"
  @ok="handleOk"
  @cancel="handleCancel"
>
  <p>内容</p>
</a-modal>
```

### 消息提示

```javascript
import { message } from 'ant-design-vue'

message.success('成功')
message.error('错误')
message.warning('警告')
message.info('信息')

// 确认框
message.confirm({
  title: '确认',
  content: '是否继续?',
  onOk() { /* 点击确定 */ },
  onCancel() { /* 点击取消 */ }
})
```

---

## 🔀 路由添加步骤

### 1. 在 `src/router/index.js` 中添加路由

```javascript
{
  path: 'new-module',
  name: 'NewModule',
  component: () => import('@/views/new-module/List.vue'),
  meta: { title: '新模块', icon: 'FileTextOutlined' }
}
```

### 2. 创建页面文件

```
src/views/new-module/
├── List.vue          # 列表页
├── Detail.vue        # 详情页
└── Create.vue        # 创建页
```

### 3. 创建 API 文件 (可选)

```javascript
// src/api/newModule.js
import request from '@/utils/request'

export const getList = (params) => {
  return request.get('/api/new-module', { params })
}
```

### 4. 在菜单中自动显示

菜单会自动根据路由配置生成，无需额外配置。

---

## 🔧 常见问题解决

### Q1: 导入文件找不到

```javascript
// ❌ 错误
import Component from './components/MyComponent'

// ✅ 正确
import Component from '@/components/MyComponent.vue'
```

### Q2: 样式不生效

```vue
<!-- ❌ 错误 -->
<style>
.className {
  color: red;
}
</style>

<!-- ✅ 正确 -->
<style scoped>
.className {
  color: red;
}
</style>
```

### Q3: 路由跳转

```javascript
// 导入路由
import { useRouter } from 'vue-router'
const router = useRouter()

// 跳转
router.push('/students')
router.push({ name: 'StudentDetail', params: { id: 1 } })
router.replace('/login')
router.back()
```

### Q4: 获取路由参数

```javascript
import { useRoute } from 'vue-router'
const route = useRoute()

// 获取路径参数
const id = route.params.id

// 获取查询参数
const query = route.query.search
```

### Q5: 使用 Pinia Store

```javascript
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 访问状态
const token = userStore.token
const isAuth = userStore.isAuthenticated

// 调用方法
userStore.setToken('new-token')
userStore.logout()
```

### Q6: HTTP 请求错误处理

```javascript
import request from '@/utils/request'

try {
  const data = await request.get('/api/path')
  console.log(data)
} catch (error) {
  console.error('请求失败:', error.message)
}
```

---

## 📦 状态管理模式

### 访问状态

```javascript
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 方法1: 直接访问
const token = userStore.token

// 方法2: 响应式引用
const { token, isAuthenticated } = storeToRefs(userStore)
```

### 修改状态

```javascript
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 单个修改
userStore.setToken('new-token')

// 批量修改
userStore.$patch({
  token: 'new-token',
  user: { id: 1, name: 'John' }
})
```

---

## 🎯 性能优化建议

### 1. 路由懒加载
✅ 已配置，所有路由都使用动态导入

### 2. 组件复用
使用 Ant Design Vue 的现成组件，减少重复代码

### 3. 状态管理
通过 Pinia 集中管理状态，避免组件间通信复杂度

### 4. 图片优化
使用 CDN，压缩图片尺寸

### 5. 代码分割
Vite 自动处理，无需额外配置

---

## 📚 项目文件导航

| 位置 | 说明 |
|------|------|
| src/router/index.js | 路由配置 |
| src/store/user.js | 用户状态 |
| src/utils/request.js | HTTP 请求 |
| src/api/*.js | API 接口 |
| src/views/* | 页面组件 |
| src/components/* | 可复用组件 |
| vite.config.js | Vite 配置 |
| package.json | 依赖配置 |
| .env | 环境变量 |

---

## 💡 最佳实践

1. **统一的 API 调用方式**
   - 所有请求都通过 `utils/request.js`

2. **统一的错误处理**
   - 在响应拦截器统一处理错误

3. **状态管理集中化**
   - 使用 Pinia 管理全局状态

4. **组件化开发**
   - 尽量拆分小的、可复用的组件

5. **类型提示注释**
   - 为函数参数和返回值加注释

6. **清晰的目录结构**
   - 按功能模块组织代码

7. **环境变量分离**
   - 敏感配置使用 .env 文件

---

## 🔗 相关资源链接

- [Vue 3 文档](https://cn.vuejs.org/)
- [Ant Design Vue](https://www.antdv.com/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)
- [Vite 文档](https://vitejs.dev/)

---

## 👨‍💻 开发工作流

```
1. 创建分支
   git checkout -b feature/module-name

2. 开发功能
   npm run dev

3. 提交代码
   git add .
   git commit -m "feat: description"

4. 推送
   git push origin feature/module-name

5. 创建 PR 并审核

6. 合并到主分支
```

---

*最后更新: 2025-12-11*  
*快速参考指南 v1.0*

