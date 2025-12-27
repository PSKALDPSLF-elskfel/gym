package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.TrainingActionCreateDTO;
import org.example.springboot.dto.command.TrainingActionUpdateDTO;
import org.example.springboot.dto.response.TrainingActionResponseDTO;
import org.example.springboot.entity.GymTrainingAction;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.GymTrainingActionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 训练动作Service
 * @author system
 */
@Slf4j
@Service
public class TrainingActionService {

    @Resource
    private GymTrainingActionMapper trainingActionMapper;

    /**
     * 创建训练动作
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingActionResponseDTO createAction(TrainingActionCreateDTO createDTO) {
        try {
            GymTrainingAction action = GymTrainingAction.builder()
                    .name(createDTO.getName())
                    .category(createDTO.getCategory())
                    .description(createDTO.getDescription())
                    .videoUrl(createDTO.getVideoUrl())
                    .imageUrl(createDTO.getImageUrl())
                    .difficulty(createDTO.getDifficulty())
                    .targetMuscle(createDTO.getTargetMuscle())
                    .status(1)
                    .build();

            trainingActionMapper.insert(action);
            log.info("创建训练动作成功: {}", action.getName());
            return entityToResponseDTO(action);
        } catch (Exception e) {
            log.error("创建训练动作失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新训练动作
     */
    @Transactional(rollbackFor = Exception.class)
    public TrainingActionResponseDTO updateAction(Long id, TrainingActionUpdateDTO updateDTO) {
        try {
            GymTrainingAction action = trainingActionMapper.selectById(id);
            if (action == null) {
                throw new BusinessException("训练动作不存在");
            }

            if (updateDTO.getName() != null) action.setName(updateDTO.getName());
            if (updateDTO.getCategory() != null) action.setCategory(updateDTO.getCategory());
            if (updateDTO.getDescription() != null) action.setDescription(updateDTO.getDescription());
            if (updateDTO.getVideoUrl() != null) action.setVideoUrl(updateDTO.getVideoUrl());
            if (updateDTO.getImageUrl() != null) action.setImageUrl(updateDTO.getImageUrl());
            if (updateDTO.getDifficulty() != null) action.setDifficulty(updateDTO.getDifficulty());
            if (updateDTO.getTargetMuscle() != null) action.setTargetMuscle(updateDTO.getTargetMuscle());
            if (updateDTO.getStatus() != null) action.setStatus(updateDTO.getStatus());

            trainingActionMapper.updateById(action);
            log.info("更新训练动作成功: {}", action.getName());
            return entityToResponseDTO(action);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新训练动作失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除训练动作
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAction(Long id) {
        try {
            GymTrainingAction action = trainingActionMapper.selectById(id);
            if (action == null) {
                throw new BusinessException("训练动作不存在");
            }
            trainingActionMapper.deleteById(id);
            log.info("删除训练动作成功: {}", action.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除训练动作失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取训练动作详情
     */
    public TrainingActionResponseDTO getActionById(Long id) {
        GymTrainingAction action = trainingActionMapper.selectById(id);
        if (action == null) {
            throw new BusinessException("训练动作不存在");
        }
        return entityToResponseDTO(action);
    }

    /**
     * 分页查询训练动作
     */
    public Page<TrainingActionResponseDTO> getActionPage(int currentPage, int pageSize, 
            String name, String category, Integer difficulty, Integer status) {
        Page<GymTrainingAction> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymTrainingAction> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(GymTrainingAction::getName, name);
        }
        if (category != null && !category.trim().isEmpty()) {
            queryWrapper.eq(GymTrainingAction::getCategory, category);
        }
        if (difficulty != null) {
            queryWrapper.eq(GymTrainingAction::getDifficulty, difficulty);
        }
        if (status != null) {
            queryWrapper.eq(GymTrainingAction::getStatus, status);
        }
        queryWrapper.orderByDesc(GymTrainingAction::getCreateTime);

        Page<GymTrainingAction> actionPage = trainingActionMapper.selectPage(page, queryWrapper);
        
        Page<TrainingActionResponseDTO> resultPage = new Page<>(actionPage.getCurrent(), actionPage.getSize(), actionPage.getTotal());
        List<TrainingActionResponseDTO> records = actionPage.getRecords().stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取所有启用的训练动作
     */
    public List<TrainingActionResponseDTO> getAllEnabledActions() {
        LambdaQueryWrapper<GymTrainingAction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymTrainingAction::getStatus, 1)
                .orderByAsc(GymTrainingAction::getCategory)
                .orderByAsc(GymTrainingAction::getDifficulty);
        return trainingActionMapper.selectList(queryWrapper).stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转响应DTO
     */
    private TrainingActionResponseDTO entityToResponseDTO(GymTrainingAction action) {
        TrainingActionResponseDTO dto = new TrainingActionResponseDTO();
        BeanUtils.copyProperties(action, dto);
        dto.setDifficultyName(action.getDifficultyDisplayName());
        return dto;
    }
}
