package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.request.ExportReportDTO;
import org.example.springboot.dto.response.DashboardStatisticsDTO;
import org.example.springboot.service.DashboardService;
import org.example.springboot.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 仪表盘控制器 - 数据统计增强版
 */
@Tag(name = "仪表盘管理")
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @Resource
    private ReportExportService reportExportService;

    @Operation(summary = "获取仪表盘统计数据（增强版）")
    @GetMapping("/statistics")
    public Result<DashboardStatisticsDTO> getStatistics() {
        log.info("获取仪表盘统计数据（增强版）");
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "导出统计报表")
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestBody ExportReportDTO exportDTO) {
        log.info("导出统计报表: type={}, format={}", exportDTO.getReportType(), exportDTO.getExportFormat());
        
        byte[] data = reportExportService.exportReport(exportDTO);
        
        String fileName = generateFileName(exportDTO.getReportType());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String reportType) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String typeStr = switch (reportType) {
            case "USER_GROWTH" -> "用户增长报表";
            case "BOOKING_TREND" -> "预约趋势报表";
            case "WORKOUT_STATS" -> "运动统计报表";
            case "REVENUE" -> "收入报表";
            default -> "统计报表";
        };
        return typeStr + "_" + date + ".xlsx";
    }
}
