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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户成就记录实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_user_achievement")
@Schema(description = "用户成就记录实体类")
public class GymUserAchievement {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "成就ID")
    @TableField("achievement_id")
    private Long achievementId;

    @Schema(description = "达成时间")
    @TableField("achieve_time")
    private LocalDateTime achieveTime;

    @Schema(description = "当前进度(%)")
    private BigDecimal progress;

    @Schema(description = "是否已达成:0-未达成,1-已达成")
    @TableField("is_achieved")
    private Integer isAchieved;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
