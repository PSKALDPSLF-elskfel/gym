package org.example.springboot.service.convert;

import org.example.springboot.dto.command.*;
import org.example.springboot.dto.response.*;
import org.example.springboot.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 排班转换类
 * @author system
 */
public class ScheduleConvert {

    /**
     * 创建命令DTO转换为实体
     * @param createDTO 创建命令DTO
     * @return GymCoachSchedule实体
     */
    public static GymCoachSchedule createCommandToEntity(ScheduleCreateDTO createDTO) {
        return GymCoachSchedule.builder()
                .coachId(createDTO.getCoachId())
                .workDate(createDTO.getWorkDate())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .workType(createDTO.getWorkType())
                .location(createDTO.getLocation())
                .remark(createDTO.getRemark())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为实体
     * @param updateDTO 更新命令DTO
     * @return GymCoachSchedule实体
     */
    public static GymCoachSchedule updateCommandToEntity(ScheduleUpdateDTO updateDTO) {
        GymCoachSchedule entity = new GymCoachSchedule();
        entity.setWorkDate(updateDTO.getWorkDate());
        entity.setStartTime(updateDTO.getStartTime());
        entity.setEndTime(updateDTO.getEndTime());
        entity.setWorkType(updateDTO.getWorkType());
        entity.setLocation(updateDTO.getLocation());
        entity.setStatus(updateDTO.getStatus());
        entity.setRemark(updateDTO.getRemark());
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }

    /**
     * 实体转换为响应DTO
     * @param entity GymCoachSchedule实体
     * @return 排班响应DTO
     */
    public static ScheduleResponseDTO entityToResponse(GymCoachSchedule entity) {
        if (entity == null) {
            return null;
        }
        
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(entity.getId());
        dto.setCoachId(entity.getCoachId());
        dto.setWorkDate(entity.getWorkDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setWorkType(entity.getWorkType());
        dto.setLocation(entity.getLocation());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    /**
     * 实体列表转换为响应DTO列表
     * @param entities GymCoachSchedule实体列表
     * @return 排班响应DTO列表
     */
    public static List<ScheduleResponseDTO> entityListToResponseList(List<GymCoachSchedule> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        
        List<ScheduleResponseDTO> result = new ArrayList<>();
        for (GymCoachSchedule entity : entities) {
            result.add(entityToResponse(entity));
        }
        return result;
    }

    /**
     * 创建排班申请命令DTO转换为实体
     * @param createDTO 创建命令DTO
     * @return GymScheduleRequest实体
     */
    public static GymScheduleRequest createRequestCommandToEntity(ScheduleRequestCreateDTO createDTO) {
        GymScheduleRequest entity = new GymScheduleRequest();
        entity.setRequestType(createDTO.getRequestType());
        entity.setTargetDate(createDTO.getTargetDate());
        entity.setReason(createDTO.getReason());
        entity.setExchangeWithCoachId(createDTO.getExchangeWithCoachId());
        entity.setExchangeScheduleId(createDTO.getExchangeScheduleId());
        entity.setAttachmentUrl(createDTO.getAttachmentUrl());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }

    /**
     * 排班申请实体转换为响应DTO
     * @param entity GymScheduleRequest实体
     * @return 排班申请响应DTO
     */
    public static ScheduleRequestResponseDTO requestEntityToResponse(GymScheduleRequest entity) {
        if (entity == null) {
            return null;
        }
        
        ScheduleRequestResponseDTO dto = new ScheduleRequestResponseDTO();
        dto.setId(entity.getId());
        dto.setCoachId(entity.getCoachId());
        dto.setRequestType(entity.getRequestType());
        dto.setTargetDate(entity.getTargetDate());
        dto.setReason(entity.getReason());
        dto.setExchangeWithCoachId(entity.getExchangeWithCoachId());
        dto.setExchangeScheduleId(entity.getExchangeScheduleId());
        dto.setStatus(entity.getStatus());
        dto.setApproverId(entity.getApproverId());
        dto.setApproveTime(entity.getApproveTime());
        dto.setApproveRemark(entity.getApproveRemark());
        dto.setAttachmentUrl(entity.getAttachmentUrl());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    /**
     * 排班申请实体列表转换为响应DTO列表
     * @param entities GymScheduleRequest实体列表
     * @return 排班申请响应DTO列表
     */
    public static List<ScheduleRequestResponseDTO> requestEntityListToResponseList(List<GymScheduleRequest> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        
        List<ScheduleRequestResponseDTO> result = new ArrayList<>();
        for (GymScheduleRequest entity : entities) {
            result.add(requestEntityToResponse(entity));
        }
        return result;
    }

    /**
     * 打卡记录实体转换为响应DTO
     * @param entity GymScheduleRecord实体
     * @return 打卡记录响应DTO
     */
    public static ScheduleRecordResponseDTO recordEntityToResponse(GymScheduleRecord entity) {
        if (entity == null) {
            return null;
        }
        
        ScheduleRecordResponseDTO dto = new ScheduleRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setScheduleId(entity.getScheduleId());
        dto.setCoachId(entity.getCoachId());
        dto.setCheckInTime(entity.getCheckInTime());
        dto.setCheckOutTime(entity.getCheckOutTime());
        dto.setCheckInLocation(entity.getCheckInLocation());
        dto.setCheckOutLocation(entity.getCheckOutLocation());
        dto.setStatus(entity.getStatus());
        dto.setAttendanceScore(entity.getAttendanceScore());
        dto.setRemark(entity.getRemark());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    /**
     * 打卡记录实体列表转换为响应DTO列表
     * @param entities GymScheduleRecord实体列表
     * @return 打卡记录响应DTO列表
     */
    public static List<ScheduleRecordResponseDTO> recordEntityListToResponseList(List<GymScheduleRecord> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        
        List<ScheduleRecordResponseDTO> result = new ArrayList<>();
        for (GymScheduleRecord entity : entities) {
            result.add(recordEntityToResponse(entity));
        }
        return result;
    }

    /**
     * 统计实体转换为响应DTO
     * @param entity GymScheduleStatistics实体
     * @return 统计响应DTO
     */
    public static ScheduleStatisticsResponseDTO statisticsEntityToResponse(GymScheduleStatistics entity) {
        if (entity == null) {
            return null;
        }
        
        ScheduleStatisticsResponseDTO dto = new ScheduleStatisticsResponseDTO();
        dto.setId(entity.getId());
        dto.setCoachId(entity.getCoachId());
        dto.setStatisticsDate(entity.getStatisticsDate());
        dto.setTotalHours(entity.getTotalHours());
        dto.setNormalHours(entity.getNormalHours());
        dto.setOvertimeHours(entity.getOvertimeHours());
        dto.setWorkDays(entity.getWorkDays());
        dto.setAbsentDays(entity.getAbsentDays());
        dto.setLateDays(entity.getLateDays());
        dto.setLeaveDays(entity.getLeaveDays());
        dto.setAverageAttendanceScore(entity.getAverageAttendanceScore());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}