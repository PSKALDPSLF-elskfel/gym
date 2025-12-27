package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 更新排班命令DTO
 * @author system
 */
@Data
@Schema(description = "更新排班命令DTO")
public class ScheduleUpdateDTO {

    @Schema(description = "工作日期")
    private LocalDate workDate;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "工作类型：NORMAL-正常排班,OVERTIME-加班,SHIFT-轮班,HOLIDAY-休息")
    private String workType;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "状态：0-已取消,1-正常,2-已完成")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
