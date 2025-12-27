package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 成就类型枚举
 */
@Getter
@AllArgsConstructor
public enum AchievementType {
    
    DURATION("运动时长"),
    CALORIES("消耗热量"),
    DISTANCE("运动距离"),
    FREQUENCY("运动频率"),
    STREAK("连续运动");
    
    private final String description;
}
