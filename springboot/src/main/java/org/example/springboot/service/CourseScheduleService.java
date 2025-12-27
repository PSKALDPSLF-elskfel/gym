package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CourseScheduleCreateDTO;
import org.example.springboot.dto.command.CourseScheduleUpdateDTO;
import org.example.springboot.dto.response.CourseScheduleResponseDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.entity.GymCourseSchedule;
import org.example.springboot.enums.CourseScheduleStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymCourseMapper;
import org.example.springboot.mapper.GymCourseScheduleMapper;
import org.example.springboot.mapper.GymCoachMapper;
import org.example.springboot.service.convert.CourseScheduleConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程时间安排业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseScheduleService {

    @Resource
    private GymCourseScheduleMapper scheduleMapper;

    @Resource
    private GymCourseMapper courseMapper;

    @Resource
    private GymCoachMapper coachMapper;

    /**
     * 创建课程时间安排
     * @param createDTO 创建命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(CourseScheduleCreateDTO createDTO) {
        log.info("开始创建课程时间安排: courseId={}", createDTO.getCourseId());

        // 1. 验证课程是否存在
        GymCourse course = courseMapper.selectById(createDTO.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 2. 验证时间合法性
        if (!createDTO.getStartTime().isBefore(createDTO.getEndTime())) {
            throw new BusinessException("开始时间必须早于结束时间");
        }

        // 3. 验证开始时间不能是过去时间
        if (createDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }

        // 4. 验证状态
        if (!CourseScheduleStatus.isValidCode(createDTO.getStatus())) {
            throw new BusinessException("无效的排课状态");
        }

        // 5. 检测时间冲突
        if (hasTimeConflict(createDTO.getCourseId(), createDTO.getStartTime(), createDTO.getEndTime(), null)) {
            throw new BusinessException("该时间段与已有排课冲突");
        }

        // 6. 转换并保存
        GymCourseSchedule entity = CourseScheduleConvert.createCommandToEntity(createDTO);
        int result = scheduleMapper.insert(entity);
        if (result <= 0) {
            throw new BusinessException("创建课程时间安排失败");
        }

        log.info("课程时间安排创建成功，ID: {}", entity.getId());
    }

    /**
     * 更新课程时间安排
     * @param updateDTO 更新命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseScheduleUpdateDTO updateDTO) {
        log.info("开始更新课程时间安排，ID: {}", updateDTO.getId());

        // 1. 检查排课是否存在
        GymCourseSchedule existingSchedule = scheduleMapper.selectById(updateDTO.getId());
        if (existingSchedule == null) {
            throw new BusinessException("课程时间安排不存在");
        }

        // 2. 验证课程是否存在
        GymCourse course = courseMapper.selectById(updateDTO.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 3. 验证时间合法性
        if (!updateDTO.getStartTime().isBefore(updateDTO.getEndTime())) {
            throw new BusinessException("开始时间必须早于结束时间");
        }

        // 4. 验证状态
        if (!CourseScheduleStatus.isValidCode(updateDTO.getStatus())) {
            throw new BusinessException("无效的排课状态");
        }

        // 5. 检测时间冲突（排除当前记录）
        if (hasTimeConflict(updateDTO.getCourseId(), updateDTO.getStartTime(), updateDTO.getEndTime(), updateDTO.getId())) {
            throw new BusinessException("该时间段与已有排课冲突");
        }

        // 6. 验证最大参与人数不能小于当前参与人数
        if (updateDTO.getMaxParticipants() < existingSchedule.getCurrentParticipants()) {
            throw new BusinessException("最大参与人数不能小于当前参与人数");
        }

        // 7. 转换并更新
        GymCourseSchedule entity = CourseScheduleConvert.updateCommandToEntity(updateDTO);
        entity.setCurrentParticipants(existingSchedule.getCurrentParticipants());
        int result = scheduleMapper.updateById(entity);
        if (result <= 0) {
            throw new BusinessException("更新课程时间安排失败");
        }

        log.info("课程时间安排更新成功，ID: {}", updateDTO.getId());
    }

    /**
     * 删除课程时间安排
     * @param id 排课ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("开始删除课程时间安排，ID: {}", id);

        // 1. 检查排课是否存在
        GymCourseSchedule existingSchedule = scheduleMapper.selectById(id);
        if (existingSchedule == null) {
            throw new BusinessException("课程时间安排不存在");
        }

        // 2. 检查是否有预约记录
        if (existingSchedule.getCurrentParticipants() > 0) {
            throw new BusinessException("该排课已有预约记录，无法删除");
        }

        // 3. 删除排课
        int result = scheduleMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除课程时间安排失败");
        }

        log.info("课程时间安排删除成功，ID: {}", id);
    }

    /**
     * 根据ID查询课程时间安排
     * @param id 排课ID
     * @return 课程时间安排响应DTO
     */
    public CourseScheduleResponseDTO getById(Long id) {
        log.info("查询课程时间安排，ID: {}", id);

        GymCourseSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("课程时间安排不存在");
        }

        // 查询课程信息
        GymCourse course = courseMapper.selectById(schedule.getCourseId());

        return CourseScheduleConvert.entityToResponse(schedule, course);
    }

    /**
     * 分页查询课程时间安排
     * @param current 当前页
     * @param size 每页大小
     * @param courseId 课程ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    public Page<CourseScheduleResponseDTO> selectPage(Long current, Long size, String courseId, 
                                                       Integer status, String startDate, String endDate) {
        return selectPage(current, size, courseId, status, startDate, endDate, null);
    }

    /**
     * 分页查询课程时间安排(支持按教练筛选)
     * @param current 当前页
     * @param size 每页大小
     * @param courseId 课程ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param coachId 教练ID(可选,为null则不筛选)
     * @return 分页结果
     */
    public Page<CourseScheduleResponseDTO> selectPage(Long current, Long size, String courseId, 
                                                       Integer status, String startDate, String endDate, Long coachId) {
        log.info("分页查询课程时间安排: current={}, size={}, courseId={}, status={}, startDate={}, endDate={}, coachId={}", 
                 current, size, courseId, status, startDate, endDate, coachId);

        Page<GymCourseSchedule> page = new Page<>(current, size);
        LambdaQueryWrapper<GymCourseSchedule> wrapper = new LambdaQueryWrapper<>();

        // 如果指定了教练ID,需要先查询该教练负责的课程
        List<String> coachCourseIds = null;
        if (coachId != null) {
            LambdaQueryWrapper<GymCourse> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.eq(GymCourse::getCoachId, coachId);
            List<GymCourse> coachCourses = courseMapper.selectList(courseWrapper);
            coachCourseIds = coachCourses.stream()
                    .map(GymCourse::getId)
                    .collect(Collectors.toList());
            
            // 如果教练没有负责的课程,直接返回空结果
            if (coachCourseIds.isEmpty()) {
                log.info("教练ID {} 没有负责的课程", coachId);
                Page<CourseScheduleResponseDTO> emptyPage = new Page<>(current, size, 0);
                emptyPage.setRecords(List.of());
                return emptyPage;
            }
            
            wrapper.in(GymCourseSchedule::getCourseId, coachCourseIds);
        }

        // 条件查询
        if (courseId != null && !courseId.isEmpty()) {
            wrapper.eq(GymCourseSchedule::getCourseId, courseId);
        }
        if (status != null) {
            wrapper.eq(GymCourseSchedule::getStatus, status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(GymCourseSchedule::getStartTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(GymCourseSchedule::getStartTime, endDate + " 23:59:59");
        }

        wrapper.orderByAsc(GymCourseSchedule::getStartTime);

        Page<GymCourseSchedule> schedulePage = scheduleMapper.selectPage(page, wrapper);

        // 批量查询课程信息
        List<String> courseIds = schedulePage.getRecords().stream()
                .map(GymCourseSchedule::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<String, GymCourse> courseMap = courseIds.isEmpty() ? 
                Map.of() : 
                courseMapper.selectBatchIds(courseIds).stream()
                        .collect(Collectors.toMap(GymCourse::getId, c -> c));

        // 转换为DTO
        Page<CourseScheduleResponseDTO> dtoPage = new Page<>(schedulePage.getCurrent(), schedulePage.getSize(), schedulePage.getTotal());
        List<CourseScheduleResponseDTO> dtoList = schedulePage.getRecords().stream()
                .map(schedule -> CourseScheduleConvert.entityToResponse(schedule, courseMap.get(schedule.getCourseId())))
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 根据课程ID查询排课列表
     * @param courseId 课程ID
     * @return 排课列表
     */
    public List<CourseScheduleResponseDTO> listByCourseId(String courseId) {
        log.info("查询课程的排课列表，courseId: {}", courseId);

        LambdaQueryWrapper<GymCourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseSchedule::getCourseId, courseId)
                .orderByAsc(GymCourseSchedule::getStartTime);

        List<GymCourseSchedule> scheduleList = scheduleMapper.selectList(wrapper);

        // 查询课程信息
        GymCourse course = courseMapper.selectById(courseId);

        return scheduleList.stream()
                .map(schedule -> CourseScheduleConvert.entityToResponse(schedule, course))
                .collect(Collectors.toList());
    }

    /**
     * 更新排课状态
     * @param id 排课ID
     * @param status 状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("更新排课状态，ID: {}, 状态: {}", id, status);

        // 1. 检查排课是否存在
        GymCourseSchedule existingSchedule = scheduleMapper.selectById(id);
        if (existingSchedule == null) {
            throw new BusinessException("课程时间安排不存在");
        }

        // 2. 验证状态
        if (!CourseScheduleStatus.isValidCode(status)) {
            throw new BusinessException("无效的排课状态");
        }

        // 3. 更新状态
        existingSchedule.setStatus(status);
        existingSchedule.setUpdateTime(LocalDateTime.now());
        int result = scheduleMapper.updateById(existingSchedule);
        if (result <= 0) {
            throw new BusinessException("更新排课状态失败");
        }

        log.info("排课状态更新成功，ID: {}", id);
    }

    /**
     * 批量删除过期排课
     * @return 删除的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteExpiredSchedules() {
        log.info("开始批量删除过期排课");

        // 查询所有已过期的排课（结束时间早于当前时间）
        LambdaQueryWrapper<GymCourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(GymCourseSchedule::getEndTime, LocalDateTime.now());

        List<GymCourseSchedule> expiredSchedules = scheduleMapper.selectList(wrapper);
        
        if (expiredSchedules.isEmpty()) {
            log.info("没有过期的排课需要删除");
            return 0;
        }

        // 检查是否有预约记录
        int deletedCount = 0;
        for (GymCourseSchedule schedule : expiredSchedules) {
            // 只删除没有预约记录的过期排课
            if (schedule.getCurrentParticipants() == 0) {
                scheduleMapper.deleteById(schedule.getId());
                deletedCount++;
            } else {
                log.info("排课ID {} 有预约记录，跳过删除", schedule.getId());
            }
        }

        log.info("批量删除过期排课完成，共删除 {} 条记录", deletedCount);
        return deletedCount;
    }

    /**
     * 检测时间冲突
     * @param courseId 课程ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的排课ID（用于更新时排除自己）
     * @return 是否有冲突
     */
    private boolean hasTimeConflict(String courseId, LocalDateTime startTime, LocalDateTime endTime, Long excludeId) {
        LambdaQueryWrapper<GymCourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourseSchedule::getCourseId, courseId)
                .ne(GymCourseSchedule::getStatus, CourseScheduleStatus.CANCELLED.getCode())
                .and(w -> w
                        .lt(GymCourseSchedule::getStartTime, endTime)
                        .gt(GymCourseSchedule::getEndTime, startTime)
                );
        
        if (excludeId != null) {
            wrapper.ne(GymCourseSchedule::getId, excludeId);
        }

        Long count = scheduleMapper.selectCount(wrapper);
        return count > 0;
    }
}
