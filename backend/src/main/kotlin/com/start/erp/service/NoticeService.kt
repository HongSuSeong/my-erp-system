package com.start.erp.service

import com.start.erp.domain.Notice
import com.start.erp.dto.*
import com.start.erp.repository.NoticeRepository
import com.start.erp.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val userRepository: UserRepository
) {

    // 전체 공지사항 조회 (고정 공지 우선)
    fun findAll(): List<NoticeListResponse> {
        return noticeRepository.findAllOrderByPinnedAndDate().map { it.toListResponse() }
    }

    // 공지사항 상세 조회 (조회수 증가)
    @Transactional
    fun findById(id: Long): NoticeResponse {
        val notice = noticeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 공지사항입니다") }

        // 조회수 증가
        notice.viewCount++
        noticeRepository.save(notice)

        return notice.toResponse()
    }

    // 카테고리별 조회
    fun findByCategory(category: String): List<NoticeListResponse> {
        return noticeRepository.findByCategory(category).map { it.toListResponse() }
    }

    // 검색
    fun search(keyword: String): List<NoticeListResponse> {
        return noticeRepository.searchByTitleOrContent(keyword).map { it.toListResponse() }
    }

    // 공지사항 생성
    @Transactional
    fun create(request: NoticeCreateRequest, userId: Long): NoticeResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다") }

        // 권한 체크 (ADMIN 또는 MANAGER만 작성 가능)
        if (user.role != "ADMIN" && user.role != "MANAGER") {
            throw IllegalArgumentException("공지사항 작성 권한이 없습니다")
        }

        val notice = Notice(
            title = request.title,
            content = request.content,
            category = request.category,
            author = user.name,
            authorId = user.id!!,
            isPinned = request.isPinned,
            attachmentUrl = request.attachmentUrl,
            attachmentName = request.attachmentName,
            attachmentSize = request.attachmentSize
        )

        val saved = noticeRepository.save(notice)
        return saved.toResponse()
    }

    // 공지사항 수정
    @Transactional
    fun update(id: Long, request: NoticeUpdateRequest, userId: Long): NoticeResponse {
        val notice = noticeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 공지사항입니다") }

        // 작성자 본인 또는 ADMIN만 수정 가능
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다") }

        if (notice.authorId != userId && user.role != "ADMIN") {
            throw IllegalArgumentException("수정 권한이 없습니다")
        }

        val updated = notice.copy(
            title = request.title,
            content = request.content,
            category = request.category,
            isPinned = request.isPinned,
            attachmentUrl = request.attachmentUrl,
            attachmentName = request.attachmentName,
            attachmentSize = request.attachmentSize,
            updatedAt = LocalDateTime.now()
        )

        val saved = noticeRepository.save(updated)
        return saved.toResponse()
    }

    // 공지사항 삭제
    @Transactional
    fun delete(id: Long, userId: Long) {
        val notice = noticeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 공지사항입니다") }

        // 작성자 본인 또는 ADMIN만 삭제 가능
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다") }

        if (notice.authorId != userId && user.role != "ADMIN") {
            throw IllegalArgumentException("삭제 권한이 없습니다")
        }

        noticeRepository.delete(notice)
    }

    // Entity -> ListResponse 변환
    private fun Notice.toListResponse() = NoticeListResponse(
        id = this.id!!,
        title = this.title,
        category = this.category,
        author = this.author,
        viewCount = this.viewCount,
        isPinned = this.isPinned,
        hasAttachment = this.attachmentUrl != null,
        createdAt = this.createdAt
    )

    // Entity -> Response 변환
    private fun Notice.toResponse() = NoticeResponse(
        id = this.id!!,
        title = this.title,
        content = this.content,
        category = this.category,
        author = this.author,
        authorId = this.authorId,
        viewCount = this.viewCount,
        isPinned = this.isPinned,
        attachmentUrl = this.attachmentUrl,
        attachmentName = this.attachmentName,
        attachmentSize = this.attachmentSize,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
