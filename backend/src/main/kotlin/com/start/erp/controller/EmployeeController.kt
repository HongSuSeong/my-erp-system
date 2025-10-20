package com.start.erp.controller

import com.start.erp.dto.EmployeeRequest
import com.start.erp.dto.EmployeeResponse
import com.start.erp.service.EmployeeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = ["http://localhost:5173"])
class EmployeeController(
    private val employeeService: EmployeeService
) {

    @GetMapping
    fun getAllEmployees(): ResponseEntity<List<EmployeeResponse>> {
        return ResponseEntity.ok(employeeService.findAll())
    }

    @GetMapping("/{id}")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<EmployeeResponse> {
        return employeeService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createEmployee(@RequestBody request: EmployeeRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @PutMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Long,
        @RequestBody request: EmployeeRequest
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(employeeService.update(id, request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            employeeService.delete(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}