package com.start.erp.dto

import java.time.LocalDateTime

// 공지사항 생성 요청
data class NoticeCreateRequest(
    val title: String,
    val content: String,
    val category: String = "GENERAL",
    val isPinned: Boolean = false,
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val attachmentSize: Long? = null
)

// 공지사항 수정 요청
data class NoticeUpdateRequest(
    val title: String,
    val content: String,
    val category: String,
    val isPinned: Boolean,
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val attachmentSize: Long? = null
)

// 공지사항 응답
data class NoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val category: String,
    val author: String,
    val authorId: Long,
    val viewCount: Int,
    val isPinned: Boolean,
    val attachmentUrl: String?,
    val attachmentName: String?,
    val attachmentSize: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

// 공지사항 목록 응답 (간략)
data class NoticeListResponse(
    val id: Long,
    val title: String,
    val category: String,
    val author: String,
    val viewCount: Int,
    val isPinned: Boolean,
    val hasAttachment: Boolean,
    val createdAt: LocalDateTime
)
