package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.NotificationCreateDTO;
import org.example.springboot.dto.response.NotificationResponseDTO;
import org.example.springboot.entity.SysNotification;
import org.example.springboot.entity.SysNotificationRead;
import org.example.springboot.entity.User;
import org.example.springboot.enums.NotificationPriority;
import org.example.springboot.enums.NotificationType;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.SysNotificationMapper;
import org.example.springboot.mapper.SysNotificationReadMapper;
import org.example.springboot.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统通知服务类
 * @author system
 */
@Slf4j
@Service
public class NotificationService {

    @Resource
    private SysNotificationMapper notificationMapper;

    @Resource
    private SysNotificationReadMapper notificationReadMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 发送通知（单个用户或群发）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long sendNotification(NotificationCreateDTO dto) {
        log.info("发送通知: {}", dto.getTitle());

        // 验证通知类型
        NotificationType type = NotificationType.fromCode(dto.getNotificationType());
        if (type == null) {
            throw new BusinessException("无效的通知类型");
        }

        // 构建通知实体
        SysNotification notification = SysNotification.builder()
                .notificationType(dto.getNotificationType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .targetUserType(dto.getTargetUserType())
                .targetUserId(dto.getTargetUserId())
                .relatedId(dto.getRelatedId())
                .relatedType(dto.getRelatedType())
                .icon(dto.getIcon())
                .priority(dto.getPriority() != null ? dto.getPriority() : 0)
                .isReadRequired(dto.getIsReadRequired() != null ? dto.getIsReadRequired() : 1)
                .remark(dto.getRemark())
                .build();

        // 判断是立即发送还是定时发送
        if (dto.getScheduledTime() != null && dto.getScheduledTime().isAfter(LocalDateTime.now())) {
            // 定时发送，保存为草稿
            notification.setStatus(2); // 草稿
            notification.setScheduledTime(dto.getScheduledTime());
        } else {
            // 立即发送
            notification.setStatus(1); // 已发送
            notification.setSendTime(LocalDateTime.now());
        }

        int result = notificationMapper.insert(notification);
        if (result <= 0) {
            throw new BusinessException("发送通知失败");
        }

        // 如果是立即发送且需要标记已读，创建读取记录
        if (notification.getStatus() == 1 && notification.getIsReadRequired() == 1) {
            createReadRecords(notification);
        }

        return notification.getId();
    }

    /**
     * 批量发送通知给指定用户列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendBatchNotification(NotificationCreateDTO dto, List<Long> userIds) {
        log.info("批量发送通知给 {} 个用户", userIds.size());

        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户列表不能为空");
        }

        // 为每个用户创建一条通知记录
        for (Long userId : userIds) {
            NotificationCreateDTO userDto = NotificationCreateDTO.builder()
                    .notificationType(dto.getNotificationType())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .targetUserType(dto.getTargetUserType())
                    .targetUserId(userId)
                    .relatedId(dto.getRelatedId())
                    .relatedType(dto.getRelatedType())
                    .icon(dto.getIcon())
                    .priority(dto.getPriority())
                    .isReadRequired(dto.getIsReadRequired())
                    .remark(dto.getRemark())
                    .build();

            sendNotification(userDto);
        }
    }

    /**
     * 创建读取记录（为目标用户或目标类型的所有用户创建）
     */
    private void createReadRecords(SysNotification notification) {
        List<SysNotificationRead> readRecords = new ArrayList<>();

        if (notification.getTargetUserId() != null) {
            // 单个用户
            SysNotificationRead readRecord = SysNotificationRead.builder()
                    .notificationId(notification.getId())
                    .userId(notification.getTargetUserId())
                    .isRead(0)
                    .build();
            readRecords.add(readRecord);
        } else if (StringUtils.hasText(notification.getTargetUserType())) {
            // 根据用户类型查询所有用户
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            
            if (!"ALL".equals(notification.getTargetUserType())) {
                userWrapper.eq(User::getUserType, notification.getTargetUserType());
            }
            userWrapper.eq(User::getStatus, 1); // 只发送给正常状态的用户
            
            List<User> users = userMapper.selectList(userWrapper);
            for (User user : users) {
                SysNotificationRead readRecord = SysNotificationRead.builder()
                        .notificationId(notification.getId())
                        .userId(user.getId())
                        .isRead(0)
                        .build();
                readRecords.add(readRecord);
            }
        }

        // 批量插入读取记录
        if (!readRecords.isEmpty()) {
            for (SysNotificationRead record : readRecords) {
                notificationReadMapper.insert(record);
            }
        }
    }

    /**
     * 获取用户通知列表（分页）
     */
    public Page<NotificationResponseDTO> getUserNotifications(Long userId, Long current, Long size, 
                                                               String notificationType, Integer isRead) {
        log.info("获取用户通知列表: userId={}, current={}, size={}", userId, current, size);

        Page<SysNotification> page = new Page<>(current, size);
        
        // 先查询通知读取记录
        LambdaQueryWrapper<SysNotificationRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.eq(SysNotificationRead::getUserId, userId);
        if (isRead != null) {
            readWrapper.eq(SysNotificationRead::getIsRead, isRead);
        }
        readWrapper.orderByDesc(SysNotificationRead::getCreateTime);
        
        Page<SysNotificationRead> readPage = notificationReadMapper.selectPage(new Page<>(current, size), readWrapper);
        
        // 获取通知ID列表
        List<Long> notificationIds = readPage.getRecords().stream()
                .map(SysNotificationRead::getNotificationId)
                .collect(Collectors.toList());
        
        if (notificationIds.isEmpty()) {
            return new Page<>(current, size, 0);
        }

        // 查询通知详情
        LambdaQueryWrapper<SysNotification> notificationWrapper = new LambdaQueryWrapper<>();
        notificationWrapper.in(SysNotification::getId, notificationIds);
        notificationWrapper.eq(SysNotification::getStatus, 1); // 只查询已发送的通知
        
        if (StringUtils.hasText(notificationType)) {
            notificationWrapper.eq(SysNotification::getNotificationType, notificationType);
        }
        
        List<SysNotification> notifications = notificationMapper.selectList(notificationWrapper);
        
        // 创建读取记录映射
        Map<Long, SysNotificationRead> readMap = readPage.getRecords().stream()
                .collect(Collectors.toMap(SysNotificationRead::getNotificationId, r -> r));
        
        // 转换为DTO
        List<NotificationResponseDTO> dtoList = notifications.stream()
                .map(n -> convertToDTO(n, readMap.get(n.getId())))
                .collect(Collectors.toList());
        
        Page<NotificationResponseDTO> resultPage = new Page<>(current, size, readPage.getTotal());
        resultPage.setRecords(dtoList);
        
        return resultPage;
    }

    /**
     * 获取用户未读通知数量
     */
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<SysNotificationRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotificationRead::getUserId, userId);
        wrapper.eq(SysNotificationRead::getIsRead, 0);
        
