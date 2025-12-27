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
 * 会员套餐实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_membership_package")
@Schema(description = "会员套餐实体类")
public class MembershipPackage {

    @TableId(type = IdType.AUTO)
    @Schema(description = "套餐ID")
    private Long id;

    @Schema(description = "套餐名称")
    private String name;

    @Schema(description = "会员类型：1-黄金会员，2-铂金会员")
    @TableField("member_type")
    private Integer memberType;

    @Schema(description = "有效天数")
    @TableField("duration_days")
    private Integer durationDays;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "套餐描述")
    private String description;

    @Schema(description = "会员权益描述")
    private String benefits;

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
     * 是否黄金会员套餐
     */
    public boolean isGoldPackage() {
        return memberType != null && memberType == 1;
    }

    /**
     * 是否铂金会员套餐
     */
    public boolean isPlatinumPackage() {
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
                return "下架";
            case 1:
                return "上架";
            default:
                return "未知";
        }
    }
}
