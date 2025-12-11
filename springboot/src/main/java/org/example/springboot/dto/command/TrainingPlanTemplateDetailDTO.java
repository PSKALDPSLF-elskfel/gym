package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 训练计划模板明细DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "训练计划模板明细DTO")
public class TrainingPlanTemplateDetailDTO {

    @Schema(description = "星期几(1-7)")
    private Integer dayOfWeek;

    @Schema(description = "动作ID")
    private Long actionId;

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

    @Schema(description = "说明")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;
}
