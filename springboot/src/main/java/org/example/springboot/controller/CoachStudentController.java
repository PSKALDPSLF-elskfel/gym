package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.StudentRemarkUpdateDTO;
import org.example.springboot.dto.response.BodyTestResponseDTO;
import org.example.springboot.dto.response.StudentDTO;
import org.example.springboot.dto.response.StudentDetailDTO;
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.service.CoachStudentService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练学员管理控制器
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/coach/students")
@RequiredArgsConstructor
@Tag(name = "教练学员管理", description = "教练端学员管理相关接口")
public class CoachStudentController {

    private final CoachStudentService coachStudentService;

    /**
     * 分页查询我的学员列表
     */
    @GetMapping("/my")
    @Operation(summary = "查询我的学员列表", description = "教练查询自己的学员列表（分页）")
    public Result<Page<StudentDTO>> getMyStudents(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "搜索关键词（昵称或手机号）") @RequestParam(required = false) String keyword,
            @Parameter(description = "会员类型筛选（0-普通，1-黄金，2-铂金）") @RequestParam(required = false) Integer memberType,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        // 从token中获取教练用户ID
        Long userId = JwtTokenUtils.getUserIdFromToken(token);
        
        // 通过userId获取coachId
        Long coachId = coachStudentService.getCoachIdByUserId(userId);
        
        Page<StudentDTO> result = coachStudentService.getMyStudents(coachId, keyword, memberType, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取学员详细信息
     */
    @GetMapping("/{userId}/detail")
    @Operation(summary = "获取学员详细信息", description = "查看学员的详细信息、体测记录和训练计划")
    public Result<StudentDetailDTO> getStudentDetail(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "学员用户ID") @PathVariable Long userId
    ) {
        Long coachUserId = JwtTokenUtils.getUserIdFromToken(token);
        Long coachId = coachStudentService.getCoachIdByUserId(coachUserId);
        
        StudentDetailDTO result = coachStudentService.getStudentDetail(userId, coachId);
        return Result.success(result);
    }

    /**
     * 更新学员备注
     */
    @PutMapping("/remark")
    @Operation(summary = "更新学员备注", description = "教练为学员添加或修改备注")
    public Result<Void> updateStudentRemark(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody StudentRemarkUpdateDTO dto
    ) {
        Long coachUserId = JwtTokenUtils.getUserIdFromToken(token);
        Long coachId = coachStudentService.getCoachIdByUserId(coachUserId);
        
        coachStudentService.updateStudentRemark(dto.getUserId(), coachId, dto.getRemark());
        return Result.success();
    }

    /**
     * 查询学员体测历史
     */
    @GetMapping("/{userId}/body-tests")
    @Operation(summary = "查询学员体测历史", description = "查看学员的所有体测记录")
    public Result<List<BodyTestResponseDTO>> getStudentBodyTests(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "学员用户ID") @PathVariable Long userId
    ) {
        Long coachUserId = JwtTokenUtils.getUserIdFromToken(token);
        Long coachId = coachStudentService.getCoachIdByUserId(coachUserId);
        
        List<BodyTestResponseDTO> result = coachStudentService.getStudentBodyTests(userId, coachId);
        return Result.success(result);
    }

    /**
     * 查询学员训练计划
     */
    @GetMapping("/{userId}/training-plans")
    @Operation(summary = "查询学员训练计划", description = "查看学员的所有训练计划")
    public Result<List<TrainingPlanResponseDTO>> getStudentTrainingPlans(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "学员用户ID") @PathVariable Long userId
    ) {
        Long coachUserId = JwtTokenUtils.getUserIdFromToken(token);
        Long coachId = coachStudentService.getCoachIdByUserId(coachUserId);
        
        List<TrainingPlanResponseDTO> result = coachStudentService.getStudentTrainingPlans(userId, coachId);
        return Result.success(result);
    }
}
