package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评价标签响应DTO
 */
@Data
@Schema(description = "评价标签响应")
public class ReviewTagResponseDTO {
    
    @Schema(description = "标签ID")
    private Long id;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Schema(description = "标签类型：1-正面标签，2-负面标签")
    private Integer tagType;
    
    @Schema(description = "标签图标")
    private String icon;
    
    @Schema(description = "使用次数")
    private Integer usageCount;
    
    @Schema(description = "排序")
    private Integer sortOrder;
}
