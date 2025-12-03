package com.start.erp.controller

import com.start.erp.config.JwtUtil
import com.start.erp.dto.*
import com.start.erp.repository.UserRepository
import com.start.erp.service.NoticeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = ["http://localhost:5173"])
class NoticeController(
    private val noticeService: NoticeService,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) {

    // 전체 공지사항 조회
    @GetMapping
    fun getAllNotices(): ResponseEntity<List<NoticeListResponse>> {
        return ResponseEntity.ok(noticeService.findAll())
    }

    // 공지사항 상세 조회
    @GetMapping("/{id}")
    fun getNotice(@PathVariable id: Long): ResponseEntity<NoticeResponse> {
        return try {
            ResponseEntity.ok(noticeService.findById(id))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    // 카테고리별 조회
    @GetMapping("/category/{category}")
    fun getNoticesByCategory(@PathVariable category: String): ResponseEntity<List<NoticeListResponse>> {
        return ResponseEntity.ok(noticeService.findByCategory(category))
    }

    // 검색
    @GetMapping("/search")
    fun searchNotices(@RequestParam keyword: String): ResponseEntity<List<NoticeListResponse>> {
        return ResponseEntity.ok(noticeService.search(keyword))
    }

    // 공지사항 작성
    @PostMapping
    fun createNotice(
        @RequestBody request: NoticeCreateRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            val userId = getUserIdFromToken(token)
            val response = noticeService.create(request, userId)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    fun updateNotice(
        @PathVariable id: Long,
        @RequestBody request: NoticeUpdateRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            val userId = getUserIdFromToken(token)
            val response = noticeService.update(id, request, userId)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    fun deleteNotice(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            val userId = getUserIdFromToken(token)
            noticeService.delete(id, userId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        }
    }

    // 토큰에서 사용자 ID 추출
    private fun getUserIdFromToken(token: String): Long {
        val cleanToken = token.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(cleanToken)
        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다") }
        return user.id!!
    }
}
