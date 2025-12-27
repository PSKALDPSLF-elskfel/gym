package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 运动详情DTO
 * @author system
 */
@Data
@Schema(description = "运动详情DTO")
public class WorkoutDetailDTO {

    @Schema(description = "动作名称", example = "杠铃深蹲")
    private String actionName;

    @Schema(description = "组数", example = "4")
    private Integer sets;

    @Schema(description = "每组次数(或时长秒数)", example = "12")
    private Integer reps;

    @Schema(description = "重量(kg)", example = "80.5")
    private BigDecimal weight;

    @Schema(description = "组间休息时间(秒)", example = "90")
    private Integer restTime;

    @Schema(description = "实际完成组数", example = "4")
    private Integer actualSets;

    @Schema(description = "实际完成次数", example = "10")
    private Integer actualReps;

    @Schema(description = "备注", example = "感觉良好")
    private String note;

    @Schema(description = "排序(动作顺序)", example = "1")
    private Integer sortOrder;
}
