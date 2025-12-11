package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练计划模板响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "训练计划模板响应DTO")
public class TrainingPlanTemplateResponseDTO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "教练ID(NULL表示系统模板)")
    private Long coachId;

    @Schema(description = "教练昵称")
    private String coachNickname;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "训练目标")
    private String goal;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "难度：1-初级，2-中级，3-高级")
    private Integer difficulty;

    @Schema(description = "建议周期(天)")
    private Integer durationDays;

    @Schema(description = "是否公开")
    private Integer isPublic;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "总动作数")
    private Integer totalExercises;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "模板明细")
    private List<TrainingPlanTemplateDetailResponseDTO> details;
}
