package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教练评价统计实体类
 */
@Data
@TableName("gym_coach_review_statistics")
public class GymCoachReviewStatistics {
    
    /**
     * 统计ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 教练ID
     */
    private Long coachId;
    
    /**
     * 总评价数
     */
    private Integer totalReviews;
    
    /**
     * 平均评分
     */
    private BigDecimal averageRating;
    
    /**
     * 5星评价数
     */
    private Integer rating5Count;
    
    /**
     * 4星评价数
     */
    private Integer rating4Count;
    
    /**
     * 3星评价数
     */
    private Integer rating3Count;
    
    /**
     * 2星评价数
     */
    private Integer rating2Count;
    
    /**
     * 1星评价数
     */
    private Integer rating1Count;
    
    /**
     * 回复率(%)
     */
    private BigDecimal replyRate;
    
    /**
     * 最后评价时间
     */
    private LocalDateTime lastReviewTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
