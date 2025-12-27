package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 器材预约响应DTO
 * @author system
 */
@Data
@Schema(description = "器材预约响应DTO")
public class EquipmentBookingResponseDTO {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "器材名称")
    private String equipmentName;

    @Schema(description = "器材编号")
    private String equipmentCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "状态:0-已取消,1-预约中,2-使用中,3-已完成,4-超时未使用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
