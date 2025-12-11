package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.NotificationCreateDTO;
import org.example.springboot.dto.response.NotificationResponseDTO;
import org.example.springboot.service.NotificationService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统通知控制器
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "系统通知管理", description = "系统通知相关接口")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @Resource
    private JwtTokenUtils jwtTokenUtils;

    /**
     * 发送通知（管理端）
     */
    @PostMapping("/send")
    @Operation(summary = "发送通知", description = "管理端发送系统通知")
    public Result<Long> sendNotification(@Valid @RequestBody NotificationCreateDTO dto) {
        Long notificationId = notificationService.sendNotification(dto);
        return Result.success(notificationId);
    }

    /**
     * 批量发送通知（管理端）
     */
    @PostMapping("/send/batch")
    @Operation(summary = "批量发送通知", description = "向指定用户列表批量发送通知")
    public Result<Void> sendBatchNotification(
            @Valid @RequestBody NotificationCreateDTO dto,
            @Parameter(description = "用户ID列表") @RequestParam List<Long> userIds) {
        notificationService.sendBatchNotification(dto, userIds);
        return Result.success();
    }

    /**
     * 获取当前用户的通知列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的通知", description = "获取当前用户的通知列表（分页）")
    public Result<Page<NotificationResponseDTO>> getMyNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "通知类型") @RequestParam(required = false) String notificationType,
            @Parameter(description = "是否已读：0-未读，1-已读") @RequestParam(required = false) Integer isRead,
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        Page<NotificationResponseDTO> page = notificationService.getUserNotifications(userId, current, size, notificationType, isRead);
        return Result.success(page);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读数量", description = "获取当前用户的未读通知数量")
    public Result<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记已读", description = "标记指定通知为已读")
    public Result<Void> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        notificationService.markAsRead(notificationId, userId);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read/all")
    @Operation(summary = "全部标记已读", description = "标记当前用户所有通知为已读")
    public Result<Void> markAllAsRead(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "删除通知", description = "删除指定通知（用户端）")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtTokenUtils.getUserIdFromToken(token.replace("Bearer ", ""));
        notificationService.deleteNotification(notificationId, userId);
        return Result.success();
    }

    /**
     * 管理端-分页查询所有通知
     */
    @GetMapping("/admin/list")
    @Operation(summary = "查询所有通知", description = "管理端分页查询所有通知")
    public Result<Page<NotificationResponseDTO>> selectPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "通知类型") @RequestParam(required = false) String notificationType,
            @Parameter(description = "目标用户类型") @RequestParam(required = false) String targetUserType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        Page<NotificationResponseDTO> page = notificationService.selectPage(current, size, notificationType, targetUserType, status);
        return Result.success(page);
    }

    /**
     * 管理端-删除通知
     */
    @DeleteMapping("/admin/{notificationId}")
    @Operation(summary = "管理端删除通知", description = "管理端物理删除通知")
    public Result<Void> deleteNotificationById(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        
        notificationService.deleteNotificationById(notificationId);
        return Result.success();
    }
}
