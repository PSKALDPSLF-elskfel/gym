package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.WorkoutRecordCreateDTO;
import org.example.springboot.dto.command.WorkoutRecordUpdateDTO;
import org.example.springboot.dto.response.*;
import org.example.springboot.service.WorkoutRecordService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录控制器
 * @author system
 */
@Tag(name = "运动记录管理")
@RestController
@RequestMapping("/workout")
@Slf4j
public class WorkoutRecordController {

    @Resource
    private WorkoutRecordService workoutRecordService;

    /**
     * 创建运动记录
     */
    @Operation(summary = "创建运动记录")
    @PostMapping("/record")
    public Result<Long> createRecord(@RequestHeader("Authorization") String token,
                                      @Valid @RequestBody WorkoutRecordCreateDTO createDTO) {
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 创建运动记录", userId);
        Long recordId = workoutRecordService.createWorkoutRecord(userId, createDTO);
        return Result.success(recordId);
    }

    /**
     * 更新运动记录
     */
    @Operation(summary = "更新运动记录")
    @PutMapping("/record/{id}")
    public Result<Void> updateRecord(@RequestHeader("Authorization") String token,
                                      @Parameter(description = "记录ID") @PathVariable Long id,
                                      @Valid @RequestBody WorkoutRecordUpdateDTO updateDTO) {
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 更新运动记录，记录ID: {}", userId, id);
        workoutRecordService.updateWorkoutRecord(userId, id, updateDTO);
        return Result.success();
    }

    /**
     * 删除运动记录
     */
    @Operation(summary = "删除运动记录")
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@RequestHeader("Authorization") String token,
                                      @Parameter(description = "记录ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 删除运动记录，记录ID: {}", userId, id);
        workoutRecordService.deleteWorkoutRecord(userId, id);
        return Result.success();
    }

    /**
     * 查询运动记录详情
     */
    @Operation(summary = "查询运动记录详情")
    @GetMapping("/record/{id}")
    public Result<WorkoutRecordResponseDTO> getRecordById(@RequestHeader("Authorization") String token,
                                                           @Parameter(description = "记录ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 查询运动记录详情，记录ID: {}", userId, id);
        WorkoutRecordResponseDTO result = workoutRecordService.getWorkoutRecordById(userId, id);
        return Result.success(result);
    }

    /**
     * 分页查询我的运动记录
     */
    @Operation(summary = "分页查询我的运动记录")
    @GetMapping("/record/my-page")
    public Result<Page<WorkoutRecordResponseDTO>> getMyRecords(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "运动类型ID") @RequestParam(required = false) Long workoutTypeId,
            @Parameter(description = "运动强度") @RequestParam(required = false) String intensity,
            @Parameter(description = "是否完成") @RequestParam(required = false) Integer isCompleted) {

        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 分页查询运动记录", userId);

        Page<WorkoutRecordResponseDTO> pageResult = workoutRecordService.getUserWorkoutRecords(
                userId, current, size, startDate, endDate, workoutTypeId, intensity, isCompleted);
        return Result.success(pageResult);
    }

    /**
     * 查询运动类型列表
     */
    @Operation(summary = "查询运动类型列表")
    @GetMapping("/type/list")
    public Result<List<WorkoutTypeResponseDTO>> getWorkoutTypes(
            @Parameter(description = "运动分类") @RequestParam(required = false) String category) {
        log.info("查询运动类型列表，category={}", category);
        List<WorkoutTypeResponseDTO> types = workoutRecordService.getWorkoutTypes(category);
        return Result.success(types);
    }

    /**
     * 查询我的运动数据统计汇总
     */
    @Operation(summary = "查询我的运动数据统计汇总")
    @GetMapping("/statistics/summary")
    public Result<WorkoutStatisticsSummaryDTO> getMyStatistics(@RequestHeader("Authorization") String token) {
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 查询运动数据统计", userId);
        WorkoutStatisticsSummaryDTO statistics = workoutRecordService.getUserWorkoutStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 查询每日统计数据
     */
    @Operation(summary = "查询每日统计数据")
    @GetMapping("/statistics/daily")
    public Result<List<WorkoutDailyStatsResponseDTO>> getDailyStats(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        log.info("用户 {} 查询每日统计数据", userId);

        List<WorkoutDailyStatsResponseDTO> statsList = workoutRecordService.getDailyStats(userId, startDate, endDate);
        return Result.success(statsList);
    }
}
