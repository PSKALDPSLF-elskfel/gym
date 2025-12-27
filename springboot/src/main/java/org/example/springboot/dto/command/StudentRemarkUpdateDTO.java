package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学员备注更新DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学员备注更新DTO")
public class StudentRemarkUpdateDTO {

    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员用户ID", required = true)
    private Long userId;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "教练备注")
    private String remark;
}
