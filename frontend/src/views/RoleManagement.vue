<template>
  <div class="role-container">
    <h2 class="page-title">권한 관리</h2>

    <!-- 로딩 중 -->
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>데이터를 불러오는 중...</span>
    </div>

    <div v-else>
      <!-- 역할별 권한 정보 -->
      <el-row :gutter="20" class="permissions-row">
        <el-col :xs="24" :sm="12" :lg="8" v-for="(permissions, role) in rolePermissions" :key="role">
          <el-card class="permission-card" :class="`role-${role.toLowerCase()}`">
            <template #header>
              <div class="card-header">
                <el-icon class="role-icon"><Medal /></el-icon>
                <span class="role-name">{{ getRoleDisplayName(role) }}</span>
              </div>
            </template>
            <div class="permissions-list">
              <div v-for="(permission, index) in permissions" :key="index" class="permission-item">
                <el-icon class="check-icon"><Check /></el-icon>
                <span>{{ permission }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 사용자 목록 및 역할 관리 -->
      <el-card class="users-card">
        <template #header>
          <div class="card-header">
            <span>사용자 역할 관리</span>
            <el-tag type="warning">
              <el-icon><Warning /></el-icon>
              ADMIN만 수정 가능
            </el-tag>
          </div>
        </template>

        <el-table :data="users" stripe style="width: 100%">
          <el-table-column type="index" label="No" width="60" />
          <el-table-column prop="username" label="아이디" width="150" />
          <el-table-column prop="name" label="이름" width="120" />
          <el-table-column prop="email" label="이메일" width="250" />
          <el-table-column prop="role" label="현재 역할" width="150">
            <template #default="scope">
              <el-tag :type="getRoleTagType(scope.row.role)" size="large">
                {{ getRoleDisplayName(scope.row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="가입일" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="lastLogin" label="최근 로그인" width="180">
            <template #default="scope">
              {{ scope.row.lastLogin ? formatDate(scope.row.lastLogin) : '없음' }}
            </template>
          </el-table-column>
          <el-table-column label="작업" width="150" fixed="right">
            <template #default="scope">
              <el-button
                size="small"
                type="primary"
                @click="openRoleDialog(scope.row)"
                :disabled="!isAdmin"
              >
                역할 변경
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 역할 변경 다이얼로그 -->
    <el-dialog
      v-model="dialogVisible"
      title="역할 변경"
      width="500px"
    >
      <div v-if="selectedUser">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="사용자">{{ selectedUser.name }} ({{ selectedUser.username }})</el-descriptions-item>
          <el-descriptions-item label="이메일">{{ selectedUser.email }}</el-descriptions-item>
          <el-descriptions-item label="현재 역할">
            <el-tag :type="getRoleTagType(selectedUser.role)">
              {{ getRoleDisplayName(selectedUser.role) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px;">
          <el-form label-width="100px">
            <el-form-item label="새 역할">
              <el-select v-model="newRole" placeholder="역할을 선택하세요" style="width: 100%">
                <el-option
                  v-for="role in roleList"
                  :key="role.name"
                  :label="`${role.displayName} - ${role.description}`"
                  :value="role.name"
                >
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ role.displayName }}</span>
                    <span style="color: #8492a6; font-size: 13px;">{{ role.description }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">취소</el-button>
        <el-button type="primary" @click="handleRoleChange" :loading="updating">
          변경
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  Loading,
  Medal,
  Check,
  Warning
} from '@element-plus/icons-vue'
import roleApi from '@/api/role'

const authStore = useAuthStore()

// 데이터
const loading = ref(false)
const updating = ref(false)
const users = ref([])
const roleList = ref([])
const rolePermissions = ref({})
const dialogVisible = ref(false)
const selectedUser = ref(null)
const newRole = ref('')

// 현재 사용자가 ADMIN인지 확인
const isAdmin = computed(() => authStore.isAdmin)

// 역할 표시명 가져오기
const getRoleDisplayName = (role) => {
  const roleMap = {
    'ADMIN': '관리자',
    'MANAGER': '매니저',
    'USER': '일반 사용자'
  }
  return roleMap[role] || role
}

// 역할 태그 타입
const getRoleTagType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'MANAGER': 'warning',
    'USER': 'info'
  }
  return typeMap[role] || ''
}

// 날짜 포맷
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 데이터 로드
const loadData = async () => {
  loading.value = true
  try {
    const [usersRes, rolesRes, permissionsRes] = await Promise.all([
      roleApi.getAllUsersWithRoles(),
      roleApi.getRoleList(),
      roleApi.getRolePermissions()
    ])

    users.value = usersRes.data
    roleList.value = rolesRes.data
    rolePermissions.value = permissionsRes.data
  } catch (error) {
    console.error('데이터 로드 실패:', error)
    ElMessage.error('데이터를 불러오는데 실패했습니다')
  } finally {
    loading.value = false
  }
}

// 역할 변경 다이얼로그 열기
const openRoleDialog = (user) => {
  selectedUser.value = user
  newRole.value = user.role
  dialogVisible.value = true
}

// 역할 변경 처리
const handleRoleChange = async () => {
  if (!selectedUser.value) return

  if (newRole.value === selectedUser.value.role) {
    ElMessage.warning('동일한 역할입니다')
    return
  }

  try {
    await ElMessageBox.confirm(
      `${selectedUser.value.name}의 역할을 ${getRoleDisplayName(newRole.value)}(으)로 변경하시겠습니까?`,
      '역할 변경 확인',
      {
        confirmButtonText: '변경',
        cancelButtonText: '취소',
        type: 'warning',
      }
    )

    updating.value = true

    await roleApi.updateUserRole(selectedUser.value.id, newRole.value)

    ElMessage.success('역할이 변경되었습니다')
    dialogVisible.value = false
    await loadData() // 목록 새로고침

  } catch (error) {
    if (error !== 'cancel') {
      console.error('역할 변경 실패:', error)
      ElMessage.error('역할 변경에 실패했습니다')
    }
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-container {
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

/* 권한 정보 카드 */
.permissions-row {
  margin-bottom: 20px;
}

.permission-card {
  height: 100%;
  transition: all 0.3s;
}

.permission-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.permission-card .card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
}

.role-icon {
  font-size: 24px;
}

/* 역할별 색상 */
.role-admin {
  border-left: 4px solid #F56C6C;
}

.role-admin .role-icon {
  color: #F56C6C;
}

.role-manager {
  border-left: 4px solid #E6A23C;
}

.role-manager .role-icon {
  color: #E6A23C;
}

.role-user {
  border-left: 4px solid #409EFF;
}

.role-user .role-icon {
  color: #409EFF;
}

.permissions-list {
  padding: 10px 0;
}

.permission-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: #606266;
}

.check-icon {
  color: #67C23A;
  font-size: 16px;
}

/* 사용자 목록 카드 */
.users-card {
  margin-top: 20px;
}

.users-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

/* 반응형 */
@media (max-width: 768px) {
  .permission-card {
    margin-bottom: 20px;
  }
}
</style>
