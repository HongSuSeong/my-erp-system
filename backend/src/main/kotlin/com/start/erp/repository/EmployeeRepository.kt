package com.start.erp.repository

import com.start.erp.domain.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun findByEmail(email: String): Employee?
    fun existsByEmail(email: String): Boolean

    fun findByDepartmentId(departmentId: Long): List<Employee>

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department")
    override fun findAll(): List<Employee>
}