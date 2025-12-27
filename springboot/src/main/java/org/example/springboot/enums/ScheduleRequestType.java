package org.example.springboot.enums;

/**
 * 排班申请类型枚举
 * @author system
 */
public enum ScheduleRequestType {
    /**
     * 调休(请假)
     */
    LEAVE(1, "调休(请假)"),
    
    /**
     * 加班
     */
    OVERTIME(2, "加班"),
    
    /**
     * 换班
     */
    SHIFT_SWAP(3, "换班");

    private final Integer code;
    private final String description;

    ScheduleRequestType(Integer code, String description) {
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
    public static ScheduleRequestType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ScheduleRequestType type : values()) {
            if (type.code.equals(code)) {
                return type;
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
