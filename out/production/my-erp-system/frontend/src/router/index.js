import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginPage from '@/views/LoginPage.vue'
import SignupPage from '@/views/SignupPage.vue'
import EmployeeList from '@/views/EmployeeList.vue'
import DepartmentList from '@/views/DepartmentList.vue'
import DepartmentDetail from '@/views/DepartmentDetail.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
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
    }
  ]
})

// 라우터 가드 (인증 체크)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/employees')
  } else {
    next()
  }
})

export default router