package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户会员记录响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户会员记录响应DTO")
public class UserMembershipResponseDTO {

    @Schema(description = "会员记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "套餐ID")
    private Long packageId;

    @Schema(description = "套餐名称")
    private String packageName;

    @Schema(description = "会员类型：1-黄金会员，2-铂金会员")
    private Integer memberType;

    @Schema(description = "会员类型名称")
    private String memberTypeName;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "购买价格")
    private BigDecimal price;

    @Schema(description = "状态：0-已过期，1-使用中")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "购买时间")
    private LocalDateTime purchaseTime;

    @Schema(description = "剩余天数")
    private Long remainingDays;

    @Schema(description = "是否已过期")
    private Boolean expired;
}
