package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知响应DTO")
public class NotificationResponseDTO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知类型")
    private String notificationType;

    @Schema(description = "通知类型描述")
    private String notificationTypeDesc;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "目标用户类型")
    private String targetUserType;

    @Schema(description = "目标用户ID")
    private Long targetUserId;

    @Schema(description = "关联业务ID")
    private String relatedId;

    @Schema(description = "关联业务类型")
    private String relatedType;

    @Schema(description = "通知图标URL")
    private String icon;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "优先级描述")
    private String priorityDesc;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "定时发送时间")
    private LocalDateTime scheduledTime;

    @Schema(description = "是否需要标记已读")
    private Integer isReadRequired;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否已读（用户查询时）")
    private Boolean isRead;

    @Schema(description = "读取时间（用户查询时）")
    private LocalDateTime readTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
