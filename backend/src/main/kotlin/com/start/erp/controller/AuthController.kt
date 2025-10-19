package com.start.erp.controller

import com.start.erp.dto.LoginRequest
import com.start.erp.dto.LoginResponse
import com.start.erp.dto.SignupRequest
import com.start.erp.dto.SignupResponse
import com.start.erp.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return try {
            val response = authService.login(request)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<Any> {
        return try {
            val response = authService.signup(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    @GetMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") token: String): ResponseEntity<Map<String, Boolean>> {
        val isValid = authService.validateToken(token.replace("Bearer ", ""))
        return ResponseEntity.ok(mapOf("valid" to isValid))
    }
}