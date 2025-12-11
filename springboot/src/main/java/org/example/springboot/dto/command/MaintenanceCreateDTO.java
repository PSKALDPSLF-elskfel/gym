package org.example.springboot.dto.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 维护记录创建DTO
 * @author system
 */
@Data
@Schema(description = "维护记录创建DTO")
public class MaintenanceCreateDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", required = true)
    private Long equipmentId;

    @NotNull(message = "维护类型不能为空")
    @Schema(description = "类型:1-保养,2-维修,3-检查", required = true)
    private Integer maintenanceType;

    @NotNull(message = "维护时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "维护时间", required = true)
    private LocalDateTime maintenanceDate;

    @Schema(description = "维护内容")
    private String content;

    @Schema(description = "费用")
    private BigDecimal cost;

    @Schema(description = "维护人员")
    private String operator;

    @Schema(description = "下次维护日期")
    private LocalDate nextMaintenanceDate;

    @Schema(description = "备注")
    private String remark;
}
