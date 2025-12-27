package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班申请响应DTO
 * @author system
 */
@Data
@Schema(description = "排班申请响应DTO")
public class ScheduleRequestResponseDTO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练姓名")
    private String coachName;

    @Schema(description = "申请类型：1-调休(请假),2-加班,3-换班")
    private Integer requestType;

    @Schema(description = "目标日期")
    private LocalDate targetDate;

    @Schema(description = "申请理由")
    private String reason;

    @Schema(description = "换班对象教练ID(仅当type=3时有效)")
    private Long exchangeWithCoachId;

    @Schema(description = "换班对象教练姓名")
    private String exchangeWithCoachName;

    @Schema(description = "被交换的排班ID(仅当type=3时有效)")
    private Long exchangeScheduleId;

    @Schema(description = "申请状态：0-待审批,1-已通过,2-已拒绝,3-已取消")
    private Integer status;

    @Schema(description = "审批人ID(管理员)")
    private Long approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见")
    private String approveRemark;

    @Schema(description = "附件地址(如医疗证明)")
    private String attachmentUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
