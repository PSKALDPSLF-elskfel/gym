package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CourseScheduleCreateDTO;
import org.example.springboot.dto.command.CourseScheduleUpdateDTO;
import org.example.springboot.dto.response.CourseScheduleResponseDTO;
import org.example.springboot.service.CourseScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程时间安排控制器
 * @author system
 */
@Tag(name = "课程时间安排管理")
@RestController
@RequestMapping("/course-schedule")
@Slf4j
public class CourseScheduleController {

    @Resource
    private CourseScheduleService courseScheduleService;

    /**
     * 创建课程时间安排
     */
    @Operation(summary = "创建课程时间安排")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CourseScheduleCreateDTO createDTO) {
        log.info("创建课程时间安排: courseId={}", createDTO.getCourseId());
        courseScheduleService.create(createDTO);
        return Result.success();
    }

    /**
     * 更新课程时间安排
     */
    @Operation(summary = "更新课程时间安排")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "排课ID") @PathVariable Long id,
            @Valid @RequestBody CourseScheduleUpdateDTO updateDTO) {
        log.info("更新课程时间安排，ID: {}", id);
        updateDTO.setId(id);
        courseScheduleService.update(updateDTO);
        return Result.success();
    }

    /**
     * 删除课程时间安排
     */
    @Operation(summary = "删除课程时间安排")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "排课ID") @PathVariable Long id) {
        log.info("删除课程时间安排，ID: {}", id);
        courseScheduleService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID查询课程时间安排
     */
    @Operation(summary = "根据ID查询课程时间安排")
    @GetMapping("/{id}")
    public Result<CourseScheduleResponseDTO> getById(
            @Parameter(description = "排课ID") @PathVariable Long id) {
        log.info("查询课程时间安排，ID: {}", id);
        CourseScheduleResponseDTO result = courseScheduleService.getById(id);
        return Result.success(result);
    }

    /**
     * 分页查询课程时间安排
     */
    @Operation(summary = "分页查询课程时间安排")
    @GetMapping("/page")
    public Result<Page<CourseScheduleResponseDTO>> selectPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "课程ID") @RequestParam(required = false) String courseId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {

        log.info("分页查询课程时间安排: current={}, size={}, courseId={}, status={}, startDate={}, endDate={}", 
                 current, size, courseId, status, startDate, endDate);

        Page<CourseScheduleResponseDTO> pageResult = courseScheduleService.selectPage(
                current, size, courseId, status, startDate, endDate);
        return Result.success(pageResult);
    }

    /**
     * 根据课程ID查询排课列表
     */
    @Operation(summary = "根据课程ID查询排课列表")
    @GetMapping("/list-by-course/{courseId}")
    public Result<List<CourseScheduleResponseDTO>> listByCourseId(
            @Parameter(description = "课程ID") @PathVariable String courseId) {
        log.info("查询课程的排课列表，courseId: {}", courseId);
        List<CourseScheduleResponseDTO> list = courseScheduleService.listByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 更新排课状态
     */
    @Operation(summary = "更新排课状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "排课ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        log.info("更新排课状态，ID: {}, 状态: {}", id, status);
        courseScheduleService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 批量删除过期排课
     */
    @Operation(summary = "批量删除过期排课")
    @DeleteMapping("/delete-expired")
    public Result<Integer> deleteExpiredSchedules() {
        log.info("批量删除过期排课");
        int count = courseScheduleService.deleteExpiredSchedules();
        return Result.success(count);
    }
}
