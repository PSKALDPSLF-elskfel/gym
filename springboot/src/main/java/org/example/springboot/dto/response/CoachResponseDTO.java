package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教练响应DTO
 * @author system
 */
@Data
@Schema(description = "教练响应DTO")
public class CoachResponseDTO {

    @Schema(description = "教练ID")
    private Long id;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

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

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
