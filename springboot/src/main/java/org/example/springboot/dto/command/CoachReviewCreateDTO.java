package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 创建评价DTO
 */
@Data
@Schema(description = "创建评价请求")
public class CoachReviewCreateDTO {
    
    @Schema(description = "教练ID", required = true)
    @NotNull(message = "教练ID不能为空")
    private Long coachId;
    
    @Schema(description = "关联训练计划ID")
    private Long planId;
    
    @Schema(description = "关联课程预约ID")
    private Long courseBookingId;
    
    @Schema(description = "评价类型：1-训练计划评价，2-课程评价", required = true)
    @NotNull(message = "评价类型不能为空")
    @Min(value = 1, message = "评价类型错误")
    @Max(value = 2, message = "评价类型错误")
    private Integer reviewType;
    
    @Schema(description = "评分(1-5星)", required = true)
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;
    
    @Schema(description = "评价标签列表")
    private List<String> tagList;
    
    @Schema(description = "评价内容", required = true)
    @NotBlank(message = "评价内容不能为空")
    @Size(max = 500, message = "评价内容不能超过500字")
    private String content;
    
    @Schema(description = "评价图片URL列表")
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> images;
    
    @Schema(description = "是否匿名：0-否，1-是")
    private Integer isAnonymous = 0;
}
