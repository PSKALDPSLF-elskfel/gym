package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.TrainingPlanCreateDTO;
import org.example.springboot.dto.command.TrainingPlanDetailDTO;
import org.example.springboot.dto.command.TrainingPlanTemplateCreateDTO;
import org.example.springboot.dto.command.TrainingPlanTemplateDetailDTO;
import org.example.springboot.dto.command.TrainingPlanTemplateUpdateDTO;
import org.example.springboot.dto.command.TrainingPlanUpdateDTO;
import org.example.springboot.dto.response.TrainingPlanDetailResponseDTO;
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.dto.response.TrainingPlanTemplateResponseDTO;
import org.example.springboot.dto.response.TrainingPlanTemplateDetailResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 训练计划Service
 * @author system
 */
@Slf4j
@Service
public class TrainingPlanService {

    @Resource
    private GymTrainingPlanMapper trainingPlanMapper;
    
    @Resource
    private GymTrainingPlanDetailMapper trainingPlanDetailMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private GymTrainingActionMapper trainingActionMapper;
    
    @Resource
    private GymTrainingPlanTemplateMapper trainingPlanTemplateMapper;
    
    @Resource
    private GymTrainingPlanTemplateDetailMapper trainingPlanTemplateDetailMapper;

    /**
     * 创建训练计划
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlanResponseDTO createPlan(TrainingPlanCreateDTO createDTO) {
        try {
            // 验证用户存在
            User user = userMapper.selectById(createDTO.getUserId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            GymTrainingPlan plan = GymTrainingPlan.builder()
                    .userId(createDTO.getUserId())
                    .coachId(createDTO.getCoachId())
                    .name(createDTO.getName())
                    .goal(createDTO.getGoal())
                    .startDate(createDTO.getStartDate())
                    .endDate(createDTO.getEndDate())
                    .status(1)
                    .version(1)
                    .remark(createDTO.getRemark())
                    .build();

            trainingPlanMapper.insert(plan);

            // 保存训练明细
            if (createDTO.getDetails() != null && !createDTO.getDetails().isEmpty()) {
                saveDetails(plan.getId(), createDTO.getDetails());
            }

            log.info("创建训练计划成功: {}", plan.getName());
            return getPlanById(plan.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建训练计划失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新训练计划
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlanResponseDTO updatePlan(Long id, TrainingPlanUpdateDTO updateDTO) {
        try {
            GymTrainingPlan plan = trainingPlanMapper.selectById(id);
            if (plan == null) {
                throw new BusinessException("训练计划不存在");
            }

            if (updateDTO.getName() != null) plan.setName(updateDTO.getName());
            if (updateDTO.getGoal() != null) plan.setGoal(updateDTO.getGoal());
            if (updateDTO.getStartDate() != null) plan.setStartDate(updateDTO.getStartDate());
            if (updateDTO.getEndDate() != null) plan.setEndDate(updateDTO.getEndDate());
            if (updateDTO.getStatus() != null) plan.setStatus(updateDTO.getStatus());
            if (updateDTO.getRemark() != null) plan.setRemark(updateDTO.getRemark());

            trainingPlanMapper.updateById(plan);

            // 更新训练明细
            if (updateDTO.getDetails() != null) {
                // 删除旧明细
                LambdaQueryWrapper<GymTrainingPlanDetail> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(GymTrainingPlanDetail::getPlanId, id);
                trainingPlanDetailMapper.delete(deleteWrapper);
                
                // 保存新明细
                saveDetails(id, updateDTO.getDetails());
            }

            log.info("更新训练计划成功: {}", plan.getName());
            return getPlanById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新训练计划失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除训练计划
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        try {
            GymTrainingPlan plan = trainingPlanMapper.selectById(id);
            if (plan == null) {
                throw new BusinessException("训练计划不存在");
            }
            
            // 删除明细
            LambdaQueryWrapper<GymTrainingPlanDetail> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(GymTrainingPlanDetail::getPlanId, id);
            trainingPlanDetailMapper.delete(deleteWrapper);
            
            // 删除计划
            trainingPlanMapper.deleteById(id);
            log.info("删除训练计划成功: {}", plan.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除训练计划失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取训练计划详情
     */
    public TrainingPlanResponseDTO getPlanById(Long id) {
        GymTrainingPlan plan = trainingPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("训练计划不存在");
        }
        return entityToResponseDTO(plan);
    }

    /**
     * 分页查询训练计划
     */
    public Page<TrainingPlanResponseDTO> getPlanPage(int currentPage, int pageSize, 
            Long userId, Long coachId, String goal, Integer status) {
        Page<GymTrainingPlan> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymTrainingPlan> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(GymTrainingPlan::getUserId, userId);
        }
        if (coachId != null) {
            queryWrapper.eq(GymTrainingPlan::getCoachId, coachId);
        }
        if (goal != null && !goal.trim().isEmpty()) {
            queryWrapper.eq(GymTrainingPlan::getGoal, goal);
        }
        if (status != null) {
            queryWrapper.eq(GymTrainingPlan::getStatus, status);
        }
        queryWrapper.orderByDesc(GymTrainingPlan::getCreateTime);

        Page<GymTrainingPlan> planPage = trainingPlanMapper.selectPage(page, queryWrapper);
        
        Page<TrainingPlanResponseDTO> resultPage = new Page<>(planPage.getCurrent(), planPage.getSize(), planPage.getTotal());
        List<TrainingPlanResponseDTO> records = planPage.getRecords().stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 更新训练明细完成状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDetailCompletion(Long detailId, Integer isCompleted, Integer actualSets, Integer actualReps, String executionNote) {
        try {
            GymTrainingPlanDetail detail = trainingPlanDetailMapper.selectById(detailId);
            if (detail == null) {
                throw new BusinessException("训练明细不存在");
            }

            detail.setIsCompleted(isCompleted);
            if (isCompleted != null && isCompleted == 1) {
                detail.setCompleteTime(LocalDateTime.now());
            }
            if (actualSets != null) {
                detail.setActualSets(actualSets);
            }
            if (actualReps != null) {
                detail.setActualReps(actualReps);
            }
            if (executionNote != null) {
                detail.setExecutionNote(executionNote);
            }

            trainingPlanDetailMapper.updateById(detail);
            log.info("更新训练明细完成状态成功: detailId={}", detailId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新训练明细完成状态失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 添加执行备注
     */
    @Transactional(rollbackFor = Exception.class)
    public void addExecutionNote(Long detailId, String note) {
        try {
            GymTrainingPlanDetail detail = trainingPlanDetailMapper.selectById(detailId);
            if (detail == null) {
                throw new BusinessException("训练明细不存在");
            }

            detail.setExecutionNote(note);
            trainingPlanDetailMapper.updateById(detail);
            log.info("添加执行备注成功: detailId={}", detailId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加执行备注失败", e);
            throw new ServiceException("添加失败");
        }
    }

    /**
     * 获取执行历史（查询用户的所有完成记录）
     */
    public List<TrainingPlanDetailResponseDTO> getExecutionHistory(Long userId, int limit) {
        // 查询用户的所有计划
        LambdaQueryWrapper<GymTrainingPlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(GymTrainingPlan::getUserId, userId);
        List<GymTrainingPlan> plans = trainingPlanMapper.selectList(planWrapper);
        
        if (plans.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> planIds = plans.stream().map(GymTrainingPlan::getId).collect(Collectors.toList());
        
        // 查询所有已完成的训练明细
        LambdaQueryWrapper<GymTrainingPlanDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(GymTrainingPlanDetail::getPlanId, planIds)
                .eq(GymTrainingPlanDetail::getIsCompleted, 1)
                .orderByDesc(GymTrainingPlanDetail::getCompleteTime)
                .last("LIMIT " + limit);
        
        List<GymTrainingPlanDetail> details = trainingPlanDetailMapper.selectList(detailWrapper);
        
        return details.stream()
                .map(this::detailToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 计算计划进度
     */
    public Double calculateProgress(Long planId) {
        LambdaQueryWrapper<GymTrainingPlanDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(GymTrainingPlanDetail::getPlanId, planId);
        List<GymTrainingPlanDetail> details = trainingPlanDetailMapper.selectList(detailWrapper);
        
        if (details.isEmpty()) {
            return 0.0;
        }
        
        long completedCount = details.stream()
                .filter(detail -> Integer.valueOf(1).equals(detail.getIsCompleted()))
                .count();
        
        return (double) completedCount / details.size() * 100;
    }

    /**
     * 保存训练明细
     */
    private void saveDetails(Long planId, List<TrainingPlanDetailDTO> details) {
        for (TrainingPlanDetailDTO detailDTO : details) {
            GymTrainingPlanDetail detail = GymTrainingPlanDetail.builder()
                    .planId(planId)
                    .dayOfWeek(detailDTO.getDayOfWeek())
                    .actionId(detailDTO.getActionId())
                    .sets(detailDTO.getSets())
                    .reps(detailDTO.getReps())
                    .weight(detailDTO.getWeight())
                    .duration(detailDTO.getDuration())
                    .restTime(detailDTO.getRestTime())
                    .sortOrder(detailDTO.getSortOrder() != null ? detailDTO.getSortOrder() : 0)
                    .isCompleted(0)
                    .build();
            trainingPlanDetailMapper.insert(detail);
        }
    }

    /**
     * 实体转响应DTO
     */
    private TrainingPlanResponseDTO entityToResponseDTO(GymTrainingPlan plan) {
        TrainingPlanResponseDTO dto = new TrainingPlanResponseDTO();
        BeanUtils.copyProperties(plan, dto);
        
        // 设置用户信息
        User user = userMapper.selectById(plan.getUserId());
        if (user != null) {
            dto.setUserNickname(user.getNickname());
            dto.setUserAvatar(user.getAvatar());
        }
        
        // 设置教练信息
        if (plan.getCoachId() != null) {
            User coach = userMapper.selectById(plan.getCoachId());
            if (coach != null) {
                dto.setCoachNickname(coach.getNickname());
            }
        }
        
        // 查询明细
        LambdaQueryWrapper<GymTrainingPlanDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(GymTrainingPlanDetail::getPlanId, plan.getId())
                .orderByAsc(GymTrainingPlanDetail::getDayOfWeek)
                .orderByAsc(GymTrainingPlanDetail::getSortOrder);
        List<GymTrainingPlanDetail> details = trainingPlanDetailMapper.selectList(detailWrapper);
        
        List<TrainingPlanDetailResponseDTO> detailDTOs = details.stream()
                .map(this::detailToResponseDTO)
                .collect(Collectors.toList());
        dto.setDetails(detailDTOs);
        
        return dto;
    }

    /**
     * 明细实体转响应DTO
     */
    private TrainingPlanDetailResponseDTO detailToResponseDTO(GymTrainingPlanDetail detail) {
        TrainingPlanDetailResponseDTO dto = new TrainingPlanDetailResponseDTO();
        BeanUtils.copyProperties(detail, dto);
        
        // 设置动作名称
        if (detail.getActionId() != null) {
            GymTrainingAction action = trainingActionMapper.selectById(detail.getActionId());
            if (action != null) {
                dto.setActionName(action.getName());
            }
        }
        
        return dto;
    }

    // ==================== 训练计划模板相关方法 ====================

    /**
     * 创建训练计划模板（仅教练可用）
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlanTemplateResponseDTO createTemplate(Long coachId, TrainingPlanTemplateCreateDTO createDTO) {
        try {
            // 验证教练存在
            GymCoach coach = null;
            if (coachId != null) {
                User coachUser = userMapper.selectById(coachId);
                if (coachUser == null) {
                    throw new BusinessException("教练不存在");
                }
            }

            GymTrainingPlanTemplate template = GymTrainingPlanTemplate.builder()
                    .coachId(coachId)
                    .name(createDTO.getName())
                    .goal(createDTO.getGoal())
                    .description(createDTO.getDescription())
                    .difficulty(createDTO.getDifficulty())
                    .durationDays(createDTO.getDurationDays() != null ? createDTO.getDurationDays() : 7)
                    .isPublic(0) // 教练创建的模板默认为私有
                    .status(1)
                    .totalExercises(0)
                    .build();

            trainingPlanTemplateMapper.insert(template);

            // 保存模板明细
            if (createDTO.getDetails() != null && !createDTO.getDetails().isEmpty()) {
                saveTemplateDetails(template.getId(), createDTO.getDetails());
                template.setTotalExercises(createDTO.getDetails().size());
                trainingPlanTemplateMapper.updateById(template);
            }

            log.info("创建训练计划模板成功: {}", template.getName());
            return getTemplateById(template.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建训练计划模板失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新训练计划模板（仅教练可用）
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlanTemplateResponseDTO updateTemplate(Long templateId, TrainingPlanTemplateUpdateDTO updateDTO) {
        try {
            GymTrainingPlanTemplate template = trainingPlanTemplateMapper.selectById(templateId);
            if (template == null) {
                throw new BusinessException("训练计划模板不存在");
            }

            if (updateDTO.getName() != null) template.setName(updateDTO.getName());
            if (updateDTO.getGoal() != null) template.setGoal(updateDTO.getGoal());
            if (updateDTO.getDescription() != null) template.setDescription(updateDTO.getDescription());
            if (updateDTO.getDifficulty() != null) template.setDifficulty(updateDTO.getDifficulty());
            if (updateDTO.getDurationDays() != null) template.setDurationDays(updateDTO.getDurationDays());
            if (updateDTO.getStatus() != null) template.setStatus(updateDTO.getStatus());

            trainingPlanTemplateMapper.updateById(template);

            // 更新模板明细
            if (updateDTO.getDetails() != null) {
                // 删除旧明细
                LambdaQueryWrapper<GymTrainingPlanTemplateDetail> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(GymTrainingPlanTemplateDetail::getTemplateId, templateId);
                trainingPlanTemplateDetailMapper.delete(deleteWrapper);
                
                // 保存新明细
                saveTemplateDetails(templateId, updateDTO.getDetails());
                template.setTotalExercises(updateDTO.getDetails().size());
                trainingPlanTemplateMapper.updateById(template);
            }

            log.info("更新训练计划模板成功: {}", template.getName());
            return getTemplateById(templateId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新训练计划模板失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除训练计划模板（仅教练可用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long templateId) {
        try {
            GymTrainingPlanTemplate template = trainingPlanTemplateMapper.selectById(templateId);
            if (template == null) {
                throw new BusinessException("训练计划模板不存在");
            }
            
            // 删除明细
            LambdaQueryWrapper<GymTrainingPlanTemplateDetail> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(GymTrainingPlanTemplateDetail::getTemplateId, templateId);
            trainingPlanTemplateDetailMapper.delete(deleteWrapper);
            
            // 删除模板
            trainingPlanTemplateMapper.deleteById(templateId);
            log.info("删除训练计划模板成功: {}", template.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除训练计划模板失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取训练计划模板详情
     */
    public TrainingPlanTemplateResponseDTO getTemplateById(Long templateId) {
        GymTrainingPlanTemplate template = trainingPlanTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException("训练计划模板不存在");
        }
        return templateEntityToResponseDTO(template);
    }

    /**
     * 分页查询训练计划模板
     */
    public Page<TrainingPlanTemplateResponseDTO> getTemplatePage(int currentPage, int pageSize,
            Long coachId, String goal, Integer difficulty, Integer status) {
        Page<GymTrainingPlanTemplate> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymTrainingPlanTemplate> queryWrapper = new LambdaQueryWrapper<>();
        
        // 查询条件：公开模板或者是当前教练的私有模板
        if (coachId != null) {
            queryWrapper.and(w -> w
                    .eq(GymTrainingPlanTemplate::getIsPublic, 1)
                    .or()
                    .eq(GymTrainingPlanTemplate::getCoachId, coachId));
        } else {
            queryWrapper.eq(GymTrainingPlanTemplate::getIsPublic, 1);
        }
        
        if (goal != null && !goal.trim().isEmpty()) {
            queryWrapper.eq(GymTrainingPlanTemplate::getGoal, goal);
        }
        if (difficulty != null) {
            queryWrapper.eq(GymTrainingPlanTemplate::getDifficulty, difficulty);
        }
        if (status != null) {
            queryWrapper.eq(GymTrainingPlanTemplate::getStatus, status);
        }
        queryWrapper.orderByDesc(GymTrainingPlanTemplate::getCreateTime);

        Page<GymTrainingPlanTemplate> templatePage = trainingPlanTemplateMapper.selectPage(page, queryWrapper);
        
        Page<TrainingPlanTemplateResponseDTO> resultPage = new Page<>(templatePage.getCurrent(), templatePage.getSize(), templatePage.getTotal());
        List<TrainingPlanTemplateResponseDTO> records = templatePage.getRecords().stream()
                .map(this::templateEntityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取教练的模板列表
     */
    public Page<TrainingPlanTemplateResponseDTO> getCoachTemplates(Long coachId, int currentPage, int pageSize) {
        Page<GymTrainingPlanTemplate> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymTrainingPlanTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymTrainingPlanTemplate::getCoachId, coachId)
                .orderByDesc(GymTrainingPlanTemplate::getCreateTime);

        Page<GymTrainingPlanTemplate> templatePage = trainingPlanTemplateMapper.selectPage(page, queryWrapper);
        
        Page<TrainingPlanTemplateResponseDTO> resultPage = new Page<>(templatePage.getCurrent(), templatePage.getSize(), templatePage.getTotal());
        List<TrainingPlanTemplateResponseDTO> records = templatePage.getRecords().stream()
                .map(this::templateEntityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 根据模板快速创建训练计划
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlanResponseDTO createPlanFromTemplate(Long userId, Long templateId, String planName) {
        try {
            log.info("开始从模板创建训练计划: userId={}, templateId={}, planName={}", userId, templateId, planName);
            
            // 验证用户存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                log.error("用户不存在: userId={}", userId);
                throw new BusinessException("用户不存在");
            }
            log.info("用户验证通过: {}", user.getNickname());

            // 获取模板
            GymTrainingPlanTemplate template = trainingPlanTemplateMapper.selectById(templateId);
            if (template == null) {
                log.error("训练计划模板不存在: templateId={}", templateId);
                throw new BusinessException("训练计划模板不存在");
            }
            if (!template.isActive()) {
                log.error("训练计划模板已禁用: templateId={}, status={}", templateId, template.getStatus());
                throw new BusinessException("训练计划模板已禁用");
            }
            log.info("模板验证通过: {}, status={}", template.getName(), template.getStatus());

            // 创建计划
            GymTrainingPlan plan = GymTrainingPlan.builder()
                    .userId(userId)
                    .name(planName)
                    .goal(template.getGoal())
                    .startDate(java.time.LocalDate.now())
                    .endDate(java.time.LocalDate.now().plusDays(template.getDurationDays()))
                    .status(1)
                    .version(1)
                    .remark("基于模板：" + template.getName())
                    .build();

            int insertResult = trainingPlanMapper.insert(plan);
            log.info("训练计划插入结果: insertResult={}, planId={}", insertResult, plan.getId());

            // 复制模板明细到计划明细
            LambdaQueryWrapper<GymTrainingPlanTemplateDetail> templateDetailWrapper = new LambdaQueryWrapper<>();
            templateDetailWrapper.eq(GymTrainingPlanTemplateDetail::getTemplateId, templateId)
                    .orderByAsc(GymTrainingPlanTemplateDetail::getDayOfWeek)
                    .orderByAsc(GymTrainingPlanTemplateDetail::getSortOrder);
            List<GymTrainingPlanTemplateDetail> templateDetails = trainingPlanTemplateDetailMapper.selectList(templateDetailWrapper);
            log.info("找到模板明细数量: {}", templateDetails.size());

            for (GymTrainingPlanTemplateDetail templateDetail : templateDetails) {
                log.debug("复制明细: day={}, actionId={}", templateDetail.getDayOfWeek(), templateDetail.getActionId());
                GymTrainingPlanDetail planDetail = GymTrainingPlanDetail.builder()
                        .planId(plan.getId())
                        .dayOfWeek(templateDetail.getDayOfWeek())
                        .actionId(templateDetail.getActionId())
                        .sets(templateDetail.getSets())
                        .reps(templateDetail.getReps())
                        .weight(templateDetail.getWeight())
                        .duration(templateDetail.getDuration())
                        .restTime(templateDetail.getRestTime())
                        .sortOrder(templateDetail.getSortOrder() != null ? templateDetail.getSortOrder() : 0)
                        .isCompleted(0)
                        .build();
                trainingPlanDetailMapper.insert(planDetail);
            }

            log.info("从模板创建训练计划成功: planId={}, planName={}", plan.getId(), plan.getName());
            return getPlanById(plan.getId());
        } catch (BusinessException e) {
            log.error("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("从模板创建训练计划失败: userId={}, templateId={}, planName={}", userId, templateId, planName, e);
            throw new ServiceException("创建失败: " + e.getMessage());
        }
    }

    /**
     * 保存训练计划模板明细
     */
    private void saveTemplateDetails(Long templateId, List<TrainingPlanTemplateDetailDTO> details) {
        for (TrainingPlanTemplateDetailDTO detailDTO : details) {
            GymTrainingPlanTemplateDetail detail = GymTrainingPlanTemplateDetail.builder()
                    .templateId(templateId)
                    .dayOfWeek(detailDTO.getDayOfWeek())
                    .actionId(detailDTO.getActionId())
                    .sets(detailDTO.getSets())
                    .reps(detailDTO.getReps())
                    .weight(detailDTO.getWeight())
                    .duration(detailDTO.getDuration())
                    .restTime(detailDTO.getRestTime())
                    .description(detailDTO.getDescription())
                    .sortOrder(detailDTO.getSortOrder() != null ? detailDTO.getSortOrder() : 0)
                    .build();
            trainingPlanTemplateDetailMapper.insert(detail);
        }
    }

    /**
     * 模板实体转响应DTO
     */
    private TrainingPlanTemplateResponseDTO templateEntityToResponseDTO(GymTrainingPlanTemplate template) {
        TrainingPlanTemplateResponseDTO dto = new TrainingPlanTemplateResponseDTO();
        BeanUtils.copyProperties(template, dto);
        
        // 设置教练信息
        if (template.getCoachId() != null) {
            User coach = userMapper.selectById(template.getCoachId());
            if (coach != null) {
                dto.setCoachNickname(coach.getNickname());
            }
        }
        
        // 查询明细
        LambdaQueryWrapper<GymTrainingPlanTemplateDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(GymTrainingPlanTemplateDetail::getTemplateId, template.getId())
                .orderByAsc(GymTrainingPlanTemplateDetail::getDayOfWeek)
                .orderByAsc(GymTrainingPlanTemplateDetail::getSortOrder);
        List<GymTrainingPlanTemplateDetail> details = trainingPlanTemplateDetailMapper.selectList(detailWrapper);
        
        List<TrainingPlanTemplateDetailResponseDTO> detailDTOs = details.stream()
                .map(this::templateDetailToResponseDTO)
                .collect(Collectors.toList());
        dto.setDetails(detailDTOs);
        
        return dto;
    }

    /**
     * 模板明细实体转响应DTO
     */
    private TrainingPlanTemplateDetailResponseDTO templateDetailToResponseDTO(GymTrainingPlanTemplateDetail detail) {
        TrainingPlanTemplateDetailResponseDTO dto = new TrainingPlanTemplateDetailResponseDTO();
        BeanUtils.copyProperties(detail, dto);
        
        // 设置动作名称
        if (detail.getActionId() != null) {
            GymTrainingAction action = trainingActionMapper.selectById(detail.getActionId());
            if (action != null) {
                dto.setActionName(action.getName());
            }
        }
        
        return dto;
    }}
