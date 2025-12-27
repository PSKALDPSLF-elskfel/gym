package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.TrainingPlanCreateDTO;
import org.example.springboot.dto.command.TrainingPlanTemplateCreateDTO;
import org.example.springboot.dto.command.TrainingPlanTemplateUpdateDTO;
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.dto.response.TrainingPlanTemplateResponseDTO;
import org.example.springboot.service.TrainingPlanService;
import org.springframework.web.bind.annotation.*;

/**
 * 训练计划模板Controller
 * @author system
 */
@RestController
@RequestMapping("/training-plan-templates")
@Tag(name = "训练计划模板管理", description = "训练计划模板相关接口")
public class TrainingPlanTemplateController {

    @Resource
    private TrainingPlanService trainingPlanService;

    @PostMapping("/coach")
    @Operation(summary = "创建训练计划模板(教练)")
    public Result<TrainingPlanTemplateResponseDTO> createTemplate(
            @Parameter(description = "教练ID") @RequestParam Long coachId,
            @Valid @RequestBody TrainingPlanTemplateCreateDTO createDTO) {
        return Result.success(trainingPlanService.createTemplate(coachId, createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新训练计划模板(教练)")
    public Result<TrainingPlanTemplateResponseDTO> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody TrainingPlanTemplateUpdateDTO updateDTO) {
        return Result.success(trainingPlanService.updateTemplate(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练计划模板(教练)")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        trainingPlanService.deleteTemplate(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取训练计划模板详情")
    public Result<TrainingPlanTemplateResponseDTO> getTemplateById(@Parameter(description = "模板ID") @PathVariable Long id) {
        return Result.success(trainingPlanService.getTemplateById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询训练计划模板")
    public Result<Page<TrainingPlanTemplateResponseDTO>> getTemplatePage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "教练ID") @RequestParam(required = false) Long coachId,
            @Parameter(description = "训练目标") @RequestParam(required = false) String goal,
            @Parameter(description = "难度") @RequestParam(required = false) Integer difficulty,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(trainingPlanService.getTemplatePage(currentPage, pageSize, coachId, goal, difficulty, status));
    }

    @GetMapping("/coach/{coachId}/list")
    @Operation(summary = "获取教练的模板列表")
    public Result<Page<TrainingPlanTemplateResponseDTO>> getCoachTemplates(
            @Parameter(description = "教练ID") @PathVariable Long coachId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(trainingPlanService.getCoachTemplates(coachId, currentPage, pageSize));
    }

    @PostMapping("/{templateId}/create-plan")
    @Operation(summary = "从模板快速创建训练计划")
    public Result<TrainingPlanResponseDTO> createPlanFromTemplate(
            @Parameter(description = "模板ID") @PathVariable Long templateId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "计划名称") @RequestParam String planName) {
        return Result.success(trainingPlanService.createPlanFromTemplate(userId, templateId, planName));
    }
}
