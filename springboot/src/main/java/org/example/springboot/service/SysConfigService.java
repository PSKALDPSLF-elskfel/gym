package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.SysConfigCreateDTO;
import org.example.springboot.dto.command.SysConfigUpdateDTO;
import org.example.springboot.dto.response.SysConfigResponseDTO;
import org.example.springboot.entity.SysConfig;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.SysConfigMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置Service
 * @author system
 */
@Slf4j
@Service
public class SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;

    /**
     * 创建系统配置
     */
    @Transactional(rollbackFor = Exception.class)
    public SysConfigResponseDTO createConfig(SysConfigCreateDTO createDTO) {
        try {
            // 检查配置键是否已存在
            LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysConfig::getConfigKey, createDTO.getConfigKey());
            if (sysConfigMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("配置键已存在");
            }

            SysConfig config = SysConfig.builder()
                    .configKey(createDTO.getConfigKey())
                    .configValue(createDTO.getConfigValue())
                    .description(createDTO.getDescription())
                    .configGroup(createDTO.getConfigGroup())
                    .isSystem(0)
                    .status(1)
                    .build();

            sysConfigMapper.insert(config);
            log.info("创建系统配置成功: {}", config.getConfigKey());
            return entityToResponseDTO(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建系统配置失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新系统配置
     */
    @Transactional(rollbackFor = Exception.class)
    public SysConfigResponseDTO updateConfig(Long id, SysConfigUpdateDTO updateDTO) {
        try {
            SysConfig config = sysConfigMapper.selectById(id);
            if (config == null) {
                throw new BusinessException("配置不存在");
            }

            // 系统内置配置不允许修改状态
            if (config.isSystemConfig() && updateDTO.getStatus() != null && updateDTO.getStatus() == 0) {
                throw new BusinessException("系统内置配置不允许禁用");
            }

            if (updateDTO.getConfigValue() != null) config.setConfigValue(updateDTO.getConfigValue());
            if (updateDTO.getDescription() != null) config.setDescription(updateDTO.getDescription());
            if (updateDTO.getConfigGroup() != null) config.setConfigGroup(updateDTO.getConfigGroup());
            if (updateDTO.getStatus() != null) config.setStatus(updateDTO.getStatus());

            sysConfigMapper.updateById(config);
            log.info("更新系统配置成功: {}", config.getConfigKey());
            return entityToResponseDTO(config);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新系统配置失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除系统配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        try {
            SysConfig config = sysConfigMapper.selectById(id);
            if (config == null) {
                throw new BusinessException("配置不存在");
            }
            
            // 系统内置配置不允许删除
            if (config.isSystemConfig()) {
                throw new BusinessException("系统内置配置不允许删除");
            }
            
            sysConfigMapper.deleteById(id);
            log.info("删除系统配置成功: {}", config.getConfigKey());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除系统配置失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取系统配置详情
     */
    public SysConfigResponseDTO getConfigById(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        return entityToResponseDTO(config);
    }

    /**
     * 根据配置键获取配置值
     */
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 1);
        SysConfig config = sysConfigMapper.selectOne(queryWrapper);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 分页查询系统配置
     */
    public Page<SysConfigResponseDTO> getConfigPage(int currentPage, int pageSize, 
            String configKey, String configGroup, Integer status) {
        Page<SysConfig> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        
        if (configKey != null && !configKey.trim().isEmpty()) {
            queryWrapper.like(SysConfig::getConfigKey, configKey);
        }
        if (configGroup != null && !configGroup.trim().isEmpty()) {
            queryWrapper.eq(SysConfig::getConfigGroup, configGroup);
        }
        if (status != null) {
            queryWrapper.eq(SysConfig::getStatus, status);
        }
        queryWrapper.orderByAsc(SysConfig::getConfigGroup)
                .orderByAsc(SysConfig::getConfigKey);

        Page<SysConfig> configPage = sysConfigMapper.selectPage(page, queryWrapper);
        
        Page<SysConfigResponseDTO> resultPage = new Page<>(configPage.getCurrent(), configPage.getSize(), configPage.getTotal());
        List<SysConfigResponseDTO> records = configPage.getRecords().stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取所有配置分组
     */
    public List<String> getAllConfigGroups() {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysConfig::getConfigGroup)
                .groupBy(SysConfig::getConfigGroup)
                .orderByAsc(SysConfig::getConfigGroup);
        return sysConfigMapper.selectList(queryWrapper).stream()
                .map(SysConfig::getConfigGroup)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 实体转响应DTO
     */
    private SysConfigResponseDTO entityToResponseDTO(SysConfig config) {
        SysConfigResponseDTO dto = new SysConfigResponseDTO();
        BeanUtils.copyProperties(config, dto);
        dto.setStatusName(Integer.valueOf(1).equals(config.getStatus()) ? "启用" : "禁用");
        return dto;
    }
}
