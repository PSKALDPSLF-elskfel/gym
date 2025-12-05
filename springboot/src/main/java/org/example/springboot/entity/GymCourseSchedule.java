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
 * 课程时间安排实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_course_schedule")
@Schema(description = "课程时间安排实体类")
public class GymCourseSchedule {

    @TableId(type = IdType.AUTO)
    @Schema(description = "排课ID")
    private Long id;

    @Schema(description = "课程ID")
    @TableField("course_id")
    private String courseId;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "最大参与人数")
    @TableField("max_participants")
    private Integer maxParticipants;

    @Schema(description = "当前参与人数")
    @TableField("current_participants")
    private Integer currentParticipants;

    @Schema(description = "状态：0-取消，1-正常，2-已满")
    private Integer status;

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
     * 是否正常
     */
    public boolean isNormal() {
        return status != null && status == 1;
    }

    /**
     * 是否已满
     */
    public boolean isFull() {
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
                return "正常";
            case 2:
                return "已满";
            default:
                return "未知";
        }
    }

    /**
     * 检查是否可以预约
     */
    public boolean canBook() {
        return isNormal() && currentParticipants < maxParticipants;
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return startTime != null && startTime.isBefore(LocalDateTime.now());
    }
}
