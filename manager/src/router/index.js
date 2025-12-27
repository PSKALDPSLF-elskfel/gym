import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import BackendLayout from '@/layouts/BackendLayout.vue'

// 后台路由
export const backendRoutes = [
  {
    path: '/back',
    component: BackendLayout,
    redirect: '/back/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/backend/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled', requiresAuth: true }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/backend/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAuth: true }
      },
      {
        path: 'membership',
        name: 'MembershipManagement',
        component: () => import('@/views/backend/membership/index.vue'),
        meta: { title: '会员套餐管理', icon: 'Crown', requiresAuth: true }
      },
      {
        path: 'membership-records',
        name: 'MembershipRecords',
        component: () => import('@/views/backend/membership/records.vue'),
        meta: { title: '会员购买记录', icon: 'FileText', requiresAuth: true }
      },
      {
        path: 'course',
        name: 'CourseManagement',
        component: () => import('@/views/backend/course/index.vue'),
        meta: { title: '课程管理', icon: 'Book', requiresAuth: true }
      },
      {
        path: 'course/schedule/:courseId',
        name: 'CourseSchedule',
        component: () => import('@/views/backend/course/CourseSchedule.vue'),
        meta: { title: '课程时间安排', icon: 'Calendar', requiresAuth: true }
      },
      {
        path: 'booking',
        name: 'BookingManagement',
        component: () => import('@/views/backend/booking/index.vue'),
        meta: { title: '预约管理', icon: 'Calendar', requiresAuth: true }
      },
      {
        path: 'announcement',
        name: 'AnnouncementManagement',
        component: () => import('@/views/backend/announcement/index.vue'),
        meta: { title: '公告管理', icon: 'Notification', requiresAuth: true }
      },
      {
        path: 'notification',
        name: 'NotificationManagement',
        component: () => import('@/views/backend/notification/index.vue'),
        meta: { title: '通知管理', icon: 'Bell', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'BackendProfile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息', icon: 'UserFilled', requiresAuth: true }
      },
      {
        path: 'coach',
        name: 'CoachManagement',
        component: () => import('@/views/backend/coach/index.vue'),
        meta: { title: '教练管理', icon: 'User', requiresAuth: true }
      },
      {
        path: 'category',
        name: 'CourseCategoryManagement',
        component: () => import('@/views/backend/category/index.vue'),
        meta: { title: '课程分类管理', icon: 'Folder', requiresAuth: true }
      },
      {
        path: 'training-action',
        name: 'TrainingActionManagement',
        component: () => import('@/views/backend/training/index.vue'),
        meta: { title: '动作库管理', icon: 'Trophy', requiresAuth: true }
      },
      {
        path: 'system-config',
        name: 'SystemConfig',
        component: () => import('@/views/backend/system/config.vue'),
        meta: { title: '系统参数配置', icon: 'Setting', requiresAuth: true }
      },
      {
        path: 'config-settings',
        name: 'ConfigSettings',
        component: () => import('@/views/backend/system/ConfigSettings.vue'),
        meta: { title: '配置设置', icon: 'SettingOutlined', requiresAuth: true }
      },
      {
        path: 'equipment',
        name: 'EquipmentManagement',
        component: () => import('@/views/backend/equipment/index.vue'),
        meta: { title: '器材管理', icon: 'Bicycle', requiresAuth: true }
      },
      {
        path: 'equipment-maintenance',
        name: 'EquipmentMaintenance',
        component: () => import('@/views/backend/equipment/maintenance.vue'),
        meta: { title: '维护记录管理', icon: 'Tool', requiresAuth: true }
      },
      {
        path: 'equipment-booking',
        name: 'EquipmentBooking',
        component: () => import('@/views/backend/equipment/booking.vue'),
        meta: { title: '器材预约记录', icon: 'Schedule', requiresAuth: true }
      }
    ]
  }
]

// 前台路由配置
const frontendRoutes = [
  // 认证相关路由使用专门的认证布局
  {
    path: '/auth',
    component: () => import('@/layouts/AuthLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: () => import('@/views/auth/ForgotPassword.vue'),
        meta: { title: '找回密码' }
      }
    ]
  },
  // 兼容旧路由
  {
    path: '/login',
    redirect: '/auth/login'
  },
  {
    path: '/register',
    redirect: '/auth/register'
  }
]

// 错误页面路由
const errorRoutes = [
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 路由配置
const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 根路径路由 - 必须在通配符路由之前定义
    {
      path: '/',
      redirect: () => {
        const userStore = useUserStore()
        return userStore.isLoggedIn ? '/back/dashboard' : '/auth/login'
      }
    },
    ...frontendRoutes,
    ...backendRoutes,
    ...errorRoutes
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 健身房预约管理系统`
  }

  const userStore = useUserStore()

  // 1. 已登录用户访问登录相关页面时，重定向到后台首页
  const loginPaths = ['/login', '/auth/login', '/auth/register', '/register']
  if (userStore.isLoggedIn && loginPaths.includes(to.path)) {
    next('/back/dashboard')
    return
  }

  // 2. 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth) && !userStore.isLoggedIn) {
    next({
      path: '/auth/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 3. 放行
  next()
})

export default router
