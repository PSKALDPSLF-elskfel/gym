package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 运动详情响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "运动详情响应DTO")
public class WorkoutDetailResponseDTO {

    @Schema(description = "详情ID")
    private Long id;

    @Schema(description = "运动记录ID")
    private Long workoutRecordId;

    @Schema(description = "动作名称")
    private String actionName;

    @Schema(description = "组数")
    private Integer sets;

    @Schema(description = "每组次数")
    private Integer reps;

    @Schema(description = "重量(kg)")
    private BigDecimal weight;

    @Schema(description = "组间休息时间(秒)")
    private Integer restTime;

    @Schema(description = "实际完成组数")
    private Integer actualSets;

    @Schema(description = "实际完成次数")
    private Integer actualReps;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序")
    private Integer sortOrder;
}
