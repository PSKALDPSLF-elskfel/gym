package org.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.CoachCreateDTO;
import org.example.springboot.dto.command.CoachUpdateDTO;
import org.example.springboot.dto.response.CoachDashboardDTO;
import org.example.springboot.dto.response.CoachResponseDTO;
import org.example.springboot.entity.GymCoach;
import org.example.springboot.entity.User;
import org.example.springboot.mapper.GymCoachMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.service.CoachService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练Controller
 * @author system
 */
@RestController
@RequestMapping("/coaches")
@Tag(name = "教练管理", description = "教练相关接口")
public class CoachController {

    @Resource
    private CoachService coachService;

    @Resource
    private GymCoachMapper coachMapper;
    
    @Resource
    private UserMapper userMapper;

    @PostMapping
    @Operation(summary = "创建教练")
    public Result<CoachResponseDTO> createCoach(@Valid @RequestBody CoachCreateDTO createDTO) {
        return Result.success(coachService.createCoach(createDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教练")
    public Result<CoachResponseDTO> updateCoach(
            @Parameter(description = "教练ID") @PathVariable Long id,
            @Valid @RequestBody CoachUpdateDTO updateDTO) {
        return Result.success(coachService.updateCoach(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教练")
    public Result<Void> deleteCoach(@Parameter(description = "教练ID") @PathVariable Long id) {
        coachService.deleteCoach(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取教练详情")
    public Result<CoachResponseDTO> getCoachById(@Parameter(description = "教练ID") @PathVariable Long id) {
        return Result.success(coachService.getCoachById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询教练")
    public Result<Page<CoachResponseDTO>> getCoachPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "专业领域") @RequestParam(required = false) String specialty,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(coachService.getCoachPage(currentPage, pageSize, name, specialty, status));
    }

    @GetMapping("/active")
    @Operation(summary = "获取所有在职教练")
    public Result<List<CoachResponseDTO>> getAllActiveCoaches() {
        return Result.success(coachService.getAllActiveCoaches());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取教练工作台数据")
    public Result<CoachDashboardDTO> getDashboard() {
        Long userId = JwtTokenUtils.getCurrentUserId();
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 如果是管理员,返回第一个教练的Dashboard数据
        if ("ADMIN".equals(user.getUserType())) {
            GymCoach firstCoach = coachMapper.selectOne(
                new LambdaQueryWrapper<GymCoach>()
                    .eq(GymCoach::getStatus, 1)
                    .orderByAsc(GymCoach::getId)
                    .last("LIMIT 1")
            );
            if (firstCoach == null) {
                return Result.error("系统中暂无教练数据");
            }
            CoachDashboardDTO data = coachService.getDashboardData(firstCoach.getUserId());
            return Result.success(data);
        }
        
        CoachDashboardDTO data = coachService.getDashboardData(userId);
        return Result.success(data);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前教练信息")
    public Result<CoachResponseDTO> getCoachInfo() {
        Long userId = JwtTokenUtils.getCurrentUserId();
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 如果是管理员,返回第一个教练的信息
        if ("ADMIN".equals(user.getUserType())) {
            GymCoach firstCoach = coachMapper.selectOne(
                new LambdaQueryWrapper<GymCoach>()
                    .eq(GymCoach::getStatus, 1)
                    .orderByAsc(GymCoach::getId)
                    .last("LIMIT 1")
            );
            if (firstCoach == null) {
                return Result.error("系统中暂无教练数据");
            }
            CoachResponseDTO response = coachService.getCoachById(firstCoach.getId());
            return Result.success(response);
        }
        
        // 根据userId查询教练
        GymCoach coach = coachMapper.selectOne(
            new LambdaQueryWrapper<GymCoach>()
                .eq(GymCoach::getUserId, userId)
        );
        
        if (coach == null) {
            return Result.error("当前用户不是教练");
        }
        
        CoachResponseDTO response = coachService.getCoachById(coach.getId());
        return Result.success(response);
    }

    @PutMapping("/info")
    @Operation(summary = "更新教练信息")
    public Result<CoachResponseDTO> updateCoachInfo(@Valid @RequestBody CoachUpdateDTO updateDTO) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        
        // 根据userId查询教练
        GymCoach coach = coachMapper.selectOne(
            new LambdaQueryWrapper<GymCoach>()
                .eq(GymCoach::getUserId, userId)
        );
        
        if (coach == null) {
            return Result.error("当前用户不是教练");
        }
        
        CoachResponseDTO response = coachService.updateCoach(coach.getId(), updateDTO);
        return Result.success(response);
    }
}
