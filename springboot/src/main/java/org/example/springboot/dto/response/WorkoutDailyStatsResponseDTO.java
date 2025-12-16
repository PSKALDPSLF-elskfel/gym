package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日运动数据统计响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "每日运动数据统计响应DTO")
public class WorkoutDailyStatsResponseDTO {

    @Schema(description = "统计ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计日期")
    private LocalDate statDate;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "总消耗热量(千卡)")
    private Integer totalCalories;

    @Schema(description = "总运动距离(公里)")
    private BigDecimal totalDistance;

    @Schema(description = "总步数")
    private Integer totalSteps;

    @Schema(description = "运动次数")
    private Integer workoutCount;

    @Schema(description = "有氧运动时长(分钟)")
    private Integer cardioDuration;

    @Schema(description = "力量训练时长(分钟)")
    private Integer strengthDuration;

    @Schema(description = "柔韧性训练时长(分钟)")
    private Integer flexibilityDuration;

    @Schema(description = "是否休息日")
    private Integer isRestDay;

    @Schema(description = "计划完成率(%)")
    private BigDecimal planCompletionRate;
}
