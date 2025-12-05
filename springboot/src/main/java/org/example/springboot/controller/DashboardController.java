package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.response.DashboardStatisticsDTO;
import org.example.springboot.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 仪表盘控制器
 */
@Tag(name = "仪表盘管理")
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/statistics")
    public Result<DashboardStatisticsDTO> getStatistics() {
        log.info("获取仪表盘统计数据");
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        return Result.success(statistics);
    }
}
