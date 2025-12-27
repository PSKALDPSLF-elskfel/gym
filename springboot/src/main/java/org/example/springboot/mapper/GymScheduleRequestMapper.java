package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymScheduleRequest;

/**
 * 排班申请Mapper接口
 * @author system
 */
@Mapper
public interface GymScheduleRequestMapper extends BaseMapper<GymScheduleRequest> {
}
