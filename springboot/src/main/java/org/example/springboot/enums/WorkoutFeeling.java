package org.example.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运动感受枚举
 */
@Getter
@AllArgsConstructor
public enum WorkoutFeeling {
    
    GREAT("很棒"),
    GOOD("良好"),
    NORMAL("一般"),
    TIRED("疲惫"),
    BAD("不适");
    
    private final String description;
}
