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

import java.time.LocalDateTime;

/**
 * 课程签到实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_course_sign_in")
@Schema(description = "课程签到实体类")
public class GymCourseSignIn {

    @TableId(type = IdType.AUTO)
    @Schema(description = "签到ID")
    private Long id;

    @Schema(description = "预约ID")
    @TableField("booking_id")
    private Long bookingId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "排课ID")
    @TableField("schedule_id")
    private Long scheduleId;

    @Schema(description = "签到时间")
    @TableField("sign_in_time")
    private LocalDateTime signInTime;

    @Schema(description = "签到方式：1-扫码，2-手动")
    @TableField("sign_in_type")
    private Integer signInType;

    @Schema(description = "操作人ID(教练/管理员)")
    @TableField("operator_id")
    private Long operatorId;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否为扫码签到
     */
    public boolean isQrCodeSignIn() {
        return signInType != null && signInType == 1;
    }

    /**
     * 是否为手动签到
     */
    public boolean isManualSignIn() {
        return signInType != null && signInType == 2;
    }

    /**
     * 获取签到方式显示名称
     */
    public String getSignInTypeDisplayName() {
        if (signInType == null) {
            return "未知";
        }
        switch (signInType) {
            case 1:
                return "扫码签到";
            case 2:
                return "手动签到";
            default:
                return "未知";
        }
    }
}
