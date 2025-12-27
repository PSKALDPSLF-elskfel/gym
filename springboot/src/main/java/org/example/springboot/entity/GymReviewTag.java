package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价标签实体类
 */
@Data
@TableName("gym_review_tag")
public class GymReviewTag {
    
    /**
     * 标签ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签类型：1-正面标签，2-负面标签
     */
    private Integer tagType;
    
    /**
     * 标签图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 状态：0-禁用，1-启用
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
