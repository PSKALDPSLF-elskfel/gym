package org.example.springboot.enums;

import lombok.Getter;

/**
 * 评价类型枚举
 */
@Getter
public enum ReviewType {
    
    /**
     * 训练计划评价
     */
    TRAINING_PLAN(1, "训练计划评价"),
    
    /**
     * 课程评价
     */
    COURSE(2, "课程评价");
    
    private final Integer code;
    private final String description;
    
    ReviewType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static ReviewType getByCode(Integer code) {
        for (ReviewType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
