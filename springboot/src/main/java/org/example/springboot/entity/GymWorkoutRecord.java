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
 * 运动记录实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_workout_record")
@Schema(description = "运动记录实体类")
public class GymWorkoutRecord {

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "运动类型ID")
    @TableField("workout_type_id")
    private Long workoutTypeId;

    @Schema(description = "运动类型名称(冗余字段)")
    @TableField("workout_type_name")
    private String workoutTypeName;

    @Schema(description = "运动日期")
    @TableField("workout_date")
    private LocalDate workoutDate;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "运动时长(分钟)")
    private Integer duration;

    @Schema(description = "运动强度:LOW-低,MEDIUM-中,HIGH-高")
    private String intensity;

    @Schema(description = "消耗热量(千卡kcal)")
    private Integer calories;

    @Schema(description = "运动距离(公里km)")
    private BigDecimal distance;

    @Schema(description = "步数")
    private Integer steps;

    @Schema(description = "平均心率(次/分钟)")
    @TableField("heart_rate_avg")
    private Integer heartRateAvg;

    @Schema(description = "最大心率(次/分钟)")
    @TableField("heart_rate_max")
    private Integer heartRateMax;

    @Schema(description = "运动照片(JSON数组)")
    private String images;

    @Schema(description = "运动备注")
    private String note;

    @Schema(description = "运动感受:GREAT-很好,GOOD-良好,NORMAL-一般,TIRED-疲惫,BAD-不适")
    private String feeling;

    @Schema(description = "天气情况")
    private String weather;

    @Schema(description = "运动地点")
    private String location;

    @Schema(description = "是否完成:0-未完成,1-已完成")
    @TableField("is_completed")
    private Integer isCompleted;

    @Schema(description = "关联的训练计划ID")
    @TableField("training_plan_id")
    private Long trainingPlanId;

    @Schema(description = "数据来源:MANUAL-手动录入,PLAN-训练计划,DEVICE-设备同步")
    private String source;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否已完成
     */
    public boolean completed() {
        return Integer.valueOf(1).equals(this.isCompleted);
    }
}
