package com.start.erp.service

import com.start.erp.repository.DepartmentRepository
import com.start.erp.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DashboardService(
    private val departmentRepository: DepartmentRepository,
    private val employeeRepository: EmployeeRepository
) {
    fun getStatistics(): Map<String, Any> {
        val totalDepartments = departmentRepository.count()
        val totalEmployees = employeeRepository.count()
        val departments = departmentRepository.findAllWithEmployees()

        val largestDepartment = departments.maxByOrNull { it.employees.size }
        
        return mapOf(
            "totalDepartments" to totalDepartments,
            "totalEmployees" to totalEmployees,
            "largestDepartment" to (largestDepartment?.let {
                mapOf(
                    "name" to it.name,
                    "count" to it.employees.size
                )
            } ?: mapOf("name" to "없음", "count" to 0)),
            "averageEmployeesPerDepartment" to if (totalDepartments > 0) {
                totalEmployees.toDouble() / totalDepartments
            } else 0.0
        )
    }

    fun getDepartmentStats(): List<Map<String, Any>> {
        val departments = departmentRepository.findAllWithEmployees()
        return departments.map { dept ->
            mapOf(
                "id" to dept.id!!,
                "name" to dept.name,
                "count" to dept.employees.size,
                "managerName" to (dept.managerName ?: "미지정")
            )
        }
    }

    fun getPositionStats(): List<Map<String, Any>> {
        val employees = employeeRepository.findAll()

        val positionGroups = employees
            .groupBy { it.position ?: "미정" }
            .mapValues { it.value.size }
        
        return positionGroups.map { (position, count) ->
            mapOf(
                "position" to position,
                "count" to count
            )
        }.sortedByDescending { it["count"] as Int }
    }

    fun getRecentEmployees(): List<Map<String, Any>> {
        val employees = employeeRepository.findAll()
            .sortedByDescending { it.hireDate }
            .take(5)
        
        return employees.map { emp ->
            mapOf(
                "id" to emp.id!!,
                "name" to emp.name,
                "email" to emp.email,
                "departmentName" to (emp.department?.name ?: "미배정"),
                "position" to (emp.position ?: "미정"),
                "hireDate" to emp.hireDate.toString()
            )
        }
    }
}
