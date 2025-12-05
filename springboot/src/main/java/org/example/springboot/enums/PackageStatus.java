package org.example.springboot.enums;

import lombok.Getter;

/**
 * 套餐状态枚举
 * @author system
 */
@Getter
public enum PackageStatus {
    
    OFFLINE(0, "下架"),
    ONLINE(1, "上架");

    private final Integer code;
    private final String description;

    PackageStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static PackageStatus fromCode(Integer code) {
        for (PackageStatus status : PackageStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的套餐状态代码: " + code);
    }

    /**
     * 验证状态代码是否有效
     */
    public static boolean isValidCode(Integer code) {
        for (PackageStatus status : PackageStatus.values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
