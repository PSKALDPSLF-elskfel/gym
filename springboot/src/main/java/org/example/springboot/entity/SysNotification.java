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
 * 系统通知实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_notification")
@Schema(description = "系统通知实体类")
public class SysNotification {

    @TableId(type = IdType.AUTO)
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知类型：SYSTEM-系统通知，BOOKING-预约提醒，COURSE-课程相关，EQUIPMENT-器材相关，MEMBERSHIP-会员相关")
    @TableField("notification_type")
    private String notificationType;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "目标用户类型：USER-小程序用户，ADMIN-管理员，COACH-教练，ALL-全部")
    @TableField("target_user_type")
    private String targetUserType;

    @Schema(description = "目标用户ID（NULL表示发送给所有该类型用户）")
    @TableField("target_user_id")
    private Long targetUserId;

    @Schema(description = "关联业务ID（如课程ID、预约ID等）")
    @TableField("related_id")
    private String relatedId;

    @Schema(description = "关联业务类型（COURSE、BOOKING、EQUIPMENT等）")
    @TableField("related_type")
    private String relatedType;

    @Schema(description = "通知图标URL")
    private String icon;

    @Schema(description = "优先级：0-低，1-中，2-高")
    private Integer priority;

    @Schema(description = "状态：0-已删除，1-已发送，2-草稿")
    private Integer status;

    @Schema(description = "发送时间")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @Schema(description = "定时发送时间（为空时立即发送）")
    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;

    @Schema(description = "是否需要标记已读：0-否，1-是")
    @TableField("is_read_required")
    private Integer isReadRequired;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否为草稿
     */
    public boolean isDraft() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否已发送
     */
    public boolean isSent() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否已删除
     */
    public boolean isDeleted() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否为群发通知（targetUserId为null）
     */
    public boolean isBroadcast() {
        return this.targetUserId == null;
    }
}
