package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.MembershipPurchaseDTO;
import org.example.springboot.dto.response.UserMembershipResponseDTO;
import org.example.springboot.service.UserMembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户会员记录控制器
 * @author system
 */
@Tag(name = "用户会员管理")
@RestController
@RequestMapping("/user-membership")
@Slf4j
public class UserMembershipController {

    @Resource
    private UserMembershipService userMembershipService;

    /**
     * 购买会员套餐
     */
    @Operation(summary = "购买会员套餐")
    @PostMapping("/purchase")
    public Result<Void> purchasePackage(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid @RequestBody MembershipPurchaseDTO purchaseDTO) {
        log.info("用户 {} 购买会员套餐: {}", userId, purchaseDTO.getPackageId());
        userMembershipService.purchasePackage(userId, purchaseDTO);
        return Result.success();
    }

    /**
     * 查询当前有效会员
     */
    @Operation(summary = "查询当前有效会员")
    @GetMapping("/current")
    public Result<UserMembershipResponseDTO> getCurrentMembership(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("查询用户 {} 当前有效会员", userId);
        UserMembershipResponseDTO result = userMembershipService.getCurrentMembership(userId);
        return Result.success(result);
    }

    /**
     * 查询用户会员购买历史
     */
    @Operation(summary = "查询用户会员购买历史")
    @GetMapping("/my-history")
    public Result<List<UserMembershipResponseDTO>> getUserMembershipHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("查询用户 {} 会员购买历史", userId);
        List<UserMembershipResponseDTO> result = userMembershipService.getUserMembershipHistory(userId);
        return Result.success(result);
    }

    /**
     * 分页查询会员记录（管理员）
     */
    @Operation(summary = "分页查询会员记录")
    @GetMapping("/page")
    public Result<Page<UserMembershipResponseDTO>> selectPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "会员类型") @RequestParam(required = false) Integer memberType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询会员记录: current={}, size={}, userId={}, memberType={}, status={}", 
                current, size, userId, memberType, status);
        Page<UserMembershipResponseDTO> result = userMembershipService.selectPage(current, size, userId, memberType, status);
        return Result.success(result);
    }
}
