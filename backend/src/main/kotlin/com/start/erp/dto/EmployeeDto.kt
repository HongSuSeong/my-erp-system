package com.start.erp.dto
import java.time.LocalDate

data class EmployeeRequest(
    val name: String,
    val email: String,
    val departmentId: Long?,  // 부서 ID로 변경
    val position: String?
)

data class EmployeeResponse(
    val id: Long,
    val name: String,
    val email: String,
    val departmentId: Long?,
    val departmentName: String?,  // 부서 이름 추가
    val position: String?,
    val hireDate: LocalDate
)