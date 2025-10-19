package com.start.erp.service

import com.start.erp.domain.Employee
import com.start.erp.repository.EmployeeRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EmployeeService(
    private val employeeRepository: EmployeeRepository
) {

    fun findAll(): List<Employee> {
        return employeeRepository.findAll()
    }

    fun findById(id: Long): Employee? {
        return employeeRepository.findById(id).orElse(null)
    }

    @Transactional
    fun save(employee: Employee): Employee {
        if (employeeRepository.existsByEmail(employee.email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다.")
        }

        return employeeRepository.save(employee)
    }

    @Transactional
    fun update(id: Long, employee: Employee): Employee {
        val existing = findById(id)
            ?: throw IllegalArgumentException("존재하지 않는 사원입니다.")

        val updated = existing.copy(
            name = employee.name,
            email = employee.email,
            department = employee.department,
            position = employee.position
        )

        return employeeRepository.save(updated)
    }

    @Transactional
    fun delete(id: Long) {
        employeeRepository.deleteById(id)
    }
}