package com.start.erp.service

import com.start.erp.domain.Employee
import com.start.erp.dto.EmployeeRequest
import com.start.erp.dto.EmployeeResponse
import com.start.erp.repository.DepartmentRepository
import com.start.erp.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val departmentRepository: DepartmentRepository
) {

    // 전체 사원 조회
    fun findAll(): List<EmployeeResponse> {
        return employeeRepository.findAll().map { it.toDto() }
    }

    // 사원 상세 조회
    fun findById(id: Long): EmployeeResponse? {
        return employeeRepository.findById(id)
            .map { it.toDto() }
            .orElse(null)
    }

    // 사원 등록
    @Transactional
    fun create(request: EmployeeRequest): EmployeeResponse {
        // 이메일 중복 체크
        if (employeeRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다")
        }

        // 부서 조회
        val department = request.departmentId?.let {
            departmentRepository.findById(it)
                .orElseThrow { IllegalArgumentException("존재하지 않는 부서입니다") }
        }

        val employee = Employee(
            name = request.name,
            email = request.email,
            department = department,
            position = request.position,
            hireDate = LocalDate.now()
        )

        val saved = employeeRepository.save(employee)
        return saved.toDto()
    }

    // 사원 수정
    @Transactional
    fun update(id: Long, request: EmployeeRequest): EmployeeResponse {
        val employee = employeeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사원입니다") }

        // 이메일 중복 체크 (자신 제외)
        employeeRepository.findByEmail(request.email)?.let {
            if (it.id != id) {
                throw IllegalArgumentException("이미 존재하는 이메일입니다")
            }
        }

        // 부서 조회
        val department = request.departmentId?.let {
            departmentRepository.findById(it)
                .orElseThrow { IllegalArgumentException("존재하지 않는 부서입니다") }
        }

        val updated = employee.copy(
            name = request.name,
            email = request.email,
            department = department,
            position = request.position
        )

        val saved = employeeRepository.save(updated)
        return saved.toDto()
    }

    // 사원 삭제
    @Transactional
    fun delete(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw IllegalArgumentException("존재하지 않는 사원입니다")
        }
        employeeRepository.deleteById(id)
    }

    // Entity -> DTO 변환
    private fun Employee.toDto() = EmployeeResponse(
        id = this.id!!,
        name = this.name,
        email = this.email,
        departmentId = this.department?.id,
        departmentName = this.department?.name,
        position = this.position,
        hireDate = this.hireDate
    )
}