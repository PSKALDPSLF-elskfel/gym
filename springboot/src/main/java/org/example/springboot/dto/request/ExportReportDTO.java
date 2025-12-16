package org.example.springboot.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 导出报表请求DTO
 */
@Data
@Schema(description = "导出报表请求参数")
public class ExportReportDTO {

    @Schema(description = "报表类型：USER_GROWTH-用户增长,BOOKING_TREND-预约趋势,WORKOUT_STATS-运动统计,REVENUE-收入报表")
    private String reportType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "导出格式：EXCEL, PDF, CSV")
    private String exportFormat = "EXCEL";
}
