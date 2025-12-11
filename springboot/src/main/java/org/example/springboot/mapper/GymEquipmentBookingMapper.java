package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymEquipmentBooking;

/**
 * 器材预约Mapper接口
 * @author system
 */
@Mapper
public interface GymEquipmentBookingMapper extends BaseMapper<GymEquipmentBooking> {
}
