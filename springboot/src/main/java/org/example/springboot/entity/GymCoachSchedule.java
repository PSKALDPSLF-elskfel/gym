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
import java.time.LocalTime;

/**
 * 教练排班表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_coach_schedule")
@Schema(description = "教练排班实体")
public class GymCoachSchedule {

    @TableId(type = IdType.AUTO)
    @Schema(description = "排班ID")
    private Long id;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "工作日期")
    @TableField("work_date")
    private LocalDate workDate;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalTime endTime;

    @Schema(description = "工作类型：NORMAL-正常排班,OVERTIME-加班,SHIFT-轮班,HOLIDAY-休息")
    @TableField("work_type")
    private String workType;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "状态：0-已取消,1-正常,2-已完成")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建者ID(管理员)")
    @TableField("create_by")
    private Long createBy;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否正常状态
     */
    public boolean isNormal() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return Integer.valueOf(2).equals(this.status);
    }
}
