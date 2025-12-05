package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 教练更新DTO
 * @author system
 */
@Data
@Schema(description = "教练更新DTO")
public class CoachUpdateDTO {

    @Schema(description = "专业领域")
    private String specialty;

    @Schema(description = "资质证书(JSON)")
    private String certificate;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "平均评分")
    private BigDecimal rating;

    @Schema(description = "状态：0-离职，1-在职")
    private Integer status;
}
