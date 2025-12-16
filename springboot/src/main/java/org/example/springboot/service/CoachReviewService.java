package org.example.springboot.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.CoachReviewCreateDTO;
import org.example.springboot.dto.command.CoachReviewReplyDTO;
import org.example.springboot.dto.response.CoachReviewResponseDTO;
import org.example.springboot.dto.response.CoachReviewStatisticsResponseDTO;
import org.example.springboot.dto.response.ReviewTagResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.enums.ReviewStatus;
import org.example.springboot.enums.ReviewType;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教练评价服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoachReviewService {
    
    private final GymCoachReviewMapper reviewMapper;
    private final GymCoachReviewStatisticsMapper statisticsMapper;
    private final GymReviewTagMapper tagMapper;
    private final GymReviewHelpfulMapper helpfulMapper;
    private final GymCoachMapper coachMapper;
    private final UserMapper userMapper;
    private final GymTrainingPlanMapper trainingPlanMapper;
    private final GymCourseBookingMapper courseBookingMapper;
    private final GymCourseScheduleMapper courseScheduleMapper;
    private final GymCourseMapper courseMapper;
    
    /**
     * 创建评价
     *
     * @param dto 评价信息
     * @param userId 用户ID
     * @return 评价ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(CoachReviewCreateDTO dto, Long userId) {
        // 1. 验证教练是否存在
        GymCoach coach = coachMapper.selectById(dto.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        
        // 2. 验证是否已评价
        LambdaQueryWrapper<GymCoachReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReview::getUserId, userId)
               .eq(GymCoachReview::getCoachId, dto.getCoachId())
               .eq(GymCoachReview::getReviewType, dto.getReviewType());
        
        if (dto.getPlanId() != null) {
            wrapper.eq(GymCoachReview::getPlanId, dto.getPlanId());
        }
        if (dto.getCourseBookingId() != null) {
            wrapper.eq(GymCoachReview::getCourseBookingId, dto.getCourseBookingId());
        }
        
        Long count = reviewMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("您已评价过该" + 
                (dto.getReviewType().equals(ReviewType.TRAINING_PLAN.getCode()) ? "训练计划" : "课程"));
        }
        
        // 3. 创建评价记录
        GymCoachReview review = new GymCoachReview();
        review.setUserId(userId);
        review.setCoachId(dto.getCoachId());
        review.setPlanId(dto.getPlanId());
        review.setCourseBookingId(dto.getCourseBookingId());
        review.setReviewType(dto.getReviewType());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setIsAnonymous(dto.getIsAnonymous());
        review.setStatus(ReviewStatus.NORMAL.getCode());
        review.setIsTop(0);
        
        // 标签列表转JSON
        if (!CollectionUtils.isEmpty(dto.getTagList())) {
            review.setTagList(JSON.toJSONString(dto.getTagList()));
            // 更新标签使用次数
            updateTagUsageCount(dto.getTagList());
        }
        
        // 图片列表转JSON
        if (!CollectionUtils.isEmpty(dto.getImages())) {
            review.setImages(JSON.toJSONString(dto.getImages()));
        }
        
        reviewMapper.insert(review);
        
        // 4. 更新统计信息
        updateReviewStatistics(dto.getCoachId());
        
        // 5. 更新教练平均评分
        updateCoachRating(dto.getCoachId());
        
        log.info("用户 {} 对教练 {} 创建评价成功，评价ID: {}", userId, dto.getCoachId(), review.getId());
        
        return review.getId();
    }
    
    /**
     * 教练回复评价
     *
     * @param reviewId 评价ID
     * @param dto 回复内容
     * @param coachId 教练ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long reviewId, CoachReviewReplyDTO dto, Long coachId) {
        GymCoachReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        if (!review.getCoachId().equals(coachId)) {
            throw new BusinessException("只能回复自己的评价");
        }
        
        if (StringUtils.hasText(review.getReply())) {
            throw new BusinessException("已回复过该评价");
        }
        
        review.setReply(dto.getReply());
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        
        // 更新统计信息（回复率）
        updateReviewStatistics(coachId);
        
        log.info("教练 {} 回复评价 {} 成功", coachId, reviewId);
    }
    
    /**
     * 删除评价
     *
     * @param reviewId 评价ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long reviewId, Long userId) {
        GymCoachReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评价");
        }
        
        review.setStatus(ReviewStatus.DELETED.getCode());
        reviewMapper.updateById(review);
        
        // 更新统计信息
        updateReviewStatistics(review.getCoachId());
        
        // 更新教练评分
        updateCoachRating(review.getCoachId());
        
        log.info("用户 {} 删除评价 {} 成功", userId, reviewId);
    }
    
    /**
     * 点赞/取消点赞评价
     *
     * @param reviewId 评价ID
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void toggleHelpful(Long reviewId, Long userId) {
        // 检查评价是否存在
        GymCoachReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 查询是否已点赞
        LambdaQueryWrapper<GymReviewHelpful> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymReviewHelpful::getReviewId, reviewId)
               .eq(GymReviewHelpful::getUserId, userId);
        
        GymReviewHelpful helpful = helpfulMapper.selectOne(wrapper);
        
        if (helpful == null) {
            // 添加点赞
            helpful = new GymReviewHelpful();
            helpful.setReviewId(reviewId);
            helpful.setUserId(userId);
            helpful.setIsHelpful(1);
            helpfulMapper.insert(helpful);
            log.info("用户 {} 点赞评价 {} 成功", userId, reviewId);
        } else {
            // 取消点赞
            helpfulMapper.deleteById(helpful.getId());
            log.info("用户 {} 取消点赞评价 {} 成功", userId, reviewId);
        }
    }
    
    /**
     * 分页查询评价列表
     *
     * @param coachId 教练ID
     * @param rating 评分筛选
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param currentUserId 当前用户ID
     * @return 评价分页列表
     */
    public IPage<CoachReviewResponseDTO> getReviewPage(Long coachId, Integer rating, 
                                                       Integer pageNum, Integer pageSize, Long currentUserId) {
        Page<GymCoachReview> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<GymCoachReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReview::getStatus, ReviewStatus.NORMAL.getCode())
               .orderByDesc(GymCoachReview::getIsTop)
               .orderByDesc(GymCoachReview::getCreateTime);
        
        if (coachId != null) {
            wrapper.eq(GymCoachReview::getCoachId, coachId);
        }
        
        if (rating != null) {
            wrapper.eq(GymCoachReview::getRating, rating);
        }
        
        IPage<GymCoachReview> reviewPage = reviewMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        return reviewPage.convert(this::convertToResponseDTO);
    }
    
    /**
     * 获取我的评价列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页列表
     */
    public IPage<CoachReviewResponseDTO> getMyReviews(Long userId, Integer pageNum, Integer pageSize) {
        Page<GymCoachReview> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<GymCoachReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReview::getUserId, userId)
               .ne(GymCoachReview::getStatus, ReviewStatus.DELETED.getCode())
               .orderByDesc(GymCoachReview::getCreateTime);
        
        IPage<GymCoachReview> reviewPage = reviewMapper.selectPage(page, wrapper);
        
        return reviewPage.convert(this::convertToResponseDTO);
    }
    
    /**
     * 获取教练收到的评价列表（教练端）
     *
     * @param coachId 教练ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页列表
     */
    public IPage<CoachReviewResponseDTO> getCoachReceivedReviews(Long coachId, Integer pageNum, Integer pageSize) {
        Page<GymCoachReview> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<GymCoachReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReview::getCoachId, coachId)
               .ne(GymCoachReview::getStatus, ReviewStatus.DELETED.getCode())
               .orderByDesc(GymCoachReview::getCreateTime);
        
        IPage<GymCoachReview> reviewPage = reviewMapper.selectPage(page, wrapper);
        
        return reviewPage.convert(this::convertToResponseDTO);
    }
    
    /**
     * 获取评价详情
     *
     * @param reviewId 评价ID
     * @param currentUserId 当前用户ID
     * @return 评价详情
     */
    public CoachReviewResponseDTO getReviewDetail(Long reviewId, Long currentUserId) {
        GymCoachReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        return convertToResponseDTO(review);
    }
    
    /**
     * 获取教练评价统计
     *
     * @param coachId 教练ID
     * @return 统计信息
     */
    public CoachReviewStatisticsResponseDTO getReviewStatistics(Long coachId) {
        LambdaQueryWrapper<GymCoachReviewStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReviewStatistics::getCoachId, coachId);
        
        GymCoachReviewStatistics statistics = statisticsMapper.selectOne(wrapper);
        
        if (statistics == null) {
            // 如果没有统计记录，创建一个默认的
            statistics = new GymCoachReviewStatistics();
            statistics.setCoachId(coachId);
            statistics.setTotalReviews(0);
            statistics.setAverageRating(BigDecimal.ZERO);
            statistics.setRating5Count(0);
            statistics.setRating4Count(0);
            statistics.setRating3Count(0);
            statistics.setRating2Count(0);
            statistics.setRating1Count(0);
            statistics.setReplyRate(BigDecimal.ZERO);
        }
        
        CoachReviewStatisticsResponseDTO dto = new CoachReviewStatisticsResponseDTO();
        BeanUtils.copyProperties(statistics, dto);
        
        // 获取教练姓名
        GymCoach coach = coachMapper.selectById(coachId);
        if (coach != null) {
            User user = userMapper.selectById(coach.getUserId());
            if (user != null) {
                dto.setCoachName(user.getNickname());
            }
        }
        
        return dto;
    }
    
    /**
     * 获取评价标签列表
     *
     * @param tagType 标签类型：1-正面，2-负面
     * @return 标签列表
     */
    public List<ReviewTagResponseDTO> getReviewTags(Integer tagType) {
        LambdaQueryWrapper<GymReviewTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymReviewTag::getStatus, 1)
               .orderByAsc(GymReviewTag::getSortOrder);
        
        if (tagType != null) {
            wrapper.eq(GymReviewTag::getTagType, tagType);
        }
        
        List<GymReviewTag> tags = tagMapper.selectList(wrapper);
        
        return tags.stream().map(tag -> {
            ReviewTagResponseDTO dto = new ReviewTagResponseDTO();
            BeanUtils.copyProperties(tag, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private CoachReviewResponseDTO convertToResponseDTO(GymCoachReview review) {
        CoachReviewResponseDTO dto = new CoachReviewResponseDTO();
        BeanUtils.copyProperties(review, dto);
        
        // 设置评价类型描述
        ReviewType reviewType = ReviewType.getByCode(review.getReviewType());
        if (reviewType != null) {
            dto.setReviewTypeDesc(reviewType.getDescription());
        }
        
        // 解析标签JSON
        if (StringUtils.hasText(review.getTagList())) {
            dto.setTagList(JSON.parseArray(review.getTagList(), String.class));
        }
        
        // 解析图片JSON
        if (StringUtils.hasText(review.getImages())) {
            dto.setImages(JSON.parseArray(review.getImages(), String.class));
        }
        
        // 获取用户信息（如果不是匿名）
        if (review.getIsAnonymous() != 1) {
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                dto.setUserNickname(user.getNickname());
                dto.setUserAvatar(user.getAvatar());
            }
        } else {
            dto.setUserNickname("匿名用户");
        }
        
        // 获取教练信息
        GymCoach coach = coachMapper.selectById(review.getCoachId());
        if (coach != null) {
            User coachUser = userMapper.selectById(coach.getUserId());
            if (coachUser != null) {
                dto.setCoachName(coachUser.getNickname());
            }
        }
        
        // 获取训练计划名称
        if (review.getPlanId() != null) {
            GymTrainingPlan plan = trainingPlanMapper.selectById(review.getPlanId());
            if (plan != null) {
                dto.setPlanName(plan.getName());
            }
        }
        
        // 获取课程名称
        if (review.getCourseBookingId() != null) {
            GymCourseBooking booking = courseBookingMapper.selectById(review.getCourseBookingId());
            if (booking != null) {
                GymCourseSchedule schedule = courseScheduleMapper.selectById(booking.getScheduleId());
                if (schedule != null) {
                    GymCourse course = courseMapper.selectById(schedule.getCourseId());
                    if (course != null) {
                        dto.setCourseName(course.getName());
                    }
                }
            }
        }
        
        // 获取点赞数量
        LambdaQueryWrapper<GymReviewHelpful> helpfulWrapper = new LambdaQueryWrapper<>();
        helpfulWrapper.eq(GymReviewHelpful::getReviewId, review.getId());
        Long helpfulCount = helpfulMapper.selectCount(helpfulWrapper);
        dto.setHelpfulCount(helpfulCount.intValue());
        
        dto.setIsHelpfulByCurrentUser(false);
        
        return dto;
    }
    
    /**
     * 更新评价统计信息
     */
    private void updateReviewStatistics(Long coachId) {
        // 查询所有有效评价
        LambdaQueryWrapper<GymCoachReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymCoachReview::getCoachId, coachId)
               .eq(GymCoachReview::getStatus, ReviewStatus.NORMAL.getCode());
        
        List<GymCoachReview> reviews = reviewMapper.selectList(wrapper);
        
        int totalReviews = reviews.size();
        int rating5 = 0, rating4 = 0, rating3 = 0, rating2 = 0, rating1 = 0;
        int replyCount = 0;
        BigDecimal totalRating = BigDecimal.ZERO;
        LocalDateTime lastReviewTime = null;
        
        for (GymCoachReview review : reviews) {
            // 统计星级
            switch (review.getRating()) {
                case 5: rating5++; break;
                case 4: rating4++; break;
                case 3: rating3++; break;
                case 2: rating2++; break;
                case 1: rating1++; break;
            }
            
            totalRating = totalRating.add(BigDecimal.valueOf(review.getRating()));
            
            // 统计回复数
            if (StringUtils.hasText(review.getReply())) {
                replyCount++;
            }
            
            // 最后评价时间
            if (lastReviewTime == null || review.getCreateTime().isAfter(lastReviewTime)) {
                lastReviewTime = review.getCreateTime();
            }
        }
        
        // 计算平均评分
        BigDecimal averageRating = totalReviews > 0 
            ? totalRating.divide(BigDecimal.valueOf(totalReviews), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        // 计算回复率
        BigDecimal replyRate = totalReviews > 0
            ? BigDecimal.valueOf(replyCount)
                .divide(BigDecimal.valueOf(totalReviews), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
        
        // 查询或创建统计记录
        LambdaQueryWrapper<GymCoachReviewStatistics> statsWrapper = new LambdaQueryWrapper<>();
        statsWrapper.eq(GymCoachReviewStatistics::getCoachId, coachId);
        GymCoachReviewStatistics statistics = statisticsMapper.selectOne(statsWrapper);
        
        if (statistics == null) {
            statistics = new GymCoachReviewStatistics();
            statistics.setCoachId(coachId);
        }
        
        statistics.setTotalReviews(totalReviews);
        statistics.setAverageRating(averageRating);
        statistics.setRating5Count(rating5);
        statistics.setRating4Count(rating4);
        statistics.setRating3Count(rating3);
        statistics.setRating2Count(rating2);
        statistics.setRating1Count(rating1);
        statistics.setReplyRate(replyRate);
        statistics.setLastReviewTime(lastReviewTime);
        
        if (statistics.getId() == null) {
            statisticsMapper.insert(statistics);
        } else {
            statisticsMapper.updateById(statistics);
        }
    }
    
    /**
     * 更新教练平均评分
     */
    private void updateCoachRating(Long coachId) {
        Double avgRating = reviewMapper.selectAverageRating(coachId);
        
        GymCoach coach = coachMapper.selectById(coachId);
        if (coach != null) {
            coach.setRating(avgRating != null ? BigDecimal.valueOf(avgRating) : BigDecimal.ZERO);
            coachMapper.updateById(coach);
        }
    }
    
    /**
     * 更新标签使用次数
     */
    private void updateTagUsageCount(List<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            return;
        }
        
        for (String tagName : tagNames) {
            LambdaQueryWrapper<GymReviewTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GymReviewTag::getTagName, tagName);
            
            GymReviewTag tag = tagMapper.selectOne(wrapper);
            if (tag != null) {
                tag.setUsageCount(tag.getUsageCount() + 1);
                tagMapper.updateById(tag);
            }
        }
    }
}
