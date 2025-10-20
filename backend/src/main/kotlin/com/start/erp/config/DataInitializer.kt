package com.start.erp.config

import com.start.erp.domain.Department
import com.start.erp.domain.Employee
import com.start.erp.domain.User
import com.start.erp.repository.DepartmentRepository
import com.start.erp.repository.EmployeeRepository
import com.start.erp.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@Configuration
class DataInitializer {

    @Bean
    fun initData(
        userRepository: UserRepository,
        departmentRepository: DepartmentRepository,
        employeeRepository: EmployeeRepository,
        passwordEncoder: PasswordEncoder
    ) = ApplicationRunner {
        // 관리자 계정
        if (!userRepository.existsByUsername("admin")) {
            val admin = User(
                username = "admin",
                password = passwordEncoder.encode("admin123"),
                name = "관리자",
                email = "admin@example.com",
                role = "ADMIN"
            )
            userRepository.save(admin)
            println("✅ 관리자 계정 생성: admin / admin123")
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
            println("✅ 일반 사용자 계정 생성: user / user123")
        }

        // 부서 데이터
        if (departmentRepository.count() == 0L) {
            val departments = listOf(
                Department(name = "개발팀", description = "소프트웨어 개발", location = "본사 3층", managerName = "김개발"),
                Department(name = "영업팀", description = "영업 및 마케팅", location = "본사 2층", managerName = "이영업"),
                Department(name = "인사팀", description = "인사 관리", location = "본사 1층", managerName = "박인사"),
                Department(name = "기획팀", description = "사업 기획", location = "본사 2층", managerName = "최기획"),
                Department(name = "총무팀", description = "총무 및 재무", location = "본사 1층", managerName = "정총무")
            )
            departmentRepository.saveAll(departments)
            println("✅ 부서 ${departments.size}개 생성")

            // 샘플 사원 데이터
            val devDept = departments[0]
            val salesDept = departments[1]

            val employees = listOf(
                Employee(name = "홍길동", email = "hong@example.com", department = devDept, position = "대리", hireDate = LocalDate.of(2022, 3, 15)),
                Employee(name = "김철수", email = "kim@example.com", department = salesDept, position = "과장", hireDate = LocalDate.of(2021, 1, 10)),
                Employee(name = "이영희", email = "lee@example.com", department = devDept, position = "사원", hireDate = LocalDate.of(2023, 6, 1)),
                Employee(name = "박민수", email = "park@example.com", department = devDept, position = "대리", hireDate = LocalDate.of(2022, 9, 20)),
                Employee(name = "정수아", email = "jung@example.com", department = salesDept, position = "사원", hireDate = LocalDate.of(2023, 4, 5))
            )
            employeeRepository.saveAll(employees)
            println("✅ 사원 ${employees.size}명 생성")
        }
    }
}