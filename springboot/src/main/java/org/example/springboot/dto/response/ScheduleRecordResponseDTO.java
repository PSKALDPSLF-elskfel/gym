package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 排班打卡记录响应DTO
 * @author system
 */
@Data
@Schema(description = "排班打卡记录响应DTO")
public class ScheduleRecordResponseDTO {

    @Schema(description = "打卡ID")
    private Long id;

    @Schema(description = "排班ID")
    private Long scheduleId;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

    @Schema(description = "打卡入场时间")
    private LocalDateTime checkInTime;

    @Schema(description = "打卡离场时间")
    private LocalDateTime checkOutTime;

    @Schema(description = "入场地点")
    private String checkInLocation;

    @Schema(description = "离场地点")
    private String checkOutLocation;

    @Schema(description = "打卡状态：0-未打卡,1-已打卡入场,2-已打卡离场,3-迟到,4-早退,5-缺勤")
    private Integer status;

    @Schema(description = "出勤评分(0-100)")
    private Integer attendanceScore;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
