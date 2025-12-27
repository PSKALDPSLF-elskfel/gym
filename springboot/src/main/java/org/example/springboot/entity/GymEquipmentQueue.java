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
 * 器材排队实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_equipment_queue")
@Schema(description = "器材排队实体类")
public class GymEquipmentQueue {

    @TableId(type = IdType.AUTO)
    @Schema(description = "排队ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "器材ID")
    @TableField("equipment_id")
    private Long equipmentId;

    @Schema(description = "排队号")
    @TableField("queue_number")
    private Integer queueNumber;

    @Schema(description = "加入时间")
    @TableField("join_time")
    private LocalDateTime joinTime;

    @Schema(description = "叫号时间")
    @TableField("call_time")
    private LocalDateTime callTime;

    @Schema(description = "状态:0-已取消,1-排队中,2-已叫号,3-已完成")
    private Integer status;

    @Schema(description = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否排队中
     */
    public boolean isWaiting() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否已叫号
     */
    public boolean isCalled() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return Integer.valueOf(3).equals(this.status);
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
                return "排队中";
            case 2:
                return "已叫号";
            case 3:
                return "已完成";
            default:
                return "未知";
        }
    }
}
