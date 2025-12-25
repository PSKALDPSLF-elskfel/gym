package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.response.*;
import org.example.springboot.entity.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教练学员管理服务
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoachStudentService {

    private final GymCoachMapper coachMapper;
    private final GymCoachStudentMapper coachStudentMapper;
    private final UserMapper userMapper;
    private final GymTrainingPlanMapper trainingPlanMapper;
    private final GymBodyTestMapper bodyTestMapper;

    /**
     * 根据用户ID获取教练ID
     * 支持管理员访问,管理员返回第一个教练的ID
     * @param userId 用户ID
     * @return 教练ID
     */
    public Long getCoachIdByUserId(Long userId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果是管理员,返回第一个教练的ID
        if ("ADMIN".equals(user.getUserType())) {
            LambdaQueryWrapper<GymCoach> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GymCoach::getStatus, 1)
                   .orderByAsc(GymCoach::getId)
                   .last("LIMIT 1");
            GymCoach coach = coachMapper.selectOne(wrapper);
            if (coach == null) {
                throw new BusinessException("系统中暂无教练数据");
            }
            return coach.getId();
        }
        
        // 根据 userId 查询教练记录
        LambdaQueryWrapper<GymCoach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoach::getUserId, userId);
        GymCoach coach = coachMapper.selectOne(wrapper);
        if (coach == null) {
            throw new BusinessException("当前用户不是教练");
        }
        return coach.getId();
    }

    /**
     * 查询教练的学员列表（分页）
     * @param coachId 教练ID
     * @param keyword 搜索关键词（昵称或手机号）
     * @param memberType 会员类型筛选
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 学员列表分页数据
     */
    public Page<StudentDTO> getMyStudents(Long coachId, String keyword, Integer memberType, Integer pageNum, Integer pageSize) {
        // 验证教练是否存在
        GymCoach coach = coachMapper.selectById(coachId);
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }

        // 查询该教练的所有训练计划关联的学员ID
        LambdaQueryWrapper<GymTrainingPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(GymTrainingPlan::getCoachId, coachId);
        List<GymTrainingPlan> plans = trainingPlanMapper.selectList(planWrapper);
        
        List<Long> studentIds = plans.stream()
                .map(GymTrainingPlan::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 同时从教练学员关系表中获取学员ID
        LambdaQueryWrapper<GymCoachStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(GymCoachStudent::getCoachId, coachId);
        List<GymCoachStudent> coachStudents = coachStudentMapper.selectList(csWrapper);
        
        List<Long> studentIdsFromRelation = coachStudents.stream()
                .map(GymCoachStudent::getStudentId)
                .distinct()
                .collect(Collectors.toList());
        
        // 合并两种方式获取的学员ID(去重)
        studentIds.addAll(studentIdsFromRelation);
        studentIds = studentIds.stream().distinct().collect(Collectors.toList());
        
        if (studentIds.isEmpty()) {
            // 没有学员数据，返回空列表
            log.info("教练{}暂无学员", coachId);
            return new Page<>(pageNum, pageSize);
        }

        // 获取所有学员ID（去重）

        // 查询学员用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, studentIds)
                .eq(User::getUserType, "USER");
        
        // 添加搜索条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            userWrapper.and(wrapper -> wrapper
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }
        
        // 添加会员类型筛选
        if (memberType != null) {
            userWrapper.eq(User::getMemberType, memberType);
        }

        // 分页查询
        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), userWrapper);
        
        // 转换为StudentDTO
        Page<StudentDTO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(userPage.getTotal());
        
        List<StudentDTO> studentList = new ArrayList<>();
        for (User user : userPage.getRecords()) {
            StudentDTO dto = buildStudentDTO(user, coachId);
            studentList.add(dto);
        }
        
        resultPage.setRecords(studentList);
        return resultPage;
    }

    /**
     * 构建学员基本信息DTO
     */
    private StudentDTO buildStudentDTO(User user, Long coachId) {
        StudentDTO dto = StudentDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .memberType(user.getMemberType())
                .memberTypeName(user.getMemberTypeDisplayName())
                .memberExpireTime(user.getMemberExpireTime())
                .isMemberValid(user.isMemberValid())
                .createTime(user.getCreateTime())
                .build();

        // 查询教练备注
        LambdaQueryWrapper<GymCoachStudent> remarkWrapper = new LambdaQueryWrapper<>();
        remarkWrapper.eq(GymCoachStudent::getCoachId, coachId)
                .eq(GymCoachStudent::getStudentId, user.getId());
        GymCoachStudent coachStudent = coachStudentMapper.selectOne(remarkWrapper);
        if (coachStudent != null) {
            dto.setCoachRemark(coachStudent.getRemark());
        }

        // 查询训练计划数量和最近训练计划
        LambdaQueryWrapper<GymTrainingPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(GymTrainingPlan::getUserId, user.getId())
                .eq(GymTrainingPlan::getCoachId, coachId)
                .orderByDesc(GymTrainingPlan::getCreateTime);
        List<GymTrainingPlan> plans = trainingPlanMapper.selectList(planWrapper);
        
        dto.setTrainingPlanCount(plans.size());
        if (!plans.isEmpty()) {
            GymTrainingPlan latestPlan = plans.get(0);
            dto.setLatestPlanName(latestPlan.getName());
            dto.setLatestTrainingTime(latestPlan.getCreateTime());
        }

        return dto;
    }

    /**
     * 获取学员详细信息
     * @param userId 学员用户ID
     * @param coachId 教练ID
     * @return 学员详细信息
     */
    public StudentDetailDTO getStudentDetail(Long userId, Long coachId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("学员不存在");
        }

        // 验证是否为该教练的学员
        LambdaQueryWrapper<GymTrainingPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(GymTrainingPlan::getUserId, userId)
                .eq(GymTrainingPlan::getCoachId, coachId);
        Long planCount = trainingPlanMapper.selectCount(planWrapper);
        
        // 同时检查教练学员关系表
        LambdaQueryWrapper<GymCoachStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(GymCoachStudent::getCoachId, coachId)
                .eq(GymCoachStudent::getStudentId, userId);
        Long relationCount = coachStudentMapper.selectCount(csWrapper);
        
        if (planCount == 0 && relationCount == 0) {
            throw new BusinessException("无权查看该学员信息");
        }

        // 构建基本信息
        StudentDetailDTO dto = StudentDetailDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .memberType(user.getMemberType())
                .memberTypeName(user.getMemberTypeDisplayName())
                .memberExpireTime(user.getMemberExpireTime())
                .isMemberValid(user.isMemberValid())
                .createTime(user.getCreateTime())
                .build();

        // 查询教练备注
        LambdaQueryWrapper<GymCoachStudent> remarkWrapper = new LambdaQueryWrapper<>();
        remarkWrapper.eq(GymCoachStudent::getCoachId, coachId)
                .eq(GymCoachStudent::getStudentId, userId);
        GymCoachStudent coachStudent = coachStudentMapper.selectOne(remarkWrapper);
        if (coachStudent != null) {
            dto.setCoachRemark(coachStudent.getRemark());
        }

        // 查询最近体测数据
        LambdaQueryWrapper<GymBodyTest> bodyTestWrapper = new LambdaQueryWrapper<>();
        bodyTestWrapper.eq(GymBodyTest::getUserId, userId)
                .orderByDesc(GymBodyTest::getTestTime)
                .last("LIMIT 1");
        GymBodyTest latestBodyTest = bodyTestMapper.selectOne(bodyTestWrapper);
        if (latestBodyTest != null) {
            dto.setLatestBodyTest(convertToBodyTestDTO(latestBodyTest));
        }

        // 查询体测历史（最近5次）
        LambdaQueryWrapper<GymBodyTest> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(GymBodyTest::getUserId, userId)
                .orderByDesc(GymBodyTest::getTestTime)
                .last("LIMIT 5");
        List<GymBodyTest> bodyTestHistory = bodyTestMapper.selectList(historyWrapper);
        dto.setBodyTestHistory(bodyTestHistory.stream()
                .map(this::convertToBodyTestDTO)
                .collect(Collectors.toList()));

        // 查询训练计划
        LambdaQueryWrapper<GymTrainingPlan> trainingPlanWrapper = new LambdaQueryWrapper<>();
        trainingPlanWrapper.eq(GymTrainingPlan::getUserId, userId)
                .eq(GymTrainingPlan::getCoachId, coachId)
                .orderByDesc(GymTrainingPlan::getCreateTime);
        List<GymTrainingPlan> trainingPlans = trainingPlanMapper.selectList(trainingPlanWrapper);
        
        dto.setTrainingPlans(trainingPlans.stream()
                .map(this::convertToTrainingPlanDTO)
                .collect(Collectors.toList()));
        dto.setTotalTrainingPlans(trainingPlans.size());
        
        long activeCount = trainingPlans.stream()
                .filter(GymTrainingPlan::isActive)
                .count();
        dto.setActiveTrainingPlans((int) activeCount);

        return dto;
    }

    /**
     * 更新学员备注
     * @param userId 学员ID
     * @param coachId 教练ID
     * @param remark 备注内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStudentRemark(Long userId, Long coachId, String remark) {
        // 验证学员是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("学员不存在");
        }

        // 验证是否为该教练的学员
        LambdaQueryWrapper<GymTrainingPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(GymTrainingPlan::getUserId, userId)
                .eq(GymTrainingPlan::getCoachId, coachId);
        Long planCount = trainingPlanMapper.selectCount(planWrapper);
        if (planCount == 0) {
            throw new BusinessException("无权修改该学员备注");
        }

        // 查询是否已存在关系记录
        LambdaQueryWrapper<GymCoachStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachStudent::getCoachId, coachId)
                .eq(GymCoachStudent::getStudentId, userId);
        GymCoachStudent coachStudent = coachStudentMapper.selectOne(wrapper);

        if (coachStudent != null) {
            // 更新备注
            coachStudent.setRemark(remark);
            coachStudent.setUpdateTime(LocalDateTime.now());
            coachStudentMapper.updateById(coachStudent);
        } else {
            // 创建新记录
            coachStudent = GymCoachStudent.builder()
                    .coachId(coachId)
                    .studentId(userId)
                    .remark(remark)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            coachStudentMapper.insert(coachStudent);
        }

        log.info("教练{}更新学员{}备注成功", coachId, userId);
    }

    /**
     * 查询学员体测历史
     * @param userId 学员ID
     * @param coachId 教练ID
     * @return 体测历史列表
     */
    public List<BodyTestResponseDTO> getStudentBodyTests(Long userId, Long coachId) {
        // 验证权限
        validateCoachStudentRelation(userId, coachId);

        // 查询体测记录
        LambdaQueryWrapper<GymBodyTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymBodyTest::getUserId, userId)
                .orderByDesc(GymBodyTest::getTestTime);
        List<GymBodyTest> bodyTests = bodyTestMapper.selectList(wrapper);

        return bodyTests.stream()
                .map(this::convertToBodyTestDTO)
                .collect(Collectors.toList());
    }

    /**
     * 查询学员训练计划
     * @param userId 学员ID
     * @param coachId 教练ID
     * @return 训练计划列表
     */
    public List<TrainingPlanResponseDTO> getStudentTrainingPlans(Long userId, Long coachId) {
        // 查询训练计划
        LambdaQueryWrapper<GymTrainingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymTrainingPlan::getUserId, userId)
                .eq(GymTrainingPlan::getCoachId, coachId)
                .orderByDesc(GymTrainingPlan::getCreateTime);
        List<GymTrainingPlan> plans = trainingPlanMapper.selectList(wrapper);

        return plans.stream()
                .map(this::convertToTrainingPlanDTO)
                .collect(Collectors.toList());
    }

    /**
     * 验证教练和学员的关系
     */
    private void validateCoachStudentRelation(Long userId, Long coachId) {
        LambdaQueryWrapper<GymTrainingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymTrainingPlan::getUserId, userId)
                .eq(GymTrainingPlan::getCoachId, coachId);
        Long count = trainingPlanMapper.selectCount(wrapper);
        if (count == 0) {
            throw new BusinessException("无权查看该学员信息");
        }
    }

    /**
     * 转换体测数据为DTO
     */
    private BodyTestResponseDTO convertToBodyTestDTO(GymBodyTest bodyTest) {
        return BodyTestResponseDTO.builder()
                .id(bodyTest.getId())
                .userId(bodyTest.getUserId())
                .height(bodyTest.getHeight())
                .weight(bodyTest.getWeight())
                .bmi(bodyTest.getBmi())
                .bodyFat(bodyTest.getBodyFat())
                .muscleMass(bodyTest.getMuscleMass())
                .visceralFat(bodyTest.getVisceralFat())
                .basalMetabolism(bodyTest.getBasalMetabolism())
                .testTime(bodyTest.getTestTime())
                .testerId(bodyTest.getTesterId())
                .remark(bodyTest.getRemark())
                .createTime(bodyTest.getCreateTime())
                .build();
    }

    /**
     * 转换训练计划为DTO
     */
    private TrainingPlanResponseDTO convertToTrainingPlanDTO(GymTrainingPlan plan) {
        return TrainingPlanResponseDTO.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .coachId(plan.getCoachId())
                .name(plan.getName())
                .goal(plan.getGoal())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .status(plan.getStatus())
                .remark(plan.getRemark())
                .createTime(plan.getCreateTime())
                .updateTime(plan.getUpdateTime())
                .build();
    }
}
