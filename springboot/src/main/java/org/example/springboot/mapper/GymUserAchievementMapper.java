package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymUserAchievement;

/**
 * 用户成就记录Mapper接口
 * @author system
 */
@Mapper
public interface GymUserAchievementMapper extends BaseMapper<GymUserAchievement> {
}
