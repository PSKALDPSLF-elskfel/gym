package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCoachStudent;

/**
 * 教练学员关系Mapper
 * @author system
 */
@Mapper
public interface GymCoachStudentMapper extends BaseMapper<GymCoachStudent> {
}
