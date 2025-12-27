package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymEquipment;

/**
 * 器材Mapper接口
 * @author system
 */
@Mapper
public interface GymEquipmentMapper extends BaseMapper<GymEquipment> {
}
