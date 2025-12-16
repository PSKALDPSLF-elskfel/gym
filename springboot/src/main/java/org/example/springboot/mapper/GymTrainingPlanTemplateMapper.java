package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymTrainingPlanTemplate;

/**
 * 训练计划模板Mapper
 * @author system
 */
@Mapper
public interface GymTrainingPlanTemplateMapper extends BaseMapper<GymTrainingPlanTemplate> {
}
