package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户详情响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户详情响应")
public class UserDetailResponseDTO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户类型：USER-普通用户，ADMIN-管理员", example = "USER")
    private String userType;

    @Schema(description = "用户昵称/管理员姓名", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "/images/avatar1.jpg")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "会员类型：0-普通用户，1-黄金会员，2-铂金会员", example = "0")
    private Integer memberType;

    @Schema(description = "会员到期时间")
    private LocalDateTime memberExpireTime;

    @Schema(description = "管理员用户名", example = "admin")
    private String username;

    @Schema(description = "管理员邮箱", example = "admin@gym.com")
    private String email;

    @Schema(description = "状态：0-禁用，1-正常", example = "1")
    private Integer status;

    @Schema(description = "用户类型显示名称", example = "普通用户")
    private String userTypeDisplayName;

    @Schema(description = "用户状态显示名称", example = "正常")
    private String statusDisplayName;

    @Schema(description = "会员类型显示名称", example = "黄金会员")
    private String memberTypeDisplayName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
