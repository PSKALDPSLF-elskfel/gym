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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 训练计划实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_training_plan")
@Schema(description = "训练计划实体")
public class GymTrainingPlan {

    @TableId(type = IdType.AUTO)
    @Schema(description = "计划ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "训练目标(减脂/增肌/塑形/康复)")
    private String goal;

    @Schema(description = "开始日期")
    @TableField("start_date")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @TableField("end_date")
    private LocalDate endDate;

    @Schema(description = "状态：0-已结束，1-进行中")
    private Integer status;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否进行中
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }
}
