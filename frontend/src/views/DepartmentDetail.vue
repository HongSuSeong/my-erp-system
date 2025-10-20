<template>
    <div class="detail-container">
      <!-- 로딩 중 -->
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>데이터를 불러오는 중...</span>
      </div>
  
      <!-- 부서 상세 정보 -->
      <div v-else-if="department">
        <!-- 뒤로가기 버튼 -->
        <el-button @click="goBack" style="margin-bottom: 20px;">
          <el-icon><ArrowLeft /></el-icon>
          목록으로
        </el-button>
  
        <!-- 부서 정보 카드 -->
        <el-card class="dept-info-card">
          <template #header>
            <div class="card-header">
              <div class="dept-title">
                <el-icon class="title-icon"><OfficeBuilding /></el-icon>
                <h2>{{ department.name }}</h2>
              </div>
            </div>
          </template>
  
          <el-descriptions :column="2" border>
            <el-descriptions-item label="부서명">
              {{ department.name }}
            </el-descriptions-item>
            <el-descriptions-item label="부서장">
              {{ department.managerName || '미지정' }}
            </el-descriptions-item>
            <el-descriptions-item label="위치">
              {{ department.location || '없음' }}
            </el-descriptions-item>
            <el-descriptions-item label="인원">
              <el-tag type="info">{{ department.employees.length }}명</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="설명" :span="2">
              {{ department.description || '없음' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
  
        <!-- 소속 사원 목록 -->
        <el-card class="employees-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <h3>소속 사원 ({{ department.employees.length }}명)</h3>
              <el-button 
                type="primary" 
                size="small"
                @click="goToEmployeeList"
              >
                사원 관리로 이동
              </el-button>
            </div>
          </template>
  
          <el-empty 
            v-if="department.employees.length === 0"
            description="소속 사원이 없습니다"
          />
  
          <el-table 
            v-else
            :data="department.employees"
            stripe
            style="width: 100%"
          >
            <el-table-column type="index" label="No" width="60" />
            <el-table-column prop="name" label="이름" width="120" />
            <el-table-column prop="email" label="이메일" width="250" />
            <el-table-column prop="position" label="직급" width="120" />
            <el-table-column label="작업" width="120">
              <template #default="scope">
                <el-button 
                  size="small" 
                  type="primary"
                  link
                  @click="viewEmployee(scope.row.id)"
                >
                  상세보기
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { Loading, ArrowLeft, OfficeBuilding } from '@element-plus/icons-vue'
  import departmentApi from '@/api/department'
  
  const router = useRouter()
  const route = useRoute()
  
  const department = ref(null)
  const loading = ref(false)
  
  // 부서 상세 조회
  const loadDepartment = async () => {
    loading.value = true
    try {
      const response = await departmentApi.getById(route.params.id)
      department.value = response.data
      console.log('부서 상세:', response.data)
    } catch (error) {
      console.error('부서 조회 실패:', error)
      ElMessage.error('부서 정보를 불러오는데 실패했습니다')
      goBack()
    } finally {
      loading.value = false
    }
  }
  
  // 뒤로가기
  const goBack = () => {
    router.push('/departments')
  }
  
  // 사원 관리로 이동
  const goToEmployeeList = () => {
    router.push('/employees')
  }
  
  // 사원 상세보기
  const viewEmployee = (employeeId) => {
    // 나중에 사원 상세 페이지 만들면 활성화
    ElMessage.info(`사원 ID: ${employeeId}`)
  }
  
  onMounted(() => {
    loadDepartment()
  })
  </script>
  
  <style scoped>
  .detail-container {
    padding: 20px;
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
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .dept-title {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .title-icon {
    font-size: 28px;
    color: #409EFF;
  }
  
  .dept-title h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
  }
  
  .card-header h3 {
    margin: 0;
    font-size: 18px;
    color: #303133;
  }
  </style>