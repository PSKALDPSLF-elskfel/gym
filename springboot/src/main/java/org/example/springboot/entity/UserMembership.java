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
 * 用户会员记录实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_user_membership")
@Schema(description = "用户会员记录实体类")
public class UserMembership {

    @TableId(type = IdType.AUTO)
    @Schema(description = "会员记录ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "套餐ID")
    @TableField("package_id")
    private Long packageId;

    @Schema(description = "会员类型：1-黄金会员，2-铂金会员")
    @TableField("member_type")
    private Integer memberType;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "购买价格")
    private BigDecimal price;

    @Schema(description = "状态：0-已过期，1-使用中")
    private Integer status;

    @Schema(description = "购买时间")
    @TableField("purchase_time")
    private LocalDateTime purchaseTime;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否使用中
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return status != null && status == 0;
    }

    /**
     * 检查是否真实过期（基于时间）
     */
    public boolean isReallyExpired() {
        if (endTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 是否黄金会员
     */
    public boolean isGoldMember() {
        return memberType != null && memberType == 1;
    }

    /**
     * 是否铂金会员
     */
    public boolean isPlatinumMember() {
        return memberType != null && memberType == 2;
    }

    /**
     * 获取会员类型显示名称
     */
    public String getMemberTypeDisplayName() {
        if (memberType == null) {
            return "未知";
        }
        switch (memberType) {
            case 1:
                return "黄金会员";
            case 2:
                return "铂金会员";
            default:
                return "未知";
        }
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
                return "已过期";
            case 1:
                return "使用中";
            default:
                return "未知";
        }
    }
}
