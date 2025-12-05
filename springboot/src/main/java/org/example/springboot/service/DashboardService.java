package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.response.DashboardStatisticsDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.entity.GymCourseBooking;
import org.example.springboot.entity.User;
import org.example.springboot.mapper.GymCourseBookingMapper;
import org.example.springboot.mapper.GymCourseMapper;
import org.example.springboot.mapper.UserMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务
 */
@Service
@Slf4j
public class DashboardService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private GymCourseMapper courseMapper;

    @Resource
    private GymCourseBookingMapper courseBookingMapper;

    /**
     * 获取仪表盘统计数据
     */
    public DashboardStatisticsDTO getStatistics() {
        DashboardStatisticsDTO dto = new DashboardStatisticsDTO();

        // 1. 统计总用户数（只统计普通用户）
        Long totalUsers = userMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "USER")
        );
        dto.setTotalUsers(totalUsers);

        // 2. 统计总课程数
        Long totalCourses = courseMapper.selectCount(null);
        dto.setTotalCourses(totalCourses);

        // 3. 统计总预约数
        Long totalBookings = courseBookingMapper.selectCount(null);
        dto.setTotalBookings(totalBookings);

        // 4. 统计会员数量（member_type > 0）
        Long memberCount = userMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "USER")
                .gt(User::getMemberType, 0)
        );
        dto.setMemberCount(memberCount);

        // 5. 会员类型分布
        dto.setMemberTypeDistribution(getMemberTypeDistribution());

        // 6. 课程预约趋势（最近7天）
        dto.setBookingTrends(getBookingTrends());

        log.info("仪表盘统计数据: 用户={}, 课程={}, 预约={}, 会员={}", 
            totalUsers, totalCourses, totalBookings, memberCount);

        return dto;
    }

    /**
     * 获取会员类型分布
     */
    private List<DashboardStatisticsDTO.MemberTypeDistribution> getMemberTypeDistribution() {
        List<User> users = userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "USER")
        );

        // 按会员类型分组统计
        Map<Integer, Long> memberTypeMap = users.stream()
            .collect(Collectors.groupingBy(
                User::getMemberType,
                Collectors.counting()
            ));

        // 转换为DTO列表
        List<DashboardStatisticsDTO.MemberTypeDistribution> distributions = new ArrayList<>();
        
        // 定义会员类型映射
        Map<Integer, String> typeNameMap = new HashMap<>();
        typeNameMap.put(0, "普通用户");
        typeNameMap.put(1, "黄金会员");
        typeNameMap.put(2, "铂金会员");

        for (Map.Entry<Integer, String> entry : typeNameMap.entrySet()) {
            DashboardStatisticsDTO.MemberTypeDistribution distribution = 
                new DashboardStatisticsDTO.MemberTypeDistribution();
            distribution.setMemberType(entry.getValue());
            distribution.setCount(memberTypeMap.getOrDefault(entry.getKey(), 0L));
            distributions.add(distribution);
        }

        return distributions;
    }

    /**
     * 获取预约趋势（最近7天）
     */
    private List<DashboardStatisticsDTO.BookingTrend> getBookingTrends() {
        List<DashboardStatisticsDTO.BookingTrend> trends = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 获取所有预约记录
        List<GymCourseBooking> allBookings = courseBookingMapper.selectList(null);

        // 按日期分组统计
        Map<String, Long> bookingCountMap = allBookings.stream()
            .collect(Collectors.groupingBy(
                booking -> {
                    if (booking.getBookingTime() != null) {
                        return booking.getBookingTime().toLocalDate().format(formatter);
                    }
                    return "未知";
                },
                Collectors.counting()
            ));

        // 生成最近7天的数据
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);

            DashboardStatisticsDTO.BookingTrend trend = 
                new DashboardStatisticsDTO.BookingTrend();
            trend.setDate(dateStr);
            trend.setCount(bookingCountMap.getOrDefault(dateStr, 0L));
            trends.add(trend);
        }

        return trends;
    }
}
