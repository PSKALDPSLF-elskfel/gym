package org.example.springboot.enums;

import lombok.Getter;

/**
 * 器材状态枚举
 * @author system
 */
@Getter
public enum EquipmentStatus {

    FAULTY(0, "故障"),
    NORMAL(1, "正常"),
    MAINTENANCE(2, "维护中"),
    SCRAP(3, "报废");

    private final Integer code;
    private final String description;

    EquipmentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static EquipmentStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EquipmentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
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
