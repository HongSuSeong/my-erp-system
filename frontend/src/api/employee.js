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
  getAll() {
    return apiClient.get('/employees')
  },

  getById(id) {
    return apiClient.get(`/employees/${id}`)
  },

  create(employee) {
    return apiClient.post('/employees', employee)
  },

  update(id, employee) {
    return apiClient.put(`/employees/${id}`, employee)
  },

  delete(id) {
    return apiClient.delete(`/employees/${id}`)
  },
}