package org.example.springboot.service.convert;

import org.example.springboot.dto.command.CourseScheduleCreateDTO;
import org.example.springboot.dto.command.CourseScheduleUpdateDTO;
import org.example.springboot.dto.response.CourseScheduleResponseDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.entity.GymCourseSchedule;

import java.time.LocalDateTime;

/**
 * 课程时间安排转换类
 * @author system
 */
public class CourseScheduleConvert {

    /**
     * 创建命令DTO转换为实体
     * @param createDTO 创建命令DTO
     * @return GymCourseSchedule实体
     */
    public static GymCourseSchedule createCommandToEntity(CourseScheduleCreateDTO createDTO) {
        return GymCourseSchedule.builder()
                .courseId(createDTO.getCourseId())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .maxParticipants(createDTO.getMaxParticipants())
                .currentParticipants(0)
                .status(createDTO.getStatus())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为实体
     * @param updateDTO 更新命令DTO
     * @return GymCourseSchedule实体
     */
    public static GymCourseSchedule updateCommandToEntity(CourseScheduleUpdateDTO updateDTO) {
        return GymCourseSchedule.builder()
                .id(updateDTO.getId())
                .courseId(updateDTO.getCourseId())
                .startTime(updateDTO.getStartTime())
                .endTime(updateDTO.getEndTime())
                .maxParticipants(updateDTO.getMaxParticipants())
                .status(updateDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 实体转换为响应DTO
     * @param entity GymCourseSchedule实体
     * @return 课程时间安排响应DTO
     */
    public static CourseScheduleResponseDTO entityToResponse(GymCourseSchedule entity) {
        if (entity == null) {
            return null;
        }
        
        return CourseScheduleResponseDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .maxParticipants(entity.getMaxParticipants())
                .currentParticipants(entity.getCurrentParticipants())
                .remainingSlots(entity.getMaxParticipants() - entity.getCurrentParticipants())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatusDisplayName())
                .canBook(entity.canBook())
                .isExpired(entity.isExpired())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    /**
     * 实体转换为响应DTO（包含课程信息）
     * @param entity GymCourseSchedule实体
     * @param course GymCourse课程实体
     * @return 课程时间安排响应DTO
     */
    public static CourseScheduleResponseDTO entityToResponse(GymCourseSchedule entity, GymCourse course) {
        if (entity == null) {
            return null;
        }
        
        CourseScheduleResponseDTO dto = entityToResponse(entity);
        if (course != null) {
            dto.setCourseName(course.getName());
        }
        return dto;
    }
}
