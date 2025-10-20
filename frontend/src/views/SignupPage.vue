<template>
    <div class="signup-container">
      <el-card class="signup-card">
        <template #header>
          <div class="card-header">
            <h2>회원가입</h2>
          </div>
        </template>
  
        <el-form
          ref="formRef"
          :model="signupForm"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleSignup"
        >
          <el-form-item label="아이디" prop="username">
            <el-input
              v-model="signupForm.username"
              placeholder="아이디를 입력하세요 (3자 이상)"
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
              v-model="signupForm.password"
              type="password"
              placeholder="비밀번호를 입력하세요 (6자 이상)"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item label="비밀번호 확인" prop="passwordConfirm">
            <el-input
              v-model="signupForm.passwordConfirm"
              type="password"
              placeholder="비밀번호를 다시 입력하세요"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item label="이름" prop="name">
            <el-input
              v-model="signupForm.name"
              placeholder="이름을 입력하세요"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><UserFilled /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item label="이메일" prop="email">
            <el-input
              v-model="signupForm.email"
              type="email"
              placeholder="example@company.com"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              style="width: 100%"
              @click="handleSignup"
            >
              회원가입
            </el-button>
          </el-form-item>
  
          <div class="footer-links">
            <span>이미 계정이 있으신가요?</span>
            <el-link type="primary" @click="goToLogin" style="margin-left: 8px;">
              로그인
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAuthStore } from '@/stores/auth'
  import { User, Lock, UserFilled, Message } from '@element-plus/icons-vue'
  
  const router = useRouter()
  const authStore = useAuthStore()
  
  const formRef = ref(null)
  const loading = ref(false)
  
  const signupForm = reactive({
    username: '',
    password: '',
    passwordConfirm: '',
    name: '',
    email: ''
  })
  
  // 비밀번호 확인 검증
  const validatePasswordConfirm = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('비밀번호를 다시 입력하세요'))
    } else if (value !== signupForm.password) {
      callback(new Error('비밀번호가 일치하지 않습니다'))
    } else {
      callback()
    }
  }
  
  const rules = {
    username: [
      { required: true, message: '아이디를 입력하세요', trigger: 'blur' },
      { min: 3, max: 20, message: '아이디는 3-20자 사이여야 합니다', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '비밀번호를 입력하세요', trigger: 'blur' },
      { min: 6, message: '비밀번호는 최소 6자 이상이어야 합니다', trigger: 'blur' }
    ],
    passwordConfirm: [
      { required: true, validator: validatePasswordConfirm, trigger: 'blur' }
    ],
    name: [
      { required: true, message: '이름을 입력하세요', trigger: 'blur' },
      { min: 2, max: 20, message: '이름은 2-20자 사이여야 합니다', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '이메일을 입력하세요', trigger: 'blur' },
      { type: 'email', message: '올바른 이메일 형식이 아닙니다', trigger: 'blur' }
    ]
  }
  
  const handleSignup = async () => {
    if (!formRef.value) return
  
    await formRef.value.validate(async (valid) => {
      if (!valid) return
  
      loading.value = true
      try {
        const { passwordConfirm, ...userData } = signupForm
        const success = await authStore.signup(userData)
        if (success) {
          setTimeout(() => {
            router.push('/login')
          }, 1500)
        }
      } finally {
        loading.value = false
      }
    })
  }
  
  const goToLogin = () => {
    router.push('/login')
  }
  </script>
  
  <style scoped>
  .signup-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .signup-card {
    width: 100%;
    max-width: 480px;
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
    color: #606266;
  }
  
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #606266;
  }
  </style>