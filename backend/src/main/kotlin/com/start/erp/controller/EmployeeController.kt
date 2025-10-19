package com.start.erp.controller

import com.start.erp.domain.Employee
import com.start.erp.service.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = ["http://localhost:5173"])
class EmployeeController(
    private val employeeService: EmployeeService
) {
    @GetMapping
    fun getAllEmployees(): ResponseEntity<List<Employee>> {
        return ResponseEntity.ok(employeeService.findAll())
    }

    @GetMapping("/{id}")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<Employee> {
        return employeeService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createEmployee(@RequestBody employee: Employee): ResponseEntity<Employee> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.save(employee))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }

    }

    @PutMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Long,
        @RequestBody employee: Employee
    ) : ResponseEntity<Employee> {
        return try {
            ResponseEntity.ok(employeeService.update(id, employee))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Void> {
        employeeService.delete(id)
        return ResponseEntity.noContent().build()
    }
}