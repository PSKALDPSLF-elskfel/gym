package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.entity.GymAnnouncement;
import org.example.springboot.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 * @author system
 */
@Tag(name = "公告管理")
@RestController
@RequestMapping("/announcement")
@Slf4j
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 获取最新公告列表
     */
    @Operation(summary = "获取最新公告列表")
    @GetMapping("/latest")
    public Result<List<GymAnnouncement>> getLatestAnnouncements(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取最新公告，限制数量: {}", limit);
        List<GymAnnouncement> announcements = announcementService.getLatestAnnouncements(limit);
        return Result.success(announcements);
    }

    /**
     * 获取所有启用的公告
     */
    @Operation(summary = "获取所有启用的公告")
    @GetMapping("/list")
    public Result<List<GymAnnouncement>> getAllAnnouncements() {
        log.info("获取所有启用的公告");
        List<GymAnnouncement> announcements = announcementService.getAllEnabledAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 分页查询公告(管理端)
     */
    @Operation(summary = "分页查询公告")
    @GetMapping("/page")
    public Result<Page<GymAnnouncement>> queryPagedAnnouncements(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "标题") @RequestParam(required = false) String title,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("分页查询公告: current={}, size={}, title={}, status={}", current, size, title, status);
        Page<GymAnnouncement> pageResult = announcementService.selectPage(current, size, title, status);
        return Result.success(pageResult);
    }

    /**
     * 创建公告
     */
    @Operation(summary = "创建公告")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody GymAnnouncement announcement) {
        log.info("创建公告: {}", announcement.getTitle());
        announcementService.create(announcement);
        return Result.success();
    }

    /**
     * 更新公告
     */
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "公告ID") @PathVariable Long id,
            @Valid @RequestBody GymAnnouncement announcement) {
        log.info("更新公告: id={}", id);
        announcement.setId(id);
        announcementService.update(announcement);
        return Result.success();
    }

    /**
     * 删除公告
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "公告ID") @PathVariable Long id) {
        log.info("删除公告: id={}", id);
        announcementService.delete(id);
        return Result.success();
    }

    /**
     * 更新公告状态
     */
    @Operation(summary = "更新公告状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "公告ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        log.info("更新公告状态: id={}, status={}", id, status);
        announcementService.updateStatus(id, status);
        return Result.success();
    }
}
