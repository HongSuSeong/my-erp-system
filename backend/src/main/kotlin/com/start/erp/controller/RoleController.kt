package com.start.erp.controller

import com.start.erp.dto.UserRoleUpdateRequest
import com.start.erp.dto.UserRoleResponse
import com.start.erp.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = ["http://localhost:5173"])
class RoleController(
    private val roleService: RoleService
) {

    // 전체 사용자 및 역할 목록 조회
    @GetMapping("/users")
    fun getAllUsersWithRoles(): ResponseEntity<List<UserRoleResponse>> {
        return ResponseEntity.ok(roleService.getAllUsersWithRoles())
    }

    // 사용자 역할 변경
    @PutMapping("/users/{userId}")
    fun updateUserRole(
        @PathVariable userId: Long,
        @RequestBody request: UserRoleUpdateRequest
    ): ResponseEntity<UserRoleResponse> {
        return ResponseEntity.ok(roleService.updateUserRole(userId, request.role))
    }

    // 역할 목록 조회
    @GetMapping("/list")
    fun getRoleList(): ResponseEntity<List<Map<String, String>>> {
        return ResponseEntity.ok(roleService.getRoleList())
    }

    // 역할별 권한 정보 조회
    @GetMapping("/permissions")
    fun getRolePermissions(): ResponseEntity<Map<String, List<String>>> {
        return ResponseEntity.ok(roleService.getRolePermissions())
    }
}
