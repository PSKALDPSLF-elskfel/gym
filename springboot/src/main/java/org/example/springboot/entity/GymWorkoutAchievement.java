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
 * 运动成就实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_workout_achievement")
@Schema(description = "运动成就实体类")
public class GymWorkoutAchievement {

    @TableId(type = IdType.AUTO)
    @Schema(description = "成就ID")
    private Long id;

    @Schema(description = "成就名称")
    private String name;

    @Schema(description = "成就描述")
    private String description;

    @Schema(description = "成就图标")
    private String icon;

    @Schema(description = "成就类型:DURATION-时长,CALORIES-热量,DISTANCE-距离,FREQUENCY-频率,STREAK-连续")
    private String type;

    @Schema(description = "达成条件值")
    @TableField("condition_value")
    private Integer conditionValue;

    @Schema(description = "条件单位")
    @TableField("condition_unit")
    private String conditionUnit;

    @Schema(description = "获得积分")
    private Integer points;

    @Schema(description = "成就等级:1-青铜,2-白银,3-黄金,4-钻石")
    private Integer level;

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
}
