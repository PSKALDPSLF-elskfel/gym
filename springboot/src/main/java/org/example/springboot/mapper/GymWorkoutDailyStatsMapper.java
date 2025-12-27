package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymWorkoutDailyStats;

/**
 * 每日运动数据统计Mapper接口
 * @author system
 */
@Mapper
public interface GymWorkoutDailyStatsMapper extends BaseMapper<GymWorkoutDailyStats> {
}
