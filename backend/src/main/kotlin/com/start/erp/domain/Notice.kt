package com.start.erp.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notices")
data class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_seq")
    @SequenceGenerator(name = "notice_seq", sequenceName = "NOTICE_SEQ", allocationSize = 1)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    val title: String,

    @Column(nullable = false, columnDefinition = "CLOB")
    val content: String,

    @Column(nullable = false, length = 20)
    val category: String = "GENERAL", // GENERAL, URGENT, EVENT

    @Column(nullable = false, length = 50)
    val author: String,

    @Column(name = "author_id", nullable = false)
    val authorId: Long,

    @Column(name = "view_count", nullable = false)
    var viewCount: Int = 0,

    @Column(name = "is_pinned", nullable = false)
    val isPinned: Boolean = false,

    // 첨부파일 필드 추가
    @Column(name = "attachment_url", length = 500)
    val attachmentUrl: String? = null,

    @Column(name = "attachment_name", length = 200)
    val attachmentName: String? = null,

    @Column(name = "attachment_size")
    val attachmentSize: Long? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
