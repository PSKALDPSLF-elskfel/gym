package org.example.springboot.enums;

import lombok.Getter;

/**
 * 器材预约状态枚举
 * @author system
 */
@Getter
public enum EquipmentBookingStatus {

    CANCELLED(0, "已取消"),
    PENDING(1, "预约中"),
    IN_USE(2, "使用中"),
    COMPLETED(3, "已完成"),
    TIMEOUT(4, "超时未使用");

    private final Integer code;
    private final String description;

    EquipmentBookingStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static EquipmentBookingStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EquipmentBookingStatus status : values()) {
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
