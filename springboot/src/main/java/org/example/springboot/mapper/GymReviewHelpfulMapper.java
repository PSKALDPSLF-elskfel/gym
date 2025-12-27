package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymReviewHelpful;

/**
 * 评价点赞Mapper接口
 */
@Mapper
public interface GymReviewHelpfulMapper extends BaseMapper<GymReviewHelpful> {
}
