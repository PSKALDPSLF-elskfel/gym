package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班统计响应DTO
 * @author system
 */
@Data
@Schema(description = "排班统计响应DTO")
public class ScheduleStatisticsResponseDTO {

    @Schema(description = "统计ID")
    private Long id;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

    @Schema(description = "统计日期(月份, 格式: 2025-12-01)")
    private LocalDate statisticsDate;

    @Schema(description = "总工作时数")
    private BigDecimal totalHours;

    @Schema(description = "正常工作时数")
    private BigDecimal normalHours;

    @Schema(description = "加班时数")
    private BigDecimal overtimeHours;

    @Schema(description = "工作天数")
    private Integer workDays;

    @Schema(description = "缺勤天数")
    private Integer absentDays;

    @Schema(description = "迟到天数")
    private Integer lateDays;

    @Schema(description = "请假天数")
    private Integer leaveDays;

    @Schema(description = "平均出勤评分")
    private Integer averageAttendanceScore;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
