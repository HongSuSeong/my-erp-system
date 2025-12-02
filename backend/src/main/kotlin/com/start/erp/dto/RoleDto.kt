package com.start.erp.dto

import java.time.LocalDateTime

// 사용자 역할 정보 응답
data class UserRoleResponse(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: LocalDateTime,
    val lastLogin: LocalDateTime?
)

// 사용자 역할 변경 요청
data class UserRoleUpdateRequest(
    val role: String
)
