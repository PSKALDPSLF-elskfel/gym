package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymTrainingPlanDetail;

/**
 * 训练计划明细Mapper接口
 * @author system
 */
@Mapper
public interface GymTrainingPlanDetailMapper extends BaseMapper<GymTrainingPlanDetail> {
}
