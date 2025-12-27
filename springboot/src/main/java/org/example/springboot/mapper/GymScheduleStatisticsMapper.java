package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymScheduleStatistics;

/**
 * 排班统计Mapper接口
 * @author system
 */
@Mapper
public interface GymScheduleStatisticsMapper extends BaseMapper<GymScheduleStatistics> {
}
