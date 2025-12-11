package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.SysNotificationRead;

/**
 * 用户通知读取记录Mapper接口
 * @author system
 */
@Mapper
public interface SysNotificationReadMapper extends BaseMapper<SysNotificationRead> {
}
