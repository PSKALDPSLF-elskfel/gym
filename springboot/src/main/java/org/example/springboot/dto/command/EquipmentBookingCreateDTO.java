package org.example.springboot.dto.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 器材预约创建DTO
 * @author system
 */
@Data
@Schema(description = "器材预约创建DTO")
public class EquipmentBookingCreateDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", required = true)
    private Long equipmentId;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", required = true)
    private LocalDateTime endTime;
}
