package org.example.springboot.enums;

/**
 * 通知类型枚举
 * @author system
 */
public enum NotificationType {
    /**
     * 系统通知
     */
    SYSTEM("SYSTEM", "系统通知"),
    
    /**
     * 预约提醒
     */
    BOOKING("BOOKING", "预约提醒"),
    
    /**
     * 课程相关
     */
    COURSE("COURSE", "课程相关"),
    
    /**
     * 器材相关
     */
    EQUIPMENT("EQUIPMENT", "器材相关"),
    
    /**
     * 会员相关
     */
    MEMBERSHIP("MEMBERSHIP", "会员相关");

    private final String code;
    private final String description;

    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
