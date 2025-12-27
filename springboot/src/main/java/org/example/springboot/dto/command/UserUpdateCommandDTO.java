package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新命令DTO
 * @author system
 */
@Data
@Schema(description = "用户信息更新命令")
public class UserUpdateCommandDTO {

    @Schema(description = "用户昵称/管理员姓名", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Schema(description = "头像URL", example = "/images/avatar1.jpg")
    @Size(max = 255, message = "头像路径长度不能超过255个字符")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "管理员邮箱", example = "admin@gym.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
}
