import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 요청 인터셉터: 모든 요청에 토큰 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터: 401 에러 시 로그인 페이지로 이동
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default {
  // 전체 통계
  getStatistics() {
    return apiClient.get('/dashboard/statistics')
  },

  // 부서별 인원 현황
  getDepartmentStats() {
    return apiClient.get('/dashboard/department-stats')
  },

  // 직급별 분포
  getPositionStats() {
    return apiClient.get('/dashboard/position-stats')
  },

  // 최근 입사자
  getRecentEmployees() {
    return apiClient.get('/dashboard/recent-employees')
  }
}
