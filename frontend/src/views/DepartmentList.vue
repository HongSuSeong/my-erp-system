<template>
    <div class="department-container">
      <!-- 헤더 -->
      <div class="header">
        <h2>부서 관리</h2>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          부서 등록
        </el-button>
      </div>
  
      <!-- 로딩 중 -->
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>데이터를 불러오는 중...</span>
      </div>
  
      <!-- 부서 목록 -->
      <div v-else class="department-grid">
        <el-card 
          v-for="dept in departments" 
          :key="dept.id"
          class="department-card"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <div class="dept-info">
                <el-icon class="dept-icon"><OfficeBuilding /></el-icon>
                <span class="dept-name">{{ dept.name }}</span>
              </div>
              <div class="card-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  link
                  @click="viewDetail(dept.id)"
                >
                  상세보기
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="handleEdit(dept)"
                >
                  수정
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="handleDelete(dept)"
                >
                  삭제
                </el-button>
              </div>
            </div>
          </template>
  
          <div class="card-body">
            <div class="info-row">
              <el-icon><Document /></el-icon>
              <span class="label">설명:</span>
              <span class="value">{{ dept.description || '없음' }}</span>
            </div>
            <div class="info-row">
              <el-icon><Location /></el-icon>
              <span class="label">위치:</span>
              <span class="value">{{ dept.location || '없음' }}</span>
            </div>
            <div class="info-row">
              <el-icon><User /></el-icon>
              <span class="label">부서장:</span>
              <span class="value">{{ dept.managerName || '미지정' }}</span>
            </div>
            <div class="info-row">
              <el-icon><UserFilled /></el-icon>
              <span class="label">인원:</span>
              <el-tag type="info" size="small">{{ dept.employeeCount }}명</el-tag>
            </div>
          </div>
        </el-card>
      </div>
  
      <!-- 등록/수정 다이얼로그 -->
      <el-dialog
        v-model="showDialog"
        :title="isEdit ? '부서 정보 수정' : '부서 등록'"
        width="500px"
        @close="resetForm"
      >
        <el-form 
          ref="formRef"
          :model="form" 
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="부서명" prop="name">
            <el-input 
              v-model="form.name" 
              placeholder="부서명을 입력하세요"
              clearable
            />
          </el-form-item>
  
          <el-form-item label="설명" prop="description">
            <el-input 
              v-model="form.description" 
              type="textarea"
              :rows="3"
              placeholder="부서 설명을 입력하세요"
            />
          </el-form-item>
  
          <el-form-item label="위치" prop="location">
            <el-input 
              v-model="form.location" 
              placeholder="예: 본사 3층"
              clearable
            />
          </el-form-item>
  
          <el-form-item label="부서장" prop="managerName">
            <el-input 
              v-model="form.managerName" 
              placeholder="부서장 이름을 입력하세요"
              clearable
            />
          </el-form-item>
        </el-form>
  
        <template #footer>
          <el-button @click="showDialog = false">취소</el-button>
          <el-button type="primary" @click="handleSubmit">
            {{ isEdit ? '수정' : '등록' }}
          </el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { 
    Plus, 
    Loading, 
    OfficeBuilding, 
    Document, 
    Location, 
    User, 
    UserFilled 
  } from '@element-plus/icons-vue'
  import departmentApi from '@/api/department'
  
  const router = useRouter()
  
  // 데이터
  const departments = ref([])
  const loading = ref(false)
  const showDialog = ref(false)
  const isEdit = ref(false)
  const formRef = ref(null)
  
  // 폼 데이터
  const form = ref({
    id: null,
    name: '',
    description: '',
    location: '',
    managerName: ''
  })
  
  // 유효성 검사 규칙
  const rules = {
    name: [
      { required: true, message: '부서명을 입력하세요', trigger: 'blur' },
      { min: 2, max: 50, message: '부서명은 2-50자 사이여야 합니다', trigger: 'blur' }
    ]
  }
  
  // 부서 목록 조회
  const loadDepartments = async () => {
    loading.value = true
    try {
      const response = await departmentApi.getAll()
      departments.value = response.data
      console.log('부서 목록:', response.data)
    } catch (error) {
      console.error('부서 목록 조회 실패:', error)
      ElMessage.error('부서 목록을 불러오는데 실패했습니다')
    } finally {
      loading.value = false
    }
  }
  
  // 상세보기
  const viewDetail = (id) => {
    router.push(`/departments/${id}`)
  }
  
  // 등록/수정 처리
  const handleSubmit = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
      if (!valid) return
  
      try {
        if (isEdit.value) {
          await departmentApi.update(form.value.id, form.value)
          ElMessage.success('부서 정보가 수정되었습니다')
        } else {
          await departmentApi.create(form.value)
          ElMessage.success('부서가 등록되었습니다')
        }
        
        showDialog.value = false
        loadDepartments()
      } catch (error) {
        console.error('저장 실패:', error)
        const message = error.response?.data?.message || '저장에 실패했습니다'
        ElMessage.error(message)
      }
    })
  }
  
  // 수정 버튼 클릭
  const handleEdit = (dept) => {
    isEdit.value = true
    form.value = { ...dept }
    showDialog.value = true
  }
  
  // 삭제 버튼 클릭
  const handleDelete = (dept) => {
    ElMessageBox.confirm(
      `${dept.name} 부서를 삭제하시겠습니까?`,
      '삭제 확인',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
      }
    )
      .then(async () => {
        try {
          await departmentApi.delete(dept.id)
          ElMessage.success('부서가 삭제되었습니다')
          loadDepartments()
        } catch (error) {
          console.error('삭제 실패:', error)
          const message = error.response?.data?.message || '삭제에 실패했습니다'
          ElMessage.error(message)
        }
      })
      .catch(() => {
        // 취소
      })
  }
  
  // 폼 초기화
  const resetForm = () => {
    isEdit.value = false
    form.value = {
      id: null,
      name: '',
      description: '',
      location: '',
      managerName: ''
    }
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }
  
  // 컴포넌트 마운트 시 데이터 로드
  onMounted(() => {
    loadDepartments()
  })
  </script>
  
  <style scoped>
  .department-container {
    padding: 20px;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .header h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
  }
  
  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
    font-size: 16px;
    color: #909399;
  }
  
  .loading .el-icon {
    font-size: 40px;
    margin-bottom: 10px;
  }
  
  .department-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
  }
  
  .department-card {
    transition: transform 0.2s;
  }
  
  .department-card:hover {
    transform: translateY(-5px);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .dept-info {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .dept-icon {
    font-size: 24px;
    color: #409EFF;
  }
  
  .dept-name {
    font-size: 18px;
    font-weight: bold;
    color: #303133;
  }
  
  .card-actions {
    display: flex;
    gap: 5px;
  }
  
  .card-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .info-row {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #606266;
  }
  
  .info-row .el-icon {
    color: #909399;
  }
  
  .label {
    font-weight: 600;
    min-width: 60px;
  }
  
  .value {
    color: #303133;
  }
  </style>