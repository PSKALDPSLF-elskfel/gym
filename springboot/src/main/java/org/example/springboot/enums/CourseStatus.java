package org.example.springboot.enums;

/**
 * 课程状态枚举
 * @author system
 */
public enum CourseStatus {
    /**
     * 下架
     */
    OFFLINE(0, "下架"),
    
    /**
     * 上架
     */
    ONLINE(1, "上架");

    private final Integer code;
    private final String description;

    CourseStatus(Integer code, String description) {
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
    public static CourseStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CourseStatus status : values()) {
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
