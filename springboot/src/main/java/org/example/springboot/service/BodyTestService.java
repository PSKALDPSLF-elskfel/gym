package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.BodyTestCreateDTO;
import org.example.springboot.dto.response.BodyTestResponseDTO;
import org.example.springboot.entity.GymBodyTest;
import org.example.springboot.entity.User;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.GymBodyTestMapper;
import org.example.springboot.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体测数据Service
 * @author system
 */
@Slf4j
@Service
public class BodyTestService {

    @Resource
    private GymBodyTestMapper bodyTestMapper;
    
    @Resource
    private UserMapper userMapper;

    /**
     * 创建体测数据
     */
    @Transactional(rollbackFor = Exception.class)
    public BodyTestResponseDTO createBodyTest(BodyTestCreateDTO createDTO) {
        try {
            // 验证用户存在
            User user = userMapper.selectById(createDTO.getUserId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 计算BMI (体重kg / (身高m)²)
            BigDecimal bmi = null;
            if (createDTO.getHeight() != null && createDTO.getWeight() != null) {
                BigDecimal heightInMeters = createDTO.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                bmi = createDTO.getWeight().divide(heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
            }

            GymBodyTest bodyTest = GymBodyTest.builder()
                    .userId(createDTO.getUserId())
                    .height(createDTO.getHeight())
                    .weight(createDTO.getWeight())
                    .bmi(bmi)
                    .bodyFat(createDTO.getBodyFat())
                    .muscleMass(createDTO.getMuscleMass())
                    .visceralFat(createDTO.getVisceralFat())
                    .basalMetabolism(createDTO.getBasalMetabolism())
                    .testTime(createDTO.getTestTime() != null ? createDTO.getTestTime() : LocalDateTime.now())
                    .testerId(createDTO.getTesterId())
                    .remark(createDTO.getRemark())
                    .build();

            bodyTestMapper.insert(bodyTest);
            log.info("创建体测数据成功，用户ID: {}", bodyTest.getUserId());
            return entityToResponseDTO(bodyTest);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建体测数据失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 删除体测数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBodyTest(Long id) {
        try {
            GymBodyTest bodyTest = bodyTestMapper.selectById(id);
            if (bodyTest == null) {
                throw new BusinessException("体测数据不存在");
            }
            bodyTestMapper.deleteById(id);
            log.info("删除体测数据成功，ID: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除体测数据失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取体测数据详情
     */
    public BodyTestResponseDTO getBodyTestById(Long id) {
        GymBodyTest bodyTest = bodyTestMapper.selectById(id);
        if (bodyTest == null) {
            throw new BusinessException("体测数据不存在");
        }
        return entityToResponseDTO(bodyTest);
    }

    /**
     * 分页查询体测数据
     */
    public Page<BodyTestResponseDTO> getBodyTestPage(int currentPage, int pageSize, Long userId) {
        Page<GymBodyTest> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymBodyTest> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(GymBodyTest::getUserId, userId);
        }
        queryWrapper.orderByDesc(GymBodyTest::getTestTime);

        Page<GymBodyTest> bodyTestPage = bodyTestMapper.selectPage(page, queryWrapper);
        
        Page<BodyTestResponseDTO> resultPage = new Page<>(bodyTestPage.getCurrent(), bodyTestPage.getSize(), bodyTestPage.getTotal());
        List<BodyTestResponseDTO> records = bodyTestPage.getRecords().stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取用户最新体测数据
     */
    public BodyTestResponseDTO getLatestBodyTest(Long userId) {
        LambdaQueryWrapper<GymBodyTest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymBodyTest::getUserId, userId)
                .orderByDesc(GymBodyTest::getTestTime)
                .last("LIMIT 1");
        
        GymBodyTest bodyTest = bodyTestMapper.selectOne(queryWrapper);
        if (bodyTest == null) {
            throw new BusinessException("暂无体测数据");
        }
        return entityToResponseDTO(bodyTest);
    }

    /**
     * 实体转响应DTO
     */
    private BodyTestResponseDTO entityToResponseDTO(GymBodyTest bodyTest) {
        BodyTestResponseDTO dto = new BodyTestResponseDTO();
        BeanUtils.copyProperties(bodyTest, dto);
        
        // 设置用户信息
        User user = userMapper.selectById(bodyTest.getUserId());
        if (user != null) {
            dto.setUserNickname(user.getNickname());
        }
        
        // 设置测试人员信息
        if (bodyTest.getTesterId() != null) {
            User tester = userMapper.selectById(bodyTest.getTesterId());
            if (tester != null) {
                dto.setTesterName(tester.getNickname());
            }
        }
        
        return dto;
    }
}
