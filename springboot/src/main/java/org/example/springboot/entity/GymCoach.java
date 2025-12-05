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
 * 教练信息实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_coach")
@Schema(description = "教练信息实体")
public class GymCoach {

    @TableId(type = IdType.AUTO)
    @Schema(description = "教练ID")
    private Long id;

    @Schema(description = "关联用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "专业领域")
    private String specialty;

    @Schema(description = "资质证书(JSON)")
    private String certificate;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "平均评分")
    private BigDecimal rating;

    @Schema(description = "状态：0-离职，1-在职")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否在职
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }
}
