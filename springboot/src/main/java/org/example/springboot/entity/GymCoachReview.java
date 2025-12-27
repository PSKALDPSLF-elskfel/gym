package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练评价实体类
 */
@Data
@TableName("gym_coach_review")
public class GymCoachReview {
    
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 教练ID
     */
    private Long coachId;
    
    /**
     * 关联训练计划ID
     */
    private Long planId;
    
    /**
     * 关联课程预约ID
     */
    private Long courseBookingId;
    
    /**
     * 评价类型：1-训练计划评价，2-课程评价
     */
    private Integer reviewType;
    
    /**
     * 评分(1-5星)
     */
    private Integer rating;
    
    /**
     * 评价标签(JSON数组)
     */
    private String tagList;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片(JSON数组)
     */
    private String images;
    
    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;
    
    /**
     * 教练回复
     */
    private String reply;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;
    
    /**
     * 状态：0-已删除，1-正常，2-已隐藏
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
