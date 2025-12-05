package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 体测数据创建DTO
 * @author system
 */
@Data
@Schema(description = "体测数据创建DTO")
public class BodyTestCreateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "体脂率(%)")
    private BigDecimal bodyFat;

    @Schema(description = "肌肉量(kg)")
    private BigDecimal muscleMass;

    @Schema(description = "内脏脂肪等级")
    private Integer visceralFat;

    @Schema(description = "基础代谢(kcal)")
    private Integer basalMetabolism;

    @Schema(description = "测试时间")
    private LocalDateTime testTime;

    @Schema(description = "测试人员ID(教练)")
    private Long testerId;

    @Schema(description = "备注")
    private String remark;
}
