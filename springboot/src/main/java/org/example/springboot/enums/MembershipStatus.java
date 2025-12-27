package org.example.springboot.enums;

import lombok.Getter;

/**
 * 会员状态枚举
 * @author system
 */
@Getter
public enum MembershipStatus {
    
    EXPIRED(0, "已过期"),
    ACTIVE(1, "使用中");

    private final Integer code;
    private final String name;

    MembershipStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举
     */
    public static MembershipStatus fromCode(Integer code) {
        for (MembershipStatus status : MembershipStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的会员状态代码: " + code);
    }

    /**
     * 验证会员状态代码是否有效
     */
    public static boolean isValidCode(Integer code) {
        for (MembershipStatus status : MembershipStatus.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
