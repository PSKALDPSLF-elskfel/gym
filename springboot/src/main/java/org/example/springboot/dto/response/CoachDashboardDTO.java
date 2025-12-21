package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "教练工作台数据")
public class CoachDashboardDTO {
    
    @Schema(description = "学员总数")
    private Integer studentCount;
    
    @Schema(description = "训练方案总数")
    private Integer planCount;
    
    @Schema(description = "本周课程数")
    private Integer weekCourseCount;
    
    @Schema(description = "平均评分")
    private Double avgRating;
}
