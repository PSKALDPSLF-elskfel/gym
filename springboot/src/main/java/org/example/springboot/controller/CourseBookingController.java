package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CourseBookingCreateDTO;
import org.example.springboot.dto.response.CourseBookingDetailDTO;
import org.example.springboot.service.CourseBookingService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程预约控制器
 * @author system
 */
@Tag(name = "课程预约管理")
@RestController
@RequestMapping("/booking")
@Slf4j
public class CourseBookingController {

    @Resource
    private CourseBookingService bookingService;

    /**
     * 创建预约
     */
    @Operation(summary = "创建预约")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CourseBookingCreateDTO createDTO) {
        // 从JWT中获取当前用户ID
        Long userId = JwtTokenUtils.getCurrentUserId();
        createDTO.setUserId(userId);
        
        log.info("创建预约: userId={}, scheduleId={}", userId, createDTO.getScheduleId());
        Long bookingId = bookingService.createBooking(createDTO);
        return Result.success(bookingId);
    }

    /**
     * 取消预约
     */
    @Operation(summary = "取消预约")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@Parameter(description = "预约ID") @PathVariable Long id) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("取消预约: bookingId={}, userId={}", id, userId);
        bookingService.cancelBooking(id, userId);
        return Result.success();
    }

    /**
     * 获取预约详情
     */
    @Operation(summary = "获取预约详情")
    @GetMapping("/{id}")
    public Result<CourseBookingDetailDTO> getById(@Parameter(description = "预约ID") @PathVariable Long id) {
        log.info("查询预约详情: bookingId={}", id);
        CourseBookingDetailDTO detail = bookingService.getBookingById(id);
        return Result.success(detail);
    }

    /**
     * 获取我的预约列表
     */
    @Operation(summary = "获取我的预约列表")
    @GetMapping("/my")
    public Result<List<CourseBookingDetailDTO>> getMyBookings(
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("查询我的预约列表: userId={}, status={}", userId, status);
        List<CourseBookingDetailDTO> bookings = bookingService.getUserBookings(userId, status);
        return Result.success(bookings);
    }

    /**
     * 获取课程时间的预约列表
     */
    @Operation(summary = "获取课程时间的预约列表")
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<CourseBookingDetailDTO>> getScheduleBookings(
            @Parameter(description = "排课ID") @PathVariable Long scheduleId) {
        log.info("查询课程时间预约列表: scheduleId={}", scheduleId);
        List<CourseBookingDetailDTO> bookings = bookingService.getScheduleBookings(scheduleId);
        return Result.success(bookings);
    }

    /**
     * 分页查询预约(管理端)
     */
    @Operation(summary = "分页查询预约")
    @GetMapping("/page")
    public Result<Page<CourseBookingDetailDTO>> queryPagedBookings(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "姓名") @RequestParam(required = false) String nickname,
            @Parameter(description = "课程名称") @RequestParam(required = false) String courseName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        
        log.info("分页查询预约: current={}, size={}, nickname={}, courseName={}, status={}", 
                current, size, nickname, courseName, status);
        
        Page<CourseBookingDetailDTO> pageResult = bookingService.selectPage(
                current, size, nickname, courseName, status, startDate, endDate);
        
        return Result.success(pageResult);
    }

    /**
     * 检查用户是否已预约
     */
    @Operation(summary = "检查用户是否已预约")
    @GetMapping("/check")
    public Result<Boolean> checkBooked(@Parameter(description = "排课ID") @RequestParam Long scheduleId) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        log.info("检查用户是否已预约: userId={}, scheduleId={}", userId, scheduleId);
        boolean booked = bookingService.checkUserBooked(userId, scheduleId);
        return Result.success(booked);
    }
}
