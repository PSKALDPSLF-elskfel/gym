package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建排班申请命令DTO
 * @author system
 */
@Data
@Schema(description = "创建排班申请命令DTO")
public class ScheduleRequestCreateDTO {

    @Schema(description = "申请类型：1-调休(请假),2-加班,3-换班")
    @NotNull(message = "申请类型不能为空")
    private Integer requestType;

    @Schema(description = "目标日期")
    @NotNull(message = "目标日期不能为空")
    private LocalDate targetDate;

    @Schema(description = "申请理由")
    private String reason;

    @Schema(description = "换班对象教练ID(仅当type=3时有效)")
    private Long exchangeWithCoachId;

    @Schema(description = "被交换的排班ID(仅当type=3时有效)")
    private Long exchangeScheduleId;

    @Schema(description = "附件地址(如医疗证明)")
    private String attachmentUrl;
}
