package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 器材排队创建DTO
 * @author system
 */
@Data
@Schema(description = "器材排队创建DTO")
public class EquipmentQueueCreateDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", required = true)
    private Long equipmentId;
}
