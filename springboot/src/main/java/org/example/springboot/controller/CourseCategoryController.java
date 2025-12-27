package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CourseCategoryCreateDTO;
import org.example.springboot.dto.command.CourseCategoryUpdateDTO;
import org.example.springboot.dto.response.CourseCategoryResponseDTO;
import org.example.springboot.service.CourseCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程分类Controller
 * @author system
 */
@RestController
@RequestMapping("/course-categories")
@Tag(name = "课程分类管理", description = "课程分类相关接口")
public class CourseCategoryController {

    @Resource
    private CourseCategoryService courseCategoryService;

    @PostMapping
    @Operation(summary = "创建课程分类")
    public Result<CourseCategoryResponseDTO> createCategory(@Valid @RequestBody CourseCategoryCreateDTO createDTO) {
        return Result.success(courseCategoryService.createCategory(createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程分类")
    public Result<CourseCategoryResponseDTO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CourseCategoryUpdateDTO updateDTO) {
        return Result.success(courseCategoryService.updateCategory(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程分类")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        courseCategoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程分类详情")
    public Result<CourseCategoryResponseDTO> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        return Result.success(courseCategoryService.getCategoryById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询课程分类")
    public Result<Page<CourseCategoryResponseDTO>> getCategoryPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(courseCategoryService.getCategoryPage(currentPage, pageSize, name, status));
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的课程分类")
    public Result<List<CourseCategoryResponseDTO>> getAllEnabledCategories() {
        return Result.success(courseCategoryService.getAllEnabledCategories());
    }
}
