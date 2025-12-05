package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.TrainingPlanCreateDTO;
import org.example.springboot.dto.command.TrainingPlanDetailDTO;
import org.example.springboot.dto.command.TrainingPlanUpdateDTO;
import org.example.springboot.dto.response.TrainingPlanDetailResponseDTO;
import org.example.springboot.dto.response.TrainingPlanResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
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
}
