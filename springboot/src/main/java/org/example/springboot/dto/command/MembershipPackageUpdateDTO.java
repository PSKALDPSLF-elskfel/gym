package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 会员套餐更新命令DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员套餐更新命令DTO")
public class MembershipPackageUpdateDTO {

    @Schema(description = "套餐ID", example = "1")
    @NotNull(message = "套餐ID不能为空")
    private Long id;

    @Schema(description = "套餐名称", example = "黄金会员月卡")
    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 100, message = "套餐名称长度不能超过100个字符")
    private String name;

    @Schema(description = "会员类型：1-黄金会员，2-铂金会员", example = "1")
    @NotNull(message = "会员类型不能为空")
    @Min(value = 1, message = "会员类型必须为1或2")
    @Max(value = 2, message = "会员类型必须为1或2")
    private Integer memberType;

    @Schema(description = "有效天数", example = "30")
    @NotNull(message = "有效天数不能为空")
    @Min(value = 1, message = "有效天数必须大于0")
    @Max(value = 3650, message = "有效天数不能超过3650天")
    private Integer durationDays;

    @Schema(description = "价格", example = "199.00")
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @DecimalMax(value = "99999.99", message = "价格不能超过99999.99")
    @Digits(integer = 5, fraction = 2, message = "价格格式不正确，最多5位整数2位小数")
    private BigDecimal price;

    @Schema(description = "套餐描述", example = "黄金会员月卡，享受健身房训练权限")
    @Size(max = 500, message = "套餐描述长度不能超过500个字符")
    private String description;

    @Schema(description = "会员权益描述", example = "健身房自主训练权限")
    @Size(max = 500, message = "会员权益描述长度不能超过500个字符")
    private String benefits;

    @Schema(description = "状态：0-下架，1-上架", example = "1")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;
}
