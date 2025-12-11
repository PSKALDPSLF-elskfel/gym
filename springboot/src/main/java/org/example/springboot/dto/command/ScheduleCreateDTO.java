package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 创建排班命令DTO
 * @author system
 */
@Data
@Schema(description = "创建排班命令DTO")
public class ScheduleCreateDTO {

    @Schema(description = "教练ID")
    @NotNull(message = "教练ID不能为空")
    private Long coachId;

    @Schema(description = "工作日期")
    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    @Schema(description = "工作类型：NORMAL-正常排班,OVERTIME-加班,SHIFT-轮班,HOLIDAY-休息")
    private String workType = "NORMAL";

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "备注")
    private String remark;
}
