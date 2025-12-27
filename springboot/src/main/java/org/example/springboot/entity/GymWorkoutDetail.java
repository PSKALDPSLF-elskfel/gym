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
 * 运动详情实体类(力量训练动作明细)
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_workout_detail")
@Schema(description = "运动详情实体类")
public class GymWorkoutDetail {

    @TableId(type = IdType.AUTO)
    @Schema(description = "详情ID")
    private Long id;

    @Schema(description = "运动记录ID")
    @TableField("workout_record_id")
    private Long workoutRecordId;

    @Schema(description = "动作名称")
    @TableField("action_name")
    private String actionName;

    @Schema(description = "组数")
    private Integer sets;

    @Schema(description = "每组次数(或时长秒数)")
    private Integer reps;

    @Schema(description = "重量(kg)")
    private BigDecimal weight;

    @Schema(description = "组间休息时间(秒)")
    @TableField("rest_time")
    private Integer restTime;

    @Schema(description = "实际完成组数")
    @TableField("actual_sets")
    private Integer actualSets;

    @Schema(description = "实际完成次数")
    @TableField("actual_reps")
    private Integer actualReps;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "排序(动作顺序)")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
