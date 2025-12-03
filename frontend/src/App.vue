<template>
  <div id="app">
    <el-container>
      <!-- 헤더 (로그인된 경우에만 표시) -->
      <el-header v-if="authStore.isAuthenticated" class="app-header">
        <div class="header-content">
          <div class="header-left">
            <h1>ERP 시스템</h1>
            <!-- 네비게이션 메뉴 -->
            <el-menu
              :default-active="currentRoute"
              mode="horizontal"
              :ellipsis="false"
              background-color="#409EFF"
              text-color="#fff"
              active-text-color="#ffd04b"
              @select="handleMenuSelect"
            >
              <el-menu-item index="/dashboard">
                <el-icon><DataAnalysis /></el-icon>
                <span>대시보드</span>
              </el-menu-item>
              <el-menu-item index="/employees">
                <el-icon><UserFilled /></el-icon>
                <span>사원 관리</span>
              </el-menu-item>
              <el-menu-item index="/departments">
                <el-icon><OfficeBuilding /></el-icon>
                <span>부서 관리</span>
              </el-menu-item>
              <el-menu-item index="/notices">
                <el-icon><Bell /></el-icon>
                <span>공지사항</span>
              </el-menu-item>
              <!-- ADMIN만 보이는 메뉴 -->
              <el-menu-item index="/roles" v-if="authStore.isAdmin">
                <el-icon><Lock /></el-icon>
                <span>권한 관리</span>
              </el-menu-item>
            </el-menu>
          </div>
          <div class="header-right">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ authStore.userName }} 
              <el-tag 
                :type="getRoleTagType(authStore.user?.role)" 
                size="small"
                style="margin-left: 8px;"
              >
                {{ getRoleDisplayName(authStore.user?.role) }}
              </el-tag>
            </span>
            <el-button type="info" @click="handleLogout" plain>
              <el-icon><SwitchButton /></el-icon>
              로그아웃
            </el-button>
          </div>
        </div>
      </el-header>

      <!-- 메인 컨텐츠 -->
      <el-main :class="{ 'no-header': !authStore.isAuthenticated }">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  User, 
  SwitchButton, 
  UserFilled, 
  OfficeBuilding,
  DataAnalysis,
  Lock,
  Bell
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 현재 라우트
const currentRoute = computed(() => route.path)

// 역할 표시명
const getRoleDisplayName = (role) => {
  const roleMap = {
    'ADMIN': '관리자',
    'MANAGER': '매니저',
    'USER': '일반사용자'
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

// 메뉴 선택 핸들러
const handleMenuSelect = (index) => {
  router.push(index)
}

// 로그아웃
const handleLogout = () => {
  ElMessageBox.confirm(
    '로그아웃 하시겠습니까?',
    '로그아웃',
    {
      confirmButtonText: '확인',
      cancelButtonText: '취소',
      type: 'warning',
    }
  )
    .then(() => {
      authStore.logout()
      router.push('/login')
    })
    .catch(() => {
      // 취소
    })
}
</script>

<style scoped>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-header {
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
  height: 60px !important;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  white-space: nowrap;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.el-main {
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
  padding: 20px;
}

.el-main.no-header {
  min-height: 100vh;
  padding: 0;
}

/* 메뉴 스타일 조정 */
:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu--horizontal .el-menu-item) {
  height: 60px;
  line-height: 60px;
  border-bottom: none;
}
</style>
