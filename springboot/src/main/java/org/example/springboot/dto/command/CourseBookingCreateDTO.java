package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程预约创建DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程预约创建DTO")
public class CourseBookingCreateDTO {

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID", required = true)
    private Long scheduleId;

    @Schema(description = "用户ID(后端自动获取,前端无需传递)")
    private Long userId;
}
