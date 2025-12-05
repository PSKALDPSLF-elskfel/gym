package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CourseBookingCreateDTO;
import org.example.springboot.dto.response.CourseBookingDetailDTO;
import org.example.springboot.dto.response.CourseBookingResponseDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.entity.GymCourseBooking;
import org.example.springboot.entity.GymCourseSchedule;
import org.example.springboot.entity.User;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymCourseBookingMapper;
import org.example.springboot.mapper.GymCourseMapper;
import org.example.springboot.mapper.GymCourseScheduleMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.service.convert.CourseBookingConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程预约业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseBookingService {

    @Resource
    private GymCourseBookingMapper bookingMapper;

    @Resource
    private GymCourseScheduleMapper scheduleMapper;

    @Resource
    private GymCourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建预约
     * @param createDTO 创建命令DTO
     * @return 预约ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createBooking(CourseBookingCreateDTO createDTO) {
        log.info("开始创建课程预约: userId={}, scheduleId={}", createDTO.getUserId(), createDTO.getScheduleId());

        // 1. 校验课程时间是否存在
        GymCourseSchedule schedule = scheduleMapper.selectById(createDTO.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("课程时间不存在");
        }

        // 2. 校验课程是否存在
        GymCourse course = courseMapper.selectById(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 3. 校验课程是否上架
        if (!course.isOnline()) {
            throw new BusinessException("课程已下架，无法预约");
        }

        // 4. 校验课程时间状态
        if (!schedule.isNormal()) {
            if (schedule.isCancelled()) {
                throw new BusinessException("该课程时间已取消");
            } else if (schedule.isFull()) {
                throw new BusinessException("该课程时间已满员");
            } else {
                throw new BusinessException("该课程时间状态异常");
            }
        }

        // 5. 校验课程是否已过期
        if (schedule.isExpired()) {
            throw new BusinessException("该课程时间已过期，无法预约");
        }

        // 6. 校验是否可以预约(人数限制)
        if (!schedule.canBook()) {
            throw new BusinessException("该课程时间已满员");
        }

        // 7. 校验用户是否已预约该课程时间
        if (checkUserBooked(createDTO.getUserId(), createDTO.getScheduleId())) {
            throw new BusinessException("您已预约该课程时间，请勿重复预约");
        }

        // 8. 获取用户信息并计算价格
        User user = userMapper.selectById(createDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        PriceInfo priceInfo = calculatePrice(course.getPrice(), user);

        // 9. 创建预约记录
        GymCourseBooking booking = GymCourseBooking.builder()
                .userId(createDTO.getUserId())
                .scheduleId(createDTO.getScheduleId())
                .bookingTime(LocalDateTime.now())
                .originalPrice(priceInfo.getOriginalPrice())
                .actualPrice(priceInfo.getActualPrice())
                .discountRate(priceInfo.getDiscountRate())
                .status(1) // 1-已预约
                .build();

        int result = bookingMapper.insert(booking);
        if (result <= 0) {
            throw new BusinessException("创建预约失败");
        }

        // 10. 更新课程时间的当前参与人数
        schedule.setCurrentParticipants(schedule.getCurrentParticipants() + 1);
        
        // 11. 如果达到最大人数，更新状态为已满
        if (schedule.getCurrentParticipants() >= schedule.getMaxParticipants()) {
            schedule.setStatus(2); // 2-已满
        }
        
        scheduleMapper.updateById(schedule);

        log.info("课程预约创建成功，预约ID: {}, 原价: {}, 实付: {}, 折扣率: {}", 
                booking.getId(), priceInfo.getOriginalPrice(), priceInfo.getActualPrice(), priceInfo.getDiscountRate());

        return booking.getId();
    }

    /**
     * 取消预约
     * @param bookingId 预约ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long bookingId, Long userId) {
        log.info("开始取消预约: bookingId={}, userId={}", bookingId, userId);

        // 1. 校验预约是否存在
        GymCourseBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        // 2. 校验是否为本人预约
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("只能取消自己的预约");
        }

        // 3. 校验预约状态
        if (!booking.canCancel()) {
            if (booking.isCancelled()) {
                throw new BusinessException("预约已取消，请勿重复操作");
            } else if (booking.isCompleted()) {
                throw new BusinessException("预约已完成，无法取消");
            } else {
                throw new BusinessException("预约状态异常，无法取消");
            }
        }

        // 4. 更新预约状态
        booking.setStatus(0); // 0-已取消
        booking.setCancelTime(LocalDateTime.now());
        int result = bookingMapper.updateById(booking);
        if (result <= 0) {
            throw new BusinessException("取消预约失败");
        }

        // 5. 更新课程时间的当前参与人数
        GymCourseSchedule schedule = scheduleMapper.selectById(booking.getScheduleId());
        if (schedule != null) {
            schedule.setCurrentParticipants(Math.max(0, schedule.getCurrentParticipants() - 1));
            
            // 6. 如果之前是已满状态，恢复为正常状态
            if (schedule.isFull()) {
                schedule.setStatus(1); // 1-正常
            }
            
            scheduleMapper.updateById(schedule);
        }

        log.info("预约取消成功，预约ID: {}", bookingId);
    }

    /**
     * 获取预约详情
     * @param bookingId 预约ID
     * @return 预约详情
     */
    public CourseBookingDetailDTO getBookingById(Long bookingId) {
        log.info("查询预约详情: bookingId={}", bookingId);

        GymCourseBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        // 查询关联信息
        User user = userMapper.selectById(booking.getUserId());
        GymCourseSchedule schedule = scheduleMapper.selectById(booking.getScheduleId());
        GymCourse course = null;
        if (schedule != null) {
            course = courseMapper.selectById(schedule.getCourseId());
        }

        return CourseBookingConvert.toDetailDTO(booking, user, schedule, course);
    }

    /**
     * 获取用户的预约列表
     * @param userId 用户ID
     * @param status 状态筛选(可选)
     * @return 预约列表
     */
    public List<CourseBookingDetailDTO> getUserBookings(Long userId, Integer status) {
        log.info("查询用户预约列表: userId={}, status={}", userId, status);

        LambdaQueryWrapper<GymCourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseBooking::getUserId, userId);
        if (status != null) {
            wrapper.eq(GymCourseBooking::getStatus, status);
        }
        wrapper.orderByDesc(GymCourseBooking::getCreateTime);

        List<GymCourseBooking> bookings = bookingMapper.selectList(wrapper);
        
        // 批量查询关联信息
        List<Long> scheduleIds = bookings.stream()
                .map(GymCourseBooking::getScheduleId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, GymCourseSchedule> scheduleMap = scheduleIds.isEmpty() ? 
                Map.of() : 
                scheduleMapper.selectBatchIds(scheduleIds).stream()
                        .collect(Collectors.toMap(GymCourseSchedule::getId, s -> s));

        List<String> courseIds = scheduleMap.values().stream()
                .map(GymCourseSchedule::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, GymCourse> courseMap = courseIds.isEmpty() ? 
                Map.of() : 
                courseMapper.selectBatchIds(courseIds).stream()
                        .collect(Collectors.toMap(GymCourse::getId, c -> c));

        User user = userMapper.selectById(userId);

        // 转换为DetailDTO
        List<CourseBookingDetailDTO> result = new ArrayList<>();
        for (GymCourseBooking booking : bookings) {
            GymCourseSchedule schedule = scheduleMap.get(booking.getScheduleId());
            GymCourse course = schedule != null ? courseMap.get(schedule.getCourseId()) : null;
            result.add(CourseBookingConvert.toDetailDTO(booking, user, schedule, course));
        }

        return result;
    }

    /**
     * 获取课程时间的预约列表
     * @param scheduleId 排课ID
     * @return 预约列表
     */
    public List<CourseBookingDetailDTO> getScheduleBookings(Long scheduleId) {
        log.info("查询课程时间预约列表: scheduleId={}", scheduleId);

        LambdaQueryWrapper<GymCourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseBooking::getScheduleId, scheduleId);
        wrapper.orderByDesc(GymCourseBooking::getCreateTime);

        List<GymCourseBooking> bookings = bookingMapper.selectList(wrapper);

        // 查询关联信息
        GymCourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        GymCourse course = null;
        if (schedule != null) {
            course = courseMapper.selectById(schedule.getCourseId());
        }

        // 批量查询用户信息
        List<Long> userIds = bookings.stream()
                .map(GymCourseBooking::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = userIds.isEmpty() ? 
                Map.of() : 
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        // 转换为DetailDTO
        List<CourseBookingDetailDTO> result = new ArrayList<>();
        for (GymCourseBooking booking : bookings) {
            User user = userMap.get(booking.getUserId());
            result.add(CourseBookingConvert.toDetailDTO(booking, user, schedule, course));
        }

        return result;
    }

    /**
     * 分页查询预约(管理端)
     * @param current 当前页
     * @param size 每页大小
     * @param nickname 姓名(可选)
     * @param courseName 课程名称(可选)
     * @param status 状态(可选)
     * @param startDate 开始日期(可选)
     * @param endDate 结束日期(可选)
     * @return 分页结果
     */
    public Page<CourseBookingDetailDTO> selectPage(Long current, Long size, 
                                                     String nickname, String courseName, Integer status,
                                                     String startDate, String endDate) {
        log.info("分页查询预约: current={}, size={}, nickname={}, courseName={}, status={}", 
                current, size, nickname, courseName, status);

        Page<GymCourseBooking> page = new Page<>(current, size);
        LambdaQueryWrapper<GymCourseBooking> wrapper = new LambdaQueryWrapper<>();

        // 如果有姓名条件，先查询用户ID
        if (StringUtils.hasText(nickname)) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getNickname, nickname);
            List<User> users = userMapper.selectList(userWrapper);
            
            if (users.isEmpty()) {
                // 没有匹配的用户，返回空结果
                return new Page<>(current, size);
            }
            
            List<Long> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            wrapper.in(GymCourseBooking::getUserId, userIds);
        }
        
        // 如果有课程名称条件，先查询排课ID
        if (StringUtils.hasText(courseName)) {
            // 先查询课程
            LambdaQueryWrapper<GymCourse> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.like(GymCourse::getName, courseName);
            List<GymCourse> courses = courseMapper.selectList(courseWrapper);
            
            if (courses.isEmpty()) {
                // 没有匹配的课程，返回空结果
                return new Page<>(current, size);
            }
            
            List<String> courseIds = courses.stream()
                    .map(GymCourse::getId)
                    .collect(Collectors.toList());
            
            // 再查询这些课程的排课
            LambdaQueryWrapper<GymCourseSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
            scheduleWrapper.in(GymCourseSchedule::getCourseId, courseIds);
            List<GymCourseSchedule> schedules = scheduleMapper.selectList(scheduleWrapper);
            
            if (schedules.isEmpty()) {
                // 没有匹配的排课，返回空结果
                return new Page<>(current, size);
            }
            
            List<Long> scheduleIds = schedules.stream()
                    .map(GymCourseSchedule::getId)
                    .collect(Collectors.toList());
            wrapper.in(GymCourseBooking::getScheduleId, scheduleIds);
        }
        
        if (status != null) {
            wrapper.eq(GymCourseBooking::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(GymCourseBooking::getCreateTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(GymCourseBooking::getCreateTime, endDate + " 23:59:59");
        }

        wrapper.orderByDesc(GymCourseBooking::getCreateTime);

        Page<GymCourseBooking> bookingPage = bookingMapper.selectPage(page, wrapper);

        // 批量查询关联信息
        List<GymCourseBooking> bookings = bookingPage.getRecords();
        
        List<Long> userIds = bookings.stream()
                .map(GymCourseBooking::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, User> userMap = userIds.isEmpty() ? 
                Map.of() : 
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        List<Long> scheduleIds = bookings.stream()
                .map(GymCourseBooking::getScheduleId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, GymCourseSchedule> scheduleMap = scheduleIds.isEmpty() ? 
                Map.of() : 
                scheduleMapper.selectBatchIds(scheduleIds).stream()
                        .collect(Collectors.toMap(GymCourseSchedule::getId, s -> s));

        List<String> courseIds = scheduleMap.values().stream()
                .map(GymCourseSchedule::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, GymCourse> courseMap = courseIds.isEmpty() ? 
                Map.of() : 
                courseMapper.selectBatchIds(courseIds).stream()
                        .collect(Collectors.toMap(GymCourse::getId, c -> c));

        // 转换为DetailDTO
        List<CourseBookingDetailDTO> detailList = new ArrayList<>();
        for (GymCourseBooking booking : bookings) {
            User user = userMap.get(booking.getUserId());
            GymCourseSchedule schedule = scheduleMap.get(booking.getScheduleId());
            GymCourse course = schedule != null ? courseMap.get(schedule.getCourseId()) : null;
            detailList.add(CourseBookingConvert.toDetailDTO(booking, user, schedule, course));
        }

        // 构建分页结果
        Page<CourseBookingDetailDTO> resultPage = new Page<>(current, size, bookingPage.getTotal());
        resultPage.setRecords(detailList);

        return resultPage;
    }

    /**
     * 检查用户是否已预约该课程时间
     * @param userId 用户ID
     * @param scheduleId 排课ID
     * @return 是否已预约
     */
    public boolean checkUserBooked(Long userId, Long scheduleId) {
        LambdaQueryWrapper<GymCourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseBooking::getUserId, userId);
        wrapper.eq(GymCourseBooking::getScheduleId, scheduleId);
        wrapper.eq(GymCourseBooking::getStatus, 1); // 只查询已预约状态

        Long count = bookingMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 计算价格和折扣
     * @param originalPrice 原价
     * @param user 用户
     * @return 价格信息
     */
    private PriceInfo calculatePrice(BigDecimal originalPrice, User user) {
        BigDecimal discountRate = BigDecimal.ONE; // 默认无折扣
        
        // 黄金会员享受95折优惠
        if (user.isGoldMember() && user.isMemberValid()) {
            discountRate = new BigDecimal("0.95");
        } 
        // 铂金会员享受9折优惠
        else if (user.isPlatinumMember() && user.isMemberValid()) {
            discountRate = new BigDecimal("0.90");
        }

        // 计算实际价格(保留2位小数，四舍五入)
        BigDecimal actualPrice = originalPrice.multiply(discountRate)
                .setScale(2, RoundingMode.HALF_UP);

        return new PriceInfo(originalPrice, actualPrice, discountRate);
    }

    /**
     * 价格信息内部类
     */
    private static class PriceInfo {
        private final BigDecimal originalPrice;
        private final BigDecimal actualPrice;
        private final BigDecimal discountRate;

        public PriceInfo(BigDecimal originalPrice, BigDecimal actualPrice, BigDecimal discountRate) {
            this.originalPrice = originalPrice;
            this.actualPrice = actualPrice;
            this.discountRate = discountRate;
        }

        public BigDecimal getOriginalPrice() {
            return originalPrice;
        }

        public BigDecimal getActualPrice() {
            return actualPrice;
        }

        public BigDecimal getDiscountRate() {
            return discountRate;
        }
    }
}
