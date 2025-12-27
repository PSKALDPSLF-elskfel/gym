package org.example.springboot.enums;

/**
 * 工作类型枚举
 * @author system
 */
public enum WorkType {
    /**
     * 正常排班
     */
    NORMAL("NORMAL", "正常排班"),
    
    /**
     * 加班
     */
    OVERTIME("OVERTIME", "加班"),
    
    /**
     * 轮班
     */
    SHIFT("SHIFT", "轮班"),
    
    /**
     * 休息
     */
    HOLIDAY("HOLIDAY", "休息");

    private final String code;
    private final String description;

    WorkType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static WorkType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (WorkType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 验证code是否有效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}
