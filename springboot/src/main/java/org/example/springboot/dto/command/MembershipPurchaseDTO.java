package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员套餐购买DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员套餐购买DTO")
public class MembershipPurchaseDTO {

    @NotNull(message = "套餐ID不能为空")
    @Schema(description = "套餐ID", required = true)
    private Long packageId;
}
