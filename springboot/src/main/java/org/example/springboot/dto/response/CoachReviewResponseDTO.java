package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教练评价响应DTO
 */
@Data
@Schema(description = "教练评价响应")
public class CoachReviewResponseDTO {
    
    @Schema(description = "评价ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String userNickname;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "教练ID")
    private Long coachId;
    
    @Schema(description = "教练姓名")
    private String coachName;
    
    @Schema(description = "关联训练计划ID")
    private Long planId;
    
    @Schema(description = "训练计划名称")
    private String planName;
    
    @Schema(description = "关联课程预约ID")
    private Long courseBookingId;
    
    @Schema(description = "课程名称")
    private String courseName;
    
    @Schema(description = "评价类型：1-训练计划评价，2-课程评价")
    private Integer reviewType;
    
    @Schema(description = "评价类型描述")
    private String reviewTypeDesc;
    
    @Schema(description = "评分(1-5星)")
    private Integer rating;
    
    @Schema(description = "评价标签列表")
    private List<String> tagList;
    
    @Schema(description = "评价内容")
    private String content;
    
    @Schema(description = "评价图片URL列表")
    private List<String> images;
    
    @Schema(description = "是否匿名：0-否，1-是")
    private Integer isAnonymous;
    
    @Schema(description = "教练回复")
    private String reply;
    
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;
    
    @Schema(description = "是否置顶：0-否，1-是")
    private Integer isTop;
    
    @Schema(description = "有帮助数量")
    private Integer helpfulCount;
    
    @Schema(description = "当前用户是否点赞")
    private Boolean isHelpfulByCurrentUser;
    
    @Schema(description = "状态：0-已删除，1-正常，2-已隐藏")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
