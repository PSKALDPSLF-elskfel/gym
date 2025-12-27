package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 课程签到DTO
 * @author system
 */
@Data
@Schema(description = "课程签到DTO")
public class CourseSignInDTO {

    @NotNull(message = "预约ID不能为空")
    @Schema(description = "预约ID", required = true)
    private Long bookingId;

    @Schema(description = "签到方式：1-扫码，2-手动，默认2")
    private Integer signInType = 2;
}
