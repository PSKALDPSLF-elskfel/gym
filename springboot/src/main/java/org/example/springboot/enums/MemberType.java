package org.example.springboot.enums;

import lombok.Getter;

/**
 * 会员类型枚举
 * @author system
 */
@Getter
public enum MemberType {
    
    GOLD(1, "黄金会员", "健身房自主训练权限+课程95折优惠"),
    PLATINUM(2, "铂金会员", "健身房自主训练权限+课程九折优惠");

    private final Integer code;
    private final String name;
    private final String benefits;

    MemberType(Integer code, String name, String benefits) {
        this.code = code;
        this.name = name;
        this.benefits = benefits;
    }

    /**
     * 根据代码获取枚举
     */
    public static MemberType fromCode(Integer code) {
        for (MemberType type : MemberType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的会员类型代码: " + code);
    }

    /**
     * 验证会员类型代码是否有效
     */
    public static boolean isValidCode(Integer code) {
        for (MemberType type : MemberType.values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
