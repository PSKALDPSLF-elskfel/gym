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
 * 排班规则表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_schedule_rule")
@Schema(description = "排班规则实体")
public class GymScheduleRule {

    @TableId(type = IdType.AUTO)
    @Schema(description = "规则ID")
    private Long id;

    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @Schema(description = "规则类型")
    @TableField("rule_type")
    private String ruleType;

    @Schema(description = "规则值(JSON格式)")
    @TableField("rule_value")
    private String ruleValue;

    @Schema(description = "规则说明")
    private String description;

    @Schema(description = "状态：0-禁用,1-启用")
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
     * 是否禁用
     */
    public boolean isDisabled() {
        return Integer.valueOf(0).equals(this.status);
    }
}
