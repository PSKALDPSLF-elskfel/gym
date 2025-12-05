package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCourseSchedule;

/**
 * 课程时间安排Mapper接口
 * @author system
 */
@Mapper
public interface GymCourseScheduleMapper extends BaseMapper<GymCourseSchedule> {
    // 继承BaseMapper，获得基础的CRUD操作
    // 所有复杂查询都在Service层使用Lambda构造器实现
}
