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
 * 排班申请表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_schedule_request")
@Schema(description = "排班申请实体")
public class GymScheduleRequest {

    @TableId(type = IdType.AUTO)
    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "申请类型：1-调休(请假),2-加班,3-换班")
    @TableField("request_type")
    private Integer requestType;

    @Schema(description = "目标日期")
    @TableField("target_date")
    private LocalDate targetDate;

    @Schema(description = "申请理由")
    private String reason;

    @Schema(description = "换班对象教练ID(仅当type=3时有效)")
    @TableField("exchange_with_coach_id")
    private Long exchangeWithCoachId;

    @Schema(description = "被交换的排班ID(仅当type=3时有效)")
    @TableField("exchange_schedule_id")
    private Long exchangeScheduleId;

    @Schema(description = "申请状态：0-待审批,1-已通过,2-已拒绝,3-已取消")
    private Integer status;

    @Schema(description = "审批人ID(管理员)")
    @TableField("approver_id")
    private Long approverId;

    @Schema(description = "审批时间")
    @TableField("approve_time")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见")
    @TableField("approve_remark")
    private String approveRemark;

    @Schema(description = "附件地址(如医疗证明)")
    @TableField("attachment_url")
    private String attachmentUrl;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否待审批
     */
    public boolean isPending() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否已通过
     */
    public boolean isApproved() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否已拒绝
     */
    public boolean isRejected() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 是否为调休申请
     */
    public boolean isLeaveRequest() {
        return Integer.valueOf(1).equals(this.requestType);
    }

    /**
     * 是否为加班申请
     */
    public boolean isOvertimeRequest() {
        return Integer.valueOf(2).equals(this.requestType);
    }

    /**
     * 是否为换班申请
     */
    public boolean isShiftSwapRequest() {
        return Integer.valueOf(3).equals(this.requestType);
    }
}
