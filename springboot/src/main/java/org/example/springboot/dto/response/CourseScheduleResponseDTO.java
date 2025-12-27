package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程时间安排响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程时间安排响应DTO")
public class CourseScheduleResponseDTO {

    @Schema(description = "排课ID")
    private Long id;

    @Schema(description = "课程ID")
    private String courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "当前参与人数")
    private Integer currentParticipants;

    @Schema(description = "剩余名额")
    private Integer remainingSlots;

    @Schema(description = "状态：0-取消，1-正常，2-已满")
    private Integer status;

    @Schema(description = "状态显示名称")
    private String statusDisplayName;

    @Schema(description = "是否可以预约")
    private Boolean canBook;

    @Schema(description = "是否已过期")
    private Boolean isExpired;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
