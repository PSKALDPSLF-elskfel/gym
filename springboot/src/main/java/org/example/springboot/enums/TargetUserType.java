package org.example.springboot.enums;

/**
 * 目标用户类型枚举
 * @author system
 */
public enum TargetUserType {
    /**
     * 小程序用户
     */
    USER("USER", "小程序用户"),
    
    /**
     * 管理员
     */
    ADMIN("ADMIN", "管理员"),
    
    /**
     * 教练
     */
    COACH("COACH", "教练"),
    
    /**
     * 全部
     */
    ALL("ALL", "全部");

    private final String code;
    private final String description;

    TargetUserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TargetUserType fromCode(String code) {
        for (TargetUserType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
