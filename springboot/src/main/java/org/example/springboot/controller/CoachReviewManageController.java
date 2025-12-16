package org.example.springboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CoachReviewReplyDTO;
import org.example.springboot.dto.response.CoachReviewResponseDTO;
import org.example.springboot.dto.response.CoachReviewStatisticsResponseDTO;
import org.example.springboot.service.CoachReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 教练评价管理控制器（教练端）
 */
@Tag(name = "教练端-评价管理")
@RestController
@RequestMapping("/api/coach/reviews")
@RequiredArgsConstructor
public class CoachReviewManageController {
    
    private final CoachReviewService reviewService;
    
    /**
     * 获取我收到的评价列表
     */
    @Operation(summary = "获取我收到的评价列表")
    @GetMapping("/received")
    public Result<IPage<CoachReviewResponseDTO>> getReceivedReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long coachId = getCurrentCoachId();
        IPage<CoachReviewResponseDTO> page = reviewService.getCoachReceivedReviews(coachId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 回复评价
     */
    @Operation(summary = "回复评价")
    @PostMapping("/{reviewId}/reply")
    public Result<Void> replyReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CoachReviewReplyDTO dto) {
        
        Long coachId = getCurrentCoachId();
        reviewService.replyReview(reviewId, dto, coachId);
        return Result.success();
    }
    
    /**
     * 获取我的评价统计
     */
    @Operation(summary = "获取我的评价统计")
    @GetMapping("/statistics")
    public Result<CoachReviewStatisticsResponseDTO> getMyStatistics() {
        Long coachId = getCurrentCoachId();
        CoachReviewStatisticsResponseDTO statistics = reviewService.getReviewStatistics(coachId);
        return Result.success(statistics);
    }
    
    /**
     * 获取评价详情
     */
    @Operation(summary = "获取评价详情")
    @GetMapping("/{reviewId}")
    public Result<CoachReviewResponseDTO> getReviewDetail(@PathVariable Long reviewId) {
        CoachReviewResponseDTO detail = reviewService.getReviewDetail(reviewId, null);
        return Result.success(detail);
    }
    
    /**
     * 获取当前登录教练ID
     * TODO: 需要根据实际的教练认证逻辑调整
     */
    private Long getCurrentCoachId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            Long userId = (Long) authentication.getPrincipal();
            // TODO: 根据userId查询对应的coachId
            // 这里需要查询gym_coach表获取coachId
            return userId; // 临时返回，实际需要查询
        }
        throw new RuntimeException("未登录");
    }
}
