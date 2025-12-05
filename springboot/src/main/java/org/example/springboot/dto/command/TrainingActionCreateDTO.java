package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 训练动作创建DTO
 * @author system
 */
@Data
@Schema(description = "训练动作创建DTO")
public class TrainingActionCreateDTO {

    @NotBlank(message = "动作名称不能为空")
    @Schema(description = "动作名称")
    private String name;

    @Schema(description = "动作分类(胸/背/腿/肩/臂/腹/核心/全身)")
    private String category;

    @Schema(description = "动作描述")
    private String description;

    @Schema(description = "示范视频URL")
    private String videoUrl;

    @Schema(description = "示范图片URL")
    private String imageUrl;

    @NotNull(message = "难度不能为空")
    @Schema(description = "难度：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @Schema(description = "目标肌群")
    private String targetMuscle;
}
