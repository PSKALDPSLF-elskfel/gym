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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材维护记录实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_equipment_maintenance")
@Schema(description = "器材维护记录实体类")
public class GymEquipmentMaintenance {

    @TableId(type = IdType.AUTO)
    @Schema(description = "维护ID")
    private Long id;

    @Schema(description = "器材ID")
    @TableField("equipment_id")
    private Long equipmentId;

    @Schema(description = "类型:1-保养,2-维修,3-检查")
    @TableField("maintenance_type")
    private Integer maintenanceType;

    @Schema(description = "维护时间")
    @TableField("maintenance_date")
    private LocalDateTime maintenanceDate;

    @Schema(description = "维护内容")
    private String content;

    @Schema(description = "费用")
    private BigDecimal cost;

    @Schema(description = "维护人员")
    private String operator;

    @Schema(description = "下次维护日期")
    @TableField("next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否保养
     */
    public boolean isMaintenance() {
        return Integer.valueOf(1).equals(this.maintenanceType);
    }

    /**
     * 是否维修
     */
    public boolean isRepair() {
        return Integer.valueOf(2).equals(this.maintenanceType);
    }

    /**
     * 是否检查
     */
    public boolean isInspection() {
        return Integer.valueOf(3).equals(this.maintenanceType);
    }

    /**
     * 获取维护类型显示名称
     */
    public String getMaintenanceTypeDisplayName() {
        if (maintenanceType == null) {
            return "未知";
        }
        switch (maintenanceType) {
            case 1:
                return "保养";
            case 2:
                return "维修";
            case 3:
                return "检查";
            default:
                return "未知";
        }
    }
}
