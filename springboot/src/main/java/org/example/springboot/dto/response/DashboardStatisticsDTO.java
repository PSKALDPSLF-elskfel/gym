package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
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

    @Schema(description = "教练数量")
    private Long coachCount;

    @Schema(description = "训练计划总数")
    private Long totalTrainingPlans;

    @Schema(description = "运动记录总数")
    private Long totalWorkoutRecords;

    @Schema(description = "体测记录总数")
    private Long totalBodyTests;

    @Schema(description = "本月新增用户数")
    private Long monthNewUsers;

    @Schema(description = "本周新增用户数")
    private Long weekNewUsers;

    @Schema(description = "今日活跃用户数")
    private Long todayActiveUsers;

    @Schema(description = "会员续费率（百分比）")
    private BigDecimal renewalRate;

    @Schema(description = "平均课程满座率（百分比）")
    private BigDecimal courseOccupancyRate;

    @Schema(description = "会员类型分布数据")
    private List<MemberTypeDistribution> memberTypeDistribution;

    @Schema(description = "课程预约趋势数据")
    private List<BookingTrend> bookingTrends;

    @Schema(description = "用户增长趋势（最近30天）")
    private List<UserGrowthTrend> userGrowthTrends;

    @Schema(description = "课程类型受欢迎度")
    private List<CoursePopularity> coursePopularities;

    @Schema(description = "运动数据统计（最近30天）")
    private List<WorkoutStatistics> workoutStatistics;

    @Schema(description = "教练工作量统计")
    private List<CoachWorkload> coachWorkloads;

    @Schema(description = "用户行为分析")
    private UserBehaviorAnalysis userBehaviorAnalysis;

    @Schema(description = "运营数据概览")
    private OperationalOverview operationalOverview;

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

        @Schema(description = "占比（百分比）")
        private BigDecimal percentage;
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

        @Schema(description = "完成数量")
        private Long completedCount;

        @Schema(description = "取消数量")
        private Long cancelledCount;
    }

    /**
     * 用户增长趋势
     */
    @Data
    @Schema(description = "用户增长趋势")
    public static class UserGrowthTrend {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "新增用户数")
        private Long newUsers;

        @Schema(description = "累计用户数")
        private Long totalUsers;
    }

    /**
     * 课程受欢迎度
     */
    @Data
    @Schema(description = "课程受欢迎度")
    public static class CoursePopularity {
        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "预约次数")
        private Long bookingCount;

        @Schema(description = "平均满座率（百分比）")
        private BigDecimal occupancyRate;
    }

    /**
     * 运动数据统计
     */
    @Data
    @Schema(description = "运动数据统计")
    public static class WorkoutStatistics {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "运动次数")
        private Long workoutCount;

        @Schema(description = "总时长（分钟）")
        private Integer totalDuration;

        @Schema(description = "总热量（千卡）")
        private Integer totalCalories;
    }

    /**
     * 教练工作量统计
     */
    @Data
    @Schema(description = "教练工作量统计")
    public static class CoachWorkload {
        @Schema(description = "教练姓名")
        private String coachName;

        @Schema(description = "课程数量")
        private Long courseCount;

        @Schema(description = "学员数量")
        private Long studentCount;

        @Schema(description = "平均评分")
        private BigDecimal rating;
    }

    /**
     * 用户行为分析
     */
    @Data
    @Schema(description = "用户行为分析")
    public static class UserBehaviorAnalysis {
        @Schema(description = "平均每周运动次数")
        private BigDecimal avgWeeklyWorkouts;

        @Schema(description = "平均每次运动时长（分钟）")
        private BigDecimal avgWorkoutDuration;

        @Schema(description = "最受欢迎的运动时段")
        private String peakWorkoutTime;

        @Schema(description = "用户留存率（7天，百分比）")
        private BigDecimal retentionRate7Days;

        @Schema(description = "用户留存率（30天，百分比）")
        private BigDecimal retentionRate30Days;

        @Schema(description = "活跃用户占比（百分比）")
        private BigDecimal activeUserRate;
    }

    /**
     * 运营数据概览
     */
    @Data
    @Schema(description = "运营数据概览")
    public static class OperationalOverview {
        @Schema(description = "本月总收入（元）")
        private BigDecimal monthRevenue;

        @Schema(description = "本月会员充值金额（元）")
        private BigDecimal monthRecharge;

        @Schema(description = "课程预约转化率（百分比）")
        private BigDecimal bookingConversionRate;

        @Schema(description = "课程出席率（百分比）")
        private BigDecimal courseAttendanceRate;

        @Schema(description = "平均客单价（元）")
        private BigDecimal avgOrderValue;

        @Schema(description = "会员流失率（百分比）")
        private BigDecimal churnRate;
    }
}
