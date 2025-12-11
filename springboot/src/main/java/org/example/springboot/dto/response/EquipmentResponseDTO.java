package org.example.springboot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材响应DTO
 * @author system
 */
@Data
@Schema(description = "器材响应DTO")
public class EquipmentResponseDTO {

    @Schema(description = "器材ID")
    private Long id;

    @Schema(description = "器材名称")
    private String name;

    @Schema(description = "器材编号")
    private String code;

    @Schema(description = "器材类型(有氧/力量/自由重量)")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "位置")
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "购买日期")
    private LocalDate purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "保修到期日")
    private LocalDate warrantyExpire;

    @Schema(description = "状态:0-故障,1-正常,2-维护中,3-报废")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "当前使用者ID")
    private Long currentUserId;

    @Schema(description = "当前使用者昵称")
    private String currentUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "使用开始时间")
    private LocalDateTime usageStartTime;

    @Schema(description = "是否空闲")
    private Boolean available;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
