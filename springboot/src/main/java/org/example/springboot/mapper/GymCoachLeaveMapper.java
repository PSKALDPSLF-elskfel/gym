package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCoachLeave;

/**
 * 教练请假Mapper接口
 * @author system
 */
@Mapper
public interface GymCoachLeaveMapper extends BaseMapper<GymCoachLeave> {
}
