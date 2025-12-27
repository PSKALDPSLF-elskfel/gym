package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价点赞实体类
 */
@Data
@TableName("gym_review_helpful")
public class GymReviewHelpful {
    
    /**
     * 点赞ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 评价ID
     */
    private Long reviewId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 是否有帮助：0-无帮助，1-有帮助
     */
    private Integer isHelpful;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
