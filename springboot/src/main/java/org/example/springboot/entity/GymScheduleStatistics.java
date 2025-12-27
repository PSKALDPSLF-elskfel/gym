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
 * 排班统计表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_schedule_statistics")
@Schema(description = "排班统计实体")
public class GymScheduleStatistics {

    @TableId(type = IdType.AUTO)
    @Schema(description = "统计ID")
    private Long id;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "统计日期(月份, 格式: 2025-12-01)")
    @TableField("statistics_date")
    private LocalDate statisticsDate;

    @Schema(description = "总工作时数")
    @TableField("total_hours")
    private BigDecimal totalHours;

    @Schema(description = "正常工作时数")
    @TableField("normal_hours")
    private BigDecimal normalHours;

    @Schema(description = "加班时数")
    @TableField("overtime_hours")
    private BigDecimal overtimeHours;

    @Schema(description = "工作天数")
    @TableField("work_days")
    private Integer workDays;

    @Schema(description = "缺勤天数")
    @TableField("absent_days")
    private Integer absentDays;

    @Schema(description = "迟到天数")
    @TableField("late_days")
    private Integer lateDays;

    @Schema(description = "请假天数")
    @TableField("leave_days")
    private Integer leaveDays;

    @Schema(description = "平均出勤评分")
    @TableField("average_attendance_score")
    private Integer averageAttendanceScore;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
