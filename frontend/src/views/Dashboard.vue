<template>
  <div class="dashboard-container">
    <h2 class="page-title">대시보드</h2>

    <!-- 로딩 중 -->
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>데이터를 불러오는 중...</span>
    </div>

    <div v-else>
      <!-- 통계 카드 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card total-departments">
            <div class="stat-content">
              <el-icon class="stat-icon"><OfficeBuilding /></el-icon>
              <div class="stat-info">
                <div class="stat-label">전체 부서</div>
                <div class="stat-value">{{ statistics.totalDepartments }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card total-employees">
            <div class="stat-content">
              <el-icon class="stat-icon"><UserFilled /></el-icon>
              <div class="stat-info">
                <div class="stat-label">전체 사원</div>
                <div class="stat-value">{{ statistics.totalEmployees }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card largest-dept">
            <div class="stat-content">
              <el-icon class="stat-icon"><TrophyBase /></el-icon>
              <div class="stat-info">
                <div class="stat-label">최대 부서</div>
                <div class="stat-value">{{ statistics.largestDepartment?.name }}</div>
                <div class="stat-sub">({{ statistics.largestDepartment?.count }}명)</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card avg-employees">
            <div class="stat-content">
              <el-icon class="stat-icon"><DataAnalysis /></el-icon>
              <div class="stat-info">
                <div class="stat-label">평균 인원</div>
                <div class="stat-value">{{ averageEmployees }}</div>
                <div class="stat-sub">명/부서</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 차트 영역 -->
      <el-row :gutter="20" class="charts-row">
        <!-- 부서별 인원 현황 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>부서별 인원 현황</span>
              </div>
            </template>
            <div class="chart-wrapper">
              <div
                v-for="dept in departmentStats"
                :key="dept.id"
                class="dept-bar-item"
              >
                <div class="dept-bar-label">
                  <span class="dept-name">{{ dept.name }}</span>
                  <span class="dept-count">{{ dept.count }}명</span>
                </div>
                <el-progress
                  :percentage="getDepartmentPercentage(dept.count)"
                  :color="getProgressColor(getDepartmentPercentage(dept.count))"
                  :stroke-width="20"
                />
              </div>
              <el-empty v-if="departmentStats.length === 0" description="데이터가 없습니다" />
            </div>
          </el-card>
        </el-col>

        <!-- 직급별 분포 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>직급별 분포</span>
              </div>
            </template>
            <div class="chart-wrapper">
              <div
                v-for="(item, index) in positionStats"
                :key="index"
                class="position-item"
              >
                <div class="position-info">
                  <el-tag
                    :type="getPositionTagType(item.position)"
                    size="large"
                    class="position-tag"
                  >
                    {{ item.position }}
                  </el-tag>
                  <span class="position-count">{{ item.count }}명</span>
                </div>
                <el-progress
                  :percentage="getPositionPercentage(item.count)"
                  :stroke-width="12"
                />
              </div>
              <el-empty v-if="positionStats.length === 0" description="데이터가 없습니다" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 최근 입사자 -->
      <el-row>
        <el-col :span="24">
          <el-card class="recent-card">
            <template #header>
              <div class="card-header">
                <span>최근 입사자</span>
                <el-button type="primary" size="small" @click="goToEmployees">
                  전체 보기
                </el-button>
              </div>
            </template>
            <el-table :data="recentEmployees" stripe style="width: 100%">
              <el-table-column type="index" label="No" width="60" />
              <el-table-column prop="name" label="이름" width="120" />
              <el-table-column prop="email" label="이메일" width="250" />
              <el-table-column prop="departmentName" label="부서" width="120">
                <template #default="scope">
                  <el-tag v-if="scope.row.departmentName !== '미배정'" type="info">
                    {{ scope.row.departmentName }}
                  </el-tag>
                  <span v-else style="color: #909399;">미배정</span>
                </template>
              </el-table-column>
              <el-table-column prop="position" label="직급" width="120" />
              <el-table-column prop="hireDate" label="입사일" width="150" />
            </el-table>
            <el-empty
              v-if="recentEmployees.length === 0"
              description="최근 입사자가 없습니다"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Loading,
  OfficeBuilding,
  UserFilled,
  TrophyBase,
  DataAnalysis
} from '@element-plus/icons-vue'
import dashboardApi from '@/api/dashboard'

const router = useRouter()

// 데이터
const loading = ref(false)
const statistics = ref({
  totalDepartments: 0,
  totalEmployees: 0,
  largestDepartment: { name: '없음', count: 0 },
  averageEmployeesPerDepartment: 0
})
const departmentStats = ref([])
const positionStats = ref([])
const recentEmployees = ref([])

// 계산된 속성
const averageEmployees = computed(() => {
  return statistics.value.averageEmployeesPerDepartment.toFixed(1)
})

// 부서별 인원 백분율 계산
const getDepartmentPercentage = (count) => {
  const max = Math.max(...departmentStats.value.map(d => d.count), 1)
  return Math.round((count / max) * 100)
}

// 직급별 백분율 계산
const getPositionPercentage = (count) => {
  const total = positionStats.value.reduce((sum, item) => sum + item.count, 0)
  return total > 0 ? Math.round((count / total) * 100) : 0
}

// 프로그레스 바 색상
const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67C23A'
  if (percentage >= 50) return '#409EFF'
  if (percentage >= 30) return '#E6A23C'
  return '#F56C6C'
}

