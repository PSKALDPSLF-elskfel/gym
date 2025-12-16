package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymTrainingPlanTemplateDetail;

/**
 * 训练计划模板明细Mapper
 * @author system
 */
@Mapper
public interface GymTrainingPlanTemplateDetailMapper extends BaseMapper<GymTrainingPlanTemplateDetail> {
}
