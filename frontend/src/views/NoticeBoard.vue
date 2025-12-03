<template>
  <div class="notice-container">
    <div class="header">
      <h2>공지사항</h2>
      <div class="header-actions">
        <!-- 검색 -->
        <el-input
          v-model="searchKeyword"
          placeholder="제목 또는 내용 검색"
          style="width: 300px; margin-right: 10px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>

        <!-- 카테고리 필터 -->
        <el-select
          v-model="selectedCategory"
          placeholder="카테고리"
          style="width: 150px; margin-right: 10px;"
          @change="handleCategoryFilter"
        >
          <el-option label="전체" value="" />
          <el-option label="일반" value="GENERAL" />
          <el-option label="긴급" value="URGENT" />
          <el-option label="이벤트" value="EVENT" />
        </el-select>

        <!-- 작성 버튼 (ADMIN, MANAGER만) -->
        <el-button
          v-if="canWrite"
          type="primary"
          @click="showDialog = true"
        >
          <el-icon><Plus /></el-icon>
          공지 작성
        </el-button>
      </div>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>데이터를 불러오는 중...</span>
    </div>

    <!-- 공지사항 목록 -->
    <div v-else>
      <el-table
        :data="notices"
        stripe
        style="width: 100%"
        @row-click="handleRowClick"
        :row-class-name="getRowClassName"
      >
        <el-table-column width="80">
          <template #default="scope">
            <el-icon v-if="scope.row.isPinned" class="pin-icon" color="#F56C6C">
              <StarFilled />
            </el-icon>
            <span v-else>{{ scope.row.id }}</span>
          </template>
        </el-table-column>

        <el-table-column label="카테고리" width="120">
          <template #default="scope">
            <el-tag :type="getCategoryType(scope.row.category)" size="small">
              {{ getCategoryName(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="제목" min-width="300">
          <template #default="scope">
            <span class="notice-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="author" label="작성자" width="120" />

        <el-table-column prop="viewCount" label="조회수" width="100" align="center" />

        <el-table-column prop="createdAt" label="작성일" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="notices.length === 0" description="공지사항이 없습니다" />
    </div>

    <!-- 공지사항 작성/수정 다이얼로그 -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '공지사항 수정' : '공지사항 작성'"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="카테고리" prop="category">
          <el-radio-group v-model="form.category">
            <el-radio label="GENERAL">일반</el-radio>
            <el-radio label="URGENT">긴급</el-radio>
            <el-radio label="EVENT">이벤트</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="제목" prop="title">
          <el-input
            v-model="form.title"
            placeholder="제목을 입력하세요"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="내용" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="내용을 입력하세요"
          />
        </el-form-item>

        <el-form-item label="상단 고정" v-if="isAdmin">
          <el-switch v-model="form.isPinned" />
          <span style="margin-left: 10px; color: #909399; font-size: 13px;">
            상단에 고정하여 표시합니다
          </span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showDialog = false">취소</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '수정' : '작성' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 공지사항 상세 다이얼로그 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="detailNotice?.title"
      width="800px"
    >
      <div v-if="detailNotice" class="notice-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag :type="getCategoryType(detailNotice.category)" size="small">
              {{ getCategoryName(detailNotice.category) }}
            </el-tag>
            <span class="author">작성자: {{ detailNotice.author }}</span>
            <span class="date">{{ formatDate(detailNotice.createdAt) }}</span>
            <span class="views">조회수: {{ detailNotice.viewCount }}</span>
          </div>
          <div class="detail-actions" v-if="canEdit(detailNotice)">
            <el-button size="small" @click="handleEdit(detailNotice)">수정</el-button>
            <el-button size="small" type="danger" @click="handleDelete(detailNotice)">삭제</el-button>
          </div>
        </div>
        <el-divider />
        <div class="detail-content" v-html="formatContent(detailNotice.content)"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  Loading,
  Plus,
  Search,
  StarFilled
} from '@element-plus/icons-vue'
import noticeApi from '@/api/notice'

const authStore = useAuthStore()

// 데이터
const loading = ref(false)
const submitting = ref(false)
const notices = ref([])
const showDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const detailNotice = ref(null)
const formRef = ref(null)

// 검색 & 필터
const searchKeyword = ref('')
const selectedCategory = ref('')

// 폼 데이터
const form = ref({
  title: '',
  content: '',
  category: 'GENERAL',
  isPinned: false
})

// 유효성 검사
const rules = {
  title: [
    { required: true, message: '제목을 입력하세요', trigger: 'blur' },
    { min: 2, max: 200, message: '제목은 2-200자 사이여야 합니다', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '내용을 입력하세요', trigger: 'blur' },
    { min: 10, message: '내용은 최소 10자 이상이어야 합니다', trigger: 'blur' }
  ]
}

// 권한 체크
const isAdmin = computed(() => authStore.isAdmin)
const canWrite = computed(() => {
  const role = authStore.user?.role
  return role === 'ADMIN' || role === 'MANAGER'
})

// ⚠️ 버그: authorId 타입 비교 문제
const canEdit = (notice) => {
  if (!authStore.user) return false
  return notice.authorId === authStore.user.id || authStore.isAdmin
}

// 카테고리 관련
const getCategoryName = (category) => {
  const categoryMap = {
    'GENERAL': '일반',
    'URGENT': '긴급',
    'EVENT': '이벤트'
  }
  return categoryMap[category] || category
}

const getCategoryType = (category) => {
  const typeMap = {
    'GENERAL': 'info',
    'URGENT': 'danger',
    'EVENT': 'success'
  }
  return typeMap[category] || ''
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

// 내용 포맷 (줄바꿈 처리)
const formatContent = (content) => {
  return content.replace(/\n/g, '<br>')
}

// 행 클래스 (고정 공지 강조)
const getRowClassName = ({ row }) => {
  return row.isPinned ? 'pinned-row' : ''
}

// 공지사항 목록 조회
const loadNotices = async () => {
  loading.value = true
  try {
    const response = await noticeApi.getAll()
    notices.value = response.data
  } catch (error) {
    console.error('공지사항 조회 실패:', error)
    ElMessage.error('공지사항을 불러오는데 실패했습니다')
  } finally {
    loading.value = false
  }
}

// 검색
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadNotices()
    return
  }

  loading.value = true
  try {
    const response = await noticeApi.search(searchKeyword.value)
    notices.value = response.data
  } catch (error) {
    console.error('검색 실패:', error)
    ElMessage.error('검색에 실패했습니다')
  } finally {
    loading.value = false
  }
}

// 카테고리 필터
const handleCategoryFilter = async () => {
  if (!selectedCategory.value) {
    loadNotices()
    return
  }

  loading.value = true
  try {
    const response = await noticeApi.getByCategory(selectedCategory.value)
    notices.value = response.data
  } catch (error) {
    console.error('카테고리 필터 실패:', error)
    ElMessage.error('필터링에 실패했습니다')
  } finally {
    loading.value = false
  }
}

// ⚠️ 버그: 조회수가 목록에 실시간 반영 안됨
// 행 클릭 (상세보기)
const handleRowClick = async (row) => {
  try {
    const response = await noticeApi.getById(row.id)
    detailNotice.value = response.data
    showDetailDialog.value = true
  } catch (error) {
    console.error('상세 조회 실패:', error)
    ElMessage.error('공지사항을 불러오는데 실패했습니다')
  }
}

// 수정 버튼
const handleEdit = (notice) => {
  showDetailDialog.value = false
  isEdit.value = true
  form.value = {
    id: notice.id,
    title: notice.title,
    content: notice.content,
    category: notice.category,
    isPinned: notice.isPinned
  }
  showDialog.value = true
}

// 작성/수정 제출
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value) {
        await noticeApi.update(form.value.id, form.value)
        ElMessage.success('공지사항이 수정되었습니다')
      } else {
        await noticeApi.create(form.value)
        ElMessage.success('공지사항이 작성되었습니다')
      }

      showDialog.value = false
      await loadNotices()
    } catch (error) {
      console.error('저장 실패:', error)
      const message = error.response?.data?.message || '저장에 실패했습니다'
      ElMessage.error(message)
    } finally {
      submitting.value = false
    }
  })
}

