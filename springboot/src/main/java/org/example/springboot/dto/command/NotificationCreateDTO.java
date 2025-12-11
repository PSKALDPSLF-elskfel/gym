package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 创建通知DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建通知DTO")
public class NotificationCreateDTO {

    @NotBlank(message = "通知类型不能为空")
    @Schema(description = "通知类型：SYSTEM-系统通知，BOOKING-预约提醒，COURSE-课程相关，EQUIPMENT-器材相关，MEMBERSHIP-会员相关", required = true)
    private String notificationType;

    @NotBlank(message = "通知标题不能为空")
    @Schema(description = "通知标题", required = true)
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @Schema(description = "通知内容", required = true)
    private String content;

    @Schema(description = "目标用户类型：USER-小程序用户，ADMIN-管理员，COACH-教练，ALL-全部")
    private String targetUserType;

    @Schema(description = "目标用户ID（NULL表示发送给所有该类型用户）")
    private Long targetUserId;

    @Schema(description = "关联业务ID（如课程ID、预约ID等）")
    private String relatedId;

    @Schema(description = "关联业务类型（COURSE、BOOKING、EQUIPMENT等）")
    private String relatedType;

    @Schema(description = "通知图标URL")
    private String icon;

    @Schema(description = "优先级：0-低，1-中，2-高")
    private Integer priority;

    @Schema(description = "定时发送时间（为空时立即发送）")
    private LocalDateTime scheduledTime;

    @Schema(description = "是否需要标记已读：0-否，1-是")
    private Integer isReadRequired;

    @Schema(description = "备注")
    private String remark;
}
