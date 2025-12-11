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
 * 训练计划模板实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_training_plan_template")
@Schema(description = "训练计划模板实体")
public class GymTrainingPlanTemplate {

    @TableId(type = IdType.AUTO)
    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "教练ID(NULL表示系统模板)")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "训练目标(减脂/增肌/塑形/康复)")
    private String goal;

    @Schema(description = "模板说明")
    private String description;

    @Schema(description = "难度：1-初级，2-中级，3-高级")
    private Integer difficulty;

    @Schema(description = "建议周期(天)")
    @TableField("duration_days")
    private Integer durationDays;

    @Schema(description = "是否公开:0-私有,1-公开(系统模板)")
    @TableField("is_public")
    private Integer isPublic;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "总动作数")
    @TableField("total_exercises")
    private Integer totalExercises;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否为系统模板
     */
    public boolean isSystemTemplate() {
        return this.coachId == null;
    }

    /**
     * 是否为公开模板
     */
    public boolean isPublicTemplate() {
        return Integer.valueOf(1).equals(this.isPublic);
    }

    /**
     * 是否启用
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }
}
