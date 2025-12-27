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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运动类型实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_workout_type")
@Schema(description = "运动类型实体类")
public class GymWorkoutType {

    @TableId(type = IdType.AUTO)
    @Schema(description = "运动类型ID")
    private Long id;

    @Schema(description = "运动类型名称")
    private String name;

    @Schema(description = "运动大类:CARDIO-有氧运动,STRENGTH-力量训练,FLEXIBILITY-柔韧性训练,SPORTS-球类运动,OTHER-其他")
    private String category;

    @Schema(description = "图标URL")
    private String icon;

    @Schema(description = "MET代谢当量值(用于热量计算)")
    @TableField("met_value")
    private BigDecimal metValue;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "状态:0-禁用,1-启用")
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
}
