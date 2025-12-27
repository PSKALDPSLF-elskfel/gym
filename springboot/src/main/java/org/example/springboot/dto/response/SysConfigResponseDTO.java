package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置响应DTO
 * @author system
 */
@Data
@Schema(description = "系统配置响应DTO")
public class SysConfigResponseDTO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    private String description;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "是否系统内置：0-否，1-是")
    private Integer isSystem;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
