package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置创建DTO
 * @author system
 */
@Data
@Schema(description = "系统配置创建DTO")
public class SysConfigCreateDTO {

    @NotBlank(message = "配置键不能为空")
    @Schema(description = "配置键")
    private String configKey;

    @NotBlank(message = "配置值不能为空")
    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    private String description;

    @Schema(description = "配置分组")
    private String configGroup;
}
