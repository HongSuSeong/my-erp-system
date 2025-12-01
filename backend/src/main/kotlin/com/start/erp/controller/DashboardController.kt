package com.start.erp.controller

import com.start.erp.service.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = ["http://localhost:5173"])
class DashboardController(
    private val dashboardService: DashboardService
) {

    // 전체 통계
    @GetMapping("/statistics")
    fun getStatistics(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(dashboardService.getStatistics())
    }

    // 부서별 인원 현황
    @GetMapping("/department-stats")
    fun getDepartmentStats(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(dashboardService.getDepartmentStats())
    }

    // 직급별 분포
    @GetMapping("/position-stats")
    fun getPositionStats(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(dashboardService.getPositionStats())
    }

    // 최근 입사자 (최근 5명)
    @GetMapping("/recent-employees")
    fun getRecentEmployees(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(dashboardService.getRecentEmployees())
    }
}
