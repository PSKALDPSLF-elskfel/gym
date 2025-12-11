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
 * 排班打卡记录表实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_schedule_record")
@Schema(description = "排班打卡记录实体")
public class GymScheduleRecord {

    @TableId(type = IdType.AUTO)
    @Schema(description = "打卡ID")
    private Long id;

    @Schema(description = "排班ID")
    @TableField("schedule_id")
    private Long scheduleId;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "打卡入场时间")
    @TableField("check_in_time")
    private LocalDateTime checkInTime;

    @Schema(description = "打卡离场时间")
    @TableField("check_out_time")
    private LocalDateTime checkOutTime;

    @Schema(description = "入场地点")
    @TableField("check_in_location")
    private String checkInLocation;

    @Schema(description = "离场地点")
    @TableField("check_out_location")
    private String checkOutLocation;

    @Schema(description = "打卡状态：0-未打卡,1-已打卡入场,2-已打卡离场,3-迟到,4-早退,5-缺勤")
    private Integer status;

    @Schema(description = "出勤评分(0-100)")
    @TableField("attendance_score")
    private Integer attendanceScore;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否未打卡
     */
    public boolean isNotChecked() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 是否已入场
     */
    public boolean isCheckedIn() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 是否已离场
     */
    public boolean isCheckedOut() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 是否迟到
     */
    public boolean isLate() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 是否早退
     */
    public boolean isEarlyLeave() {
        return Integer.valueOf(4).equals(this.status);
    }

    /**
     * 是否缺勤
     */
    public boolean isAbsent() {
        return Integer.valueOf(5).equals(this.status);
    }
}
