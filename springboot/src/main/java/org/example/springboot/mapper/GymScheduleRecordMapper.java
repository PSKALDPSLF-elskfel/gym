package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymScheduleRecord;

/**
 * 排班打卡记录Mapper接口
 * @author system
 */
@Mapper
public interface GymScheduleRecordMapper extends BaseMapper<GymScheduleRecord> {
}
