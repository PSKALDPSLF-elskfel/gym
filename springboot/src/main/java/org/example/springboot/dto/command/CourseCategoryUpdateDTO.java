package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程分类更新DTO
 * @author system
 */
@Data
@Schema(description = "课程分类更新DTO")
public class CourseCategoryUpdateDTO {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
