package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymScheduleRule;

/**
 * 排班规则Mapper接口
 * @author system
 */
@Mapper
public interface GymScheduleRuleMapper extends BaseMapper<GymScheduleRule> {
}
