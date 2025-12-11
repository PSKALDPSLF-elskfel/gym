package org.example.springboot.enums;

/**
 * 请假申请状态枚举
 * @author system
 */
public enum LeaveStatus {
    /**
     * 待审批
     */
    PENDING(0, "待审批"),
    
    /**
     * 已批准
     */
    APPROVED(1, "已批准"),
    
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝");

    private final Integer code;
    private final String description;

    LeaveStatus(Integer code, String description) {
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
    public static LeaveStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LeaveStatus status : values()) {
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
