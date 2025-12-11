package org.example.springboot.enums;

import lombok.Getter;

/**
 * 维护类型枚举
 * @author system
 */
@Getter
public enum MaintenanceType {

    MAINTENANCE(1, "保养"),
    REPAIR(2, "维修"),
    INSPECTION(3, "检查");

    private final Integer code;
    private final String description;

    MaintenanceType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static MaintenanceType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaintenanceType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 校验code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}
