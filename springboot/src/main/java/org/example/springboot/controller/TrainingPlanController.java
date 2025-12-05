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
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.service.TrainingPlanService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    @Operation(summary = "获取训练计划详情")
    public Result<TrainingPlanResponseDTO> getPlanById(@Parameter(description = "计划ID") @PathVariable Long id) {
        return Result.success(trainingPlanService.getPlanById(id));
    }

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
}
