package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCoachSchedule;

/**
 * 教练排班Mapper接口
 * @author system
 */
@Mapper
public interface GymCoachScheduleMapper extends BaseMapper<GymCoachSchedule> {
}
