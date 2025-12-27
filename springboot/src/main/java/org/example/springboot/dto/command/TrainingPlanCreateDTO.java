package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 训练计划创建DTO
 * @author system
 */
@Data
@Schema(description = "训练计划创建DTO")
public class TrainingPlanCreateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "教练ID")
    private Long coachId;

    @NotBlank(message = "计划名称不能为空")
    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "训练目标(减脂/增肌/塑形/康复)")
    private String goal;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "训练明细列表")
    private List<TrainingPlanDetailDTO> details;
}
