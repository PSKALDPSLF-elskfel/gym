package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程创建命令DTO
 * @author system
 */
@Data
@Schema(description = "课程创建命令DTO")
public class CourseCreateDTO {

    @Schema(description = "课程ID（UUID，前端生成）")
    @NotBlank(message = "课程ID不能为空")
    @Size(max = 36, message = "课程ID长度不能超过36个字符")
    private String id;

    @Schema(description = "课程名称")
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    private String name;

    @Schema(description = "课程分类ID")
    private Long categoryId;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "课程时长（分钟）")
    @NotNull(message = "课程时长不能为空")
    @Min(value = 1, message = "课程时长至少为1分钟")
    @Max(value = 1440, message = "课程时长不能超过1440分钟")
    private Integer duration;

    @Schema(description = "最大参与人数")
    @NotNull(message = "最大参与人数不能为空")
    @Min(value = 1, message = "最大参与人数至少为1人")
    @Max(value = 1000, message = "最大参与人数不能超过1000人")
    private Integer maxParticipants;

    @Schema(description = "课程价格")
    @NotNull(message = "课程价格不能为空")
    @DecimalMin(value = "0.00", message = "课程价格不能为负数")
    @DecimalMax(value = "99999.99", message = "课程价格不能超过99999.99")
    private BigDecimal price;

    @Schema(description = "状态：0-下架，1-上架")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
}
