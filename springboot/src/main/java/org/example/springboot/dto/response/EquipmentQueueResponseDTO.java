package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 器材排队响应DTO
 * @author system
 */
@Data
@Schema(description = "器材排队响应DTO")
public class EquipmentQueueResponseDTO {

    @Schema(description = "排队ID")
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

    @Schema(description = "排队号")
    private Integer queueNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "叫号时间")
    private LocalDateTime callTime;

    @Schema(description = "状态:0-已取消,1-排队中,2-已叫号,3-已完成")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
