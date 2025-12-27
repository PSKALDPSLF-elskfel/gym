package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员套餐响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员套餐响应DTO")
public class MembershipPackageResponseDTO {

    @Schema(description = "套餐ID")
    private Long id;

    @Schema(description = "套餐名称")
    private String name;

    @Schema(description = "会员类型：1-黄金会员，2-铂金会员")
    private Integer memberType;

    @Schema(description = "会员类型显示名称")
    private String memberTypeDisplayName;

    @Schema(description = "有效天数")
    private Integer durationDays;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "套餐描述")
    private String description;

    @Schema(description = "会员权益描述")
    private String benefits;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "状态显示名称")
    private String statusDisplayName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
