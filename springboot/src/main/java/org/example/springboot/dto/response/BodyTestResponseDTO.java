package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 体测数据响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "体测数据响应DTO")
public class BodyTestResponseDTO {

    @Schema(description = "体测ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "BMI指数")
    private BigDecimal bmi;

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

    @Schema(description = "测试人员ID")
    private Long testerId;

    @Schema(description = "测试人员姓名")
    private String testerName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
