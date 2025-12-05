package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CoachCreateDTO;
import org.example.springboot.dto.command.CoachUpdateDTO;
import org.example.springboot.dto.response.CoachResponseDTO;
import org.example.springboot.entity.GymCoach;
import org.example.springboot.entity.User;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.GymCoachMapper;
import org.example.springboot.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教练Service
 * @author system
 */
@Slf4j
@Service
public class CoachService {

    @Resource
    private GymCoachMapper coachMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建教练
     */
    @Transactional(rollbackFor = Exception.class)
    public CoachResponseDTO createCoach(CoachCreateDTO createDTO) {
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(createDTO.getUserId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 检查该用户是否已是教练
            LambdaQueryWrapper<GymCoach> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GymCoach::getUserId, createDTO.getUserId());
            if (coachMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("该用户已是教练");
            }

            GymCoach coach = GymCoach.builder()
                    .userId(createDTO.getUserId())
                    .specialty(createDTO.getSpecialty())
                    .certificate(createDTO.getCertificate())
                    .introduction(createDTO.getIntroduction())
                    .rating(BigDecimal.ZERO)
                    .status(1)
                    .build();

            coachMapper.insert(coach);
            log.info("创建教练成功: userId={}", coach.getUserId());
            return entityToResponseDTO(coach, user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建教练失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新教练
     */
    @Transactional(rollbackFor = Exception.class)
    public CoachResponseDTO updateCoach(Long id, CoachUpdateDTO updateDTO) {
        try {
            GymCoach coach = coachMapper.selectById(id);
            if (coach == null) {
                throw new BusinessException("教练不存在");
            }

            if (updateDTO.getSpecialty() != null) coach.setSpecialty(updateDTO.getSpecialty());
            if (updateDTO.getCertificate() != null) coach.setCertificate(updateDTO.getCertificate());
            if (updateDTO.getIntroduction() != null) coach.setIntroduction(updateDTO.getIntroduction());
            if (updateDTO.getRating() != null) coach.setRating(updateDTO.getRating());
            if (updateDTO.getStatus() != null) coach.setStatus(updateDTO.getStatus());

            coachMapper.updateById(coach);
            log.info("更新教练成功: id={}", id);
            
            User user = userMapper.selectById(coach.getUserId());
            return entityToResponseDTO(coach, user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新教练失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除教练
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoach(Long id) {
        try {
            GymCoach coach = coachMapper.selectById(id);
            if (coach == null) {
                throw new BusinessException("教练不存在");
            }
            coachMapper.deleteById(id);
            log.info("删除教练成功: id={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除教练失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取教练详情
     */
    public CoachResponseDTO getCoachById(Long id) {
        GymCoach coach = coachMapper.selectById(id);
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        User user = userMapper.selectById(coach.getUserId());
        return entityToResponseDTO(coach, user);
    }

    /**
     * 分页查询教练
     */
    public Page<CoachResponseDTO> getCoachPage(int currentPage, int pageSize, 
            String name, String specialty, Integer status) {
        Page<GymCoach> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymCoach> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(GymCoach::getStatus, status);
        }
        if (specialty != null && !specialty.trim().isEmpty()) {
            queryWrapper.like(GymCoach::getSpecialty, specialty);
        }
        queryWrapper.orderByDesc(GymCoach::getCreateTime);

        Page<GymCoach> coachPage = coachMapper.selectPage(page, queryWrapper);
        
        // 获取所有用户信息
        List<Long> userIds = coachPage.getRecords().stream()
                .map(GymCoach::getUserId)
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        Page<CoachResponseDTO> resultPage = new Page<>(coachPage.getCurrent(), coachPage.getSize(), coachPage.getTotal());
        List<CoachResponseDTO> records = coachPage.getRecords().stream()
                .map(coach -> entityToResponseDTO(coach, userMap.get(coach.getUserId())))
                .filter(dto -> {
                    if (name != null && !name.trim().isEmpty()) {
                        return (dto.getNickname() != null && dto.getNickname().contains(name)) ||
                               (dto.getUsername() != null && dto.getUsername().contains(name));
                    }
                    return true;
                })
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取所有在职教练
     */
    public List<CoachResponseDTO> getAllActiveCoaches() {
        LambdaQueryWrapper<GymCoach> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCoach::getStatus, 1)
                .orderByDesc(GymCoach::getRating);
        
        List<GymCoach> coaches = coachMapper.selectList(queryWrapper);
        List<Long> userIds = coaches.stream()
                .map(GymCoach::getUserId)
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        return coaches.stream()
                .map(coach -> entityToResponseDTO(coach, userMap.get(coach.getUserId())))
                .collect(Collectors.toList());
    }

    /**
     * 实体转响应DTO
     */
    private CoachResponseDTO entityToResponseDTO(GymCoach coach, User user) {
        CoachResponseDTO dto = new CoachResponseDTO();
        BeanUtils.copyProperties(coach, dto);
        
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setAvatar(user.getAvatar());
            dto.setPhone(user.getPhone());
        }
        
        dto.setStatusName(Integer.valueOf(1).equals(coach.getStatus()) ? "在职" : "离职");
        return dto;
    }
}
