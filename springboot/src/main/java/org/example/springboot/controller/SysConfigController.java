package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.SysConfigCreateDTO;
import org.example.springboot.dto.command.SysConfigUpdateDTO;
import org.example.springboot.dto.response.SysConfigResponseDTO;
import org.example.springboot.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置Controller
 * @author system
 */
@RestController
@RequestMapping("/sys-configs")
@Tag(name = "系统配置管理", description = "系统配置相关接口")
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @PostMapping
    @Operation(summary = "创建系统配置")
    public Result<SysConfigResponseDTO> createConfig(@Valid @RequestBody SysConfigCreateDTO createDTO) {
        return Result.success(sysConfigService.createConfig(createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新系统配置")
    public Result<SysConfigResponseDTO> updateConfig(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Valid @RequestBody SysConfigUpdateDTO updateDTO) {
        return Result.success(sysConfigService.updateConfig(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除系统配置")
    public Result<Void> deleteConfig(@Parameter(description = "配置ID") @PathVariable Long id) {
        sysConfigService.deleteConfig(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取系统配置详情")
    public Result<SysConfigResponseDTO> getConfigById(@Parameter(description = "配置ID") @PathVariable Long id) {
        return Result.success(sysConfigService.getConfigById(id));
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据配置键获取配置值")
    public Result<String> getConfigValue(@Parameter(description = "配置键") @PathVariable String configKey) {
        return Result.success(sysConfigService.getConfigValue(configKey));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询系统配置")
    public Result<Page<SysConfigResponseDTO>> getConfigPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "配置键") @RequestParam(required = false) String configKey,
            @Parameter(description = "配置分组") @RequestParam(required = false) String configGroup,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(sysConfigService.getConfigPage(currentPage, pageSize, configKey, configGroup, status));
    }

    @GetMapping("/groups")
    @Operation(summary = "获取所有配置分组")
    public Result<List<String>> getAllConfigGroups() {
        return Result.success(sysConfigService.getAllConfigGroups());
    }
}
