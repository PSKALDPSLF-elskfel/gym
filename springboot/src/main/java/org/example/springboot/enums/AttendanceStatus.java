package org.example.springboot.enums;

/**
 * 打卡状态枚举
 * @author system
 */
public enum AttendanceStatus {
    /**
     * 未打卡
     */
    NOT_CHECKED(0, "未打卡"),
    
    /**
     * 已打卡入场
     */
    CHECKED_IN(1, "已打卡入场"),
    
    /**
     * 已打卡离场
     */
    CHECKED_OUT(2, "已打卡离场"),
    
    /**
     * 迟到
     */
    LATE(3, "迟到"),
    
    /**
     * 早退
     */
    EARLY_LEAVE(4, "早退"),
    
    /**
     * 缺勤
     */
    ABSENT(5, "缺勤");

    private final Integer code;
    private final String description;

    AttendanceStatus(Integer code, String description) {
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
    public static AttendanceStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AttendanceStatus status : values()) {
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
