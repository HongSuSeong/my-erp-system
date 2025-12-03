package com.start.erp.repository

import com.start.erp.domain.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoticeRepository : JpaRepository<Notice, Long> {
    // 카테고리별 조회
    fun findByCategory(category: String): List<Notice>

    // 제목으로 검색
    fun findByTitleContainingIgnoreCase(keyword: String): List<Notice>

    // 제목 또는 내용으로 검색
    @Query("SELECT n FROM Notice n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    fun searchByTitleOrContent(keyword: String): List<Notice>

    // 고정 공지 조회
    fun findByIsPinnedTrueOrderByCreatedAtDesc(): List<Notice>

    // 일반 공지 조회 (최신순)
    fun findByIsPinnedFalseOrderByCreatedAtDesc(): List<Notice>

    // 전체 조회 (고정 공지 먼저, 그 다음 최신순)
    @Query("SELECT n FROM Notice n ORDER BY n.isPinned DESC, n.createdAt DESC")
    fun findAllOrderByPinnedAndDate(): List<Notice>
}
