package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.EquipmentCreateDTO;
import org.example.springboot.dto.command.EquipmentUpdateDTO;
import org.example.springboot.dto.response.EquipmentResponseDTO;
import org.example.springboot.service.EquipmentService;
import org.springframework.web.bind.annotation.*;

/**
 * 器材管理控制器（后台管理）
 * @author system
 */
@Tag(name = "器材管理")
@RestController
@RequestMapping("/equipment")
@Slf4j
public class EquipmentController {

    @Resource
    private EquipmentService equipmentService;

    /**
     * 创建器材
     */
    @Operation(summary = "创建器材")
    @PostMapping
    public Result<EquipmentResponseDTO> create(@Valid @RequestBody EquipmentCreateDTO createDTO) {
        log.info("创建器材: {}", createDTO.getName());
        EquipmentResponseDTO result = equipmentService.createEquipment(createDTO);
        return Result.success(result);
    }

    /**
     * 更新器材
     */
    @Operation(summary = "更新器材")
    @PutMapping("/{id}")
    public Result<EquipmentResponseDTO> update(
            @Parameter(description = "器材ID") @PathVariable Long id,
            @Valid @RequestBody EquipmentUpdateDTO updateDTO) {
        log.info("更新器材，ID: {}", id);
        updateDTO.setId(id);
        EquipmentResponseDTO result = equipmentService.updateEquipment(updateDTO);
        return Result.success(result);
    }

    /**
     * 删除器材
     */
    @Operation(summary = "删除器材")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "器材ID") @PathVariable Long id) {
        log.info("删除器材，ID: {}", id);
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    /**
     * 根据ID查询器材
     */
    @Operation(summary = "根据ID查询器材")
    @GetMapping("/{id}")
    public Result<EquipmentResponseDTO> getById(
            @Parameter(description = "器材ID") @PathVariable Long id) {
        log.info("查询器材，ID: {}", id);
        EquipmentResponseDTO result = equipmentService.getEquipmentById(id);
        return Result.success(result);
    }

    /**
     * 分页查询器材
     */
    @Operation(summary = "分页查询器材")
    @GetMapping("/page")
    public Result<Page<EquipmentResponseDTO>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "器材名称") @RequestParam(required = false) String name,
            @Parameter(description = "器材类型") @RequestParam(required = false) String category,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        log.info("分页查询器材: current={}, size={}, name={}, category={}, status={}", 
                current, size, name, category, status);

        Page<EquipmentResponseDTO> pageResult = equipmentService.getEquipmentPage(current, size, name, category, status);
        return Result.success(pageResult);
    }
}
