package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.MembershipPackageCreateDTO;
import org.example.springboot.dto.command.MembershipPackageUpdateDTO;
import org.example.springboot.dto.response.MembershipPackageResponseDTO;
import org.example.springboot.service.MembershipPackageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员套餐控制器
 * @author system
 */
@Tag(name = "会员套餐管理")
@RestController
@RequestMapping("/membership-package")
@Slf4j
public class MembershipPackageController {

    @Resource
    private MembershipPackageService membershipPackageService;

    /**
     * 创建会员套餐
     */
    @Operation(summary = "创建会员套餐")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody MembershipPackageCreateDTO createDTO) {
        log.info("创建会员套餐: {}", createDTO.getName());
        membershipPackageService.create(createDTO);
        return Result.success();
    }

    /**
     * 更新会员套餐
     */
    @Operation(summary = "更新会员套餐")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "套餐ID") @PathVariable Long id,
            @Valid @RequestBody MembershipPackageUpdateDTO updateDTO) {
        log.info("更新会员套餐，ID: {}", id);
        updateDTO.setId(id);
        membershipPackageService.update(updateDTO);
        return Result.success();
    }

    /**
     * 删除会员套餐
     */
    @Operation(summary = "删除会员套餐")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "套餐ID") @PathVariable Long id) {
        log.info("删除会员套餐，ID: {}", id);
        membershipPackageService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID查询会员套餐
     */
    @Operation(summary = "根据ID查询会员套餐")
    @GetMapping("/{id}")
    public Result<MembershipPackageResponseDTO> getById(
            @Parameter(description = "套餐ID") @PathVariable Long id) {
        log.info("查询会员套餐，ID: {}", id);
        MembershipPackageResponseDTO result = membershipPackageService.getById(id);
        return Result.success(result);
    }

    /**
     * 分页查询会员套餐
     */
    @Operation(summary = "分页查询会员套餐")
    @GetMapping("/page")
    public Result<Page<MembershipPackageResponseDTO>> selectPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "套餐名称") @RequestParam(required = false) String name,
            @Parameter(description = "会员类型") @RequestParam(required = false) Integer memberType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("分页查询会员套餐: current={}, size={}, name={}, memberType={}, status={}", 
                 current, size, name, memberType, status);
        
        Page<MembershipPackageResponseDTO> pageResult = membershipPackageService.selectPage(
                current, size, name, memberType, status);
        return Result.success(pageResult);
    }

    /**
     * 更新套餐状态
     */
    @Operation(summary = "更新套餐状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "套餐ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        log.info("更新套餐状态，ID: {}, 状态: {}", id, status);
        membershipPackageService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 根据会员类型查询套餐列表（仅上架的）
     */
    @Operation(summary = "根据会员类型查询套餐列表")
    @GetMapping("/list-by-type")
    public Result<List<MembershipPackageResponseDTO>> listByMemberType(
            @Parameter(description = "会员类型") @RequestParam(required = false) Integer memberType) {
        log.info("查询会员类型套餐列表，会员类型: {}", memberType);
        List<MembershipPackageResponseDTO> list = membershipPackageService.listByMemberType(memberType);
        return Result.success(list);
    }

    /**
     * 查询所有上架的套餐列表
     */
    @Operation(summary = "查询所有上架的套餐列表")
    @GetMapping("/list-online")
    public Result<List<MembershipPackageResponseDTO>> listOnline() {
        log.info("查询所有上架的套餐列表");
        List<MembershipPackageResponseDTO> list = membershipPackageService.listOnline();
        return Result.success(list);
    }
}
