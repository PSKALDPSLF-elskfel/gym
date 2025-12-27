package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CourseCategoryCreateDTO;
import org.example.springboot.dto.command.CourseCategoryUpdateDTO;
import org.example.springboot.dto.response.CourseCategoryResponseDTO;
import org.example.springboot.entity.GymCourseCategory;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.GymCourseCategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程分类Service
 * @author system
 */
@Slf4j
@Service
public class CourseCategoryService {

    @Resource
    private GymCourseCategoryMapper courseCategoryMapper;

    /**
     * 创建课程分类
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseCategoryResponseDTO createCategory(CourseCategoryCreateDTO createDTO) {
        try {
            // 检查分类名称是否已存在
            LambdaQueryWrapper<GymCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GymCourseCategory::getName, createDTO.getName());
            if (courseCategoryMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("分类名称已存在");
            }

            GymCourseCategory category = GymCourseCategory.builder()
                    .name(createDTO.getName())
                    .description(createDTO.getDescription())
                    .icon(createDTO.getIcon())
                    .sortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0)
                    .status(1)
                    .build();

            courseCategoryMapper.insert(category);
            log.info("创建课程分类成功: {}", category.getName());
            return entityToResponseDTO(category);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建课程分类失败", e);
            throw new ServiceException("创建失败");
        }
    }

    /**
     * 更新课程分类
     */
    @Transactional(rollbackFor = Exception.class)
    public CourseCategoryResponseDTO updateCategory(Long id, CourseCategoryUpdateDTO updateDTO) {
        try {
            GymCourseCategory category = courseCategoryMapper.selectById(id);
            if (category == null) {
                throw new BusinessException("分类不存在");
            }

            if (updateDTO.getName() != null) {
                LambdaQueryWrapper<GymCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(GymCourseCategory::getName, updateDTO.getName())
                        .ne(GymCourseCategory::getId, id);
                if (courseCategoryMapper.selectCount(queryWrapper) > 0) {
                    throw new BusinessException("分类名称已存在");
                }
                category.setName(updateDTO.getName());
            }
            
            if (updateDTO.getDescription() != null) category.setDescription(updateDTO.getDescription());
            if (updateDTO.getIcon() != null) category.setIcon(updateDTO.getIcon());
            if (updateDTO.getSortOrder() != null) category.setSortOrder(updateDTO.getSortOrder());
            if (updateDTO.getStatus() != null) category.setStatus(updateDTO.getStatus());

            courseCategoryMapper.updateById(category);
            log.info("更新课程分类成功: {}", category.getName());
            return entityToResponseDTO(category);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新课程分类失败", e);
            throw new ServiceException("更新失败");
        }
    }

    /**
     * 删除课程分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        try {
            GymCourseCategory category = courseCategoryMapper.selectById(id);
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
            courseCategoryMapper.deleteById(id);
            log.info("删除课程分类成功: {}", category.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除课程分类失败", e);
            throw new ServiceException("删除失败");
        }
    }

    /**
     * 获取课程分类详情
     */
    public CourseCategoryResponseDTO getCategoryById(Long id) {
        GymCourseCategory category = courseCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return entityToResponseDTO(category);
    }

    /**
     * 分页查询课程分类
     */
    public Page<CourseCategoryResponseDTO> getCategoryPage(int currentPage, int pageSize, String name, Integer status) {
        Page<GymCourseCategory> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<GymCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(GymCourseCategory::getName, name);
        }
        if (status != null) {
            queryWrapper.eq(GymCourseCategory::getStatus, status);
        }
        queryWrapper.orderByAsc(GymCourseCategory::getSortOrder)
                .orderByDesc(GymCourseCategory::getCreateTime);

        Page<GymCourseCategory> categoryPage = courseCategoryMapper.selectPage(page, queryWrapper);
        
        Page<CourseCategoryResponseDTO> resultPage = new Page<>(categoryPage.getCurrent(), categoryPage.getSize(), categoryPage.getTotal());
        List<CourseCategoryResponseDTO> records = categoryPage.getRecords().stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    /**
     * 获取所有启用的课程分类
     */
    public List<CourseCategoryResponseDTO> getAllEnabledCategories() {
        LambdaQueryWrapper<GymCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GymCourseCategory::getStatus, 1)
                .orderByAsc(GymCourseCategory::getSortOrder);
        return courseCategoryMapper.selectList(queryWrapper).stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转响应DTO
     */
    private CourseCategoryResponseDTO entityToResponseDTO(GymCourseCategory category) {
        CourseCategoryResponseDTO dto = new CourseCategoryResponseDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
