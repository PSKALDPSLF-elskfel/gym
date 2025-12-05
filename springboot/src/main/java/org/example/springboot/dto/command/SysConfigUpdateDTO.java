package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置更新DTO
 * @author system
 */
@Data
@Schema(description = "系统配置更新DTO")
public class SysConfigUpdateDTO {

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    private String description;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
