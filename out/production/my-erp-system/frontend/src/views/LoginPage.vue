<template>
    <div class="login-container">
      <el-card class="login-card">
        <template #header>
          <div class="card-header">
            <h2>ERP 시스템 로그인</h2>
          </div>
        </template>
  
        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="아이디" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="아이디를 입력하세요"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item label="비밀번호" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="비밀번호를 입력하세요"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              style="width: 100%"
              @click="handleLogin"
            >
              로그인
            </el-button>
          </el-form-item>
  
          <div class="footer-links">
            <el-link type="primary" @click="goToSignup">
              회원가입
            </el-link>
          </div>
        </el-form>
  
        <!-- 테스트 계정 안내 -->
        <el-alert
          title="테스트 계정"
          type="info"
          :closable="false"
          style="margin-top: 20px"
        >
          <template #default>
            <div style="font-size: 13px;">
              <p style="margin: 5px 0;">관리자: admin / admin123</p>
              <p style="margin: 5px 0;">일반사용자: user / user123</p>
            </div>
          </template>
        </el-alert>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAuthStore } from '@/stores/auth'
  import { User, Lock } from '@element-plus/icons-vue'
  
  const router = useRouter()
  const authStore = useAuthStore()
  
  const formRef = ref(null)
  const loading = ref(false)
  
  const loginForm = reactive({
    username: '',
    password: ''
  })
  
  const rules = {
    username: [
      { required: true, message: '아이디를 입력하세요', trigger: 'blur' },
      { min: 3, message: '아이디는 최소 3자 이상이어야 합니다', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '비밀번호를 입력하세요', trigger: 'blur' },
      { min: 6, message: '비밀번호는 최소 6자 이상이어야 합니다', trigger: 'blur' }
    ]
  }
  
  const handleLogin = async () => {
    if (!formRef.value) return
  
    await formRef.value.validate(async (valid) => {
      if (!valid) return
  
      loading.value = true
      try {
        const success = await authStore.login(loginForm)
        if (success) {
          router.push('/employees')
        }
      } finally {
        loading.value = false
      }
    })
  }
  
  const goToSignup = () => {
    router.push('/signup')
  }
  </script>
  
  <style scoped>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .login-card {
    width: 100%;
    max-width: 420px;
    margin: 20px;
  }
  
  .card-header {
    text-align: center;
  }
  
  .card-header h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
  }
  
  .footer-links {
    text-align: center;
    margin-top: 10px;
  }
  
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #606266;
  }
  </style>