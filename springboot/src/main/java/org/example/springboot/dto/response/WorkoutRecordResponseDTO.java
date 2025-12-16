package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 运动记录响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "运动记录响应DTO")
public class WorkoutRecordResponseDTO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "运动类型ID")
    private Long workoutTypeId;

    @Schema(description = "运动类型名称")
    private String workoutTypeName;

    @Schema(description = "运动大类")
    private String workoutCategory;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "运动日期")
    private LocalDate workoutDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "运动时长(分钟)")
    private Integer duration;

    @Schema(description = "运动强度")
    private String intensity;

    @Schema(description = "消耗热量(千卡)")
    private Integer calories;

    @Schema(description = "运动距离(公里)")
    private BigDecimal distance;

    @Schema(description = "步数")
    private Integer steps;

    @Schema(description = "平均心率")
    private Integer heartRateAvg;

    @Schema(description = "最大心率")
    private Integer heartRateMax;

    @Schema(description = "运动照片URL列表")
    private List<String> images;

    @Schema(description = "运动备注")
    private String note;

    @Schema(description = "运动感受")
    private String feeling;

    @Schema(description = "天气情况")
    private String weather;

    @Schema(description = "运动地点")
    private String location;

    @Schema(description = "是否完成")
    private Integer isCompleted;

    @Schema(description = "关联的训练计划ID")
    private Long trainingPlanId;

    @Schema(description = "数据来源")
    private String source;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "运动详情列表")
    private List<WorkoutDetailResponseDTO> details;
}
