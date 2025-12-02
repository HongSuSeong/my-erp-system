package com.start.erp.service

import com.start.erp.domain.Role
import com.start.erp.dto.UserRoleResponse
import com.start.erp.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val userRepository: UserRepository
) {

    // 전체 사용자 및 역할 목록 조회
    fun getAllUsersWithRoles(): List<UserRoleResponse> {
        return userRepository.findAll().map { user ->
            UserRoleResponse(
                id = user.id!!,
                username = user.username,
                name = user.name,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt,
                lastLogin = user.lastLogin
            )
        }
    }

    // 사용자 역할 변경
    @Transactional
    fun updateUserRole(userId: Long, newRole: String): UserRoleResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다") }

        // 역할 유효성 검증
        try {
            Role.fromString(newRole)
        } catch (e: Exception) {
            throw IllegalArgumentException("유효하지 않은 역할입니다")
        }

        // 역할 변경
        val updatedUser = user.copy(role = newRole)
        val saved = userRepository.save(updatedUser)

        return UserRoleResponse(
            id = saved.id!!,
            username = saved.username,
            name = saved.name,
            email = saved.email,
            role = saved.role,
            createdAt = saved.createdAt,
            lastLogin = saved.lastLogin
        )
    }

    // 역할 목록 조회
    fun getRoleList(): List<Map<String, String>> {
        return Role.values().map { role ->
            mapOf(
                "name" to role.name,
                "displayName" to role.displayName,
                "description" to role.description
            )
        }
    }

    // 역할별 권한 정보 조회
    fun getRolePermissions(): Map<String, List<String>> {
        return mapOf(
            "ADMIN" to listOf(
                "사원 조회", "사원 등록", "사원 수정", "사원 삭제",
                "부서 조회", "부서 등록", "부서 수정", "부서 삭제",
                "역할 관리", "통계 조회"
            ),
            "MANAGER" to listOf(
                "사원 조회", "사원 등록", "사원 수정",
                "부서 조회", "부서 수정",
                "통계 조회"
            ),
            "USER" to listOf(
                "사원 조회", "부서 조회", "통계 조회"
            )
        )
    }
}
