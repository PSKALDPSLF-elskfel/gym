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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日运动数据统计实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_workout_daily_stats")
@Schema(description = "每日运动数据统计实体类")
public class GymWorkoutDailyStats {

    @TableId(type = IdType.AUTO)
    @Schema(description = "统计ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "统计日期")
    @TableField("stat_date")
    private LocalDate statDate;

    @Schema(description = "总运动时长(分钟)")
    @TableField("total_duration")
    private Integer totalDuration;

    @Schema(description = "总消耗热量(千卡)")
    @TableField("total_calories")
    private Integer totalCalories;

    @Schema(description = "总运动距离(公里)")
    @TableField("total_distance")
    private BigDecimal totalDistance;

    @Schema(description = "总步数")
    @TableField("total_steps")
    private Integer totalSteps;

    @Schema(description = "运动次数")
    @TableField("workout_count")
    private Integer workoutCount;

    @Schema(description = "有氧运动时长(分钟)")
    @TableField("cardio_duration")
    private Integer cardioDuration;

    @Schema(description = "力量训练时长(分钟)")
    @TableField("strength_duration")
    private Integer strengthDuration;

    @Schema(description = "柔韧性训练时长(分钟)")
    @TableField("flexibility_duration")
    private Integer flexibilityDuration;

    @Schema(description = "是否休息日:0-否,1-是")
    @TableField("is_rest_day")
    private Integer isRestDay;

    @Schema(description = "计划完成率(%)")
    @TableField("plan_completion_rate")
    private BigDecimal planCompletionRate;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
