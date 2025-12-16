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
 * 训练计划明细实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_training_plan_detail")
@Schema(description = "训练计划明细实体")
public class GymTrainingPlanDetail {

    @TableId(type = IdType.AUTO)
    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "计划ID")
    @TableField("plan_id")
    private Long planId;

    @Schema(description = "星期几(1-7)")
    @TableField("day_of_week")
    private Integer dayOfWeek;

    @Schema(description = "动作ID")
    @TableField("action_id")
    private Long actionId;

    @Schema(description = "组数")
    private Integer sets;

    @Schema(description = "次数")
    private Integer reps;

    @Schema(description = "重量(kg)")
    private BigDecimal weight;

    @Schema(description = "时长(分钟)")
    private Integer duration;

    @Schema(description = "组间休息(秒)")
    @TableField("rest_time")
    private Integer restTime;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "是否完成：0-未完成，1-已完成")
    @TableField("is_completed")
    private Integer isCompleted;

    @Schema(description = "完成时间")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    @Schema(description = "执行备注")
    @TableField("execution_note")
    private String executionNote;

    @Schema(description = "实际完成组数")
    @TableField("actual_sets")
    private Integer actualSets;

    @Schema(description = "实际完成次数")
    @TableField("actual_reps")
    private Integer actualReps;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return Integer.valueOf(1).equals(this.isCompleted);
    }
}
