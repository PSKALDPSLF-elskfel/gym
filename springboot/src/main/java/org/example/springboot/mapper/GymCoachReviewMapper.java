package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.GymCoachReview;

import java.util.Map;

/**
 * 教练评价Mapper接口
 */
@Mapper
public interface GymCoachReviewMapper extends BaseMapper<GymCoachReview> {
    
    /**
     * 分页查询教练评价(带用户和教练信息)
     *
     * @param page 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<Map<String, Object>> selectReviewPageWithUserInfo(@Param("page") Page<?> page, 
                                                             @Param("params") Map<String, Object> params);
    
    /**
     * 查询教练平均评分
     *
     * @param coachId 教练ID
     * @return 平均评分
     */
    Double selectAverageRating(@Param("coachId") Long coachId);
    
    /**
     * 统计教练各星级评价数量
     *
     * @param coachId 教练ID
     * @return 统计结果
     */
    Map<String, Object> selectRatingStatistics(@Param("coachId") Long coachId);
}
