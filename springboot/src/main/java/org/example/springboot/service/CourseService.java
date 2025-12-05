package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CourseCreateDTO;
import org.example.springboot.dto.command.CourseUpdateDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.enums.CourseStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymCourseMapper;
import org.example.springboot.service.convert.CourseConvert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class CourseService {

    @Resource
    private GymCourseMapper gymCourseMapper;

    /**
     * 创建课程
     * @param createDTO 创建命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(CourseCreateDTO createDTO) {
        log.info("开始创建课程: {}", createDTO.getName());

        // 校验课程ID是否已存在
        GymCourse existingCourse = gymCourseMapper.selectById(createDTO.getId());
        if (existingCourse != null) {
            throw new BusinessException("课程ID已存在");
        }

        // 校验状态
        if (!CourseStatus.isValidCode(createDTO.getStatus())) {
            throw new BusinessException("无效的课程状态");
        }

        // 检查课程名称是否重复
        LambdaQueryWrapper<GymCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCourse::getName, createDTO.getName());
        Long count = gymCourseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("课程名称已存在");
        }

        // 转换并保存
        GymCourse entity = CourseConvert.createCommandToEntity(createDTO);
        int result = gymCourseMapper.insert(entity);
        if (result <= 0) {
            throw new BusinessException("创建课程失败");
        }

        log.info("课程创建成功，ID: {}", entity.getId());
    }

    /**
     * 更新课程
     * @param updateDTO 更新命令DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseUpdateDTO updateDTO) {
        log.info("开始更新课程，ID: {}", updateDTO.getId());

        // 检查课程是否存在
        GymCourse existingCourse = gymCourseMapper.selectById(updateDTO.getId());
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }

        // 校验状态
        if (!CourseStatus.isValidCode(updateDTO.getStatus())) {
            throw new BusinessException("无效的课程状态");
        }

        // 检查课程名称是否与其他课程重复
        LambdaQueryWrapper<GymCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCourse::getName, updateDTO.getName())
                .ne(GymCourse::getId, updateDTO.getId());
        Long count = gymCourseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("课程名称已存在");
        }

        // 转换并更新
        GymCourse entity = CourseConvert.updateCommandToEntity(updateDTO);
        int result = gymCourseMapper.updateById(entity);
        if (result <= 0) {
            throw new BusinessException("更新课程失败");
        }

        log.info("课程更新成功，ID: {}", updateDTO.getId());
    }

    /**
     * 删除课程
     * @param id 课程ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("开始删除课程，ID: {}", id);

        // 检查课程是否存在
        GymCourse existingCourse = gymCourseMapper.selectById(id);
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }

        // TODO: 检查是否有关联的课程时间安排或预约记录
        // 如果有关联数据，应该禁止删除或提示先处理关联数据

        int result = gymCourseMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除课程失败");
        }

        log.info("课程删除成功，ID: {}", id);
    }

    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程响应DTO
     */
    public CourseResponseDTO getById(String id) {
        log.info("查询课程，ID: {}", id);

        GymCourse course = gymCourseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        return CourseConvert.entityToResponse(course);
    }

    /**
     * 分页查询课程
     * @param current 当前页
     * @param size 每页大小
     * @param name 课程名称（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    public Page<CourseResponseDTO> selectPage(Long current, Long size, String name, Integer status) {
        log.info("分页查询课程: current={}, size={}, name={}, status={}", current, size, name, status);

        Page<GymCourse> page = new Page<>(current, size);
        LambdaQueryWrapper<GymCourse> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (StringUtils.hasText(name)) {
            wrapper.like(GymCourse::getName, name);
        }
        if (status != null) {
            wrapper.eq(GymCourse::getStatus, status);
        }

        wrapper.orderByDesc(GymCourse::getCreateTime);

        Page<GymCourse> coursePage = gymCourseMapper.selectPage(page, wrapper);

        // 转换为DTO
        Page<CourseResponseDTO> dtoPage = new Page<>(coursePage.getCurrent(), coursePage.getSize(), coursePage.getTotal());
        List<CourseResponseDTO> dtoList = coursePage.getRecords().stream()
                .map(CourseConvert::entityToResponse)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 更新课程状态
     * @param id 课程ID
     * @param status 状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String id, Integer status) {
        log.info("更新课程状态，ID: {}, 状态: {}", id, status);

        // 检查课程是否存在
        GymCourse existingCourse = gymCourseMapper.selectById(id);
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }

        // 校验状态
        if (!CourseStatus.isValidCode(status)) {
            throw new BusinessException("无效的课程状态");
        }

        // 更新状态
        existingCourse.setStatus(status);
        int result = gymCourseMapper.updateById(existingCourse);
        if (result <= 0) {
            throw new BusinessException("更新课程状态失败");
        }

        log.info("课程状态更新成功，ID: {}", id);
    }

    /**
     * 查询所有上架的课程列表
     * @return 课程列表
     */
    public List<CourseResponseDTO> listOnline() {
        log.info("查询所有上架的课程列表");

        LambdaQueryWrapper<GymCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCourse::getStatus, CourseStatus.ONLINE.getCode())
                .orderByDesc(GymCourse::getCreateTime);

        List<GymCourse> courseList = gymCourseMapper.selectList(wrapper);

        return courseList.stream()
                .map(CourseConvert::entityToResponse)
                .collect(Collectors.toList());
    }
}
