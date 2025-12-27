package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.BodyTestCreateDTO;
import org.example.springboot.dto.response.BodyTestResponseDTO;
import org.example.springboot.service.BodyTestService;
import org.springframework.web.bind.annotation.*;

/**
 * 体测数据Controller
 * @author system
 */
@RestController
@RequestMapping("/body-tests")
@Tag(name = "体测数据管理", description = "体测数据相关接口")
public class BodyTestController {

    @Resource
    private BodyTestService bodyTestService;

    @PostMapping
    @Operation(summary = "创建体测数据")
    public Result<BodyTestResponseDTO> createBodyTest(@Valid @RequestBody BodyTestCreateDTO createDTO) {
        return Result.success(bodyTestService.createBodyTest(createDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除体测数据")
    public Result<Void> deleteBodyTest(@Parameter(description = "体测ID") @PathVariable Long id) {
        bodyTestService.deleteBodyTest(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取体测数据详情")
    public Result<BodyTestResponseDTO> getBodyTestById(@Parameter(description = "体测ID") @PathVariable Long id) {
        return Result.success(bodyTestService.getBodyTestById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询体测数据")
    public Result<Page<BodyTestResponseDTO>> getBodyTestPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        return Result.success(bodyTestService.getBodyTestPage(currentPage, pageSize, userId));
    }

    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "获取用户最新体测数据")
    public Result<BodyTestResponseDTO> getLatestBodyTest(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(bodyTestService.getLatestBodyTest(userId));
    }
}
