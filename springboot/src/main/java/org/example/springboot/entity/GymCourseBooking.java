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
 * 课程预约实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_course_booking")
@Schema(description = "课程预约实体类")
public class GymCourseBooking {

    @TableId(type = IdType.AUTO)
    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "排课ID")
    @TableField("schedule_id")
    private Long scheduleId;

    @Schema(description = "预约时间")
    @TableField("booking_time")
    private LocalDateTime bookingTime;

    @Schema(description = "原价")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @Schema(description = "实际支付价格")
    @TableField("actual_price")
    private BigDecimal actualPrice;

    @Schema(description = "折扣率")
    @TableField("discount_rate")
    private BigDecimal discountRate;

    @Schema(description = "状态：0-已取消，1-已预约，2-已完成")
    private Integer status;

    @Schema(description = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return status != null && status == 0;
    }

    /**
     * 是否已预约
     */
    public boolean isBooked() {
        return status != null && status == 1;
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return status != null && status == 2;
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
                return "已取消";
            case 1:
                return "已预约";
            case 2:
                return "已完成";
            default:
                return "未知";
        }
    }

    /**
     * 检查是否可以取消
     */
    public boolean canCancel() {
        return isBooked();
    }

    /**
     * 检查是否享受了折扣
     */
    public boolean hasDiscount() {
        return discountRate != null && discountRate.compareTo(BigDecimal.ONE) < 0;
    }

    /**
     * 计算节省的金额
     */
    public BigDecimal getSavedAmount() {
        if (originalPrice == null || actualPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalPrice.subtract(actualPrice);
    }
}
