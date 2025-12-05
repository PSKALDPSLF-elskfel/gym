package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.TrainingActionCreateDTO;
import org.example.springboot.dto.command.TrainingActionUpdateDTO;
import org.example.springboot.dto.response.TrainingActionResponseDTO;
import org.example.springboot.service.TrainingActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 训练动作Controller
 * @author system
 */
@RestController
@RequestMapping("/api/training-actions")
@Tag(name = "训练动作管理", description = "训练动作库相关接口")
public class TrainingActionController {

    @Resource
    private TrainingActionService trainingActionService;

    @PostMapping
    @Operation(summary = "创建训练动作")
    public Result<TrainingActionResponseDTO> createAction(@Valid @RequestBody TrainingActionCreateDTO createDTO) {
        return Result.success(trainingActionService.createAction(createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新训练动作")
    public Result<TrainingActionResponseDTO> updateAction(
            @Parameter(description = "动作ID") @PathVariable Long id,
            @Valid @RequestBody TrainingActionUpdateDTO updateDTO) {
        return Result.success(trainingActionService.updateAction(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练动作")
    public Result<Void> deleteAction(@Parameter(description = "动作ID") @PathVariable Long id) {
        trainingActionService.deleteAction(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取训练动作详情")
    public Result<TrainingActionResponseDTO> getActionById(@Parameter(description = "动作ID") @PathVariable Long id) {
        return Result.success(trainingActionService.getActionById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询训练动作")
    public Result<Page<TrainingActionResponseDTO>> getActionPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "动作名称") @RequestParam(required = false) String name,
            @Parameter(description = "动作分类") @RequestParam(required = false) String category,
            @Parameter(description = "难度") @RequestParam(required = false) Integer difficulty,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(trainingActionService.getActionPage(currentPage, pageSize, name, category, difficulty, status));
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的训练动作")
    public Result<List<TrainingActionResponseDTO>> getAllEnabledActions() {
        return Result.success(trainingActionService.getAllEnabledActions());
    }
}
