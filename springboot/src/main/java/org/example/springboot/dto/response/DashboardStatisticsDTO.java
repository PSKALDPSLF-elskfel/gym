package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 仪表盘统计数据响应DTO
 */
@Data
@Schema(description = "仪表盘统计数据")
public class DashboardStatisticsDTO {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "总课程数")
    private Long totalCourses;

    @Schema(description = "总预约数")
    private Long totalBookings;

    @Schema(description = "会员数量")
    private Long memberCount;

    @Schema(description = "会员类型分布数据")
    private List<MemberTypeDistribution> memberTypeDistribution;

    @Schema(description = "课程预约趋势数据")
    private List<BookingTrend> bookingTrends;

    /**
     * 会员类型分布
     */
    @Data
    @Schema(description = "会员类型分布")
    public static class MemberTypeDistribution {
        @Schema(description = "会员类型名称")
        private String memberType;

        @Schema(description = "数量")
        private Long count;
    }

    /**
     * 预约趋势
     */
    @Data
    @Schema(description = "预约趋势")
    public static class BookingTrend {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "预约数量")
        private Long count;
    }
}
