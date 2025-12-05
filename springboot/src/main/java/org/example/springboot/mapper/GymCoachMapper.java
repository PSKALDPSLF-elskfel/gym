package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCoach;

/**
 * 教练信息Mapper接口
 * @author system
 */
@Mapper
public interface GymCoachMapper extends BaseMapper<GymCoach> {
}