// 직급 태그 타입
const getPositionTagType = (position) => {
  const typeMap = {
    '부장': 'danger',
    '차장': 'warning',
    '과장': 'success',
    '대리': 'info',
    '사원': '',
    '미정': 'info'
  }
  return typeMap[position] || ''
}

// 데이터 로드
const loadDashboardData = async () => {
  loading.value = true
  try {
    // 모든 데이터 병렬 로드
    const [statsRes, deptRes, posRes, recentRes] = await Promise.all([
      dashboardApi.getStatistics(),
      dashboardApi.getDepartmentStats(),
      dashboardApi.getPositionStats(),
      dashboardApi.getRecentEmployees()
    ])

    statistics.value = statsRes.data
    departmentStats.value = deptRes.data
    positionStats.value = posRes.data
    recentEmployees.value = recentRes.data
  } catch (error) {
    console.error('대시보드 데이터 로드 실패:', error)
    ElMessage.error('데이터를 불러오는데 실패했습니다')
  } finally {
    loading.value = false
  }
}

// 사원 목록으로 이동
const goToEmployees = () => {
  router.push('/employees')
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.page-title {
  margin: 0 0 20px 0;
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

/* 통계 카드 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 각 통계 카드별 색상 */
.total-departments .stat-icon {
  color: #409EFF;
}

.total-employees .stat-icon {
  color: #67C23A;
}

.largest-dept .stat-icon {
  color: #E6A23C;
}

.avg-employees .stat-icon {
  color: #F56C6C;
}

/* 차트 영역 */
.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.chart-wrapper {
  padding: 10px 0;
}

/* 부서별 차트 */
.dept-bar-item {
  margin-bottom: 20px;
}

.dept-bar-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.dept-name {
  font-weight: 600;
  color: #303133;
}

.dept-count {
  color: #909399;
  font-size: 14px;
}

/* 직급별 차트 */
.position-item {
  margin-bottom: 20px;
}

.position-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.position-tag {
  min-width: 60px;
  text-align: center;
}

.position-count {
  font-weight: 600;
  color: #303133;
}

/* 최근 입사자 카드 */
.recent-card {
  margin-bottom: 20px;
}

/* 반응형 */
@media (max-width: 768px) {
  .stat-value {
    font-size: 24px;
  }

  .stat-icon {
    font-size: 36px;
  }
}
</style>
