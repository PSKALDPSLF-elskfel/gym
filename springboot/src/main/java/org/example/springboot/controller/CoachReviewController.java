package org.example.springboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CoachReviewCreateDTO;
import org.example.springboot.dto.response.CoachReviewResponseDTO;
import org.example.springboot.dto.response.CoachReviewStatisticsResponseDTO;
import org.example.springboot.dto.response.ReviewTagResponseDTO;
import org.example.springboot.service.CoachReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 教练评价控制器（用户端/小程序端）
 */
@Tag(name = "教练评价管理")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class CoachReviewController {
    
    private final CoachReviewService reviewService;
    
    /**
     * 创建评价
     */
    @Operation(summary = "创建评价")
    @PostMapping
    public Result<Long> createReview(@Valid @RequestBody CoachReviewCreateDTO dto) {
        Long userId = getCurrentUserId();
        Long reviewId = reviewService.createReview(dto, userId);
        return Result.success(reviewId);
    }
    
    /**
     * 删除评价
     */
    @Operation(summary = "删除评价")
    @DeleteMapping("/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.deleteReview(reviewId, userId);
        return Result.success();
    }
    
    /**
     * 点赞/取消点赞评价
     */
    @Operation(summary = "点赞/取消点赞评价")
    @PostMapping("/{reviewId}/helpful")
    public Result<Void> toggleHelpful(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.toggleHelpful(reviewId, userId);
        return Result.success();
    }
    
    /**
     * 分页查询教练评价列表
     */
    @Operation(summary = "分页查询教练评价列表")
    @GetMapping
    public Result<IPage<CoachReviewResponseDTO>> getReviewPage(
            @Parameter(description = "教练ID") @RequestParam(required = false) Long coachId,
            @Parameter(description = "评分筛选") @RequestParam(required = false) Integer rating,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long currentUserId = getCurrentUserId();
        IPage<CoachReviewResponseDTO> page = reviewService.getReviewPage(
            coachId, rating, pageNum, pageSize, currentUserId);
        return Result.success(page);
    }
    
    /**
     * 获取我的评价列表
     */
    @Operation(summary = "获取我的评价列表")
    @GetMapping("/my")
    public Result<IPage<CoachReviewResponseDTO>> getMyReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long userId = getCurrentUserId();
        IPage<CoachReviewResponseDTO> page = reviewService.getMyReviews(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取评价详情
     */
    @Operation(summary = "获取评价详情")
    @GetMapping("/{reviewId}")
    public Result<CoachReviewResponseDTO> getReviewDetail(@PathVariable Long reviewId) {
        Long currentUserId = getCurrentUserId();
        CoachReviewResponseDTO detail = reviewService.getReviewDetail(reviewId, currentUserId);
        return Result.success(detail);
    }
    
    /**
     * 获取教练评价统计
     */
    @Operation(summary = "获取教练评价统计")
    @GetMapping("/statistics/{coachId}")
    public Result<CoachReviewStatisticsResponseDTO> getReviewStatistics(@PathVariable Long coachId) {
        CoachReviewStatisticsResponseDTO statistics = reviewService.getReviewStatistics(coachId);
        return Result.success(statistics);
    }
    
    /**
     * 获取评价标签列表
     */
    @Operation(summary = "获取评价标签列表")
    @GetMapping("/tags")
    public Result<List<ReviewTagResponseDTO>> getReviewTags(
            @Parameter(description = "标签类型：1-正面，2-负面") @RequestParam(required = false) Integer tagType) {
        List<ReviewTagResponseDTO> tags = reviewService.getReviewTags(tagType);
        return Result.success(tags);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }
}
