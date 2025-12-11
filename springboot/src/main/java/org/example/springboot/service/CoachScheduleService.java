package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.*;
import org.example.springboot.dto.response.ScheduleRecordResponseDTO;
import org.example.springboot.dto.response.ScheduleRequestResponseDTO;
import org.example.springboot.dto.response.ScheduleResponseDTO;
import org.example.springboot.dto.response.ScheduleStatisticsResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.enums.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.example.springboot.service.convert.ScheduleConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 教练排班业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CoachScheduleService {

    @Resource
    private GymCoachScheduleMapper gymCoachScheduleMapper;

    @Resource
    private GymScheduleRequestMapper gymScheduleRequestMapper;

    @Resource
    private GymScheduleRecordMapper gymScheduleRecordMapper;

    @Resource
    private GymCoachLeaveMapper gymCoachLeaveMapper;

    @Resource
    private GymScheduleStatisticsMapper gymScheduleStatisticsMapper;

    @Resource
    private GymCoachMapper gymCoachMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建排班
     * @param createDTO 创建命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void createSchedule(ScheduleCreateDTO createDTO) {
        log.info("开始创建排班: 教练ID={}, 日期={}", createDTO.getCoachId(), createDTO.getWorkDate());

        // 校验教练
        GymCoach coach = gymCoachMapper.selectById(createDTO.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        if (!coach.isActive()) {
            throw new BusinessException("教练已离职");
        }

        // 校验工作类型
        if (StringUtils.hasText(createDTO.getWorkType()) && !WorkType.isValidCode(createDTO.getWorkType())) {
            throw new BusinessException("无效的工作类型");
        }

        // 检查是否有时间冲突
        LambdaQueryWrapper<GymCoachSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCoachSchedule::getCoachId, createDTO.getCoachId())
                .eq(GymCoachSchedule::getWorkDate, createDTO.getWorkDate())
                .eq(GymCoachSchedule::getStatus, ScheduleStatus.NORMAL.getCode())
                .le(GymCoachSchedule::getStartTime, createDTO.getEndTime())
                .ge(GymCoachSchedule::getEndTime, createDTO.getStartTime());
        
        Long count = gymCoachScheduleMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("该时间段已有排班，不能重复安排");
        }

        // 转换并保存
        GymCoachSchedule entity = ScheduleConvert.createCommandToEntity(createDTO);
        entity.setStatus(ScheduleStatus.NORMAL.getCode());
        int result = gymCoachScheduleMapper.insert(entity);
        if (result <= 0) {
            throw new BusinessException("创建排班失败");
        }

        log.info("排班创建成功，ID: {}", entity.getId());
    }

    /**
     * 更新排班
     * @param id 排班ID
     * @param updateDTO 更新命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Long id, ScheduleUpdateDTO updateDTO) {
        log.info("开始更新排班，ID: {}", id);

        // 检查排班是否存在
        GymCoachSchedule existingSchedule = gymCoachScheduleMapper.selectById(id);
        if (existingSchedule == null) {
            throw new BusinessException("排班不存在");
        }

        // 校验状态
        if (updateDTO.getStatus() != null && !ScheduleStatus.isValidCode(updateDTO.getStatus())) {
            throw new BusinessException("无效的排班状态");
        }

        // 校验工作类型
        if (StringUtils.hasText(updateDTO.getWorkType()) && !WorkType.isValidCode(updateDTO.getWorkType())) {
            throw new BusinessException("无效的工作类型");
        }

        // 检查是否有时间冲突（如果修改了时间）
        if (updateDTO.getStartTime() != null || updateDTO.getEndTime() != null || updateDTO.getWorkDate() != null) {
            LocalDate workDate = updateDTO.getWorkDate() != null ? updateDTO.getWorkDate() : existingSchedule.getWorkDate();
            LocalDateTime startTime = updateDTO.getStartTime() != null ? 
                LocalDateTime.of(workDate, updateDTO.getStartTime()) : 
                LocalDateTime.of(existingSchedule.getWorkDate(), existingSchedule.getStartTime());
            LocalDateTime endTime = updateDTO.getEndTime() != null ? 
                LocalDateTime.of(workDate, updateDTO.getEndTime()) : 
                LocalDateTime.of(existingSchedule.getWorkDate(), existingSchedule.getEndTime());

            LambdaQueryWrapper<GymCoachSchedule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GymCoachSchedule::getCoachId, existingSchedule.getCoachId())
                    .eq(GymCoachSchedule::getWorkDate, workDate)
                    .eq(GymCoachSchedule::getStatus, ScheduleStatus.NORMAL.getCode())
                    .le(GymCoachSchedule::getStartTime, endTime.toLocalTime())
                    .ge(GymCoachSchedule::getEndTime, startTime.toLocalTime())
                    .ne(GymCoachSchedule::getId, id);
            
            Long count = gymCoachScheduleMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException("该时间段已有排班，不能重复安排");
            }
        }

        // 转换并更新
        GymCoachSchedule entity = ScheduleConvert.updateCommandToEntity(updateDTO);
        int result = gymCoachScheduleMapper.updateById(entity);
        if (result <= 0) {
            throw new BusinessException("更新排班失败");
        }

        log.info("排班更新成功，ID: {}", id);
    }

    /**
     * 删除排班
     * @param id 排班ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        log.info("开始删除排班，ID: {}", id);

        // 检查排班是否存在
        GymCoachSchedule existingSchedule = gymCoachScheduleMapper.selectById(id);
        if (existingSchedule == null) {
            throw new BusinessException("排班不存在");
        }

        // 检查是否已经有打卡记录
        LambdaQueryWrapper<GymScheduleRecord> recordQuery = new LambdaQueryWrapper<>();
        recordQuery.eq(GymScheduleRecord::getScheduleId, id);
        Long recordCount = gymScheduleRecordMapper.selectCount(recordQuery);
        if (recordCount > 0) {
            throw new BusinessException("该排班已有打卡记录，不能删除");
        }

        int result = gymCoachScheduleMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除排班失败");
        }

        log.info("排班删除成功，ID: {}", id);
    }

    /**
     * 根据ID查询排班
     * @param id 排班ID
     * @return 排班响应DTO
     */
    public ScheduleResponseDTO getScheduleById(Long id) {
        log.info("查询排班，ID: {}", id);

        GymCoachSchedule entity = gymCoachScheduleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("排班不存在");
        }

        ScheduleResponseDTO result = ScheduleConvert.entityToResponse(entity);
        
        // 设置教练姓名
        GymCoach coach = gymCoachMapper.selectById(entity.getCoachId());
        if (coach != null) {
            User user = userMapper.selectById(coach.getUserId());
            if (user != null) {
                result.setCoachName(user.getNickname());
            }
        }

        return result;
    }

    /**
     * 查询教练月度排班
     * @param coachId 教练ID
     * @param year 年份
     * @param month 月份
     * @return 排班列表
     */
    public List<ScheduleResponseDTO> getMonthlySchedule(Long coachId, int year, int month) {
        log.info("查询教练月度排班: 教练ID={}, 年={}, 月={}", coachId, year, month);

        LambdaQueryWrapper<GymCoachSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCoachSchedule::getCoachId, coachId)
                .apply("YEAR(work_date) = {0} AND MONTH(work_date) = {1}", year, month)
                .orderByAsc(GymCoachSchedule::getWorkDate)
                .orderByAsc(GymCoachSchedule::getStartTime);

        List<GymCoachSchedule> entities = gymCoachScheduleMapper.selectList(queryWrapper);
        List<ScheduleResponseDTO> result = ScheduleConvert.entityListToResponseList(entities);
        
        // 设置教练姓名
        if (!result.isEmpty()) {
            GymCoach coach = gymCoachMapper.selectById(coachId);
            if (coach != null) {
                User user = userMapper.selectById(coach.getUserId());
                if (user != null) {
                    String coachName = user.getNickname();
                    result.forEach(dto -> dto.setCoachName(coachName));
                }
            }
        }

        return result;
    }

    /**
     * 创建排班申请
     * @param coachId 教练ID
     * @param createDTO 创建命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void createScheduleRequest(Long coachId, ScheduleRequestCreateDTO createDTO) {
        log.info("教练{}创建排班申请: 类型={}", coachId, createDTO.getRequestType());

        // 校验教练
        GymCoach coach = gymCoachMapper.selectById(coachId);
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        if (!coach.isActive()) {
            throw new BusinessException("教练已离职");
        }

        // 校验申请类型
        if (!ScheduleRequestType.isValidCode(createDTO.getRequestType())) {
            throw new BusinessException("无效的申请类型");
        }

        // 校验换班申请参数
        if (ScheduleRequestType.SHIFT_SWAP.getCode().equals(createDTO.getRequestType())) {
            if (createDTO.getExchangeWithCoachId() == null) {
                throw new BusinessException("换班申请必须指定换班对象");
            }
            if (createDTO.getExchangeScheduleId() == null) {
                throw new BusinessException("换班申请必须指定被交换的排班");
            }
            
            // 检查换班对象是否存在且在职
            if (createDTO.getExchangeWithCoachId().equals(coachId)) {
                throw new BusinessException("不能与自己换班");
            }
            
            GymCoach exchangeCoach = gymCoachMapper.selectById(createDTO.getExchangeWithCoachId());
            if (exchangeCoach == null || !exchangeCoach.isActive()) {
                throw new BusinessException("换班对象教练不存在或已离职");
            }
            
            // 检查被交换的排班是否存在
            GymCoachSchedule exchangeSchedule = gymCoachScheduleMapper.selectById(createDTO.getExchangeScheduleId());
            if (exchangeSchedule == null || !exchangeSchedule.isNormal()) {
                throw new BusinessException("被交换的排班不存在或状态异常");
            }
            if (!exchangeSchedule.getCoachId().equals(createDTO.getExchangeWithCoachId())) {
                throw new BusinessException("被交换的排班不属于指定的换班对象");
            }
        }

        // 转换并保存
        GymScheduleRequest entity = ScheduleConvert.createRequestCommandToEntity(createDTO);
        entity.setCoachId(coachId);
        entity.setStatus(ScheduleRequestStatus.PENDING.getCode());
        int result = gymScheduleRequestMapper.insert(entity);
        if (result <= 0) {
            throw new BusinessException("创建排班申请失败");
        }

        log.info("排班申请创建成功，ID: {}", entity.getId());
    }

    /**
     * 审批排班申请
     * @param requestId 申请ID
     * @param adminId 管理员ID
     * @param approveDTO 审批命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveScheduleRequest(Long requestId, Long adminId, ScheduleRequestApproveDTO approveDTO) {
        log.info("审批排班申请: 申请ID={}, 管理员ID={}, 状态={}", requestId, adminId, approveDTO.getStatus());

        // 检查申请是否存在
        GymScheduleRequest request = gymScheduleRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("排班申请不存在");
        }

        // 校验审批状态
        if (!ScheduleRequestStatus.APPROVED.getCode().equals(approveDTO.getStatus()) &&
            !ScheduleRequestStatus.REJECTED.getCode().equals(approveDTO.getStatus())) {
            throw new BusinessException("无效的审批状态");
        }

        // 检查申请是否已经是最终状态
        if (request.isApproved() || request.isRejected()) {
            throw new BusinessException("该申请已被处理");
        }

        // 更新申请状态
        request.setStatus(approveDTO.getStatus());
        request.setApproverId(adminId);
        request.setApproveTime(LocalDateTime.now());
        request.setApproveRemark(approveDTO.getApproveRemark());
        
        int result = gymScheduleRequestMapper.updateById(request);
        if (result <= 0) {
            throw new BusinessException("审批排班申请失败");
        }

        // 如果审批通过，执行相应的操作
        if (request.isApproved()) {
            handleApprovedScheduleRequest(request);
        }

        log.info("排班申请审批成功，ID: {}", requestId);
    }

    /**
     * 处理已批准的排班申请
     * @param request 已批准的申请
     */
    private void handleApprovedScheduleRequest(GymScheduleRequest request) {
        if (request.isLeaveRequest()) {
            // 调休申请 - 创建休息排班
            createHolidayScheduleForLeave(request);
        } else if (request.isShiftSwapRequest()) {
            // 换班申请 - 交换排班
            swapSchedules(request);
        }
        // 加班申请不需要特殊处理，只需记录即可
    }

    /**
     * 为调休申请创建休息排班
     * @param request 调休申请
     */
    private void createHolidayScheduleForLeave(GymScheduleRequest request) {
        GymCoachSchedule holidaySchedule = GymCoachSchedule.builder()
                .coachId(request.getCoachId())
                .workDate(request.getTargetDate())
                .startTime(java.time.LocalTime.of(0, 0))
                .endTime(java.time.LocalTime.of(23, 59))
                .workType(WorkType.HOLIDAY.getCode())
                .status(ScheduleStatus.NORMAL.getCode())
                .remark("调休申请批准")
                .build();
        
        gymCoachScheduleMapper.insert(holidaySchedule);
    }

    /**
     * 执行换班操作
     * @param request 换班申请
     */
    private void swapSchedules(GymScheduleRequest request) {
        // 获取两个排班
        GymCoachSchedule schedule1 = gymCoachScheduleMapper.selectById(request.getExchangeScheduleId());
        if (schedule1 == null) return;
        
        // 更新排班的教练ID
        schedule1.setCoachId(request.getCoachId());
        gymCoachScheduleMapper.updateById(schedule1);
        
        // 创建反向排班给原教练
        GymCoachSchedule schedule2 = GymCoachSchedule.builder()
                .coachId(request.getExchangeWithCoachId())
                .workDate(schedule1.getWorkDate())
                .startTime(schedule1.getStartTime())
                .endTime(schedule1.getEndTime())
                .workType(schedule1.getWorkType())
                .location(schedule1.getLocation())
                .status(ScheduleStatus.NORMAL.getCode())
                .remark("换班申请批准")
                .build();
        
        gymCoachScheduleMapper.insert(schedule2);
    }

    /**
     * 查询教练的排班申请记录
     * @param coachId 教练ID
     * @param page 分页参数
     * @param size 分页参数
     * @return 排班申请分页结果
     */
    public Page<ScheduleRequestResponseDTO> getScheduleRequestsByCoach(Long coachId, Long page, Long size) {
        log.info("查询教练的排班申请记录: 教练ID={}, page={}, size={}", coachId, page, size);

        Page<GymScheduleRequest> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GymScheduleRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymScheduleRequest::getCoachId, coachId)
                .orderByDesc(GymScheduleRequest::getCreateTime);

        Page<GymScheduleRequest> pageResult = gymScheduleRequestMapper.selectPage(pageParam, queryWrapper);
        Page<ScheduleRequestResponseDTO> result = new Page<>(page, size, pageResult.getTotal());
        
        List<ScheduleRequestResponseDTO> records = ScheduleConvert.requestEntityListToResponseList(pageResult.getRecords());
        
        // 设置相关姓名信息
        enrichRequestResponse(records);
        
        result.setRecords(records);
        return result;
    }

    /**
     * 补充申请响应中的姓名信息
     * @param records 申请响应列表
     */
    private void enrichRequestResponse(List<ScheduleRequestResponseDTO> records) {
        for (ScheduleRequestResponseDTO dto : records) {
            // 设置教练姓名
            GymCoach coach = gymCoachMapper.selectById(dto.getCoachId());
            if (coach != null) {
                User user = userMapper.selectById(coach.getUserId());
                if (user != null) {
                    dto.setCoachName(user.getNickname());
                }
            }
            
            // 设置换班对象姓名
            if (dto.getExchangeWithCoachId() != null) {
                GymCoach exchangeCoach = gymCoachMapper.selectById(dto.getExchangeWithCoachId());
                if (exchangeCoach != null) {
                    User user = userMapper.selectById(exchangeCoach.getUserId());
                    if (user != null) {
                        dto.setExchangeWithCoachName(user.getNickname());
                    }
                }
            }
            
            // 设置审批人姓名
            if (dto.getApproverId() != null) {
                User approver = userMapper.selectById(dto.getApproverId());
                if (approver != null) {
                    dto.setApproverName(approver.getNickname());
                }
            }
        }
    }

    /**
     * 打卡入场
     * @param coachId 教练ID
     * @param checkDTO 打卡命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long coachId, AttendanceCheckDTO checkDTO) {
        log.info("教练{}打卡入场: 排班ID={}", coachId, checkDTO.getScheduleId());

        // 检查排班是否存在且属于该教练
        GymCoachSchedule schedule = gymCoachScheduleMapper.selectById(checkDTO.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        if (!schedule.getCoachId().equals(coachId)) {
            throw new BusinessException("该排班不属于当前教练");
        }
        if (!schedule.isNormal()) {
            throw new BusinessException("排班状态异常");
        }

        // 检查是否已有打卡记录
        LambdaQueryWrapper<GymScheduleRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymScheduleRecord::getScheduleId, checkDTO.getScheduleId());
        GymScheduleRecord existingRecord = gymScheduleRecordMapper.selectOne(queryWrapper);
        
        LocalDateTime now = LocalDateTime.now();
        
        if (existingRecord != null) {
            // 已有记录，更新入场信息
            existingRecord.setCheckInTime(now);
            existingRecord.setCheckInLocation(checkDTO.getLocation());
            existingRecord.setRemark(checkDTO.getRemark());
            
            // 判断是否迟到
            LocalDateTime scheduledStartTime = LocalDateTime.of(schedule.getWorkDate(), schedule.getStartTime());
            if (now.isAfter(scheduledStartTime.plusMinutes(15))) {
                existingRecord.setStatus(AttendanceStatus.LATE.getCode());
                existingRecord.setAttendanceScore(90); // 默认迟到扣10分
            } else {
                existingRecord.setStatus(AttendanceStatus.CHECKED_IN.getCode());
                existingRecord.setAttendanceScore(100);
            }
            
            gymScheduleRecordMapper.updateById(existingRecord);
        } else {
            // 新建打卡记录
            GymScheduleRecord record = new GymScheduleRecord();
            record.setScheduleId(checkDTO.getScheduleId());
            record.setCoachId(coachId);
            record.setCheckInTime(now);
            record.setCheckInLocation(checkDTO.getLocation());
            record.setRemark(checkDTO.getRemark());
            
            // 判断是否迟到
            LocalDateTime scheduledStartTime = LocalDateTime.of(schedule.getWorkDate(), schedule.getStartTime());
            if (now.isAfter(scheduledStartTime.plusMinutes(15))) {
                record.setStatus(AttendanceStatus.LATE.getCode());
                record.setAttendanceScore(90); // 默认迟到扣10分
            } else {
                record.setStatus(AttendanceStatus.CHECKED_IN.getCode());
                record.setAttendanceScore(100);
            }
            
            gymScheduleRecordMapper.insert(record);
        }

        log.info("教练{}入场打卡成功", coachId);
    }

    /**
     * 打卡离场
     * @param coachId 教练ID
     * @param checkDTO 打卡命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Long coachId, AttendanceCheckDTO checkDTO) {
        log.info("教练{}打卡离场: 排班ID={}", coachId, checkDTO.getScheduleId());

        // 检查排班是否存在且属于该教练
        GymCoachSchedule schedule = gymCoachScheduleMapper.selectById(checkDTO.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        if (!schedule.getCoachId().equals(coachId)) {
            throw new BusinessException("该排班不属于当前教练");
        }
        if (!schedule.isNormal()) {
            throw new BusinessException("排班状态异常");
        }

        // 检查是否有入场打卡记录
        LambdaQueryWrapper<GymScheduleRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymScheduleRecord::getScheduleId, checkDTO.getScheduleId());
        GymScheduleRecord existingRecord = gymScheduleRecordMapper.selectOne(queryWrapper);
        
        if (existingRecord == null || existingRecord.getCheckInTime() == null) {
            throw new BusinessException("请先进行入场打卡");
        }
        
        LocalDateTime now = LocalDateTime.now();
        existingRecord.setCheckOutTime(now);
        existingRecord.setCheckOutLocation(checkDTO.getLocation());
        
        // 判断是否早退
        LocalDateTime scheduledEndTime = LocalDateTime.of(schedule.getWorkDate(), schedule.getEndTime());
        if (now.isBefore(scheduledEndTime.minusMinutes(15))) {
            existingRecord.setStatus(AttendanceStatus.EARLY_LEAVE.getCode());
            // 如果之前是迟到，则保持迟到状态；否则设置为早退
            if (!existingRecord.isLate()) {
                existingRecord.setAttendanceScore(Math.max(0, existingRecord.getAttendanceScore() - 10)); // 早退扣10分
            }
        } else {
            // 正常离场
            if (existingRecord.isLate()) {
                existingRecord.setStatus(AttendanceStatus.LATE.getCode()); // 仍然是迟到状态
            } else {
                existingRecord.setStatus(AttendanceStatus.CHECKED_OUT.getCode());
            }
        }
        
        // 添加备注
        if (StringUtils.hasText(checkDTO.getRemark())) {
            existingRecord.setRemark(existingRecord.getRemark() != null ? 
                existingRecord.getRemark() + "; " + checkDTO.getRemark() : 
                checkDTO.getRemark());
        }
        
        gymScheduleRecordMapper.updateById(existingRecord);

        log.info("教练{}离场打卡成功", coachId);
    }

    /**
     * 查询教练的打卡记录
     * @param coachId 教练ID
     * @param page 分页参数
     * @param size 分页参数
     * @return 打卡记录分页结果
     */
    public Page<ScheduleRecordResponseDTO> getAttendanceRecords(Long coachId, Long page, Long size) {
        log.info("查询教练的打卡记录: 教练ID={}, page={}, size={}", coachId, page, size);

        Page<GymScheduleRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<GymScheduleRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymScheduleRecord::getCoachId, coachId)
                .orderByDesc(GymScheduleRecord::getCreateTime);

        Page<GymScheduleRecord> pageResult = gymScheduleRecordMapper.selectPage(pageParam, queryWrapper);
        Page<ScheduleRecordResponseDTO> result = new Page<>(page, size, pageResult.getTotal());
        
        List<ScheduleRecordResponseDTO> records = ScheduleConvert.recordEntityListToResponseList(pageResult.getRecords());
        
        // 设置教练姓名
        enrichRecordResponse(records);
        
        result.setRecords(records);
        return result;
    }

    /**
     * 补充打卡记录响应中的姓名信息
     * @param records 打卡记录响应列表
     */
    private void enrichRecordResponse(List<ScheduleRecordResponseDTO> records) {
        for (ScheduleRecordResponseDTO dto : records) {
            // 设置教练姓名
            GymCoach coach = gymCoachMapper.selectById(dto.getCoachId());
            if (coach != null) {
                User user = userMapper.selectById(coach.getUserId());
                if (user != null) {
                    dto.setCoachName(user.getNickname());
                }
            }
        }
    }

    /**
     * 查询教练的月度统计
     * @param coachId 教练ID
     * @param year 年份
     * @param month 月份
     * @return 统计信息
     */
    public ScheduleStatisticsResponseDTO getMonthlyStatistics(Long coachId, int year, int month) {
        log.info("查询教练月度统计: 教练ID={}, 年={}, 月={}", coachId, year, month);

        LocalDate statisticsDate = LocalDate.of(year, month, 1);
        
        LambdaQueryWrapper<GymScheduleStatistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymScheduleStatistics::getCoachId, coachId)
                .eq(GymScheduleStatistics::getStatisticsDate, statisticsDate);

        GymScheduleStatistics entity = gymScheduleStatisticsMapper.selectOne(queryWrapper);
        if (entity == null) {
            throw new BusinessException("暂无该月度统计信息");
        }

        ScheduleStatisticsResponseDTO result = ScheduleConvert.statisticsEntityToResponse(entity);
        
        // 设置教练姓名
        GymCoach coach = gymCoachMapper.selectById(coachId);
        if (coach != null) {
            User user = userMapper.selectById(coach.getUserId());
            if (user != null) {
                result.setCoachName(user.getNickname());
            }
        }

        return result;
    }

    /**
     * 获取教练今日排班
     * @param coachId 教练ID
     * @return 今日排班列表
     */
    public List<ScheduleResponseDTO> getTodaySchedule(Long coachId) {
        log.info("查询教练今日排班: 教练ID={}", coachId);

        LambdaQueryWrapper<GymCoachSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCoachSchedule::getCoachId, coachId)
                .eq(GymCoachSchedule::getWorkDate, LocalDate.now())
                .eq(GymCoachSchedule::getStatus, ScheduleStatus.NORMAL.getCode())
                .orderByAsc(GymCoachSchedule::getStartTime);

        List<GymCoachSchedule> entities = gymCoachScheduleMapper.selectList(queryWrapper);
        List<ScheduleResponseDTO> result = ScheduleConvert.entityListToResponseList(entities);
        
        // 设置教练姓名
        if (!result.isEmpty()) {
            GymCoach coach = gymCoachMapper.selectById(coachId);
            if (coach != null) {
                User user = userMapper.selectById(coach.getUserId());
                if (user != null) {
                    String coachName = user.getNickname();
                    result.forEach(dto -> dto.setCoachName(coachName));
                }
            }
        }

        return result;
    }
}