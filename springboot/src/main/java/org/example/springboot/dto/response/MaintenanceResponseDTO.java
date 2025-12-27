package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 维护记录响应DTO
 * @author system
 */
@Data
@Schema(description = "维护记录响应DTO")
public class MaintenanceResponseDTO {

    @Schema(description = "维护ID")
    private Long id;

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "器材名称")
    private String equipmentName;

    @Schema(description = "器材编号")
    private String equipmentCode;

    @Schema(description = "类型:1-保养,2-维修,3-检查")
    private Integer maintenanceType;

    @Schema(description = "维护类型名称")
    private String maintenanceTypeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "维护时间")
    private LocalDateTime maintenanceDate;

    @Schema(description = "维护内容")
    private String content;

    @Schema(description = "费用")
    private BigDecimal cost;

    @Schema(description = "维护人员")
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "下次维护日期")
    private LocalDate nextMaintenanceDate;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
