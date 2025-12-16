package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 运动类型响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "运动类型响应DTO")
public class WorkoutTypeResponseDTO {

    @Schema(description = "运动类型ID")
    private Long id;

    @Schema(description = "运动类型名称")
    private String name;

    @Schema(description = "运动大类")
    private String category;

    @Schema(description = "图标URL")
    private String icon;

    @Schema(description = "MET代谢当量值")
    private BigDecimal metValue;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;
}
