package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 训练动作响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "训练动作响应DTO")
public class TrainingActionResponseDTO {

    @Schema(description = "动作ID")
    private Long id;

    @Schema(description = "动作名称")
    private String name;

    @Schema(description = "动作分类")
    private String category;

    @Schema(description = "动作描述")
    private String description;

    @Schema(description = "示范视频URL")
    private String videoUrl;

    @Schema(description = "示范图片URL")
    private String imageUrl;

    @Schema(description = "难度：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @Schema(description = "难度显示名称")
    private String difficultyName;

    @Schema(description = "目标肌群")
    private String targetMuscle;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
