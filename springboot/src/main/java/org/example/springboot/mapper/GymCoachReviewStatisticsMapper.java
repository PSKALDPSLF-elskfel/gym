package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.GymCoachReviewStatistics;

/**
 * 教练评价统计Mapper接口
 */
@Mapper
public interface GymCoachReviewStatisticsMapper extends BaseMapper<GymCoachReviewStatistics> {
}
