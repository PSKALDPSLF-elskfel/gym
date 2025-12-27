package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymReviewTag;

/**
 * 评价标签Mapper接口
 */
@Mapper
public interface GymReviewTagMapper extends BaseMapper<GymReviewTag> {
}
