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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_equipment")
@Schema(description = "器材实体类")
public class GymEquipment {

    @TableId(type = IdType.AUTO)
    @Schema(description = "器材ID")
    private Long id;

    @Schema(description = "器材名称")
    private String name;

    @Schema(description = "器材编号")
    private String code;

    @Schema(description = "器材类型(有氧/力量/自由重量)")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "购买日期")
    @TableField("purchase_date")
    private LocalDate purchaseDate;

    @Schema(description = "保修到期日")
    @TableField("warranty_expire")
    private LocalDate warrantyExpire;

    @Schema(description = "状态:0-故障,1-正常,2-维护中,3-报废")
    private Integer status;

    @Schema(description = "当前使用者ID")
    @TableField("current_user_id")
    private Long currentUserId;

    @Schema(description = "使用开始时间")
    @TableField("usage_start_time")
    private LocalDateTime usageStartTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否正常状态
     */
    public boolean isNormal() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否故障
     */
    public boolean isFaulty() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否维护中
     */
    public boolean isMaintenance() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否报废
     */
    public boolean isScrap() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 是否空闲（正常且无人使用）
     */
    public boolean isAvailable() {
        return isNormal() && currentUserId == null;
    }

    /**
     * 是否使用中
     */
    public boolean isInUse() {
        return currentUserId != null;
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
                return "故障";
            case 1:
                return "正常";
            case 2:
                return "维护中";
            case 3:
                return "报废";
            default:
                return "未知";
        }
    }
}
