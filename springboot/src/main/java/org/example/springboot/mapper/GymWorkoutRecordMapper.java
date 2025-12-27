package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymWorkoutRecord;

/**
 * 运动记录Mapper接口
 * @author system
 */
@Mapper
public interface GymWorkoutRecordMapper extends BaseMapper<GymWorkoutRecord> {
}
