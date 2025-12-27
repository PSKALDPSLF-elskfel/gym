package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 运动数据统计汇总DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "运动数据统计汇总DTO")
public class WorkoutStatisticsSummaryDTO {

    @Schema(description = "总运动次数")
    private Integer totalWorkouts;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "总消耗热量(千卡)")
    private Integer totalCalories;

    @Schema(description = "总运动距离(公里)")
    private BigDecimal totalDistance;

    @Schema(description = "平均运动时长(分钟)")
    private Integer avgDuration;

    @Schema(description = "运动天数")
    private Integer workoutDays;

    @Schema(description = "连续运动天数")
    private Integer streakDays;

    @Schema(description = "本周运动次数")
    private Integer weekWorkouts;

    @Schema(description = "本月运动次数")
    private Integer monthWorkouts;

    @Schema(description = "有氧运动占比(%)")
    private BigDecimal cardioPercentage;

    @Schema(description = "力量训练占比(%)")
    private BigDecimal strengthPercentage;
}
