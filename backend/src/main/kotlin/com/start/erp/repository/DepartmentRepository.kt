package com.start.erp.repository

import com.start.erp.domain.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DepartmentRepository : JpaRepository<Department, Long> {
    fun findByName(name: String): Department?
    fun existsByName(name: String): Boolean

    // 사원이 있는 부서 조회
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
    fun findAllWithEmployees(): List<Department>
}