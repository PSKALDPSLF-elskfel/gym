package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymWorkoutDetail;

/**
 * 运动详情Mapper接口
 * @author system
 */
@Mapper
public interface GymWorkoutDetailMapper extends BaseMapper<GymWorkoutDetail> {
}
