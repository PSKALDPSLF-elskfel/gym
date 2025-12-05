package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程响应DTO")
public class CourseResponseDTO {

    @Schema(description = "课程ID")
    private String id;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "课程时长（分钟）")
    private Integer duration;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "状态显示名称")
    private String statusDisplayName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
