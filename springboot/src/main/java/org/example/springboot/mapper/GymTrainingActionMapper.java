package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymTrainingAction;

/**
 * 训练动作库Mapper接口
 * @author system
 */
@Mapper
public interface GymTrainingActionMapper extends BaseMapper<GymTrainingAction> {
}
