package org.example.springboot.dto.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 运动记录创建DTO
 * @author system
 */
@Data
@Schema(description = "运动记录创建DTO")
public class WorkoutRecordCreateDTO {

    @NotNull(message = "运动类型ID不能为空")
    @Schema(description = "运动类型ID", required = true)
    private Long workoutTypeId;

    @NotNull(message = "运动日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "运动日期", required = true, example = "2025-12-12")
    private LocalDate workoutDate;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", required = true, example = "2025-12-12 08:00:00")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", example = "2025-12-12 09:00:00")
    private LocalDateTime endTime;

    @NotNull(message = "运动时长不能为空")
    @Schema(description = "运动时长(分钟)", required = true, example = "60")
    private Integer duration;

    @Schema(description = "运动强度:LOW-低,MEDIUM-中,HIGH-高", example = "MEDIUM")
    private String intensity;

    @Schema(description = "消耗热量(千卡)", example = "300")
    private Integer calories;

    @Schema(description = "运动距离(公里)", example = "5.5")
    private BigDecimal distance;

    @Schema(description = "步数", example = "8000")
    private Integer steps;

    @Schema(description = "平均心率(次/分钟)", example = "120")
    private Integer heartRateAvg;

    @Schema(description = "最大心率(次/分钟)", example = "150")
    private Integer heartRateMax;

    @Schema(description = "运动照片URL列表")
    private List<String> images;

    @Schema(description = "运动备注", example = "今天状态不错")
    private String note;

    @Schema(description = "运动感受:GREAT-很好,GOOD-良好,NORMAL-一般,TIRED-疲惫,BAD-不适", example = "GOOD")
    private String feeling;

    @Schema(description = "天气情况", example = "晴天")
    private String weather;

    @Schema(description = "运动地点", example = "健身房")
    private String location;

    @Schema(description = "是否完成:0-未完成,1-已完成", example = "1")
    private Integer isCompleted;

    @Schema(description = "关联的训练计划ID")
    private Long trainingPlanId;

    @Schema(description = "数据来源:MANUAL-手动录入,PLAN-训练计划,DEVICE-设备同步", example = "MANUAL")
    private String source;

    @Schema(description = "运动详情(力量训练动作明细)")
    private List<WorkoutDetailDTO> details;
}
