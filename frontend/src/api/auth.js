import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const authApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export default {
  // 로그인
  login(credentials) {
    return authApi.post('/auth/login', credentials)
  },

  // 회원가입
  signup(userData) {
    return authApi.post('/auth/signup', userData)
  },

  // 토큰 검증
  validateToken(token) {
    return authApi.get('/auth/validate', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  }
}