package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 教练回复评价DTO
 */
@Data
@Schema(description = "教练回复评价请求")
public class CoachReviewReplyDTO {
    
    @Schema(description = "回复内容", required = true)
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 300, message = "回复内容不能超过300字")
    private String reply;
}
