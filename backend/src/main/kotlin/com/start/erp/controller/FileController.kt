package com.start.erp.controller

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = ["http://localhost:5173"])
class FileController {

    // 파일 저장 경로 (실제 환경에서는 설정 파일로 관리)
    private val uploadDir: Path = Paths.get("uploads").toAbsolutePath().normalize()

    init {
        try {
            Files.createDirectories(uploadDir)
        } catch (ex: Exception) {
            throw RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다.", ex)
        }
    }

    // 파일 업로드
    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        return try {
            // 파일명 중복 방지를 위해 UUID 추가
            val originalFilename = file.originalFilename ?: "unnamed"
            val fileExtension = originalFilename.substringAfterLast('.', "")
            val uniqueFilename = "${UUID.randomUUID()}_$originalFilename"

            // 파일 저장
            val targetLocation = uploadDir.resolve(uniqueFilename)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            // 파일 정보 반환
            val fileInfo = mapOf(
                "filename" to uniqueFilename,
                "originalFilename" to originalFilename,
                "size" to file.size,
                "url" to "/api/files/download/$uniqueFilename"
            )

            ResponseEntity.ok(fileInfo)
        } catch (ex: IOException) {
            ResponseEntity.badRequest()
                .body(mapOf("message" to "파일 업로드에 실패했습니다: ${ex.message}"))
        }
    }

    // 파일 다운로드
    @GetMapping("/download/{filename:.+}")
    fun downloadFile(@PathVariable filename: String): ResponseEntity<Resource> {
        return try {
            val filePath = uploadDir.resolve(filename).normalize()
            val resource = UrlResource(filePath.toUri())

            if (resource.exists() && resource.isReadable) {
                // 원본 파일명 추출 (UUID 제거)
                val originalFilename = filename.substringAfter('_')

                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"${originalFilename}\"")
                    .body(resource)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (ex: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    // 파일 삭제
    @DeleteMapping("/delete/{filename:.+}")
    fun deleteFile(@PathVariable filename: String): ResponseEntity<Map<String, String>> {
        return try {
            val filePath = uploadDir.resolve(filename).normalize()
            Files.deleteIfExists(filePath)
            ResponseEntity.ok(mapOf("message" to "파일이 삭제되었습니다"))
        } catch (ex: Exception) {
            ResponseEntity.badRequest()
                .body(mapOf("message" to "파일 삭제에 실패했습니다: ${ex.message}"))
        }
    }
}
