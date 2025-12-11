package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymEquipmentQueue;

/**
 * 器材排队Mapper接口
 * @author system
 */
@Mapper
public interface GymEquipmentQueueMapper extends BaseMapper<GymEquipmentQueue> {
}
