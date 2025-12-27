package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.MembershipPurchaseDTO;
import org.example.springboot.dto.response.UserMembershipResponseDTO;
import org.example.springboot.entity.MembershipPackage;
import org.example.springboot.entity.User;
import org.example.springboot.entity.UserMembership;
import org.example.springboot.enums.MembershipStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.MembershipPackageMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.mapper.UserMembershipMapper;
import org.example.springboot.service.convert.UserMembershipConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户会员记录业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class UserMembershipService {

    @Resource
    private UserMembershipMapper userMembershipMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MembershipPackageMapper membershipPackageMapper;

    /**
     * 购买会员套餐
     * @param userId 用户ID
     * @param purchaseDTO 购买命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void purchasePackage(Long userId, MembershipPurchaseDTO purchaseDTO) {
        log.info("用户 {} 开始购买会员套餐: {}", userId, purchaseDTO.getPackageId());

        // 1. 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!user.isActive()) {
            throw new BusinessException("用户已被禁用");
        }

        // 2. 验证套餐是否存在且上架
        MembershipPackage membershipPackage = membershipPackageMapper.selectById(purchaseDTO.getPackageId());
        if (membershipPackage == null) {
            throw new BusinessException("套餐不存在");
        }
        if (!membershipPackage.isOnline()) {
            throw new BusinessException("套餐已下架，无法购买");
        }

        // 3. 计算开始时间和结束时间
        LocalDateTime startTime;
        LocalDateTime endTime;
        LocalDateTime now = LocalDateTime.now();

        // 查询用户当前是否有该类型的有效会员
        UserMembership currentMembership = getCurrentMembershipByType(userId, membershipPackage.getMemberType());
        
        if (currentMembership != null && currentMembership.getEndTime() != null && currentMembership.getEndTime().isAfter(now)) {
            // 续费：在原到期时间基础上延长
            startTime = currentMembership.getEndTime();
            endTime = startTime.plusDays(membershipPackage.getDurationDays());
            log.info("用户续费会员，开始时间: {}, 结束时间: {}", startTime, endTime);
        } else {
            // 首次购买或已过期：从当前时间开始
            startTime = now;
            endTime = startTime.plusDays(membershipPackage.getDurationDays());
            log.info("用户首次购买会员，开始时间: {}, 结束时间: {}", startTime, endTime);
        }

        // 4. 创建会员记录
        UserMembership userMembership = UserMembership.builder()
                .userId(userId)
                .packageId(membershipPackage.getId())
                .memberType(membershipPackage.getMemberType())
                .startTime(startTime)
                .endTime(endTime)
                .price(membershipPackage.getPrice())
                .status(MembershipStatus.ACTIVE.getCode())
                .purchaseTime(now)
                .build();

        int insertResult = userMembershipMapper.insert(userMembership);
        if (insertResult <= 0) {
            throw new BusinessException("购买失败，请重试");
        }

        // 5. 更新用户表的会员类型和到期时间
        user.setMemberType(membershipPackage.getMemberType());
        user.setMemberExpireTime(endTime);
        int updateResult = userMapper.updateById(user);
        if (updateResult <= 0) {
            throw new BusinessException("更新用户会员信息失败");
        }

        log.info("用户 {} 购买会员套餐成功，会员类型: {}, 到期时间: {}", userId, membershipPackage.getMemberType(), endTime);
    }

    /**
     * 查询用户当前有效的会员（按会员类型）
     * @param userId 用户ID
     * @param memberType 会员类型
     * @return 会员记录
     */
    private UserMembership getCurrentMembershipByType(Long userId, Integer memberType) {
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId)
                .eq(UserMembership::getMemberType, memberType)
                .eq(UserMembership::getStatus, MembershipStatus.ACTIVE.getCode())
                .orderByDesc(UserMembership::getEndTime)
                .last("LIMIT 1");
        return userMembershipMapper.selectOne(wrapper);
    }

    /**
     * 查询用户当前有效的会员
     * @param userId 用户ID
     * @return 会员记录响应DTO
     */
    public UserMembershipResponseDTO getCurrentMembership(Long userId) {
        log.info("查询用户 {} 当前有效会员", userId);

        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId)
                .eq(UserMembership::getStatus, MembershipStatus.ACTIVE.getCode())
                .ge(UserMembership::getEndTime, LocalDateTime.now())
                .orderByDesc(UserMembership::getEndTime)
                .last("LIMIT 1");

        UserMembership userMembership = userMembershipMapper.selectOne(wrapper);
        if (userMembership == null) {
            return null;
        }

        // 查询关联信息
        User user = userMapper.selectById(userMembership.getUserId());
        MembershipPackage membershipPackage = membershipPackageMapper.selectById(userMembership.getPackageId());

        return UserMembershipConvert.toResponseDTO(userMembership, user, membershipPackage);
    }

    /**
     * 查询用户会员购买历史
     * @param userId 用户ID
     * @return 会员记录列表
     */
    public List<UserMembershipResponseDTO> getUserMembershipHistory(Long userId) {
        log.info("查询用户 {} 会员购买历史", userId);

        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId)
                .orderByDesc(UserMembership::getPurchaseTime);

        List<UserMembership> membershipList = userMembershipMapper.selectList(wrapper);

        return membershipList.stream()
                .map(membership -> {
                    User user = userMapper.selectById(membership.getUserId());
                    MembershipPackage membershipPackage = membershipPackageMapper.selectById(membership.getPackageId());
                    return UserMembershipConvert.toResponseDTO(membership, user, membershipPackage);
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页查询会员记录（管理员）
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param memberType 会员类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    public Page<UserMembershipResponseDTO> selectPage(Long current, Long size, Long userId, Integer memberType, Integer status) {
        log.info("分页查询会员记录: current={}, size={}, userId={}, memberType={}, status={}", 
                current, size, userId, memberType, status);

        Page<UserMembership> page = new Page<>(current, size);
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (userId != null) {
            wrapper.eq(UserMembership::getUserId, userId);
        }
        if (memberType != null) {
            wrapper.eq(UserMembership::getMemberType, memberType);
        }
        if (status != null) {
            wrapper.eq(UserMembership::getStatus, status);
        }

        wrapper.orderByDesc(UserMembership::getPurchaseTime);

        Page<UserMembership> membershipPage = userMembershipMapper.selectPage(page, wrapper);

        // 转换为DTO
        Page<UserMembershipResponseDTO> dtoPage = new Page<>(membershipPage.getCurrent(), membershipPage.getSize(), membershipPage.getTotal());
        List<UserMembershipResponseDTO> dtoList = membershipPage.getRecords().stream()
                .map(membership -> {
                    User user = userMapper.selectById(membership.getUserId());
                    MembershipPackage membershipPackage = membershipPackageMapper.selectById(membership.getPackageId());
                    return UserMembershipConvert.toResponseDTO(membership, user, membershipPackage);
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 检查并更新过期会员状态
     * 此方法可以由定时任务调用
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateExpiredMemberships() {
        log.info("开始检查并更新过期会员状态");

        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getStatus, MembershipStatus.ACTIVE.getCode())
                .lt(UserMembership::getEndTime, LocalDateTime.now());

        List<UserMembership> expiredMemberships = userMembershipMapper.selectList(wrapper);

        for (UserMembership membership : expiredMemberships) {
            membership.setStatus(MembershipStatus.EXPIRED.getCode());
            userMembershipMapper.updateById(membership);

            // 更新用户表的会员状态
            User user = userMapper.selectById(membership.getUserId());
            if (user != null && user.getMemberExpireTime() != null 
                    && user.getMemberExpireTime().isBefore(LocalDateTime.now())) {
                user.setMemberType(0); // 设置为普通用户
                userMapper.updateById(user);
            }
        }

        log.info("过期会员状态更新完成，共更新 {} 条记录", expiredMemberships.size());
    }
}
