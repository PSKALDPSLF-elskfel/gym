import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/CoachLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeOutlined' }
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('@/views/students/List.vue'),
        meta: { title: '学员管理', icon: 'TeamOutlined' }
      },
      {
        path: 'students/:id',
        name: 'StudentDetail',
        component: () => import('@/views/students/Detail.vue'),
        meta: { title: '学员详情', hideInMenu: true }
      },
      {
        path: 'training-plans',
        name: 'TrainingPlans',
        component: () => import('@/views/training-plans/List.vue'),
        meta: { title: '训练方案', icon: 'FileTextOutlined' }
      },
      {
        path: 'training-plans/create',
        name: 'CreateTrainingPlan',
        component: () => import('@/views/training-plans/Create.vue'),
        meta: { title: '创建方案', hideInMenu: true }
      },
      {
        path: 'training-plans/templates',
        name: 'TrainingTemplates',
        component: () => import('@/views/training-plans/Templates.vue'),
        meta: { title: '方案模板', hideInMenu: true }
      },
      {
        path: 'training-plans/:id',
        name: 'TrainingPlanDetail',
        component: () => import('@/views/training-plans/Detail.vue'),
        meta: { title: '训练详情', hideInMenu: true }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/courses/List.vue'),
        meta: { title: '课程管理', icon: 'CalendarOutlined' }
      },
      {
        path: 'courses/:id',
        name: 'CourseDetail',
        component: () => import('@/views/courses/Detail.vue'),
        meta: { title: '课程详情', hideInMenu: true }
      },
      {
        path: 'courses/:id/attendance',
        name: 'CourseAttendance',
        component: () => import('@/views/courses/Attendance.vue'),
        meta: { title: '签到管理', hideInMenu: true }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        redirect: '/schedule/calendar',
        meta: { title: '排班管理', icon: 'ClockCircleOutlined' },
        children: [
          {
            path: 'calendar',
            name: 'ScheduleCalendar',
            component: () => import('@/views/schedule/Calendar.vue'),
            meta: { title: '我的排班' }
          },
          {
            path: 'request',
            name: 'ScheduleRequest',
            component: () => import('@/views/schedule/Request.vue'),
            meta: { title: '排班申请' }
          },
          {
            path: 'statistics',
            name: 'ScheduleStatistics',
            component: () => import('@/views/schedule/Statistics.vue'),
            meta: { title: '排班统计' }
          },
          {
            path: 'attendance',
            name: 'ScheduleAttendance',
            component: () => import('@/views/schedule/Attendance.vue'),
            meta: { title: '上下班打卡' }
          }
        ]
      },
      {
        path: 'reviews',
        name: 'Reviews',
        redirect: '/reviews/list',
        meta: { title: '学员评价', icon: 'StarOutlined' },
        children: [
          {
            path: 'list',
            name: 'ReviewsList',
            component: () => import('@/views/reviews/List.vue'),
            meta: { title: '评价列表' }
          },
          {
            path: 'statistics',
            name: 'ReviewsStatistics',
            component: () => import('@/views/reviews/Reply.vue'),
            meta: { title: '评价统计' }
          }
        ]
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人信息', hideInMenu: true }
      },
      {
        path: 'change-password',
        name: 'ChangePassword',
        component: () => import('@/views/profile/ChangePassword.vue'),
        meta: { title: '修改密码', hideInMenu: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = `${to.meta.title || ''} - 教练管理端` || '教练管理端'
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router