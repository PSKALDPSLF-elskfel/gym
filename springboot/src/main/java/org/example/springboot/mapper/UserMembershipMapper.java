package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.UserMembership;

/**
 * 用户会员记录Mapper接口
 * @author system
 */
@Mapper
public interface UserMembershipMapper extends BaseMapper<UserMembership> {
}
