package org.example.springboot.enums;

/**
 * 请假类型枚举
 * @author system
 */
public enum LeaveType {
    /**
     * 年假(带薪)
     */
    ANNUAL("ANNUAL", "年假"),
    
    /**
     * 病假(可能需要证明)
     */
    SICK("SICK", "病假"),
    
    /**
     * 事假(可能无薪)
     */
    PERSONAL("PERSONAL", "事假"),
    
    /**
     * 产假(法定假期)
     */
    MATERNITY("MATERNITY", "产假"),
    
    /**
     * 无薪假
     */
    UNPAID("UNPAID", "无薪假");

    private final String code;
    private final String description;

    LeaveType(String code, String description) {
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
    public static LeaveType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (LeaveType type : values()) {
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
