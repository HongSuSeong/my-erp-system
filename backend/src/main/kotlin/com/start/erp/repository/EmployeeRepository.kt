package com.start.erp.repository

import com.start.erp.domain.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository : JpaRepository<Employee, Long> {

    fun findByEmail(email: String): Employee?
    fun existsByEmail(email: String): Boolean
}