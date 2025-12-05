package org.example.springboot.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 * @author system
 */
@Getter
public enum UserStatus {
    
    ACTIVE(1, "活跃"),
    INACTIVE(0, "非活跃");

    private final Integer code;
    private final String description;

    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static UserStatus fromCode(String code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态代码: " + code);
    }

    /**
     * 验证用户状态代码是否有效
     */
    public static boolean isValidCode(String code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
