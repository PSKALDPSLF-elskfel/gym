package org.example.springboot.enums;

/**
 * 排班申请状态枚举
 * @author system
 */
public enum ScheduleRequestStatus {
    /**
     * 待审批
     */
    PENDING(0, "待审批"),
    
    /**
     * 已通过
     */
    APPROVED(1, "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),
    
    /**
     * 已取消
     */
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    ScheduleRequestStatus(Integer code, String description) {
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
    public static ScheduleRequestStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ScheduleRequestStatus status : values()) {
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
