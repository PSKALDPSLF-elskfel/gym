package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程预约响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程预约响应DTO")
public class CourseBookingResponseDTO {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "排课ID")
    private Long scheduleId;

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
