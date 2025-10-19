package com.start.erp.config

import com.start.erp.domain.User
import com.start.erp.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataInitializer {

    @Bean
    fun initData(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder
    ) = ApplicationRunner {
        // 관리자 계정이 없으면 생성
        if (!userRepository.existsByUsername("admin")) {
            val admin = User(
                username = "admin",
                password = passwordEncoder.encode("admin123"),
                name = "관리자",
                email = "admin@example.com",
                role = "ADMIN"
            )
            userRepository.save(admin)
            println("✅ 관리자 계정 생성 완료: admin / admin123")
        }

        // 일반 사용자 계정
        if (!userRepository.existsByUsername("user")) {
            val user = User(
                username = "user",
                password = passwordEncoder.encode("user123"),
                name = "일반사용자",
                email = "user@example.com",
                role = "USER"
            )
            userRepository.save(user)
            println("✅ 일반 사용자 계정 생성 완료: user / user123")
        }
    }
}