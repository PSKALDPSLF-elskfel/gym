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
 * 体测数据实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gym_body_test")
@Schema(description = "体测数据实体")
public class GymBodyTest {

    @TableId(type = IdType.AUTO)
    @Schema(description = "体测ID")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "BMI指数")
    private BigDecimal bmi;

    @Schema(description = "体脂率(%)")
    @TableField("body_fat")
    private BigDecimal bodyFat;

    @Schema(description = "肌肉量(kg)")
    @TableField("muscle_mass")
    private BigDecimal muscleMass;

    @Schema(description = "内脏脂肪等级")
    @TableField("visceral_fat")
    private Integer visceralFat;

    @Schema(description = "基础代谢(kcal)")
    @TableField("basal_metabolism")
    private Integer basalMetabolism;

    @Schema(description = "测试时间")
    @TableField("test_time")
    private LocalDateTime testTime;

    @Schema(description = "测试人员ID(教练)")
    @TableField("tester_id")
    private Long testerId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
