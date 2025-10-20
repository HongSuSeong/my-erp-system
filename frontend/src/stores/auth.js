import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authApi from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const username = computed(() => user.value?.username || '')
  const userName = computed(() => user.value?.name || '')

  // Actions
  
  // 로그인
  async function login(credentials) {
    try {
      const response = await authApi.login(credentials)
      const { token: newToken, username, name, role } = response.data

      // 토큰과 사용자 정보 저장
      token.value = newToken
      user.value = { username, name, role }

      // localStorage에 저장
      localStorage.setItem('token', newToken)
      localStorage.setItem('user', JSON.stringify(user.value))

      ElMessage.success(`${name}님, 환영합니다!`)
      return true
    } catch (error) {
      console.error('로그인 실패:', error)
      if (error.response?.status === 401) {
        ElMessage.error('아이디 또는 비밀번호가 일치하지 않습니다')
      } else {
        ElMessage.error('로그인에 실패했습니다')
      }
      return false
    }
  }

  // 회원가입
  async function signup(userData) {
    try {
      await authApi.signup(userData)
      ElMessage.success('회원가입이 완료되었습니다. 로그인해주세요.')
      return true
    } catch (error) {
      console.error('회원가입 실패:', error)
      const message = error.response?.data?.message || '회원가입에 실패했습니다'
      ElMessage.error(message)
      return false
    }
  }

  // 로그아웃
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.info('로그아웃되었습니다')
  }

  // 토큰 검증
  async function validateToken() {
    if (!token.value) return false

    try {
      const response = await authApi.validateToken(token.value)
      return response.data.valid
    } catch (error) {
      console.error('토큰 검증 실패:', error)
      logout()
      return false
    }
  }

  return {
    // State
    token,
    user,
    // Getters
    isAuthenticated,
    isAdmin,
    username,
    userName,
    // Actions
    login,
    signup,
    logout,
    validateToken
  }
})