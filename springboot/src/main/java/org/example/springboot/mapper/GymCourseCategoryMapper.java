package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCourseCategory;

/**
 * 课程分类Mapper接口
 * @author system
 */
@Mapper
public interface GymCourseCategoryMapper extends BaseMapper<GymCourseCategory> {
}
