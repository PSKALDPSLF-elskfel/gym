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
 * 教练请假表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_coach_leave")
@Schema(description = "教练请假实体")
public class GymCoachLeave {

    @TableId(type = IdType.AUTO)
    @Schema(description = "请假ID")
    private Long id;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "请假类型：ANNUAL-年假,SICK-病假,PERSONAL-事假,MATERNITY-产假,UNPAID-无薪假")
    @TableField("leave_type")
    private String leaveType;

    @Schema(description = "开始日期")
    @TableField("start_date")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @TableField("end_date")
    private LocalDate endDate;

    @Schema(description = "请假天数")
    @TableField("duration_days")
    private Integer durationDays;

    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "申请状态：0-待审批,1-已批准,2-已拒绝")
    private Integer status;

    @Schema(description = "批准人ID")
    @TableField("approver_id")
    private Long approverId;

    @Schema(description = "批准时间")
    @TableField("approve_time")
    private LocalDateTime approveTime;

    @Schema(description = "批准意见")
    @TableField("approve_remark")
    private String approveRemark;

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
     * 是否已批准
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
}
