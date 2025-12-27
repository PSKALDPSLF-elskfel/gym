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
 * 教练学员关系实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_coach_student")
@Schema(description = "教练学员关系实体")
public class GymCoachStudent {

    @TableId(type = IdType.AUTO)
    @Schema(description = "关系ID")
    private Long id;

    @Schema(description = "教练ID")
    @TableField("coach_id")
    private Long coachId;

    @Schema(description = "学员用户ID")
    @TableField("student_id")
    private Long studentId;

    @Schema(description = "教练备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
