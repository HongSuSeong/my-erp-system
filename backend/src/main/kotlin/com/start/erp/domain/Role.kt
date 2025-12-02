package com.start.erp.domain

enum class Role(val displayName: String, val description: String) {
    ADMIN("관리자", "모든 권한"),
    MANAGER("매니저", "부서 관리 및 사원 관리 권한"),
    USER("일반 사용자", "읽기 전용 권한");

    companion object {
        fun fromString(role: String): Role {
            return values().find { it.name == role } ?: USER
        }
    }
}
