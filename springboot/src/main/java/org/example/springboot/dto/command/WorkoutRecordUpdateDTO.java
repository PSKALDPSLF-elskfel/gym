package org.example.springboot.dto.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 运动记录更新DTO
 * @author system
 */
@Data
@Schema(description = "运动记录更新DTO")
public class WorkoutRecordUpdateDTO {

    @Schema(description = "运动类型ID")
    private Long workoutTypeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "运动日期", example = "2025-12-12")
    private LocalDate workoutDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", example = "2025-12-12 08:00:00")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", example = "2025-12-12 09:00:00")
    private LocalDateTime endTime;

    @Schema(description = "运动时长(分钟)", example = "60")
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

    @Schema(description = "运动详情(力量训练动作明细)")
    private List<WorkoutDetailDTO> details;
}
