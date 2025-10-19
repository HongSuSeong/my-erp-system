<template>
  <div id="app">
    <el-container>
      <!-- 헤더 (로그인된 경우에만 표시) -->
      <el-header v-if="authStore.isAuthenticated" class="app-header">
        <div class="header-content">
          <div class="header-left">
            <h1>ERP 시스템</h1>
          </div>
          <div class="header-right">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ authStore.userName }} ({{ authStore.username }})
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { User, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

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
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
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
</style>