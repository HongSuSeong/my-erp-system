package com.start.erp.controller

import com.start.erp.dto.DepartmentDetailResponse
import com.start.erp.dto.DepartmentRequest
import com.start.erp.dto.DepartmentResponse
import com.start.erp.service.DepartmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = ["http://localhost:5173"])
class DepartmentController(
    private val departmentService: DepartmentService
) {

    @GetMapping
    fun getAllDepartments(): ResponseEntity<List<DepartmentResponse>> {
        return ResponseEntity.ok(departmentService.findAll())
    }

    @GetMapping("/{id}")
    fun getDepartment(@PathVariable id: Long): ResponseEntity<DepartmentDetailResponse> {
        return try {
            ResponseEntity.ok(departmentService.findById(id))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createDepartment(@RequestBody request: DepartmentRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.create(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @PutMapping("/{id}")
    fun updateDepartment(
        @PathVariable id: Long,
        @RequestBody request: DepartmentRequest
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(departmentService.update(id, request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteDepartment(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            departmentService.delete(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @GetMapping("/statistics")
    fun getStatistics(): ResponseEntity<Map<String, Int>> {
        return ResponseEntity.ok(departmentService.getStatistics())
    }
}