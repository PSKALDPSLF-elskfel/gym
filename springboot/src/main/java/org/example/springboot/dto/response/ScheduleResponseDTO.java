package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 排班信息响应DTO
 * @author system
 */
@Data
@Schema(description = "排班信息响应DTO")
public class ScheduleResponseDTO {

    @Schema(description = "排班ID")
    private Long id;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
