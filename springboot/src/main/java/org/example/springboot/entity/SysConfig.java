package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_config")
@Schema(description = "系统配置实体")
public class SysConfig {

    @TableId(type = IdType.AUTO)
    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键")
    @TableField("config_key")
    private String configKey;

    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;

    @Schema(description = "配置描述")
    private String description;

    @Schema(description = "配置分组")
    @TableField("config_group")
    private String configGroup;

    @Schema(description = "是否系统内置：0-否，1-是")
    @TableField("is_system")
    private Integer isSystem;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否系统内置
     */
    public boolean isSystemConfig() {
        return Integer.valueOf(1).equals(this.isSystem);
    }
}
