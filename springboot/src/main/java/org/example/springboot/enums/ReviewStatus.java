package org.example.springboot.enums;

import lombok.Getter;

/**
 * 评价状态枚举
 */
@Getter
public enum ReviewStatus {
    
    /**
     * 已删除
     */
    DELETED(0, "已删除"),
    
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    
    /**
     * 已隐藏
     */
    HIDDEN(2, "已隐藏");
    
    private final Integer code;
    private final String description;
    
    ReviewStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static ReviewStatus getByCode(Integer code) {
        for (ReviewStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
