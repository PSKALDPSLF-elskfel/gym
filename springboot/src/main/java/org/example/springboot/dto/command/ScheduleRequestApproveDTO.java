package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批排班申请命令DTO
 * @author system
 */
@Data
@Schema(description = "审批排班申请命令DTO")
public class ScheduleRequestApproveDTO {

    @Schema(description = "审批状态：1-已通过,2-已拒绝")
    @NotNull(message = "审批状态不能为空")
    private Integer status;

    @Schema(description = "审批意见")
    private String approveRemark;
}
