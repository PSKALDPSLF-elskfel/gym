# 教练端快速启动指南

## 📌 项目概况

这是健身房预约系统的教练端 H5 应用，基于 Vue 3 + Vite + Ant Design Vue 构建。

**项目位置**: `D:\健身房预约小程序\vue3\coach`

## 🚀 快速开始（5分钟）

### 步骤1: 安装依赖
```bash
cd D:\健身房预约小程序\vue3\coach
npm install
```

### 步骤2: 启动开发服务器
```bash
npm run dev
```

浏览器自动打开 http://localhost:5173

### 步骤3: 登录测试
- 用户名: `coach`
- 密码: `123456`

## 📁 项目结构一览

```
src/
├── api/              # API 接口（待实现）
├── components/       # 可复用组件
├── layouts/          # 布局组件
├── router/           # 路由配置 ✅
├── store/            # Pinia 状态管理 ✅
├── utils/            # 工具函数 ✅
├── views/            # 页面组件 ✅
├── App.vue           # 根组件 ✅
└── main.js           # 入口文件 ✅
```

## ✨ 已实现的功能

### 核心框架 ✅
- [x] Vue 3 + Vite 项目初始化
- [x] Vue Router 4 路由配置
- [x] Pinia 状态管理
- [x] Ant Design Vue UI 集成
- [x] HTTP 请求工具（axios + 拦截器）
- [x] 路由守卫和认证流程
- [x] JWT Token 管理

### 布局与导航 ✅
- [x] 主布局框架（侧边栏 + 顶部导航）
- [x] 动态菜单导航
- [x] 面包屑导航
- [x] 用户菜单（登出等）

### 页面框架 ✅
- [x] 登录页面
- [x] 工作台仪表板（带统计卡片）
- [x] 学员管理列表
- [x] 所有子页面骨架

## 📋 可用的菜单项

| 菜单项 | 路由 | 状态 |
|--------|------|------|
| 工作台 | `/dashboard` | ✅ 完成 |
| 学员管理 | `/students` | 🔧 待实现功能 |
| 训练方案 | `/training-plans` | 🔧 待实现功能 |
| 课程管理 | `/courses` | 🔧 待实现功能 |
| 我的排班 | `/schedule` | 🔧 待实现功能 |
| 学员评价 | `/reviews` | 🔧 待实现功能 |

## 🔧 开发配置

### 环境变量 (.env)
```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_TITLE=健身房教练管理端
```

### Vite 配置
- 开发服务器端口: `5173`
- API 代理: `/api` 转发到 `http://localhost:8080`
- 别名: `@` 指向 `src` 目录

## 📦 主要依赖版本

```json
{
  "vue": "^3.3.4",
  "vue-router": "^4.6.3",
  "pinia": "^3.0.4",
  "axios": "^1.13.2",
  "ant-design-vue": "^4.2.6",
  "@ant-design/icons-vue": "^6.1.0",
  "vite": "^7.2.4",
  "@vitejs/plugin-vue": "^5.0.4"
}
```

## 🔐 认证流程

1. **登录** → 获取 JWT Token
2. **存储** → Token 保存到 localStorage
3. **请求** → axios 自动在请求头添加 Token
4. **验证** → 401 自动重定向到登录页

## 🎯 核心代码位置

| 功能 | 文件位置 |
|------|---------|
| 路由配置 | `src/router/index.js` |
| 用户状态 | `src/store/user.js` |
| HTTP 请求 | `src/utils/request.js` |
| 主布局 | `src/layouts/CoachLayout.vue` |
| 导航菜单 | `src/components/layout/NavSidebar.vue` |
| 登录页 | `src/views/auth/Login.vue` |
| 工作台 | `src/views/dashboard/Dashboard.vue` |

## 📝 常用命令

```bash
# 开发模式运行
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview

# 安装新依赖
npm install <package-name>
```

## 🐛 常见问题

### Q1: 启动时报错找不到模块
**解决**: 先运行 `npm install` 安装依赖

### Q2: 页面访问 404
**解决**: 检查路由配置 `src/router/index.js` 中是否有该路由

### Q3: 样式不生效
**解决**: 确保在 `main.js` 中导入了 Ant Design Vue 的 CSS 文件

### Q4: API 请求失败
**解决**: 
1. 确保后端服务运行在 `http://localhost:8080`
2. 检查 `.env` 中的 `VITE_API_URL` 配置
3. 查看浏览器控制台的请求详情

## 📚 相关资源

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Vue Router 4 文档](https://router.vuejs.org/zh/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)
- [Ant Design Vue 文档](https://www.antdv.com/)
- [Vite 文档](https://vitejs.dev/)
- [开发计划详情](../缺失功能开发计划.md)

## 🤝 下一步

1. **后端集成**: 修改 API 调用，连接真实后端
2. **页面实现**: 逐个实现各功能模块的完整功能
3. **数据交互**: 添加表单、表格、图表等数据展示
4. **样式优化**: 调整 UI 风格和响应式布局
5. **测试部署**: 完整的功能测试和线上部署

---

**祝你开发愉快！** 🎉
