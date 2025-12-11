package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 器材创建DTO
 * @author system
 */
@Data
@Schema(description = "器材创建DTO")
public class EquipmentCreateDTO {

    @NotBlank(message = "器材名称不能为空")
    @Schema(description = "器材名称", required = true)
    private String name;

    @NotBlank(message = "器材编号不能为空")
    @Schema(description = "器材编号", required = true)
    private String code;

    @Schema(description = "器材类型(有氧/力量/自由重量)")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "购买日期")
    private LocalDate purchaseDate;

    @Schema(description = "保修到期日")
    private LocalDate warrantyExpire;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态:0-故障,1-正常,2-维护中,3-报废", required = true)
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
