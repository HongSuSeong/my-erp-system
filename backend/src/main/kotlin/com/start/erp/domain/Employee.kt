package com.start.erp.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name= "employees")
data class Employee (

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    val department: Department? = null,

    @Column(length = 50)
    val position: String? = null,

    @Column (name = "hire_date")
    val hireDate: LocalDate = LocalDate.now()
)