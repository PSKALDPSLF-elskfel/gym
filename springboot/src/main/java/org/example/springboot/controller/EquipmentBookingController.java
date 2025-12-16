package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.EquipmentBookingCreateDTO;
import org.example.springboot.dto.command.EquipmentQueueCreateDTO;
import org.example.springboot.dto.response.EquipmentBookingResponseDTO;
import org.example.springboot.dto.response.EquipmentQueueResponseDTO;
import org.example.springboot.service.EquipmentService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 器材预约与排队控制器（小程序+后台）
 * @author system
 */
@Tag(name = "器材预约与排队")
@RestController
@RequestMapping("/equipment-booking")
@Slf4j
public class EquipmentBookingController {

    @Resource
    private EquipmentService equipmentService;

    @Resource
    private JwtTokenUtils jwtTokenUtils;

    // ==================== 器材预约 ====================

    /**
     * 创建预约
     */
    @Operation(summary = "创建预约")
    @PostMapping
    public Result<Long> createBooking(
            @Valid @RequestBody EquipmentBookingCreateDTO createDTO,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            if (userId == null) {
                throw new RuntimeException("无效的token，无法获取用户ID");
            }
            log.info("用户 {} 创建器材预约", userId);
            Long bookingId = equipmentService.createBooking(createDTO, userId);
            return Result.success(bookingId);
        } catch (Exception e) {
            log.error("创建预约失败", e);
            throw e;
        }
    }

    /**
     * 取消预约
     */
    @Operation(summary = "取消预约")
    @PutMapping("/{bookingId}/cancel")
    public Result<Void> cancelBooking(
            @Parameter(description = "预约ID") @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            throw new RuntimeException("无效的token，无法获取用户ID");
        }
        log.info("用户 {} 取消预约 {}", userId, bookingId);
        equipmentService.cancelBooking(bookingId, userId);
        return Result.success();
    }

    /**
     * 开始使用器材
     */
    @Operation(summary = "开始使用器材")
    @PutMapping("/{bookingId}/start")
    public Result<Void> startUsing(
            @Parameter(description = "预约ID") @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            throw new RuntimeException("无效的token，无法获取用户ID");
        }
        log.info("用户 {} 开始使用器材，预约ID: {}", userId, bookingId);
        equipmentService.startUsing(bookingId, userId);
        return Result.success();
    }

    /**
     * 结束使用器材
     */
    @Operation(summary = "结束使用器材")
    @PutMapping("/{bookingId}/end")
    public Result<Void> endUsing(
            @Parameter(description = "预约ID") @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            throw new RuntimeException("无效的token，无法获取用户ID");
        }
        log.info("用户 {} 结束使用器材，预约ID: {}", userId, bookingId);
        equipmentService.endUsing(bookingId, userId);
        return Result.success();
    }

    /**
     * 获取用户预约列表
     */
    @Operation(summary = "获取用户预约列表")
    @GetMapping("/my")
    public Result<List<EquipmentBookingResponseDTO>> getUserBookings(
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        log.info("查询用户 {} 的预约列表", userId);
        List<EquipmentBookingResponseDTO> list = equipmentService.getUserBookings(userId, status);
        return Result.success(list);
    }

    /**
     * 分页查询所有预约记录（后台管理）
     */
    @Operation(summary = "分页查询所有预约记录")
    @GetMapping("/page")
    public Result<Page<EquipmentBookingResponseDTO>> getBookingPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "器材ID") @RequestParam(required = false) Long equipmentId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        log.info("分页查询预约记录: current={}, size={}, equipmentId={}, userId={}, status={}",
                current, size, equipmentId, userId, status);

        Page<EquipmentBookingResponseDTO> pageResult = equipmentService.getBookingPage(
                current, size, equipmentId, userId, status);
        return Result.success(pageResult);
    }

    // ==================== 器材排队 ====================

    /**
     * 加入排队
     */
    @Operation(summary = "加入排队")
    @PostMapping("/queue")
    public Result<Long> joinQueue(
            @Valid @RequestBody EquipmentQueueCreateDTO createDTO,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            throw new RuntimeException("无效的token，无法获取用户ID");
        }
        log.info("用户 {} 加入排队", userId);
        Long queueId = equipmentService.joinQueue(userId, createDTO.getEquipmentId());
        return Result.success(queueId);
    }

    /**
     * 退出排队
     */
    @Operation(summary = "退出排队")
    @PutMapping("/queue/{queueId}/leave")
    public Result<Void> leaveQueue(
            @Parameter(description = "排队ID") @PathVariable Long queueId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            throw new RuntimeException("无效的token，无法获取用户ID");
        }
        log.info("用户 {} 退出排队 {}", userId, queueId);
        equipmentService.leaveQueue(queueId, userId);
        return Result.success();
    }

    /**
     * 获取排队列表
     */
    @Operation(summary = "获取排队列表")
    @GetMapping("/queue/{equipmentId}")
    public Result<List<EquipmentQueueResponseDTO>> getQueueList(
            @Parameter(description = "器材ID") @PathVariable Long equipmentId) {
        log.info("查询器材 {} 的排队列表", equipmentId);
        List<EquipmentQueueResponseDTO> list = equipmentService.getQueueList(equipmentId);
        return Result.success(list);
    }

    /**
     * 叫号（管理员功能）
     */
    @Operation(summary = "叫号")
    @PutMapping("/queue/{equipmentId}/call")
    public Result<Void> callNext(
            @Parameter(description = "器材ID") @PathVariable Long equipmentId) {
        log.info("叫号，器材ID: {}", equipmentId);
        equipmentService.callNext(equipmentId);
        return Result.success();
    }
}
