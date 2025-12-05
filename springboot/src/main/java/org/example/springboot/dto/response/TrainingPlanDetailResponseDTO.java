package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 训练计划明细响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "训练计划明细响应DTO")
public class TrainingPlanDetailResponseDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "计划ID")
    private Long planId;

    @Schema(description = "星期几(1-7)")
    private Integer dayOfWeek;

    @Schema(description = "动作ID")
    private Long actionId;

    @Schema(description = "动作名称")
    private String actionName;

    @Schema(description = "组数")
    private Integer sets;

    @Schema(description = "次数")
    private Integer reps;

    @Schema(description = "重量(kg)")
    private BigDecimal weight;

    @Schema(description = "时长(分钟)")
    private Integer duration;

    @Schema(description = "组间休息(秒)")
    private Integer restTime;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否完成")
    private Integer isCompleted;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;
}
