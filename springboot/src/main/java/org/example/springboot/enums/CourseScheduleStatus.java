package org.example.springboot.enums;

/**
 * 课程时间安排状态枚举
 * @author system
 */
public enum CourseScheduleStatus {
    /**
     * 已取消
     */
    CANCELLED(0, "已取消"),
    
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    
    /**
     * 已满
     */
    FULL(2, "已满");

    private final Integer code;
    private final String description;

    CourseScheduleStatus(Integer code, String description) {
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
    public static CourseScheduleStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CourseScheduleStatus status : values()) {
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