// 삭제
const handleDelete = async (notice) => {
  try {
    await ElMessageBox.confirm(
      `"${notice.title}" 공지사항을 삭제하시겠습니까?`,
      '삭제 확인',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
      }
    )

    await noticeApi.delete(notice.id)
    ElMessage.success('공지사항이 삭제되었습니다')
    showDetailDialog.value = false
    await loadNotices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('삭제 실패:', error)
      const message = error.response?.data?.message || '삭제에 실패했습니다'
      ElMessage.error(message)
    }
  }
}

// 폼 초기화
const resetForm = () => {
  isEdit.value = false
  form.value = {
    title: '',
    content: '',
    category: 'GENERAL',
    isPinned: false
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-container {
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

.header-actions {
  display: flex;
  align-items: center;
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

.pin-icon {
  font-size: 20px;
}

.notice-title {
  cursor: pointer;
  font-weight: 500;
}

.notice-title:hover {
  color: #409EFF;
}

:deep(.pinned-row) {
  background-color: #FFF3E0 !important;
  font-weight: 600;
}

:deep(.pinned-row:hover td) {
  background-color: #FFE0B2 !important;
}

/* 상세보기 스타일 */
.notice-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.detail-content {
  padding: 20px 0;
  line-height: 1.8;
  font-size: 15px;
  color: #303133;
  min-height: 200px;
  white-space: pre-wrap;
}

/* 반응형 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
    gap: 10px;
  }

  .header-actions .el-input,
  .header-actions .el-select,
  .header-actions .el-button {
    width: 100% !important;
  }
}
</style>