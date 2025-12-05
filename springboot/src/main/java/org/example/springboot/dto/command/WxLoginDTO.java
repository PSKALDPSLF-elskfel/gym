package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录DTO
 * @author system
 */
@Data
@Schema(description = "微信小程序登录DTO")
public class WxLoginDTO {

    @NotBlank(message = "登录凭证不能为空")
    @Schema(description = "微信登录凭证code")
    private String code;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;
}
