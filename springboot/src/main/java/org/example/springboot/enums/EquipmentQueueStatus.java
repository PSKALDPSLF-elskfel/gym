package org.example.springboot.enums;

import lombok.Getter;

/**
 * 器材排队状态枚举
 * @author system
 */
@Getter
public enum EquipmentQueueStatus {

    CANCELLED(0, "已取消"),
    WAITING(1, "排队中"),
    CALLED(2, "已叫号"),
    COMPLETED(3, "已完成");

    private final Integer code;
    private final String description;

    EquipmentQueueStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static EquipmentQueueStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EquipmentQueueStatus status : values()) {
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