        return notificationReadMapper.selectCount(wrapper);
    }

    /**
     * 标记通知为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        log.info("标记通知为已读: notificationId={}, userId={}", notificationId, userId);

        LambdaQueryWrapper<SysNotificationRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotificationRead::getNotificationId, notificationId);
        wrapper.eq(SysNotificationRead::getUserId, userId);
        
        SysNotificationRead readRecord = notificationReadMapper.selectOne(wrapper);
        if (readRecord == null) {
            throw new BusinessException("通知读取记录不存在");
        }

        if (readRecord.getIsRead() == 0) {
            readRecord.setIsRead(1);
            readRecord.setReadTime(LocalDateTime.now());
            
            int result = notificationReadMapper.updateById(readRecord);
            if (result <= 0) {
                throw new BusinessException("标记已读失败");
            }
        }
    }

    /**
     * 批量标记为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        log.info("标记所有通知为已读: userId={}", userId);

        LambdaQueryWrapper<SysNotificationRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotificationRead::getUserId, userId);
        wrapper.eq(SysNotificationRead::getIsRead, 0);
        
        List<SysNotificationRead> unreadRecords = notificationReadMapper.selectList(wrapper);
        
        for (SysNotificationRead record : unreadRecords) {
            record.setIsRead(1);
            record.setReadTime(LocalDateTime.now());
            notificationReadMapper.updateById(record);
        }
    }

    /**
     * 删除通知（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("删除通知: notificationId={}, userId={}", notificationId, userId);

        // 验证通知是否属于该用户
        LambdaQueryWrapper<SysNotificationRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotificationRead::getNotificationId, notificationId);
        wrapper.eq(SysNotificationRead::getUserId, userId);
        
        SysNotificationRead readRecord = notificationReadMapper.selectOne(wrapper);
        if (readRecord == null) {
            throw new BusinessException("通知不存在");
        }

        // 删除读取记录
        int result = notificationReadMapper.deleteById(readRecord.getId());
        if (result <= 0) {
            throw new BusinessException("删除通知失败");
        }
    }

    /**
     * 管理端-分页查询所有通知
     */
    public Page<NotificationResponseDTO> selectPage(Long current, Long size, String notificationType, 
                                                     String targetUserType, Integer status) {
        log.info("分页查询通知: current={}, size={}, type={}, targetType={}, status={}", 
                 current, size, notificationType, targetUserType, status);

        Page<SysNotification> page = new Page<>(current, size);
        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(notificationType)) {
            wrapper.eq(SysNotification::getNotificationType, notificationType);
        }
        if (StringUtils.hasText(targetUserType)) {
            wrapper.eq(SysNotification::getTargetUserType, targetUserType);
        }
        if (status != null) {
            wrapper.eq(SysNotification::getStatus, status);
        }
        
        wrapper.orderByDesc(SysNotification::getCreateTime);
        
        Page<SysNotification> notificationPage = notificationMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        List<NotificationResponseDTO> dtoList = notificationPage.getRecords().stream()
                .map(n -> convertToDTO(n, null))
                .collect(Collectors.toList());
        
        Page<NotificationResponseDTO> resultPage = new Page<>(current, size, notificationPage.getTotal());
        resultPage.setRecords(dtoList);
        
        return resultPage;
    }

    /**
     * 管理端-删除通知（物理删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotificationById(Long notificationId) {
        log.info("管理端删除通知: notificationId={}", notificationId);

        SysNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        // 删除所有相关的读取记录
        LambdaQueryWrapper<SysNotificationRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotificationRead::getNotificationId, notificationId);
        notificationReadMapper.delete(wrapper);

        // 删除通知
        int result = notificationMapper.deleteById(notificationId);
        if (result <= 0) {
            throw new BusinessException("删除通知失败");
        }
    }

    /**
     * 转换为DTO
     */
    private NotificationResponseDTO convertToDTO(SysNotification notification, SysNotificationRead readRecord) {
        NotificationResponseDTO dto = NotificationResponseDTO.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .targetUserType(notification.getTargetUserType())
                .targetUserId(notification.getTargetUserId())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .icon(notification.getIcon())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .sendTime(notification.getSendTime())
                .scheduledTime(notification.getScheduledTime())
                .isReadRequired(notification.getIsReadRequired())
                .remark(notification.getRemark())
                .createTime(notification.getCreateTime())
                .updateTime(notification.getUpdateTime())
                .build();

        // 设置描述信息
        NotificationType type = NotificationType.fromCode(notification.getNotificationType());
        if (type != null) {
            dto.setNotificationTypeDesc(type.getDescription());
        }

        NotificationPriority priority = NotificationPriority.fromCode(notification.getPriority());
        if (priority != null) {
            dto.setPriorityDesc(priority.getDescription());
        }

        // 设置状态描述
        if (notification.getStatus() == 0) {
            dto.setStatusDesc("已删除");
        } else if (notification.getStatus() == 1) {
            dto.setStatusDesc("已发送");
        } else if (notification.getStatus() == 2) {
            dto.setStatusDesc("草稿");
        }

        // 设置读取信息
        if (readRecord != null) {
            dto.setIsRead(readRecord.getIsRead() == 1);
            dto.setReadTime(readRecord.getReadTime());
        }

        return dto;
    }
}
