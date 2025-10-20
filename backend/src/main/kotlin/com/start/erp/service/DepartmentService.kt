package com.start.erp.service

import com.start.erp.domain.Department
import com.start.erp.dto.*
import com.start.erp.repository.DepartmentRepository
import com.start.erp.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) {

    // 전체 부서 조회
    fun findAll(): List<DepartmentResponse> {
        return departmentRepository.findAll().map { department ->
            DepartmentResponse(
                id = department.id!!,
                name = department.name,
                description = department.description,
                location = department.location,
                managerName = department.managerName,
                employeeCount = department.employees.size
            )
        }
    }

    // 부서 상세 조회 (사원 목록 포함)
    fun findById(id: Long): DepartmentDetailResponse {
        val department = departmentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 부서입니다") }

        val employees = employeeRepository.findByDepartmentId(id).map { employee ->
            EmployeeSimpleDto(
                id = employee.id!!,
                name = employee.name,
                email = employee.email,
                position = employee.position
            )
        }

        return DepartmentDetailResponse(
            id = department.id!!,
            name = department.name,
            description = department.description,
            location = department.location,
            managerName = department.managerName,
            employees = employees
        )
    }

    // 부서 등록
    @Transactional
    fun create(request: DepartmentRequest): DepartmentResponse {
        // 중복 체크
        if (departmentRepository.existsByName(request.name)) {
            throw IllegalArgumentException("이미 존재하는 부서명입니다")
        }

        val department = Department(
            name = request.name,
            description = request.description,
            location = request.location,
            managerName = request.managerName
        )

        val saved = departmentRepository.save(department)

        return DepartmentResponse(
            id = saved.id!!,
            name = saved.name,
            description = saved.description,
            location = saved.location,
            managerName = saved.managerName,
            employeeCount = 0
        )
    }

    // 부서 수정
    @Transactional
    fun update(id: Long, request: DepartmentRequest): DepartmentResponse {
        val department = departmentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 부서입니다") }

        // 다른 부서에서 같은 이름 사용 중인지 체크
        departmentRepository.findByName(request.name)?.let {
            if (it.id != id) {
                throw IllegalArgumentException("이미 존재하는 부서명입니다")
            }
        }

        val updated = department.copy(
            name = request.name,
            description = request.description,
            location = request.location,
            managerName = request.managerName
        )

        val saved = departmentRepository.save(updated)

        return DepartmentResponse(
            id = saved.id!!,
            name = saved.name,
            description = saved.description,
            location = saved.location,
            managerName = saved.managerName,
            employeeCount = saved.employees.size
        )
    }

    // 부서 삭제
    @Transactional
    fun delete(id: Long) {
        val department = departmentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 부서입니다") }

        // 소속 사원이 있는지 확인
        val employeeCount = employeeRepository.findByDepartmentId(id).size
        if (employeeCount > 0) {
            throw IllegalArgumentException("소속 사원이 있는 부서는 삭제할 수 없습니다 (사원 ${employeeCount}명)")
        }

        departmentRepository.delete(department)
    }

    // 부서별 사원 수 통계
    fun getStatistics(): Map<String, Int> {
        val departments = departmentRepository.findAllWithEmployees()
        return departments.associate { it.name to it.employees.size }
    }
}