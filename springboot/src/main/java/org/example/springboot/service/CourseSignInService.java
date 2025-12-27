package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CourseSignInDTO;
import org.example.springboot.entity.GymCourseBooking;
import org.example.springboot.entity.GymCourseSchedule;
import org.example.springboot.entity.GymCourseSignIn;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymCourseBookingMapper;
import org.example.springboot.mapper.GymCourseScheduleMapper;
import org.example.springboot.mapper.GymCourseSignInMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程签到业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseSignInService {

    @Resource
    private GymCourseSignInMapper signInMapper;

    @Resource
    private GymCourseBookingMapper bookingMapper;

    @Resource
    private GymCourseScheduleMapper scheduleMapper;

    /**
     * 学员签到
     * @param scheduleId 排课ID
     * @param signInDTO 签到DTO
     * @param operatorId 操作人ID(教练/管理员)
     */
    @Transactional(rollbackFor = Exception.class)
    public void signIn(Long scheduleId, CourseSignInDTO signInDTO, Long operatorId) {
        log.info("开始签到: scheduleId={}, bookingId={}, operatorId={}", 
                scheduleId, signInDTO.getBookingId(), operatorId);

        // 1. 校验预约是否存在
        GymCourseBooking booking = bookingMapper.selectById(signInDTO.getBookingId());
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        // 2. 校验预约是否属于该排课
        if (!booking.getScheduleId().equals(scheduleId)) {
            throw new BusinessException("预约与排课不匹配");
        }

        // 3. 校验预约状态
        if (!booking.isBooked()) {
            if (booking.isCancelled()) {
                throw new BusinessException("预约已取消，无法签到");
            } else if (booking.isCompleted()) {
                throw new BusinessException("预约已完成");
            } else {
                throw new BusinessException("预约状态异常，无法签到");
            }
        }

        // 4. 校验是否已签到
        LambdaQueryWrapper<GymCourseSignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseSignIn::getBookingId, signInDTO.getBookingId());
        Long count = signInMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("该学员已签到，请勿重复签到");
        }

        // 5. 校验排课是否存在
        GymCourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }

        // 6. 设置签到方式默认值
        Integer signInType = signInDTO.getSignInType();
        if (signInType == null) {
            signInType = 2; // 默认手动签到
        }

        // 7. 创建签到记录
        GymCourseSignIn signIn = GymCourseSignIn.builder()
                .bookingId(signInDTO.getBookingId())
                .userId(booking.getUserId())
                .scheduleId(scheduleId)
                .signInTime(LocalDateTime.now())
                .signInType(signInType)
                .operatorId(operatorId)
                .build();

        int result = signInMapper.insert(signIn);
        if (result <= 0) {
            throw new BusinessException("签到失败");
        }

        log.info("签到成功，签到ID: {}, 用户ID: {}", signIn.getId(), booking.getUserId());
    }

    /**
     * 根据预约ID列表查询签到信息
     * @param bookingId 预约ID
     * @return 签到记录
     */
    public GymCourseSignIn getByBookingId(Long bookingId) {
        LambdaQueryWrapper<GymCourseSignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseSignIn::getBookingId, bookingId);
        wrapper.last("LIMIT 1");
        return signInMapper.selectOne(wrapper);
    }

    /**
     * 批量查询签到信息
     * @param wrapper 查询条件
     * @return 签到记录列表
     */
    public List<GymCourseSignIn> listByBookingIds(LambdaQueryWrapper<GymCourseSignIn> wrapper) {
        if (wrapper == null) {
            return new ArrayList<>();
        }
        try {
            return signInMapper.selectList(wrapper);
        } catch (Exception e) {
            log.error("查询签到信息失败", e);
            return new ArrayList<>();
        }
    }
}