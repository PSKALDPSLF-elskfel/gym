package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学员基本信息DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学员基本信息DTO")
public class StudentDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "会员类型：0-普通用户，1-黄金会员，2-铂金会员")
    private Integer memberType;

    @Schema(description = "会员类型显示名称")
    private String memberTypeName;

    @Schema(description = "会员到期时间")
    private LocalDateTime memberExpireTime;

    @Schema(description = "是否会员有效")
    private Boolean isMemberValid;

    @Schema(description = "教练备注")
    private String coachRemark;

    @Schema(description = "训练计划数量")
    private Integer trainingPlanCount;

    @Schema(description = "最近训练计划名称")
    private String latestPlanName;

    @Schema(description = "最近训练时间")
    private LocalDateTime latestTrainingTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
