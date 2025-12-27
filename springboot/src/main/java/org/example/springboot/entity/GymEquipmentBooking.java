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
 * 器材预约实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_equipment_booking")
@Schema(description = "器材预约实体类")
public class GymEquipmentBooking {

    @TableId(type = IdType.AUTO)
    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "器材ID")
    @TableField("equipment_id")
    private Long equipmentId;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "实际开始时间")
    @TableField("actual_start_time")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    @TableField("actual_end_time")
    private LocalDateTime actualEndTime;

    @Schema(description = "状态:0-已取消,1-预约中,2-使用中,3-已完成,4-超时未使用")
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
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否预约中
     */
    public boolean isPending() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否使用中
     */
    public boolean isInUse() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 是否超时未使用
     */
    public boolean isTimeout() {
        return Integer.valueOf(4).equals(this.status);
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
                return "预约中";
            case 2:
                return "使用中";
            case 3:
                return "已完成";
            case 4:
                return "超时未使用";
            default:
                return "未知";
        }
    }
}
