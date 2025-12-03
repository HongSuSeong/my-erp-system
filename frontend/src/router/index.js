import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginPage from '@/views/LoginPage.vue'
import SignupPage from '@/views/SignupPage.vue'
import Dashboard from '@/views/Dashboard.vue'
import EmployeeList from '@/views/EmployeeList.vue'
import DepartmentList from '@/views/DepartmentList.vue'
import DepartmentDetail from '@/views/DepartmentDetail.vue'
import RoleManagement from '@/views/RoleManagement.vue'
import NoticeBoard from '@/views/NoticeBoard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/employees',
      name: 'employees',
      component: EmployeeList,
      meta: { requiresAuth: true }
    },
    {
      path: '/departments',
      name: 'departments',
      component: DepartmentList,
      meta: { requiresAuth: true }
    },
    {
      path: '/departments/:id',
      name: 'departmentDetail',
      component: DepartmentDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/notices',
      name: 'notices',
      component: NoticeBoard,
      meta: { requiresAuth: true }
    },
    {
      path: '/roles',
      name: 'roles',
      component: RoleManagement,
      meta: { 
        requiresAuth: true,
        requiresAdmin: true
      }
    }
  ]
})

// 라우터 가드 (인증 및 권한 체크)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth
  const requiresAdmin = to.meta.requiresAdmin

  // 인증 체크
  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // ADMIN 권한 체크
  if (requiresAdmin && !authStore.isAdmin) {
    next('/dashboard')
    return
  }

  // 로그인 페이지 접근 시 이미 로그인되어 있으면 대시보드로
  if (to.path === '/login' && authStore.isAuthenticated) {
    next('/dashboard')
    return
  }

  next()
})

export default router
