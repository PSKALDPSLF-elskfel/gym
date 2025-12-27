package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 打卡命令DTO
 * @author system
 */
@Data
@Schema(description = "打卡命令DTO")
public class AttendanceCheckDTO {

    @Schema(description = "排班ID")
    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;

    @Schema(description = "打卡地点")
    private String location;

    @Schema(description = "备注")
    private String remark;
}
