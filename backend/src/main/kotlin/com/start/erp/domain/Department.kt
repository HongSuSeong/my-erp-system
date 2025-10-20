package com.start.erp.domain

import jakarta.persistence.*

@Entity
@Table(name = "departments")
data class Department(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dept_seq")
    @SequenceGenerator(name = "dept_seq", sequenceName = "DEPARTMENT_SEQ", allocationSize = 1)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,

    @Column(length = 200)
    val description: String? = null,

    @Column(length = 50)
    val location: String? = null,

    @Column(name = "manager_name", length = 50)
    val managerName: String? = null,

    // 양방향 관계 (부서 삭제 시 사원 처리를 위해)
    @OneToMany(mappedBy = "department", cascade = [CascadeType.ALL], orphanRemoval = false)
    val employees: MutableList<Employee> = mutableListOf()
) {
    // equals, hashCode에서 employees 제외 (순환 참조 방지)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Department) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Department(id=$id, name='$name', description=$description)"
    }
}