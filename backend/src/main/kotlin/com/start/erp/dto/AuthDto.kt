package com.start.erp.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val username: String,
    val name: String,
    val role: String
)

data class SignupRequest(
    val username: String,
    val password: String,
    val name: String,
    val email: String
)

data class SignupResponse(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val message: String = "회원가입이 완료되었습니다"
)