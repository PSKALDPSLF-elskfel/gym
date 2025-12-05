package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
@Schema(description = "用户实体类")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户类型：USER-普通用户，ADMIN-管理员，COACH-教练")
    @TableField("userType")
    private String userType;

    @Schema(description = "用户昵称/管理员姓名")
    private String nickname;

    @Schema(description = "头像URL")
    @Size(max = 255, message = "头像路径长度不能超过255个字符")
    private String avatar;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "会员类型：0-普通用户，1-黄金会员，2-铂金会员（仅小程序用户）")
    @TableField("member_type")
    private Integer memberType;

    @Schema(description = "会员到期时间（仅小程序用户）")
    @TableField("member_expire_time")
    private LocalDateTime memberExpireTime;

    @Schema(description = "管理员用户名")
    private String username;

    @Schema(description = "管理员密码（BCrypt加密）")
    private String password;

    @Schema(description = "管理员邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.userType);
    }

    /**
     * 是否为教练
     */
    public boolean isCoach() {
        return "COACH".equals(this.userType);
    }

    /**
     * 是否为普通用户
     */
    public boolean isUser() {
        return "USER".equals(this.userType);
    }

    /**
     * 是否为正常状态
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否被禁用
     */
    public boolean isDisabled() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否为黄金会员
     */
    public boolean isGoldMember() {
        return Integer.valueOf(1).equals(this.memberType);
    }

    /**
     * 是否为铂金会员
     */
    public boolean isPlatinumMember() {
        return Integer.valueOf(2).equals(this.memberType);
    }

    /**
     * 是否为会员（黄金或铂金）
     */
    public boolean isMember() {
        return isGoldMember() || isPlatinumMember();
    }

    /**
     * 会员是否有效（未过期）
     */
    public boolean isMemberValid() {
        if (!isMember() || memberExpireTime == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(memberExpireTime);
    }

    /**
     * 获取用户类型显示名称
     */
    public String getUserTypeDisplayName() {
        if ("ADMIN".equals(userType)) {
            return "管理员";
        } else if ("USER".equals(userType)) {
            return "普通用户";
        } else if ("COACH".equals(userType)) {
            return "教练";
        }
        return "未知";
    }

    /**
     * 获取用户状态显示名称
     */
    public String getStatusDisplayName() {
        if (Integer.valueOf(1).equals(status)) {
            return "正常";
        } else if (Integer.valueOf(0).equals(status)) {
            return "禁用";
        }
        return "未知";
    }

    /**
     * 获取会员类型显示名称
     */
    public String getMemberTypeDisplayName() {
        if (Integer.valueOf(1).equals(memberType)) {
            return "黄金会员";
        } else if (Integer.valueOf(2).equals(memberType)) {
            return "铂金会员";
        }
        return "普通用户";
    }
}
