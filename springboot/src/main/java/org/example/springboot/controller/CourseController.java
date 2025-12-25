package org.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CourseCreateDTO;
import org.example.springboot.dto.command.CourseUpdateDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.entity.GymCoach;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymCoachMapper;
import org.example.springboot.service.CourseService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 * @author system
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private GymCoachMapper coachMapper;

    /**
     * 创建课程
     */
    @Operation(summary = "创建课程")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CourseCreateDTO createDTO) {
        log.info("创建课程: {}", createDTO.getName());
        courseService.create(createDTO);
        return Result.success();
    }

    /**
     * 更新课程
     */
    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "课程ID") @PathVariable String id,
            @Valid @RequestBody CourseUpdateDTO updateDTO) {
        log.info("更新课程，ID: {}", id);
        updateDTO.setId(id);
        courseService.update(updateDTO);
        return Result.success();
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "课程ID") @PathVariable String id) {
        log.info("删除课程，ID: {}", id);
        courseService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID查询课程
     */
    @Operation(summary = "根据ID查询课程")
    @GetMapping("/{id}")
    public Result<CourseResponseDTO> getById(
            @Parameter(description = "课程ID") @PathVariable String id) {
        log.info("查询课程，ID: {}", id);
        CourseResponseDTO result = courseService.getById(id);
        return Result.success(result);
    }

    /**
     * 分页查询课程
     */
    @Operation(summary = "分页查询课程")
    @GetMapping("/page")
    public Result<Page<CourseResponseDTO>> selectPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "课程名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        log.info("分页查询课程: current={}, size={}, name={}, status={}", current, size, name, status);

        Page<CourseResponseDTO> pageResult = courseService.selectPage(current, size, name, status);
        return Result.success(pageResult);
    }

    /**
     * 更新课程状态
     */
    @Operation(summary = "更新课程状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "课程ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        log.info("更新课程状态，ID: {}, 状态: {}", id, status);
        courseService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 查询所有上架的课程列表
     */
    @Operation(summary = "查询所有上架的课程列表")
    @GetMapping("/list-online")
    public Result<List<CourseResponseDTO>> listOnline() {
        log.info("查询所有上架的课程列表");
        List<CourseResponseDTO> list = courseService.listOnline();
        return Result.success(list);
    }

    /**
     * 查询教练负责的课程列表(教练端专用)
     */
    @Operation(summary = "查询我负责的课程列表")
    @GetMapping("/my-courses")
    public Result<List<CourseResponseDTO>> listMyManagedCourses() {
        Long currentUserId = JwtTokenUtils.getCurrentUserId();

        // 获取教练ID
        LambdaQueryWrapper<GymCoach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoach::getUserId, currentUserId);
        GymCoach coach = coachMapper.selectOne(wrapper);

        if (coach == null) {
            throw new BusinessException("当前用户不是教练");
        }

        log.info("查询教练负责的课程列表, coachId={}", coach.getId());
        List<CourseResponseDTO> list = courseService.listByCoachId(coach.getId());
        return Result.success(list);
    }
}
