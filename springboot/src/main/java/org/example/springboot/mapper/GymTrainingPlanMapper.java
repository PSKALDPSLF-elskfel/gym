package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymTrainingPlan;

/**
 * 训练计划Mapper接口
 * @author system
 */
@Mapper
public interface GymTrainingPlanMapper extends BaseMapper<GymTrainingPlan> {
}
