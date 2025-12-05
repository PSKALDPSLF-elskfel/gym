package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_course")
@Schema(description = "课程实体类")
public class GymCourse {

    @TableId
    @Schema(description = "课程ID（UUID）")
    private String id;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "封面图片")
    @TableField("cover_image")
    private String coverImage;

    @Schema(description = "课程时长（分钟）")
    private Integer duration;

    @Schema(description = "最大参与人数")
    @TableField("max_participants")
    private Integer maxParticipants;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否上架
     */
    public boolean isOnline() {
        return status != null && status == 1;
    }

    /**
     * 是否下架
     */
    public boolean isOffline() {
        return status != null && status == 0;
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "下架";
            case 1:
                return "上架";
            default:
                return "未知";
        }
    }
}
