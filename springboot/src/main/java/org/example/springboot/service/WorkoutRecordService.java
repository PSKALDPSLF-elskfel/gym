package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.WorkoutDetailDTO;
import org.example.springboot.dto.command.WorkoutRecordCreateDTO;
import org.example.springboot.dto.command.WorkoutRecordUpdateDTO;
import org.example.springboot.dto.response.*;
import org.example.springboot.entity.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运动记录业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class WorkoutRecordService {

    @Resource
    private GymWorkoutRecordMapper workoutRecordMapper;

    @Resource
    private GymWorkoutDetailMapper workoutDetailMapper;

    @Resource
    private GymWorkoutTypeMapper workoutTypeMapper;

    @Resource
    private GymWorkoutDailyStatsMapper dailyStatsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ObjectMapper objectMapper;

    // ==================== 运动记录管理 ====================

    /**
     * 创建运动记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkoutRecord(Long userId, WorkoutRecordCreateDTO createDTO) {
        log.info("用户 {} 开始创建运动记录", userId);

        // 验证userId
        if (userId == null) {
            throw new BusinessException("用户ID不能为空，请重新登录");
        }

        // 验证运动类型是否存在
        GymWorkoutType workoutType = workoutTypeMapper.selectById(createDTO.getWorkoutTypeId());
        if (workoutType == null) {
            throw new BusinessException("运动类型不存在");
        }

        // 构建运动记录实体
        GymWorkoutRecord record = GymWorkoutRecord.builder()
                .userId(userId)
                .workoutTypeId(createDTO.getWorkoutTypeId())
                .workoutTypeName(workoutType.getName())
                .workoutDate(createDTO.getWorkoutDate())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .duration(createDTO.getDuration())
                .intensity(createDTO.getIntensity() != null ? createDTO.getIntensity() : "MEDIUM")
                .calories(createDTO.getCalories() != null ? createDTO.getCalories() : 0)
                .distance(createDTO.getDistance())
                .steps(createDTO.getSteps())
                .heartRateAvg(createDTO.getHeartRateAvg())
                .heartRateMax(createDTO.getHeartRateMax())
                .images(convertImagesToJson(createDTO.getImages()))
                .note(createDTO.getNote())
                .feeling(createDTO.getFeeling())
                .weather(createDTO.getWeather())
                .location(createDTO.getLocation())
                .isCompleted(createDTO.getIsCompleted() != null ? createDTO.getIsCompleted() : 1)
                .trainingPlanId(createDTO.getTrainingPlanId())
                .source(createDTO.getSource() != null ? createDTO.getSource() : "MANUAL")
                .build();

        int result = workoutRecordMapper.insert(record);
        if (result <= 0) {
            throw new BusinessException("创建运动记录失败");
        }

        // 保存运动详情（力量训练）
        if (!CollectionUtils.isEmpty(createDTO.getDetails())) {
            saveWorkoutDetails(record.getId(), createDTO.getDetails());
        }

        log.info("运动记录创建成功，ID: {}", record.getId());
        return record.getId();
    }

    /**
     * 更新运动记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkoutRecord(Long userId, Long recordId, WorkoutRecordUpdateDTO updateDTO) {
        log.info("用户 {} 开始更新运动记录，记录ID: {}", userId, recordId);

        // 检查记录是否存在且属于当前用户
        GymWorkoutRecord record = workoutRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("运动记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此记录");
        }

        // 更新字段
        if (updateDTO.getWorkoutTypeId() != null) {
            GymWorkoutType workoutType = workoutTypeMapper.selectById(updateDTO.getWorkoutTypeId());
            if (workoutType == null) {
                throw new BusinessException("运动类型不存在");
            }
            record.setWorkoutTypeId(updateDTO.getWorkoutTypeId());
            record.setWorkoutTypeName(workoutType.getName());
        }
        if (updateDTO.getWorkoutDate() != null) {
            record.setWorkoutDate(updateDTO.getWorkoutDate());
        }
        if (updateDTO.getStartTime() != null) {
            record.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            record.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getDuration() != null) {
            record.setDuration(updateDTO.getDuration());
        }
        if (updateDTO.getIntensity() != null) {
            record.setIntensity(updateDTO.getIntensity());
        }
        if (updateDTO.getCalories() != null) {
            record.setCalories(updateDTO.getCalories());
        }
        if (updateDTO.getDistance() != null) {
            record.setDistance(updateDTO.getDistance());
        }
        if (updateDTO.getSteps() != null) {
            record.setSteps(updateDTO.getSteps());
        }
        if (updateDTO.getHeartRateAvg() != null) {
            record.setHeartRateAvg(updateDTO.getHeartRateAvg());
        }
        if (updateDTO.getHeartRateMax() != null) {
            record.setHeartRateMax(updateDTO.getHeartRateMax());
        }
        if (updateDTO.getImages() != null) {
            record.setImages(convertImagesToJson(updateDTO.getImages()));
        }
        if (updateDTO.getNote() != null) {
            record.setNote(updateDTO.getNote());
        }
        if (updateDTO.getFeeling() != null) {
            record.setFeeling(updateDTO.getFeeling());
        }
        if (updateDTO.getWeather() != null) {
            record.setWeather(updateDTO.getWeather());
        }
        if (updateDTO.getLocation() != null) {
            record.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getIsCompleted() != null) {
            record.setIsCompleted(updateDTO.getIsCompleted());
        }

        int result = workoutRecordMapper.updateById(record);
        if (result <= 0) {
            throw new BusinessException("更新运动记录失败");
        }

        // 更新运动详情
        if (updateDTO.getDetails() != null) {
            // 删除旧的详情
            LambdaQueryWrapper<GymWorkoutDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GymWorkoutDetail::getWorkoutRecordId, recordId);
            workoutDetailMapper.delete(wrapper);

            // 保存新的详情
            if (!CollectionUtils.isEmpty(updateDTO.getDetails())) {
                saveWorkoutDetails(recordId, updateDTO.getDetails());
            }
        }

        log.info("运动记录更新成功，ID: {}", recordId);
    }

    /**
     * 删除运动记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkoutRecord(Long userId, Long recordId) {
        log.info("用户 {} 开始删除运动记录，记录ID: {}", userId, recordId);

        // 检查记录是否存在且属于当前用户
        GymWorkoutRecord record = workoutRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("运动记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此记录");
        }

        // 删除运动详情
        LambdaQueryWrapper<GymWorkoutDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutDetail::getWorkoutRecordId, recordId);
        workoutDetailMapper.delete(wrapper);

        // 删除记录
        int result = workoutRecordMapper.deleteById(recordId);
        if (result <= 0) {
            throw new BusinessException("删除运动记录失败");
        }

        log.info("运动记录删除成功，ID: {}", recordId);
    }

    /**
     * 查询运动记录详情
     */
    public WorkoutRecordResponseDTO getWorkoutRecordById(Long userId, Long recordId) {
        log.info("用户 {} 查询运动记录详情，记录ID: {}", userId, recordId);

        GymWorkoutRecord record = workoutRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("运动记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此记录");
        }

        return convertToResponseDTO(record, true);
    }

    /**
     * 分页查询用户的运动记录
     */
    public Page<WorkoutRecordResponseDTO> getUserWorkoutRecords(Long userId, Long current, Long size,
                                                                 LocalDate startDate, LocalDate endDate,
                                                                 Long workoutTypeId, String intensity,
                                                                 Integer isCompleted) {
        log.info("分页查询用户运动记录: userId={}, current={}, size={}", userId, current, size);

        Page<GymWorkoutRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<GymWorkoutRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutRecord::getUserId, userId);

        if (startDate != null) {
            wrapper.ge(GymWorkoutRecord::getWorkoutDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(GymWorkoutRecord::getWorkoutDate, endDate);
        }
        if (workoutTypeId != null) {
            wrapper.eq(GymWorkoutRecord::getWorkoutTypeId, workoutTypeId);
        }
        if (StringUtils.hasText(intensity)) {
            wrapper.eq(GymWorkoutRecord::getIntensity, intensity);
        }
        if (isCompleted != null) {
            wrapper.eq(GymWorkoutRecord::getIsCompleted, isCompleted);
        }

        wrapper.orderByDesc(GymWorkoutRecord::getWorkoutDate, GymWorkoutRecord::getStartTime);

        Page<GymWorkoutRecord> recordPage = workoutRecordMapper.selectPage(page, wrapper);

        Page<WorkoutRecordResponseDTO> responsePage = new Page<>();
        BeanUtils.copyProperties(recordPage, responsePage, "records");

        List<WorkoutRecordResponseDTO> responseDTOList = recordPage.getRecords().stream()
                .map(record -> convertToResponseDTO(record, false))
                .collect(Collectors.toList());
        responsePage.setRecords(responseDTOList);

        return responsePage;
    }

    /**
     * 查询运动类型列表
     */
    public List<WorkoutTypeResponseDTO> getWorkoutTypes(String category) {
        log.info("查询运动类型列表，category={}", category);

        LambdaQueryWrapper<GymWorkoutType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutType::getStatus, 1); // 仅查询启用的

        if (StringUtils.hasText(category)) {
            wrapper.eq(GymWorkoutType::getCategory, category);
        }

        wrapper.orderByAsc(GymWorkoutType::getSortOrder, GymWorkoutType::getId);

        List<GymWorkoutType> types = workoutTypeMapper.selectList(wrapper);

        return types.stream()
                .map(this::convertTypeToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 查询用户运动数据统计汇总
     */
    public WorkoutStatisticsSummaryDTO getUserWorkoutStatistics(Long userId) {
        log.info("查询用户运动数据统计，userId={}", userId);

        // 查询所有运动记录
        LambdaQueryWrapper<GymWorkoutRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutRecord::getUserId, userId)
                .eq(GymWorkoutRecord::getIsCompleted, 1);
        List<GymWorkoutRecord> records = workoutRecordMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(records)) {
            return buildEmptyStatistics();
        }

        // 计算总计数据
        int totalWorkouts = records.size();
        int totalDuration = records.stream().mapToInt(GymWorkoutRecord::getDuration).sum();
        int totalCalories = records.stream().mapToInt(GymWorkoutRecord::getCalories).sum();
        BigDecimal totalDistance = records.stream()
                .map(r -> r.getDistance() != null ? r.getDistance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int avgDuration = totalWorkouts > 0 ? totalDuration / totalWorkouts : 0;

        // 计算运动天数
        long workoutDays = records.stream()
                .map(GymWorkoutRecord::getWorkoutDate)
                .distinct()
                .count();

        // 计算连续运动天数
        int streakDays = calculateStreakDays(userId);

        // 计算本周运动次数
        LocalDate weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        int weekWorkouts = (int) records.stream()
                .filter(r -> !r.getWorkoutDate().isBefore(weekStart))
                .count();

        // 计算本月运动次数
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        int monthWorkouts = (int) records.stream()
                .filter(r -> !r.getWorkoutDate().isBefore(monthStart))
                .count();

        // 计算有氧和力量训练占比
        int cardioDuration = 0;
        int strengthDuration = 0;
        for (GymWorkoutRecord record : records) {
            GymWorkoutType type = workoutTypeMapper.selectById(record.getWorkoutTypeId());
            if (type != null) {
                if ("CARDIO".equals(type.getCategory())) {
                    cardioDuration += record.getDuration();
                } else if ("STRENGTH".equals(type.getCategory())) {
                    strengthDuration += record.getDuration();
                }
            }
        }

        BigDecimal cardioPercentage = totalDuration > 0 
                ? BigDecimal.valueOf(cardioDuration * 100.0 / totalDuration).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal strengthPercentage = totalDuration > 0
                ? BigDecimal.valueOf(strengthDuration * 100.0 / totalDuration).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return WorkoutStatisticsSummaryDTO.builder()
                .totalWorkouts(totalWorkouts)
                .totalDuration(totalDuration)
                .totalCalories(totalCalories)
                .totalDistance(totalDistance)
                .avgDuration(avgDuration)
                .workoutDays((int) workoutDays)
                .streakDays(streakDays)
                .weekWorkouts(weekWorkouts)
                .monthWorkouts(monthWorkouts)
                .cardioPercentage(cardioPercentage)
                .strengthPercentage(strengthPercentage)
                .build();
    }

    /**
     * 查询每日统计数据
     */
    public List<WorkoutDailyStatsResponseDTO> getDailyStats(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("查询每日统计数据: userId={}, startDate={}, endDate={}", userId, startDate, endDate);

        LambdaQueryWrapper<GymWorkoutDailyStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutDailyStats::getUserId, userId);

        if (startDate != null) {
            wrapper.ge(GymWorkoutDailyStats::getStatDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(GymWorkoutDailyStats::getStatDate, endDate);
        }

        wrapper.orderByAsc(GymWorkoutDailyStats::getStatDate);

        List<GymWorkoutDailyStats> statsList = dailyStatsMapper.selectList(wrapper);

        return statsList.stream()
                .map(this::convertStatsToResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 保存运动详情
     */
    private void saveWorkoutDetails(Long recordId, List<WorkoutDetailDTO> detailDTOs) {
        for (int i = 0; i < detailDTOs.size(); i++) {
            WorkoutDetailDTO detailDTO = detailDTOs.get(i);
            GymWorkoutDetail detail = GymWorkoutDetail.builder()
                    .workoutRecordId(recordId)
                    .actionName(detailDTO.getActionName())
                    .sets(detailDTO.getSets())
                    .reps(detailDTO.getReps())
                    .weight(detailDTO.getWeight())
                    .restTime(detailDTO.getRestTime())
                    .actualSets(detailDTO.getActualSets())
                    .actualReps(detailDTO.getActualReps())
                    .note(detailDTO.getNote())
                    .sortOrder(detailDTO.getSortOrder() != null ? detailDTO.getSortOrder() : i + 1)
                    .build();
            workoutDetailMapper.insert(detail);
        }
    }

    /**
     * 计算连续运动天数
     */
    private int calculateStreakDays(Long userId) {
        LambdaQueryWrapper<GymWorkoutRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymWorkoutRecord::getUserId, userId)
                .eq(GymWorkoutRecord::getIsCompleted, 1)
                .orderByDesc(GymWorkoutRecord::getWorkoutDate);

        List<GymWorkoutRecord> records = workoutRecordMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(records)) {
            return 0;
        }

        // 获取不重复的日期列表
        List<LocalDate> dates = records.stream()
                .map(GymWorkoutRecord::getWorkoutDate)
                .distinct()
                .sorted((a, b) -> b.compareTo(a)) // 降序排列
                .collect(Collectors.toList());

        int streak = 1;
        for (int i = 0; i < dates.size() - 1; i++) {
            LocalDate current = dates.get(i);
            LocalDate next = dates.get(i + 1);
            
            // 如果两个日期相差1天，则连续
            if (current.minusDays(1).equals(next)) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * 转换图片列表为JSON字符串
     */
    private String convertImagesToJson(List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            log.error("图片列表转JSON失败", e);
            return null;
        }
    }

    /**
     * 从JSON字符串解析图片列表
     */
    private List<String> parseImagesFromJson(String imagesJson) {
        if (!StringUtils.hasText(imagesJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析图片JSON失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 转换为响应DTO
     */
    private WorkoutRecordResponseDTO convertToResponseDTO(GymWorkoutRecord record, boolean includeDetails) {
        WorkoutRecordResponseDTO dto = new WorkoutRecordResponseDTO();
        BeanUtils.copyProperties(record, dto);

        // 获取用户昵称
        User user = userMapper.selectById(record.getUserId());
        if (user != null) {
            dto.setUserNickname(user.getNickname());
        }

        // 获取运动类型分类
        GymWorkoutType workoutType = workoutTypeMapper.selectById(record.getWorkoutTypeId());
        if (workoutType != null) {
            dto.setWorkoutCategory(workoutType.getCategory());
        }

        // 解析图片列表
        dto.setImages(parseImagesFromJson(record.getImages()));

        // 加载运动详情
        if (includeDetails) {
            LambdaQueryWrapper<GymWorkoutDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GymWorkoutDetail::getWorkoutRecordId, record.getId())
                    .orderByAsc(GymWorkoutDetail::getSortOrder);
            List<GymWorkoutDetail> details = workoutDetailMapper.selectList(wrapper);
            List<WorkoutDetailResponseDTO> detailDTOs = details.stream()
                    .map(this::convertDetailToResponseDTO)
                    .collect(Collectors.toList());
            dto.setDetails(detailDTOs);
        }

        return dto;
    }

    /**
     * 转换详情为响应DTO
     */
    private WorkoutDetailResponseDTO convertDetailToResponseDTO(GymWorkoutDetail detail) {
        WorkoutDetailResponseDTO dto = new WorkoutDetailResponseDTO();
        BeanUtils.copyProperties(detail, dto);
        return dto;
    }

    /**
     * 转换运动类型为响应DTO
     */
    private WorkoutTypeResponseDTO convertTypeToResponseDTO(GymWorkoutType type) {
        WorkoutTypeResponseDTO dto = new WorkoutTypeResponseDTO();
        BeanUtils.copyProperties(type, dto);
        return dto;
    }

    /**
     * 转换统计数据为响应DTO
     */
    private WorkoutDailyStatsResponseDTO convertStatsToResponseDTO(GymWorkoutDailyStats stats) {
        WorkoutDailyStatsResponseDTO dto = new WorkoutDailyStatsResponseDTO();
        BeanUtils.copyProperties(stats, dto);
        return dto;
    }

    /**
     * 构建空的统计数据
     */
    private WorkoutStatisticsSummaryDTO buildEmptyStatistics() {
        return WorkoutStatisticsSummaryDTO.builder()
                .totalWorkouts(0)
                .totalDuration(0)
                .totalCalories(0)
                .totalDistance(BigDecimal.ZERO)
                .avgDuration(0)
                .workoutDays(0)
                .streakDays(0)
                .weekWorkouts(0)
                .monthWorkouts(0)
                .cardioPercentage(BigDecimal.ZERO)
                .strengthPercentage(BigDecimal.ZERO)
                .build();
    }
}