package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教练评价统计响应DTO
 */
@Data
@Schema(description = "教练评价统计响应")
public class CoachReviewStatisticsResponseDTO {
    
    @Schema(description = "教练ID")
    private Long coachId;
    
    @Schema(description = "教练姓名")
    private String coachName;
    
    @Schema(description = "总评价数")
    private Integer totalReviews;
    
    @Schema(description = "平均评分")
    private BigDecimal averageRating;
    
    @Schema(description = "5星评价数")
    private Integer rating5Count;
    
    @Schema(description = "4星评价数")
    private Integer rating4Count;
    
    @Schema(description = "3星评价数")
    private Integer rating3Count;
    
    @Schema(description = "2星评价数")
    private Integer rating2Count;
    
    @Schema(description = "1星评价数")
    private Integer rating1Count;
    
    @Schema(description = "回复率(%)")
    private BigDecimal replyRate;
    
    @Schema(description = "最后评价时间")
    private LocalDateTime lastReviewTime;
}
