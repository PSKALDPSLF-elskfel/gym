package org.example.springboot.service.convert;

import org.example.springboot.dto.command.CourseCreateDTO;
import org.example.springboot.dto.command.CourseUpdateDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.entity.GymCourse;

import java.time.LocalDateTime;

/**
 * 课程转换类
 * @author system
 */
public class CourseConvert {

    /**
     * 创建命令DTO转换为实体
     * @param createDTO 创建命令DTO
     * @return GymCourse实体
     */
    public static GymCourse createCommandToEntity(CourseCreateDTO createDTO) {
        return GymCourse.builder()
                .id(createDTO.getId())
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .coverImage(createDTO.getCoverImage())
                .duration(createDTO.getDuration())
                .maxParticipants(createDTO.getMaxParticipants())
                .price(createDTO.getPrice())
                .status(createDTO.getStatus())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为实体
     * @param updateDTO 更新命令DTO
     * @return GymCourse实体
     */
    public static GymCourse updateCommandToEntity(CourseUpdateDTO updateDTO) {
        return GymCourse.builder()
                .id(updateDTO.getId())
                .name(updateDTO.getName())
                .description(updateDTO.getDescription())
                .coverImage(updateDTO.getCoverImage())
                .duration(updateDTO.getDuration())
                .maxParticipants(updateDTO.getMaxParticipants())
                .price(updateDTO.getPrice())
                .status(updateDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 实体转换为响应DTO
     * @param entity GymCourse实体
     * @return 课程响应DTO
     */
    public static CourseResponseDTO entityToResponse(GymCourse entity) {
        if (entity == null) {
            return null;
        }
        
        return CourseResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .coverImage(entity.getCoverImage())
                .duration(entity.getDuration())
                .maxParticipants(entity.getMaxParticipants())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatusDisplayName())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
