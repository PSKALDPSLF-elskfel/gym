package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCourseSignIn;

/**
 * 课程签到Mapper
 * @author system
 */
@Mapper
public interface GymCourseSignInMapper extends BaseMapper<GymCourseSignIn> {
}
