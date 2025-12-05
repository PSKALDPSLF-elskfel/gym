package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCourseBooking;

/**
 * 课程预约Mapper接口
 * @author system
 */
@Mapper
public interface GymCourseBookingMapper extends BaseMapper<GymCourseBooking> {
}
