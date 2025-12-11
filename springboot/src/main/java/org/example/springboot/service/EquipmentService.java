package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.*;
import org.example.springboot.dto.response.*;
import org.example.springboot.entity.*;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 器材业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class EquipmentService {

    @Resource
    private GymEquipmentMapper equipmentMapper;

    @Resource
    private GymEquipmentBookingMapper bookingMapper;

    @Resource
    private GymEquipmentQueueMapper queueMapper;

    @Resource
    private GymEquipmentMaintenanceMapper maintenanceMapper;

    @Resource
    private UserMapper userMapper;

    // ==================== 器材管理 ====================

    /**
     * 创建器材
     */
    @Transactional(rollbackFor = Exception.class)
    public EquipmentResponseDTO createEquipment(EquipmentCreateDTO createDTO) {
        log.info("开始创建器材: {}", createDTO.getName());

        // 检查器材编号是否已存在
        LambdaQueryWrapper<GymEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymEquipment::getCode, createDTO.getCode());
        Long count = equipmentMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("器材编号已存在");
        }

        // 构建实体
        GymEquipment equipment = GymEquipment.builder()
                .name(createDTO.getName())
                .code(createDTO.getCode())
                .category(createDTO.getCategory())
                .brand(createDTO.getBrand())
                .model(createDTO.getModel())
                .location(createDTO.getLocation())
                .purchaseDate(createDTO.getPurchaseDate())
                .warrantyExpire(createDTO.getWarrantyExpire())
                .status(createDTO.getStatus())
                .remark(createDTO.getRemark())
                .build();

        int result = equipmentMapper.insert(equipment);
        if (result <= 0) {
            throw new BusinessException("创建器材失败");
        }

        log.info("器材创建成功，ID: {}", equipment.getId());
        return convertToResponseDTO(equipment);
    }

    /**
     * 更新器材
     */
    @Transactional(rollbackFor = Exception.class)
    public EquipmentResponseDTO updateEquipment(EquipmentUpdateDTO updateDTO) {
        log.info("开始更新器材，ID: {}", updateDTO.getId());

        // 检查器材是否存在
        GymEquipment existingEquipment = equipmentMapper.selectById(updateDTO.getId());
        if (existingEquipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 更新字段
        if (StringUtils.hasText(updateDTO.getName())) {
            existingEquipment.setName(updateDTO.getName());
        }
        if (StringUtils.hasText(updateDTO.getCategory())) {
            existingEquipment.setCategory(updateDTO.getCategory());
        }
        if (StringUtils.hasText(updateDTO.getBrand())) {
            existingEquipment.setBrand(updateDTO.getBrand());
        }
        if (StringUtils.hasText(updateDTO.getModel())) {
            existingEquipment.setModel(updateDTO.getModel());
        }
        if (StringUtils.hasText(updateDTO.getLocation())) {
            existingEquipment.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getPurchaseDate() != null) {
            existingEquipment.setPurchaseDate(updateDTO.getPurchaseDate());
        }
        if (updateDTO.getWarrantyExpire() != null) {
            existingEquipment.setWarrantyExpire(updateDTO.getWarrantyExpire());
        }
        if (updateDTO.getStatus() != null) {
            existingEquipment.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getRemark() != null) {
            existingEquipment.setRemark(updateDTO.getRemark());
        }

        int result = equipmentMapper.updateById(existingEquipment);
        if (result <= 0) {
            throw new BusinessException("更新器材失败");
        }

        log.info("器材更新成功，ID: {}", updateDTO.getId());
        return convertToResponseDTO(existingEquipment);
    }

    /**
     * 删除器材
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(Long id) {
        log.info("开始删除器材，ID: {}", id);

        // 检查器材是否存在
        GymEquipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 检查是否有进行中的预约
        LambdaQueryWrapper<GymEquipmentBooking> bookingWrapper = new LambdaQueryWrapper<>();
        bookingWrapper.eq(GymEquipmentBooking::getEquipmentId, id)
                .in(GymEquipmentBooking::getStatus, 1, 2); // 预约中或使用中
        Long bookingCount = bookingMapper.selectCount(bookingWrapper);
        if (bookingCount > 0) {
            throw new BusinessException("该器材存在进行中的预约，无法删除");
        }

        // 检查是否有排队记录
        LambdaQueryWrapper<GymEquipmentQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(GymEquipmentQueue::getEquipmentId, id)
                .in(GymEquipmentQueue::getStatus, 1, 2); // 排队中或已叫号
        Long queueCount = queueMapper.selectCount(queueWrapper);
        if (queueCount > 0) {
            throw new BusinessException("该器材存在排队记录，无法删除");
        }

        int result = equipmentMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除器材失败");
        }

        log.info("器材删除成功，ID: {}", id);
    }

    /**
     * 根据ID查询器材
     */
    public EquipmentResponseDTO getEquipmentById(Long id) {
        log.info("查询器材，ID: {}", id);

        GymEquipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        return convertToResponseDTO(equipment);
    }

    /**
     * 分页查询器材
     */
    public Page<EquipmentResponseDTO> getEquipmentPage(Long current, Long size, String name, 
                                                        String category, Integer status) {
        log.info("分页查询器材: current={}, size={}, name={}, category={}, status={}", 
                current, size, name, category, status);

        Page<GymEquipment> page = new Page<>(current, size);
        LambdaQueryWrapper<GymEquipment> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like(GymEquipment::getName, name);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(GymEquipment::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(GymEquipment::getStatus, status);
        }

        wrapper.orderByDesc(GymEquipment::getCreateTime);

        Page<GymEquipment> equipmentPage = equipmentMapper.selectPage(page, wrapper);

        // 转换为DTO
        Page<EquipmentResponseDTO> dtoPage = new Page<>(equipmentPage.getCurrent(), 
                equipmentPage.getSize(), equipmentPage.getTotal());
        List<EquipmentResponseDTO> dtoList = equipmentPage.getRecords().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    // ==================== 器材预约 ====================

    /**
     * 创建预约
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createBooking(EquipmentBookingCreateDTO createDTO, Long userId) {
        log.info("用户 {} 开始预约器材 {}", userId, createDTO.getEquipmentId());

        // 检查器材是否存在且可用
        GymEquipment equipment = equipmentMapper.selectById(createDTO.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        if (!equipment.isNormal()) {
            throw new BusinessException("器材状态异常，无法预约");
        }

        // 检查时间合法性
        if (createDTO.getStartTime().isAfter(createDTO.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (createDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }

        // 检查时间段是否已被预约
        LambdaQueryWrapper<GymEquipmentBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymEquipmentBooking::getEquipmentId, createDTO.getEquipmentId())
                .in(GymEquipmentBooking::getStatus, 1, 2) // 预约中或使用中
                .and(w -> w.lt(GymEquipmentBooking::getStartTime, createDTO.getEndTime())
                        .gt(GymEquipmentBooking::getEndTime, createDTO.getStartTime()));
        Long count = bookingMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该时间段已被预约");
        }

        // 创建预约记录
        GymEquipmentBooking booking = GymEquipmentBooking.builder()
                .userId(userId)
                .equipmentId(createDTO.getEquipmentId())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .status(1) // 预约中
                .build();

        int result = bookingMapper.insert(booking);
        if (result <= 0) {
            throw new BusinessException("创建预约失败");
        }

        log.info("预约创建成功，ID: {}", booking.getId());
        return booking.getId();
    }

    /**
     * 取消预约
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long bookingId, Long userId) {
        log.info("用户 {} 取消预约 {}", userId, bookingId);

        GymEquipmentBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 验证是否为本人预约
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权取消他人的预约");
        }

        // 只能取消预约中的记录
        if (!booking.isPending()) {
            throw new BusinessException("只能取消预约中的记录");
        }

        booking.setStatus(0); // 已取消
        booking.setCancelTime(LocalDateTime.now());

        int result = bookingMapper.updateById(booking);
        if (result <= 0) {
            throw new BusinessException("取消预约失败");
        }

        log.info("预约取消成功，ID: {}", bookingId);
    }

    /**
     * 开始使用器材
     */
    @Transactional(rollbackFor = Exception.class)
    public void startUsing(Long bookingId, Long userId) {
        log.info("用户 {} 开始使用器材，预约ID: {}", userId, bookingId);

        GymEquipmentBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 验证是否为本人预约
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的预约");
        }

        // 只能开始预约中的记录
        if (!booking.isPending()) {
            throw new BusinessException("只能开始预约中的记录");
        }

        // 更新预约状态
        booking.setStatus(2); // 使用中
        booking.setActualStartTime(LocalDateTime.now());
        bookingMapper.updateById(booking);

        // 更新器材状态
        GymEquipment equipment = equipmentMapper.selectById(booking.getEquipmentId());
        equipment.setCurrentUserId(userId);
        equipment.setUsageStartTime(LocalDateTime.now());
        equipmentMapper.updateById(equipment);

        log.info("开始使用器材成功，预约ID: {}", bookingId);
    }

    /**
     * 结束使用器材
     */
    @Transactional(rollbackFor = Exception.class)
    public void endUsing(Long bookingId, Long userId) {
        log.info("用户 {} 结束使用器材，预约ID: {}", userId, bookingId);

        GymEquipmentBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 验证是否为本人预约
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的预约");
        }

        // 只能结束使用中的记录
        if (!booking.isInUse()) {
            throw new BusinessException("只能结束使用中的记录");
        }

        // 更新预约状态
        booking.setStatus(3); // 已完成
        booking.setActualEndTime(LocalDateTime.now());
        bookingMapper.updateById(booking);

        // 更新器材状态
        GymEquipment equipment = equipmentMapper.selectById(booking.getEquipmentId());
        equipment.setCurrentUserId(null);
        equipment.setUsageStartTime(null);
        equipmentMapper.updateById(equipment);

        log.info("结束使用器材成功，预约ID: {}", bookingId);
    }

    /**
     * 获取用户预约列表
     */
    public List<EquipmentBookingResponseDTO> getUserBookings(Long userId, Integer status) {
        log.info("查询用户预约列表: userId={}, status={}", userId, status);

        LambdaQueryWrapper<GymEquipmentBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymEquipmentBooking::getUserId, userId);
        if (status != null) {
            wrapper.eq(GymEquipmentBooking::getStatus, status);
        }
        wrapper.orderByDesc(GymEquipmentBooking::getCreateTime);

        List<GymEquipmentBooking> bookings = bookingMapper.selectList(wrapper);
        return bookings.stream()
                .map(this::convertToBookingResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== 器材排队 ====================

    /**
     * 加入排队
     */
    @Transactional(rollbackFor = Exception.class)
    public Long joinQueue(Long userId, Long equipmentId) {
        log.info("用户 {} 加入器材 {} 排队", userId, equipmentId);

        // 检查器材是否存在
        GymEquipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 检查用户是否已在队列中
        LambdaQueryWrapper<GymEquipmentQueue> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(GymEquipmentQueue::getUserId, userId)
                .eq(GymEquipmentQueue::getEquipmentId, equipmentId)
                .in(GymEquipmentQueue::getStatus, 1, 2); // 排队中或已叫号
        Long existCount = queueMapper.selectCount(checkWrapper);
        if (existCount > 0) {
            throw new BusinessException("您已在该器材的排队队列中");
        }

        // 获取当前最大排队号
        LambdaQueryWrapper<GymEquipmentQueue> maxWrapper = new LambdaQueryWrapper<>();
        maxWrapper.eq(GymEquipmentQueue::getEquipmentId, equipmentId)
                .orderByDesc(GymEquipmentQueue::getQueueNumber)
                .last("LIMIT 1");
        GymEquipmentQueue maxQueue = queueMapper.selectOne(maxWrapper);
        int nextQueueNumber = (maxQueue != null) ? maxQueue.getQueueNumber() + 1 : 1;

        // 创建排队记录
        GymEquipmentQueue queue = GymEquipmentQueue.builder()
                .userId(userId)
                .equipmentId(equipmentId)
                .queueNumber(nextQueueNumber)
                .joinTime(LocalDateTime.now())
                .status(1) // 排队中
                .build();

        int result = queueMapper.insert(queue);
        if (result <= 0) {
            throw new BusinessException("加入排队失败");
        }

        log.info("加入排队成功，ID: {}, 排队号: {}", queue.getId(), nextQueueNumber);
        return queue.getId();
    }

    /**
     * 退出排队
     */
    @Transactional(rollbackFor = Exception.class)
    public void leaveQueue(Long queueId, Long userId) {
        log.info("用户 {} 退出排队 {}", userId, queueId);

        GymEquipmentQueue queue = queueMapper.selectById(queueId);
        if (queue == null) {
            throw new BusinessException("排队记录不存在");
        }

        // 验证是否为本人
        if (!queue.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的排队");
        }

        // 只能退出排队中的记录
        if (!queue.isWaiting()) {
            throw new BusinessException("只能退出排队中的记录");
        }

        queue.setStatus(0); // 已取消
        queue.setCancelTime(LocalDateTime.now());

        int result = queueMapper.updateById(queue);
        if (result <= 0) {
            throw new BusinessException("退出排队失败");
        }

        log.info("退出排队成功，ID: {}", queueId);
    }

    /**
     * 获取排队列表
     */
    public List<EquipmentQueueResponseDTO> getQueueList(Long equipmentId) {
        log.info("查询器材排队列表: equipmentId={}", equipmentId);

        LambdaQueryWrapper<GymEquipmentQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymEquipmentQueue::getEquipmentId, equipmentId)
                .in(GymEquipmentQueue::getStatus, 1, 2) // 排队中或已叫号
                .orderByAsc(GymEquipmentQueue::getQueueNumber);

        List<GymEquipmentQueue> queues = queueMapper.selectList(wrapper);
        return queues.stream()
                .map(this::convertToQueueResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 叫号（管理员功能）
     */
    @Transactional(rollbackFor = Exception.class)
    public void callNext(Long equipmentId) {
        log.info("叫号，器材ID: {}", equipmentId);

        // 查找下一个排队中的记录
        LambdaQueryWrapper<GymEquipmentQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymEquipmentQueue::getEquipmentId, equipmentId)
                .eq(GymEquipmentQueue::getStatus, 1) // 排队中
                .orderByAsc(GymEquipmentQueue::getQueueNumber)
                .last("LIMIT 1");

        GymEquipmentQueue queue = queueMapper.selectOne(wrapper);
        if (queue == null) {
            throw new BusinessException("没有等待中的排队记录");
        }

        queue.setStatus(2); // 已叫号
        queue.setCallTime(LocalDateTime.now());

        int result = queueMapper.updateById(queue);
        if (result <= 0) {
            throw new BusinessException("叫号失败");
        }

        log.info("叫号成功，排队ID: {}, 排队号: {}", queue.getId(), queue.getQueueNumber());
    }

    // ==================== 维护记录 ====================

    /**
     * 添加维护记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMaintenance(MaintenanceCreateDTO createDTO) {
        log.info("添加维护记录，器材ID: {}", createDTO.getEquipmentId());

        // 检查器材是否存在
        GymEquipment equipment = equipmentMapper.selectById(createDTO.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 创建维护记录
        GymEquipmentMaintenance maintenance = GymEquipmentMaintenance.builder()
                .equipmentId(createDTO.getEquipmentId())
                .maintenanceType(createDTO.getMaintenanceType())
                .maintenanceDate(createDTO.getMaintenanceDate())
                .content(createDTO.getContent())
                .cost(createDTO.getCost())
                .operator(createDTO.getOperator())
                .nextMaintenanceDate(createDTO.getNextMaintenanceDate())
                .remark(createDTO.getRemark())
                .build();

        int result = maintenanceMapper.insert(maintenance);
        if (result <= 0) {
            throw new BusinessException("添加维护记录失败");
        }

        // 如果是维修，更新器材状态为维护中
        if (createDTO.getMaintenanceType() == 2) {
            equipment.setStatus(2); // 维护中
            equipmentMapper.updateById(equipment);
        }

        log.info("维护记录添加成功，ID: {}", maintenance.getId());
    }

    /**
     * 分页查询维护记录
     */
    public Page<MaintenanceResponseDTO> getMaintenanceRecords(Long current, Long size, 
                                                               Long equipmentId, Integer maintenanceType) {
        log.info("分页查询维护记录: current={}, size={}, equipmentId={}, maintenanceType={}", 
                current, size, equipmentId, maintenanceType);

        Page<GymEquipmentMaintenance> page = new Page<>(current, size);
        LambdaQueryWrapper<GymEquipmentMaintenance> wrapper = new LambdaQueryWrapper<>();

        if (equipmentId != null) {
            wrapper.eq(GymEquipmentMaintenance::getEquipmentId, equipmentId);
        }
        if (maintenanceType != null) {
            wrapper.eq(GymEquipmentMaintenance::getMaintenanceType, maintenanceType);
        }

        wrapper.orderByDesc(GymEquipmentMaintenance::getMaintenanceDate);

        Page<GymEquipmentMaintenance> maintenancePage = maintenanceMapper.selectPage(page, wrapper);

        // 转换为DTO
        Page<MaintenanceResponseDTO> dtoPage = new Page<>(maintenancePage.getCurrent(), 
                maintenancePage.getSize(), maintenancePage.getTotal());
        List<MaintenanceResponseDTO> dtoList = maintenancePage.getRecords().stream()
                .map(this::convertToMaintenanceResponseDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为器材响应DTO
     */
    private EquipmentResponseDTO convertToResponseDTO(GymEquipment equipment) {
        EquipmentResponseDTO dto = new EquipmentResponseDTO();
        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setCode(equipment.getCode());
        dto.setCategory(equipment.getCategory());
        dto.setBrand(equipment.getBrand());
        dto.setModel(equipment.getModel());
        dto.setLocation(equipment.getLocation());
        dto.setPurchaseDate(equipment.getPurchaseDate());
        dto.setWarrantyExpire(equipment.getWarrantyExpire());
        dto.setStatus(equipment.getStatus());
        dto.setStatusName(equipment.getStatusDisplayName());
        dto.setCurrentUserId(equipment.getCurrentUserId());
        dto.setUsageStartTime(equipment.getUsageStartTime());
        dto.setAvailable(equipment.isAvailable());
        dto.setRemark(equipment.getRemark());
        dto.setCreateTime(equipment.getCreateTime());
        dto.setUpdateTime(equipment.getUpdateTime());

        // 查询当前使用者昵称
        if (equipment.getCurrentUserId() != null) {
            User user = userMapper.selectById(equipment.getCurrentUserId());
            if (user != null) {
                dto.setCurrentUserName(user.getNickname());
            }
        }

        return dto;
    }

    /**
     * 转换为预约响应DTO
     */
    private EquipmentBookingResponseDTO convertToBookingResponseDTO(GymEquipmentBooking booking) {
        EquipmentBookingResponseDTO dto = new EquipmentBookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setEquipmentId(booking.getEquipmentId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setActualStartTime(booking.getActualStartTime());
        dto.setActualEndTime(booking.getActualEndTime());
        dto.setStatus(booking.getStatus());
        dto.setStatusName(booking.getStatusDisplayName());
        dto.setCancelTime(booking.getCancelTime());
        dto.setCreateTime(booking.getCreateTime());
        dto.setUpdateTime(booking.getUpdateTime());

        // 查询用户信息
        User user = userMapper.selectById(booking.getUserId());
        if (user != null) {
            dto.setUserName(user.getNickname());
        }

        // 查询器材信息
        GymEquipment equipment = equipmentMapper.selectById(booking.getEquipmentId());
        if (equipment != null) {
            dto.setEquipmentName(equipment.getName());
            dto.setEquipmentCode(equipment.getCode());
        }

        return dto;
    }

    /**
     * 转换为排队响应DTO
     */
    private EquipmentQueueResponseDTO convertToQueueResponseDTO(GymEquipmentQueue queue) {
        EquipmentQueueResponseDTO dto = new EquipmentQueueResponseDTO();
        dto.setId(queue.getId());
        dto.setUserId(queue.getUserId());
        dto.setEquipmentId(queue.getEquipmentId());
        dto.setQueueNumber(queue.getQueueNumber());
        dto.setJoinTime(queue.getJoinTime());
        dto.setCallTime(queue.getCallTime());
        dto.setStatus(queue.getStatus());
        dto.setStatusName(queue.getStatusDisplayName());
        dto.setCancelTime(queue.getCancelTime());
        dto.setCreateTime(queue.getCreateTime());

        // 查询用户信息
        User user = userMapper.selectById(queue.getUserId());
        if (user != null) {
            dto.setUserName(user.getNickname());
        }

        // 查询器材信息
        GymEquipment equipment = equipmentMapper.selectById(queue.getEquipmentId());
        if (equipment != null) {
            dto.setEquipmentName(equipment.getName());
            dto.setEquipmentCode(equipment.getCode());
        }

        return dto;
    }

    /**
     * 转换为维护记录响应DTO
     */
    private MaintenanceResponseDTO convertToMaintenanceResponseDTO(GymEquipmentMaintenance maintenance) {
        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
        dto.setId(maintenance.getId());
        dto.setEquipmentId(maintenance.getEquipmentId());
        dto.setMaintenanceType(maintenance.getMaintenanceType());
        dto.setMaintenanceTypeName(maintenance.getMaintenanceTypeDisplayName());
        dto.setMaintenanceDate(maintenance.getMaintenanceDate());
        dto.setContent(maintenance.getContent());
        dto.setCost(maintenance.getCost());
        dto.setOperator(maintenance.getOperator());
        dto.setNextMaintenanceDate(maintenance.getNextMaintenanceDate());
        dto.setRemark(maintenance.getRemark());
        dto.setCreateTime(maintenance.getCreateTime());

        // 查询器材信息
        GymEquipment equipment = equipmentMapper.selectById(maintenance.getEquipmentId());
        if (equipment != null) {
            dto.setEquipmentName(equipment.getName());
            dto.setEquipmentCode(equipment.getCode());
        }

        return dto;
    }
}
