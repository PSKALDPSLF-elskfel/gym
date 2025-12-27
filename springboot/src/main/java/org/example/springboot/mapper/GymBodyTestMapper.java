package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymBodyTest;

/**
 * 体测数据Mapper接口
 * @author system
 */
@Mapper
public interface GymBodyTestMapper extends BaseMapper<GymBodyTest> {
}
