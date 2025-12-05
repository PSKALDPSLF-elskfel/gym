package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 课程分类创建DTO
 * @author system
 */
@Data
@Schema(description = "课程分类创建DTO")
public class CourseCategoryCreateDTO {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;
}
