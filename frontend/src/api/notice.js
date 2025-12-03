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
  // 전체 공지사항 조회
  getAll() {
    return apiClient.get('/notices')
  },

  // 공지사항 상세 조회
  getById(id) {
    return apiClient.get(`/notices/${id}`)
  },

  // 카테고리별 조회
  getByCategory(category) {
    return apiClient.get(`/notices/category/${category}`)
  },

  // 검색
  search(keyword) {
    return apiClient.get('/notices/search', { params: { keyword } })
  },

  // 공지사항 작성
  create(notice) {
    return apiClient.post('/notices', notice)
  },

  // 공지사항 수정
  update(id, notice) {
    return apiClient.put(`/notices/${id}`, notice)
  },

  // 공지사항 삭제
  delete(id) {
    return apiClient.delete(`/notices/${id}`)
  }
}
