package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.MaintenanceCreateDTO;
import org.example.springboot.dto.response.MaintenanceResponseDTO;
import org.example.springboot.service.EquipmentService;
import org.springframework.web.bind.annotation.*;

/**
 * 器材维护记录控制器（后台管理）
 * @author system
 */
@Tag(name = "器材维护管理")
@RestController
@RequestMapping("/equipment-maintenance")
@Slf4j
public class EquipmentMaintenanceController {

    @Resource
    private EquipmentService equipmentService;

    /**
     * 添加维护记录
     */
    @Operation(summary = "添加维护记录")
    @PostMapping
    public Result<Void> addMaintenance(@Valid @RequestBody MaintenanceCreateDTO createDTO) {
        log.info("添加维护记录，器材ID: {}", createDTO.getEquipmentId());
        equipmentService.addMaintenance(createDTO);
        return Result.success();
    }

    /**
     * 分页查询维护记录
     */
    @Operation(summary = "分页查询维护记录")
    @GetMapping("/page")
    public Result<Page<MaintenanceResponseDTO>> page(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "器材ID") @RequestParam(required = false) Long equipmentId,
            @Parameter(description = "维护类型") @RequestParam(required = false) Integer maintenanceType) {

        log.info("分页查询维护记录: current={}, size={}, equipmentId={}, maintenanceType={}", 
                current, size, equipmentId, maintenanceType);

        Page<MaintenanceResponseDTO> pageResult = equipmentService.getMaintenanceRecords(
                current, size, equipmentId, maintenanceType);
        return Result.success(pageResult);
    }
}
