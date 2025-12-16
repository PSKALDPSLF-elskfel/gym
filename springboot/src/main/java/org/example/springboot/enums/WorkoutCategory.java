package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动类型分类枚举
 */
@Getter
@AllArgsConstructor
public enum WorkoutCategory {
    
    CARDIO("有氧运动"),
    STRENGTH("力量训练"),
    FLEXIBILITY("柔韧性训练"),
    SPORTS("球类运动"),
    OTHER("其他");
    
    private final String description;
}
