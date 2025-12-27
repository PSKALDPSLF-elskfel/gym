package org.example.springboot.enums;

/**
 * 排班状态枚举
 * @author system
 */
public enum ScheduleStatus {
    /**
     * 已取消
     */
    CANCELLED(0, "已取消"),
    
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    
    /**
     * 已完成
     */
    COMPLETED(2, "已完成");

    private final Integer code;
    private final String description;

    ScheduleStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static ScheduleStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ScheduleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 验证code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return fromCode(code) != null;
    }
}
