package org.example.springboot.enums;

/**
 * 通知优先级枚举
 * @author system
 */
public enum NotificationPriority {
    /**
     * 低优先级
     */
    LOW(0, "低"),
    
    /**
     * 中优先级
     */
    MEDIUM(1, "中"),
    
    /**
     * 高优先级
     */
    HIGH(2, "高");

    private final Integer code;
    private final String description;

    NotificationPriority(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationPriority fromCode(Integer code) {
        for (NotificationPriority priority : values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        return null;
    }
}
