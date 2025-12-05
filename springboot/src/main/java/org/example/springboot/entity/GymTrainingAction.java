package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 训练动作库实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_training_action")
@Schema(description = "训练动作库实体")
public class GymTrainingAction {

    @TableId(type = IdType.AUTO)
    @Schema(description = "动作ID")
    private Long id;

    @Schema(description = "动作名称")
    private String name;

    @Schema(description = "动作分类(胸/背/腿/肩/臂/腹/核心/全身)")
    private String category;

    @Schema(description = "动作描述")
    private String description;

    @Schema(description = "示范视频URL")
    @TableField("video_url")
    private String videoUrl;

    @Schema(description = "示范图片URL")
    @TableField("image_url")
    private String imageUrl;

    @Schema(description = "难度：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @Schema(description = "目标肌群")
    @TableField("target_muscle")
    private String targetMuscle;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 获取难度显示名称
     */
    public String getDifficultyDisplayName() {
        if (Integer.valueOf(1).equals(difficulty)) {
            return "简单";
        } else if (Integer.valueOf(2).equals(difficulty)) {
            return "中等";
        } else if (Integer.valueOf(3).equals(difficulty)) {
            return "困难";
        }
        return "未知";
    }
}
