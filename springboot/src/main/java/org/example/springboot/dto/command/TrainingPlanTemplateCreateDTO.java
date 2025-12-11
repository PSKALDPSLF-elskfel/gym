package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 训练计划模板创建DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "训练计划模板创建DTO")
public class TrainingPlanTemplateCreateDTO {

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "训练目标(减脂/增肌/塑形/康复)")
    private String goal;

    @Schema(description = "模板说明")
    private String description;

    @NotNull(message = "难度不能为空")
    @Schema(description = "难度：1-初级，2-中级，3-高级")
    private Integer difficulty;

    @Schema(description = "建议周期(天)")
    private Integer durationDays;

    @Schema(description = "模板明细")
    @Valid
    private List<TrainingPlanTemplateDetailDTO> details;
}
