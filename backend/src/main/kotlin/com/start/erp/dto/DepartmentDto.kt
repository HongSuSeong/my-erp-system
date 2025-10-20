package com.start.erp.dto

// 부서 생성/수정 요청
data class DepartmentRequest(
    val name: String,
    val description: String?,
    val location: String?,
    val managerName: String?
)

// 부서 응답 (기본)
data class DepartmentResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val location: String?,
    val managerName: String?,
    val employeeCount: Int = 0
)

// 부서 상세 (사원 목록 포함)
data class DepartmentDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val location: String?,
    val managerName: String?,
    val employees: List<EmployeeSimpleDto>
)

// 간단한 사원 정보
data class EmployeeSimpleDto(
    val id: Long,
    val name: String,
    val email: String,
    val position: String?
)