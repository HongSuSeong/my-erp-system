import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'multipart/form-data',
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

export default {
  // 파일 업로드
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post('/files/upload', formData)
  },

  // 파일 다운로드 URL 생성
  getDownloadUrl(filename) {
    return `${API_BASE_URL}/files/download/${filename}`
  },

  // 파일 삭제
  delete(filename) {
    return apiClient.delete(`/files/delete/${filename}`)
  }
}
