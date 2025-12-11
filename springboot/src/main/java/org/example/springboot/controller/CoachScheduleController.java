package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.*;
import org.example.springboot.dto.response.*;
import org.example.springboot.service.CoachScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练排班控制器
 * @author system
 */
@Tag(name = "教练排班管理")
@RestController
@RequestMapping("/coach-schedule")
@Slf4j
public class CoachScheduleController {

    @Resource
    private CoachScheduleService coachScheduleService;

    /**
     * 创建排班
     */
    @Operation(summary = "创建排班")
    @PostMapping("/schedule")
    public Result<Void> createSchedule(@Valid @RequestBody ScheduleCreateDTO createDTO) {
        log.info("创建排班: 教练ID={}, 日期={}", createDTO.getCoachId(), createDTO.getWorkDate());
        coachScheduleService.createSchedule(createDTO);
        return Result.success();
    }

    /**
     * 更新排班
     */
    @Operation(summary = "更新排班")
    @PutMapping("/schedule/{id}")
    public Result<Void> updateSchedule(
            @Parameter(description = "排班ID") @PathVariable Long id,
            @Valid @RequestBody ScheduleUpdateDTO updateDTO) {
        log.info("更新排班，ID: {}", id);
        coachScheduleService.updateSchedule(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除排班
     */
    @Operation(summary = "删除排班")
    @DeleteMapping("/schedule/{id}")
    public Result<Void> deleteSchedule(@Parameter(description = "排班ID") @PathVariable Long id) {
        log.info("删除排班，ID: {}", id);
        coachScheduleService.deleteSchedule(id);
        return Result.success();
    }

    /**
     * 根据ID查询排班
     */
    @Operation(summary = "根据ID查询排班")
    @GetMapping("/schedule/{id}")
    public Result<ScheduleResponseDTO> getScheduleById(
            @Parameter(description = "排班ID") @PathVariable Long id) {
        log.info("查询排班，ID: {}", id);
        ScheduleResponseDTO result = coachScheduleService.getScheduleById(id);
        return Result.success(result);
    }

    /**
     * 查询教练月度排班
     */
    @Operation(summary = "查询教练月度排班")
    @GetMapping("/schedule/monthly")
    public Result<List<ScheduleResponseDTO>> getMonthlySchedule(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Parameter(description = "年份") @RequestParam int year,
            @Parameter(description = "月份") @RequestParam int month) {
        log.info("查询教练月度排班: 教练ID={}, 年={}, 月={}", coachId, year, month);
        List<ScheduleResponseDTO> result = coachScheduleService.getMonthlySchedule(coachId, year, month);
        return Result.success(result);
    }

    /**
     * 查询教练今日排班
     */
    @Operation(summary = "查询教练今日排班")
    @GetMapping("/schedule/today")
    public Result<List<ScheduleResponseDTO>> getTodaySchedule(
            @Parameter(description = "教练ID") @RequestParam Long coachId) {
        log.info("查询教练今日排班: 教练ID={}", coachId);
        List<ScheduleResponseDTO> result = coachScheduleService.getTodaySchedule(coachId);
        return Result.success(result);
    }

    /**
     * 创建排班申请
     */
    @Operation(summary = "创建排班申请")
    @PostMapping("/request")
    public Result<Void> createScheduleRequest(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Valid @RequestBody ScheduleRequestCreateDTO createDTO) {
        log.info("教练{}创建排班申请: 类型={}", coachId, createDTO.getRequestType());
        coachScheduleService.createScheduleRequest(coachId, createDTO);
        return Result.success();
    }

    /**
     * 审批排班申请
     */
    @Operation(summary = "审批排班申请")
    @PutMapping("/request/{requestId}/approve")
    public Result<Void> approveScheduleRequest(
            @Parameter(description = "申请ID") @PathVariable Long requestId,
            @Parameter(description = "管理员ID") @RequestParam Long adminId,
            @Valid @RequestBody ScheduleRequestApproveDTO approveDTO) {
        log.info("审批排班申请: 申请ID={}, 管理员ID={}, 状态={}", requestId, adminId, approveDTO.getStatus());
        coachScheduleService.approveScheduleRequest(requestId, adminId, approveDTO);
        return Result.success();
    }

    /**
     * 查询教练的排班申请记录
     */
    @Operation(summary = "查询教练的排班申请记录")
    @GetMapping("/request/list")
    public Result<Page<ScheduleRequestResponseDTO>> getScheduleRequestsByCoach(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        log.info("查询教练的排班申请记录: 教练ID={}, page={}, size={}", coachId, page, size);
        Page<ScheduleRequestResponseDTO> result = coachScheduleService.getScheduleRequestsByCoach(coachId, page, size);
        return Result.success(result);
    }

    /**
     * 打卡入场
     */
    @Operation(summary = "打卡入场")
    @PostMapping("/attendance/check-in")
    public Result<Void> checkIn(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Valid @RequestBody AttendanceCheckDTO checkDTO) {
        log.info("教练{}打卡入场: 排班ID={}", coachId, checkDTO.getScheduleId());
        coachScheduleService.checkIn(coachId, checkDTO);
        return Result.success();
    }

    /**
     * 打卡离场
     */
    @Operation(summary = "打卡离场")
    @PostMapping("/attendance/check-out")
    public Result<Void> checkOut(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Valid @RequestBody AttendanceCheckDTO checkDTO) {
        log.info("教练{}打卡离场: 排班ID={}", coachId, checkDTO.getScheduleId());
        coachScheduleService.checkOut(coachId, checkDTO);
        return Result.success();
    }

    /**
     * 查询教练的打卡记录
     */
    @Operation(summary = "查询教练的打卡记录")
    @GetMapping("/attendance/records")
    public Result<Page<ScheduleRecordResponseDTO>> getAttendanceRecords(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        log.info("查询教练的打卡记录: 教练ID={}, page={}, size={}", coachId, page, size);
        Page<ScheduleRecordResponseDTO> result = coachScheduleService.getAttendanceRecords(coachId, page, size);
        return Result.success(result);
    }

    /**
     * 查询教练的月度统计
     */
    @Operation(summary = "查询教练的月度统计")
    @GetMapping("/statistics/monthly")
    public Result<ScheduleStatisticsResponseDTO> getMonthlyStatistics(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Parameter(description = "年份") @RequestParam int year,
            @Parameter(description = "月份") @RequestParam int month) {
        log.info("查询教练月度统计: 教练ID={}, 年={}, 月={}", coachId, year, month);
        ScheduleStatisticsResponseDTO result = coachScheduleService.getMonthlyStatistics(coachId, year, month);
        return Result.success(result);
    }
}