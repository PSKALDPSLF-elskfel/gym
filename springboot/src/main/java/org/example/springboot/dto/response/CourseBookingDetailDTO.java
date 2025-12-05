package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程预约详情DTO(包含课程和用户信息)
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程预约详情DTO")
public class CourseBookingDetailDTO {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "用户会员类型：0-普通用户，1-黄金会员，2-铂金会员")
    private Integer userMemberType;

    @Schema(description = "用户会员类型显示名称")
    private String userMemberTypeDisplayName;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "课程ID")
    private String courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程封面")
    private String courseCoverImage;

    @Schema(description = "课程时长(分钟)")
    private Integer courseDuration;

    @Schema(description = "课程开始时间")
    private LocalDateTime scheduleStartTime;

    @Schema(description = "课程结束时间")
    private LocalDateTime scheduleEndTime;

    @Schema(description = "预约时间")
    private LocalDateTime bookingTime;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "实际支付价格")
    private BigDecimal actualPrice;

    @Schema(description = "折扣率")
    private BigDecimal discountRate;

    @Schema(description = "状态：0-已取消，1-已预约，2-已完成")
    private Integer status;

    @Schema(description = "状态显示名称")
    private String statusDisplayName;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "节省金额")
    private BigDecimal savedAmount;
}
