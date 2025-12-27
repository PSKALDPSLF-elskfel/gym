package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动强度枚举
 */
@Getter
@AllArgsConstructor
public enum WorkoutIntensity {
    
    LOW("低强度"),
    MEDIUM("中等强度"),
    HIGH("高强度");
    
    private final String description;
}
