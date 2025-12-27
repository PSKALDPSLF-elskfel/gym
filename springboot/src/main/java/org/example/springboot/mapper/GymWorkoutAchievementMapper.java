package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymWorkoutAchievement;

/**
 * 运动成就Mapper接口
 * @author system
 */
@Mapper
public interface GymWorkoutAchievementMapper extends BaseMapper<GymWorkoutAchievement> {
}
