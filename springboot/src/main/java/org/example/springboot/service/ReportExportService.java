package org.example.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.request.ExportReportDTO;
import org.example.springboot.dto.response.DashboardStatisticsDTO;
import org.example.springboot.util.ExcelExportUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 报表导出服务
 */
@Service
@Slf4j
public class ReportExportService {

    @Resource
    private DashboardService dashboardService;

    /**
     * 导出报表
     */
    public byte[] exportReport(ExportReportDTO exportDTO) {
        String reportType = exportDTO.getReportType();
        
        return switch (reportType) {
            case "USER_GROWTH" -> exportUserGrowthReport(exportDTO);
            case "BOOKING_TREND" -> exportBookingTrendReport(exportDTO);
            case "WORKOUT_STATS" -> exportWorkoutStatsReport(exportDTO);
            case "REVENUE" -> exportRevenueReport(exportDTO);
            default -> exportComprehensiveReport();
        };
    }

    /**
     * 导出用户增长报表
     */
    private byte[] exportUserGrowthReport(ExportReportDTO exportDTO) {
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        
        List<String> headers = Arrays.asList("日期", "新增用户数", "累计用户数");
        List<List<Object>> dataList = new ArrayList<>();
        
        for (DashboardStatisticsDTO.UserGrowthTrend trend : statistics.getUserGrowthTrends()) {
            List<Object> row = Arrays.asList(
                trend.getDate(),
                trend.getNewUsers(),
                trend.getTotalUsers()
            );
            dataList.add(row);
        }
        
        return ExcelExportUtil.exportExcel("用户增长趋势", headers, dataList);
    }

    /**
     * 导出预约趋势报表
     */
    private byte[] exportBookingTrendReport(ExportReportDTO exportDTO) {
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        
        List<String> headers = Arrays.asList("日期", "总预约数", "已完成数", "已取消数");
        List<List<Object>> dataList = new ArrayList<>();
        
        for (DashboardStatisticsDTO.BookingTrend trend : statistics.getBookingTrends()) {
            List<Object> row = Arrays.asList(
                trend.getDate(),
                trend.getCount(),
                trend.getCompletedCount(),
                trend.getCancelledCount()
            );
            dataList.add(row);
        }
        
        return ExcelExportUtil.exportExcel("预约趋势", headers, dataList);
    }

    /**
     * 导出运动统计报表
     */
    private byte[] exportWorkoutStatsReport(ExportReportDTO exportDTO) {
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        
        List<String> headers = Arrays.asList("日期", "运动次数", "总时长(分钟)", "总热量(千卡)");
        List<List<Object>> dataList = new ArrayList<>();
        
        for (DashboardStatisticsDTO.WorkoutStatistics stat : statistics.getWorkoutStatistics()) {
            List<Object> row = Arrays.asList(
                stat.getDate(),
                stat.getWorkoutCount(),
                stat.getTotalDuration(),
                stat.getTotalCalories()
            );
            dataList.add(row);
        }
        
        return ExcelExportUtil.exportExcel("运动数据统计", headers, dataList);
    }

    /**
     * 导出收入报表
     */
    private byte[] exportRevenueReport(ExportReportDTO exportDTO) {
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        DashboardStatisticsDTO.OperationalOverview overview = statistics.getOperationalOverview();
        
        List<String> headers = Arrays.asList("指标", "数值");
        List<List<Object>> dataList = new ArrayList<>();
        
        dataList.add(Arrays.asList("本月总收入", overview.getMonthRevenue()));
        dataList.add(Arrays.asList("本月会员充值", overview.getMonthRecharge()));
        dataList.add(Arrays.asList("平均客单价", overview.getAvgOrderValue()));
        dataList.add(Arrays.asList("课程预约转化率", overview.getBookingConversionRate() + "%"));
        dataList.add(Arrays.asList("课程出席率", overview.getCourseAttendanceRate() + "%"));
        dataList.add(Arrays.asList("会员流失率", overview.getChurnRate() + "%"));
        
        return ExcelExportUtil.exportExcel("运营数据报表", headers, dataList);
    }

    /**
     * 导出综合报表
     */
    private byte[] exportComprehensiveReport() {
        DashboardStatisticsDTO statistics = dashboardService.getStatistics();
        
        // 基础数据Sheet
        List<String> basicHeaders = Arrays.asList("指标", "数值");
        List<List<Object>> basicData = new ArrayList<>();
        basicData.add(Arrays.asList("总用户数", statistics.getTotalUsers()));
        basicData.add(Arrays.asList("会员数量", statistics.getMemberCount()));
        basicData.add(Arrays.asList("教练数量", statistics.getCoachCount()));
        basicData.add(Arrays.asList("总课程数", statistics.getTotalCourses()));
        basicData.add(Arrays.asList("总预约数", statistics.getTotalBookings()));
        basicData.add(Arrays.asList("训练计划总数", statistics.getTotalTrainingPlans()));
        basicData.add(Arrays.asList("运动记录总数", statistics.getTotalWorkoutRecords()));
        basicData.add(Arrays.asList("体测记录总数", statistics.getTotalBodyTests()));
        basicData.add(Arrays.asList("本月新增用户", statistics.getMonthNewUsers()));
        basicData.add(Arrays.asList("本周新增用户", statistics.getWeekNewUsers()));
        basicData.add(Arrays.asList("今日活跃用户", statistics.getTodayActiveUsers()));
        
        ExcelExportUtil.SheetData basicSheet = new ExcelExportUtil.SheetData(basicHeaders, basicData);
        
        // 会员类型分布Sheet
        List<String> memberHeaders = Arrays.asList("会员类型", "数量", "占比(%)");
        List<List<Object>> memberData = new ArrayList<>();
        for (DashboardStatisticsDTO.MemberTypeDistribution dist : statistics.getMemberTypeDistribution()) {
            memberData.add(Arrays.asList(
                dist.getMemberType(),
                dist.getCount(),
                dist.getPercentage()
            ));
        }
        ExcelExportUtil.SheetData memberSheet = new ExcelExportUtil.SheetData(memberHeaders, memberData);
        
        // 课程受欢迎度Sheet
        List<String> courseHeaders = Arrays.asList("课程名称", "预约次数", "满座率(%)");
        List<List<Object>> courseData = new ArrayList<>();
        for (DashboardStatisticsDTO.CoursePopularity popularity : statistics.getCoursePopularities()) {
            courseData.add(Arrays.asList(
                popularity.getCourseName(),
                popularity.getBookingCount(),
                popularity.getOccupancyRate()
            ));
        }
        ExcelExportUtil.SheetData courseSheet = new ExcelExportUtil.SheetData(courseHeaders, courseData);
        
        // 教练工作量Sheet
        List<String> coachHeaders = Arrays.asList("教练姓名", "课程数量", "学员数量", "平均评分");
        List<List<Object>> coachData = new ArrayList<>();
        for (DashboardStatisticsDTO.CoachWorkload workload : statistics.getCoachWorkloads()) {
            coachData.add(Arrays.asList(
                workload.getCoachName(),
                workload.getCourseCount(),
                workload.getStudentCount(),
                workload.getRating()
            ));
        }
        ExcelExportUtil.SheetData coachSheet = new ExcelExportUtil.SheetData(coachHeaders, coachData);
        
        return ExcelExportUtil.exportMultiSheetExcel(java.util.Map.of(
            "基础数据", basicSheet,
            "会员分布", memberSheet,
            "课程受欢迎度", courseSheet,
            "教练工作量", coachSheet
        ));
    }
}
