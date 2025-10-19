package com.start.erp.service

import com.start.erp.config.JwtUtil
import com.start.erp.domain.User
import com.start.erp.dto.LoginRequest
import com.start.erp.dto.LoginResponse
import com.start.erp.dto.SignupRequest
import com.start.erp.dto.SignupResponse
import com.start.erp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    // 로그인
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        // 사용자 조회
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다") }

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다")
        }

        // 마지막 로그인 시간 업데이트
        user.lastLogin = LocalDateTime.now()
        userRepository.save(user)

        // JWT 토큰 생성
        val token = jwtUtil.generateToken(user.username, user.role)

        return LoginResponse(
            token = token,
            username = user.username,
            name = user.name,
            role = user.role
        )
    }

    // 회원가입
    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        // 중복 확인
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("이미 존재하는 아이디입니다")
        }

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다")
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // 사용자 생성
        val user = User(
            username = request.username,
            password = encodedPassword,
            name = request.name,
            email = request.email,
            role = "USER"
        )

        val savedUser = userRepository.save(user)

        return SignupResponse(
            id = savedUser.id!!,
            username = savedUser.username,
            name = savedUser.name,
            email = savedUser.email
        )
    }

    // 토큰 검증
    fun validateToken(token: String): Boolean {
        return jwtUtil.validateToken(token)
    }
}