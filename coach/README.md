# 🏋️ 健身房教练端管理系统

基于 Vue 3 + Vite + Ant Design Vue 构建的现代化教练端应用。

## 📊 项目信息

- **项目名**: coach-h5
- **版本**: 1.0.0
- **开发框架**: Vue 3 + Vite
- **状态**: 基础框架已完成，功能实现中

## ✨ 功能概览

### 已完成 ✅
- [x] 完整的项目框架搭建
- [x] Vue Router 路由管理
- [x] Pinia 状态管理
- [x] Ant Design Vue UI 组件集成
- [x] HTTP 请求工具 (axios)
- [x] JWT 身份验证流程
- [x] 路由守卫和权限控制
- [x] 响应式布局设计
- [x] 登录页面
- [x] 首页仪表板
- [x] 导航菜单系统

### 规划中 🔧
- [ ] 学员管理模块
- [ ] 训练方案制定
- [ ] 课程管理
- [ ] 排班管理
- [ ] 学员评价反馈
- [ ] 数据统计分析
- [ ] API 接口集成

## 🎯 模块结构

```
教练端应用
├── 首页 (Dashboard)
│   └── 统计数据卡片
├── 学员管理 (Students)
│   ├── 学员列表
│   ├── 学员详情
│   └── 体测记录查看
├── 训练方案 (Training Plans)
│   ├── 方案列表管理
│   ├── 创建方案向导
│   └── 方案模板库
├── 课程管理 (Courses)
│   ├── 课程列表
│   ├── 课程详情
│   └── 学员签到
├── 排班管理 (Schedule)
│   ├── 排班日历
│   └── 申请管理
└── 学员评价 (Reviews)
    ├── 评价列表
    └── 评价回复
```

## 🚀 快速开始

### 环境要求
- Node.js >= 14.0
- npm >= 6.0

### 安装步骤

```bash
# 1. 进入项目目录
cd vue3/coach

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 打开浏览器
# 访问 http://localhost:5173
```

### 登录凭证（演示）
- **用户名**: coach
- **密码**: 123456

## 📚 项目文档

| 文档 | 说明 |
|------|------|
| [QUICK_START.md](./QUICK_START.md) | 快速启动指南 |
| [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) | 详细的项目结构说明 |
| [../缺失功能开发计划.md](../缺失功能开发计划.md) | 完整的开发计划 |

## 🛠 技术栈

### 核心框架
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代构建工具
- **Vue Router 4** - 官方路由管理
- **Pinia** - 现代化状态管理

### UI 组件
- **Ant Design Vue 4** - 企业级 UI 组件库
- **Ant Design Icons Vue** - 图标库

### HTTP & 工具
- **Axios** - Promise 基础 HTTP 请求库
- **JavaScript ES6+** - 现代 JavaScript

## 📂 目录结构

```
src/
├── api/                    # API 接口调用
├── components/
│   └── layout/            # 布局组件（导航栏、菜单）
├── layouts/               # 页面布局组件
├── router/                # 路由配置
├── store/                 # Pinia 状态管理
├── utils/                 # 工具函数
├── views/                 # 页面组件
│   ├── auth/             # 认证相关
│   ├── dashboard/        # 首页
│   ├── students/         # 学员管理
│   ├── training-plans/   # 训练方案
│   ├── courses/          # 课程管理
│   ├── schedule/         # 排班管理
│   ├── reviews/          # 评价管理
│   └── error/            # 错误页面
├── App.vue               # 根组件
└── main.js               # 应用入口
```

## 🔐 认证机制

- JWT Token 基础认证
- localStorage 持久化存储
- 请求自动注入 Authorization 头
- 401 自动重定向登录页
- 登出清除本地数据

## 🎨 UI 设计

- Ant Design Vue 企业级组件
- 响应式栅格布局
- 深色/浅色主题切换预留
- 移动端适配

## 📝 开发规范

### 代码风格
- Vue 3 Composition API
- ES6+ JavaScript
- 单文件组件 (.vue)

### 命名约定
- 组件: PascalCase (MyComponent.vue)
- 文件: kebab-case (my-component.js)
- 常量: UPPER_CASE

### 项目结构
- 按功能模块划分目录
- 共享组件放在 components
- 每个功能模块有独立文件夹
- API 调用统一在 api 目录

## 📦 可用命令

```bash
# 开发服务器（热重载）
npm run dev

# 生产构建
npm run build

# 预览构建产物
npm run preview

# 安装依赖
npm install

# 安装指定依赖
npm install <package-name>
```

## 🔗 相关链接

### 官方文档
- [Vue 3](https://cn.vuejs.org/)
- [Vue Router](https://router.vuejs.org/zh/)
- [Pinia](https://pinia.vuejs.org/zh/)
- [Ant Design Vue](https://www.antdv.com/)
- [Vite](https://vitejs.dev/)

### 项目相关
- 后端服务: `../springboot/`
- 小程序端: `../uniapp/`
- Vue3 管理端: `../src/`

## 📋 项目进度

### 第一阶段 - 基础框架（进行中）
- [x] 项目初始化
- [x] 路由配置
- [x] 状态管理
- [x] 认证流程
- [x] 布局组件
- [ ] API 接口实现

### 第二阶段 - 功能模块（待开始）
- [ ] 学员管理
- [ ] 训练方案
- [ ] 课程管理
- [ ] 排班管理
- [ ] 评价管理

### 第三阶段 - 优化部署（待开始）
- [ ] 性能优化
- [ ] 单元测试
- [ ] 线上部署
- [ ] 运维文档

## 🤝 贡献指南

1. 按照开发计划实现功能
2. 遵循代码规范
3. 提交清晰的 commit 信息
4. 更新相关文档

## ⚠️ 注意事项

- 开发前运行 `npm install` 安装依赖
- 确保后端服务运行在 `http://localhost:8080`
- Token 过期时自动重新登录
- 所有 API 调用需要通过 `utils/request.js`

## 📞 技术支持

遇到问题请参考：
1. 快速启动指南 (QUICK_START.md)
2. 项目结构文档 (PROJECT_STRUCTURE.md)
3. 开发计划文档 (缺失功能开发计划.md)
4. 官方框架文档

## 📄 许可证

内部项目 - 不对外开放

---

**最后更新**: 2025-12-11
**版本**: 1.0.0
**状态**: 基础框架完成，功能实现中 ⚙️
