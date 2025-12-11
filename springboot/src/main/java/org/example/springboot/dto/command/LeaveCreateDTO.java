package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建请假申请命令DTO
 * @author system
 */
@Data
@Schema(description = "创建请假申请命令DTO")
public class LeaveCreateDTO {

    @Schema(description = "请假类型：ANNUAL-年假,SICK-病假,PERSONAL-事假,MATERNITY-产假,UNPAID-无薪假")
    @NotNull(message = "请假类型不能为空")
    private String leaveType;

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "请假原因")
    private String reason;
}
