<template>
    <div class="employee-container">
      <!-- 헤더 -->
      <div class="header">
        <h2>사원 관리</h2>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          사원 등록
        </el-button>
      </div>
  
      <!-- 로딩 중 -->
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>데이터를 불러오는 중...</span>
      </div>
  
      <!-- 사원 목록 테이블 -->
      <el-table 
        v-else
        :data="employees" 
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'id', order: 'ascending' }"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="name" label="이름" width="120" />
        <el-table-column prop="email" label="이메일" width="250" />
        <el-table-column prop="departmentName" label="부서" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.departmentName" type="info">
              {{ scope.row.departmentName }}
            </el-tag>
            <span v-else style="color: #909399;">미배정</span>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="직급" width="120" />
        <el-table-column prop="hireDate" label="입사일" width="150" sortable />
        <el-table-column label="작업" width="180" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary"
              @click="handleEdit(scope.row)"
            >
              수정
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              @click="handleDelete(scope.row)"
            >
              삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 등록/수정 다이얼로그 -->
      <el-dialog
        v-model="showDialog"
        :title="isEdit ? '사원 정보 수정' : '사원 등록'"
        width="500px"
        @close="resetForm"
      >
        <el-form 
          ref="formRef"
          :model="form" 
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="이름" prop="name">
            <el-input v-model="form.name" placeholder="이름을 입력하세요" />
          </el-form-item>
  
          <el-form-item label="이메일" prop="email">
            <el-input 
              v-model="form.email" 
              placeholder="example@company.com"
              type="email"
            />
          </el-form-item>
  
          <el-form-item label="부서" prop="departmentId">
            <el-select 
              v-model="form.departmentId" 
              placeholder="부서를 선택하세요"
              clearable style="width: 100%">
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            >
              <span>{{ dept.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px;">
                {{ dept.employeeCount }}명
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="직급" prop="position">
          <el-select v-model="form.position" placeholder="직급을 선택하세요">
            <el-option label="사원" value="사원" />
            <el-option label="대리" value="대리" />
            <el-option label="과장" value="과장" />
            <el-option label="차장" value="차장" />
            <el-option label="부장" value="부장" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'
import employeeApi from '@/api/employee'
import departmentApi from '@/api/department'

// 데이터
const employees = ref([])
const departments = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 폼 데이터
const form = ref({
  id: null,
  name: '',
  email: '',
  departmentId: null,
  position: ''
})

// 유효성 검사 규칙
const rules = {
  name: [
    { required: true, message: '이름을 입력하세요', trigger: 'blur' },
    { min: 2, max: 20, message: '이름은 2-20자 사이여야 합니다', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '이메일을 입력하세요', trigger: 'blur' },
    { type: 'email', message: '올바른 이메일 형식이 아닙니다', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '직급을 선택하세요', trigger: 'change' }
  ]
}

// 사원 목록 조회
const loadEmployees = async () => {
  loading.value = true
  try {
    const response = await employeeApi.getAll()
    employees.value = response.data
    console.log('사원 목록:', response.data)
  } catch (error) {
    console.error('사원 목록 조회 실패:', error)
    ElMessage.error('사원 목록을 불러오는데 실패했습니다')
  } finally {
    loading.value = false
  }
}

// 부서 목록 조회
const loadDepartments = async () => {
  try {
    const response = await departmentApi.getAll()
    departments.value = response.data
    console.log('부서 목록:', response.data)
  } catch (error) {
    console.error('부서 목록 조회 실패:', error)
    ElMessage.error('부서 목록을 불러오는데 실패했습니다')
  }
}

// 등록/수정 처리
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (isEdit.value) {
        // 수정
        await employeeApi.update(form.value.id, {
          name: form.value.name,
          email: form.value.email,
          departmentId: form.value.departmentId,
          position: form.value.position
        })
        ElMessage.success('사원 정보가 수정되었습니다')
      } else {
        // 등록
        await employeeApi.create({
          name: form.value.name,
          email: form.value.email,
          departmentId: form.value.departmentId,
          position: form.value.position
        })
        ElMessage.success('사원이 등록되었습니다')
      }
      
      showDialog.value = false
      loadEmployees() // 목록 새로고침
    } catch (error) {
      console.error('저장 실패:', error)
      const message = error.response?.data?.message || '저장에 실패했습니다'
      ElMessage.error(message)
    }
  })
}

// 수정 버튼 클릭
const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    name: row.name,
    email: row.email,
    departmentId: row.departmentId,
    position: row.position
  }
  showDialog.value = true
}

// 삭제 버튼 클릭
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `${row.name} 사원을 삭제하시겠습니까?`,
    '삭제 확인',
    {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        await employeeApi.delete(row.id)
        ElMessage.success('사원이 삭제되었습니다')
        loadEmployees() // 목록 새로고침
      } catch (error) {
        console.error('삭제 실패:', error)
        ElMessage.error('삭제에 실패했습니다')
      }
    })
    .catch(() => {
      // 취소 버튼 클릭 시
    })
}

// 폼 초기화
const resetForm = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    email: '',
    departmentId: null,
    position: ''
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadEmployees()
  loadDepartments()
})
</script>

<style scoped>
.employee-container {
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
</style>