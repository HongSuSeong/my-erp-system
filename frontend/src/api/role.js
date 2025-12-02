import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 요청 인터셉터
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

// 응답 인터셉터
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
  // 전체 사용자 및 역할 목록 조회
  getAllUsersWithRoles() {
    return apiClient.get('/roles/users')
  },

  // 사용자 역할 변경
  updateUserRole(userId, role) {
    return apiClient.put(`/roles/users/${userId}`, { role })
  },

  // 역할 목록 조회
  getRoleList() {
    return apiClient.get('/roles/list')
  },

  // 역할별 권한 정보 조회
  getRolePermissions() {
    return apiClient.get('/roles/permissions')
  }
}
