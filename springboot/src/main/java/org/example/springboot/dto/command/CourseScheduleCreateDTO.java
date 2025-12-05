package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程时间安排创建命令DTO
 * @author system
 */
@Data
@Schema(description = "课程时间安排创建命令DTO")
public class CourseScheduleCreateDTO {

    @Schema(description = "课程ID")
    @NotBlank(message = "课程ID不能为空")
    @Size(max = 36, message = "课程ID长度不能超过36个字符")
    private String courseId;

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @Schema(description = "最大参与人数")
    @NotNull(message = "最大参与人数不能为空")
    @Min(value = 1, message = "最大参与人数至少为1人")
    @Max(value = 1000, message = "最大参与人数不能超过1000人")
    private Integer maxParticipants;

    @Schema(description = "状态：0-取消，1-正常，2-已满")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值必须为0、1或2")
    @Max(value = 2, message = "状态值必须为0、1或2")
    private Integer status;
}
