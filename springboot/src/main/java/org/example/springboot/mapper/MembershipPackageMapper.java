package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.MembershipPackage;

/**
 * 会员套餐Mapper接口
 * @author system
 */
@Mapper
public interface MembershipPackageMapper extends BaseMapper<MembershipPackage> {
}
