package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教练创建DTO
 * @author system
 */
@Data
@Schema(description = "教练创建DTO")
public class CoachCreateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "专业领域")
    private String specialty;

    @Schema(description = "资质证书(JSON)")
    private String certificate;

    @Schema(description = "个人简介")
    private String introduction;
}
