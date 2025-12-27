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
 * 训练计划模板明细实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_training_plan_template_detail")
@Schema(description = "训练计划模板明细实体")
public class GymTrainingPlanTemplateDetail {

    @TableId(type = IdType.AUTO)
    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "模板ID")
    @TableField("template_id")
    private Long templateId;

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

    @Schema(description = "说明")
    private String description;

    @Schema(description = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
