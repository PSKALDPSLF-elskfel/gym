package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 训练动作更新DTO
 * @author system
 */
@Data
@Schema(description = "训练动作更新DTO")
public class TrainingActionUpdateDTO {

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

    @Schema(description = "难度：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @Schema(description = "目标肌群")
    private String targetMuscle;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
