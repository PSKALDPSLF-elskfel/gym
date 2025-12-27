package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动数据来源枚举
 */
@Getter
@AllArgsConstructor
public enum WorkoutSource {
    
    MANUAL("手动录入"),
    PLAN("训练计划"),
    DEVICE("设备同步");
    
    private final String description;
}
