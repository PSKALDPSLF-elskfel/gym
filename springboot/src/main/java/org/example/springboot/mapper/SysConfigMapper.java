package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.SysConfig;

/**
 * 系统配置Mapper接口
 * @author system
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
