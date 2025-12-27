package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.springboot.entity.MembershipPackage;
import org.example.springboot.mapper.MembershipPackageMapper;
import org.example.springboot.dto.command.MembershipPackageCreateDTO;
import org.example.springboot.dto.command.MembershipPackageUpdateDTO;
import org.example.springboot.dto.response.MembershipPackageResponseDTO;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.service.convert.MembershipPackageConvert;
import org.example.springboot.enums.MemberType;
import org.example.springboot.enums.PackageStatus;

/**
 * 会员套餐业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class MembershipPackageService {

    @Resource
    private MembershipPackageMapper membershipPackageMapper;

    /**
     * 创建会员套餐
     * @param createDTO 创建命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(MembershipPackageCreateDTO createDTO) {
        log.info("开始创建会员套餐: {}", createDTO.getName());
        
        // 校验会员类型
        if (!MemberType.isValidCode(createDTO.getMemberType())) {
            throw new BusinessException("无效的会员类型");
        }
        
        // 校验状态
        if (!PackageStatus.isValidCode(createDTO.getStatus())) {
            throw new BusinessException("无效的套餐状态");
        }
        
        // 检查套餐名称是否重复
        LambdaQueryWrapper<MembershipPackage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MembershipPackage::getName, createDTO.getName());
        Long count = membershipPackageMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("套餐名称已存在");
        }
        
        // 转换并保存
        MembershipPackage entity = MembershipPackageConvert.createCommandToEntity(createDTO);
        membershipPackageMapper.insert(entity);
        
        log.info("会员套餐创建成功，ID: {}", entity.getId());
    }

    /**
     * 更新会员套餐
     * @param updateDTO 更新命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(MembershipPackageUpdateDTO updateDTO) {
        log.info("开始更新会员套餐，ID: {}", updateDTO.getId());
        
        // 检查套餐是否存在
        MembershipPackage existingPackage = membershipPackageMapper.selectById(updateDTO.getId());
        if (existingPackage == null) {
            throw new BusinessException("套餐不存在");
        }
        
        // 校验会员类型
        if (!MemberType.isValidCode(updateDTO.getMemberType())) {
            throw new BusinessException("无效的会员类型");
        }
        
        // 校验状态
        if (!PackageStatus.isValidCode(updateDTO.getStatus())) {
            throw new BusinessException("无效的套餐状态");
        }
        
        // 检查套餐名称是否与其他套餐重复
        LambdaQueryWrapper<MembershipPackage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MembershipPackage::getName, updateDTO.getName())
                   .ne(MembershipPackage::getId, updateDTO.getId());
        Long count = membershipPackageMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("套餐名称已存在");
        }
        
        // 转换并更新
        MembershipPackage entity = MembershipPackageConvert.updateCommandToEntity(updateDTO);
        membershipPackageMapper.updateById(entity);
        
        log.info("会员套餐更新成功，ID: {}", updateDTO.getId());
    }

    /**
     * 删除会员套餐
     * @param id 套餐ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("开始删除会员套餐，ID: {}", id);
        
        // 检查套餐是否存在
        MembershipPackage existingPackage = membershipPackageMapper.selectById(id);
        if (existingPackage == null) {
            throw new BusinessException("套餐不存在");
        }
        
        // TODO: 未来需要检查是否有关联的会员记录
        // 如果有用户购买了该套餐，应该禁止删除或提示先处理会员记录
        
        membershipPackageMapper.deleteById(id);
        
        log.info("会员套餐删除成功，ID: {}", id);
    }

    /**
     * 根据ID查询会员套餐
     * @param id 套餐ID
     * @return 会员套餐响应DTO
     */
    public MembershipPackageResponseDTO getById(Long id) {
        log.info("查询会员套餐，ID: {}", id);
        
        MembershipPackage entity = membershipPackageMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("套餐不存在");
        }
        
        return MembershipPackageConvert.entityToResponse(entity);
    }

    /**
     * 分页查询会员套餐
     * @param current 当前页码
     * @param size 每页大小
     * @param name 套餐名称（模糊查询）
     * @param memberType 会员类型
     * @param status 状态
     * @return 分页结果
     */
    public Page<MembershipPackageResponseDTO> selectPage(Long current, Long size, 
                                                          String name, Integer memberType, Integer status) {
        log.info("分页查询会员套餐，页码: {}, 大小: {}, 名称: {}, 会员类型: {}, 状态: {}", 
                 current, size, name, memberType, status);
        
        // 构建查询条件
        LambdaQueryWrapper<MembershipPackage> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(MembershipPackage::getName, name);
        }
        
        if (memberType != null) {
            queryWrapper.eq(MembershipPackage::getMemberType, memberType);
        }
        
        if (status != null) {
            queryWrapper.eq(MembershipPackage::getStatus, status);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(MembershipPackage::getCreateTime);
        
        // 执行分页查询
        Page<MembershipPackage> page = new Page<>(current, size);
        Page<MembershipPackage> resultPage = membershipPackageMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<MembershipPackageResponseDTO> responsePage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<MembershipPackageResponseDTO> records = resultPage.getRecords().stream()
                .map(MembershipPackageConvert::entityToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(records);
        
        return responsePage;
    }

    /**
     * 更新套餐状态
     * @param id 套餐ID
     * @param status 新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("更新会员套餐状态，ID: {}, 状态: {}", id, status);
        
        // 检查套餐是否存在
        MembershipPackage existingPackage = membershipPackageMapper.selectById(id);
        if (existingPackage == null) {
            throw new BusinessException("套餐不存在");
        }
        
        // 校验状态
        if (!PackageStatus.isValidCode(status)) {
            throw new BusinessException("无效的套餐状态");
        }
        
        // 更新状态
        MembershipPackage updateEntity = MembershipPackage.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .build();
        membershipPackageMapper.updateById(updateEntity);
        
        log.info("会员套餐状态更新成功，ID: {}", id);
    }

    /**
     * 根据会员类型查询套餐列表（仅上架的）
     * @param memberType 会员类型
     * @return 套餐列表
     */
    public List<MembershipPackageResponseDTO> listByMemberType(Integer memberType) {
        log.info("查询会员类型套餐列表，会员类型: {}", memberType);
        
        LambdaQueryWrapper<MembershipPackage> queryWrapper = new LambdaQueryWrapper<>();
        
        if (memberType != null) {
            queryWrapper.eq(MembershipPackage::getMemberType, memberType);
        }
        
        // 只查询上架的套餐
        queryWrapper.eq(MembershipPackage::getStatus, PackageStatus.ONLINE.getCode());
        
        // 按价格升序排列
        queryWrapper.orderByAsc(MembershipPackage::getPrice);
        
        List<MembershipPackage> list = membershipPackageMapper.selectList(queryWrapper);
        
        return list.stream()
                .map(MembershipPackageConvert::entityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有上架的套餐列表
     * @return 套餐列表
     */
    public List<MembershipPackageResponseDTO> listOnline() {
        log.info("查询所有上架的套餐列表");
        
        LambdaQueryWrapper<MembershipPackage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MembershipPackage::getStatus, PackageStatus.ONLINE.getCode());
        queryWrapper.orderByAsc(MembershipPackage::getMemberType)
                   .orderByAsc(MembershipPackage::getPrice);
        
        List<MembershipPackage> list = membershipPackageMapper.selectList(queryWrapper);
        
        return list.stream()
                .map(MembershipPackageConvert::entityToResponse)
                .collect(Collectors.toList());
    }
}
