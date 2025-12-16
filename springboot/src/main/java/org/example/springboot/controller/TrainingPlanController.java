package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.TrainingPlanCreateDTO;
import org.example.springboot.dto.command.TrainingPlanUpdateDTO;
import org.example.springboot.dto.response.TrainingPlanDetailResponseDTO;
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.service.TrainingPlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练计划Controller
 * @author system
 */
@RestController
@RequestMapping("/api/training-plans")
@Tag(name = "训练计划管理", description = "训练计划相关接口")
public class TrainingPlanController {

    @Resource
    private TrainingPlanService trainingPlanService;

    @GetMapping("/page")
    @Operation(summary = "分页查询训练计划")
    public Result<Page<TrainingPlanResponseDTO>> getPlanPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "教练ID") @RequestParam(required = false) Long coachId,
            @Parameter(description = "训练目标") @RequestParam(required = false) String goal,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(trainingPlanService.getPlanPage(currentPage, pageSize, userId, coachId, goal, status));
    }

    @GetMapping("/execution-history")
    @Operation(summary = "获取执行历史")
    public Result<List<TrainingPlanDetailResponseDTO>> getExecutionHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") int limit) {
        return Result.success(trainingPlanService.getExecutionHistory(userId, limit));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取训练计划详情")
    public Result<TrainingPlanResponseDTO> getPlanById(@Parameter(description = "计划ID") @PathVariable Long id) {
        return Result.success(trainingPlanService.getPlanById(id));
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "计算计划进度")
    public Result<Double> calculateProgress(
            @Parameter(description = "计划ID") @PathVariable Long id) {
        return Result.success(trainingPlanService.calculateProgress(id));
    }

    @PostMapping
    @Operation(summary = "创建训练计划")
    public Result<TrainingPlanResponseDTO> createPlan(@Valid @RequestBody TrainingPlanCreateDTO createDTO) {
        return Result.success(trainingPlanService.createPlan(createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新训练计划")
    public Result<TrainingPlanResponseDTO> updatePlan(
            @Parameter(description = "计划ID") @PathVariable Long id,
            @Valid @RequestBody TrainingPlanUpdateDTO updateDTO) {
        return Result.success(trainingPlanService.updatePlan(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练计划")
    public Result<Void> deletePlan(@Parameter(description = "计划ID") @PathVariable Long id) {
        trainingPlanService.deletePlan(id);
        return Result.success();
    }

    @PutMapping("/detail/{detailId}/completion")
    @Operation(summary = "更新训练明细完成状态")
    public Result<Void> updateDetailCompletion(
            @Parameter(description = "明细ID") @PathVariable Long detailId,
            @Parameter(description = "是否完成") @RequestParam(required = false) Integer isCompleted,
            @Parameter(description = "实际组数") @RequestParam(required = false) Integer actualSets,
            @Parameter(description = "实际次数") @RequestParam(required = false) Integer actualReps,
            @Parameter(description = "执行备注") @RequestParam(required = false) String executionNote) {
        System.out.println("更新训练明细完成状态: detailId=" + detailId + ", isCompleted=" + isCompleted);
        trainingPlanService.updateDetailCompletion(detailId, isCompleted, actualSets, actualReps, executionNote);
        return Result.success();
    }

    @PostMapping("/detail/{detailId}/note")
    @Operation(summary = "添加执行备注")
    public Result<Void> addExecutionNote(
            @Parameter(description = "明细ID") @PathVariable Long detailId,
            @Parameter(description = "备注内容") @RequestParam String note) {
        trainingPlanService.addExecutionNote(detailId, note);
        return Result.success();
    }
}
