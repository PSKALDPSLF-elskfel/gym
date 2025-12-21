-- 修复教练评价统计数据
-- 该脚本会重新计算所有教练的评价统计信息，确保统计数据与实际评价数据一致

-- 1. 先查看当前统计数据和实际评价数据的差异
SELECT 
    '=== 当前统计数据与实际评价数据对比 ===' as info;

SELECT 
    c.id as coach_id,
    u.nickname as coach_name,
    s.total_reviews as stat_total,
    s.rating_5_count as stat_5_star,
    s.rating_4_count as stat_4_star,
    s.rating_3_count as stat_3_star,
    s.rating_2_count as stat_2_star,
    s.rating_1_count as stat_1_star,
    s.average_rating as stat_avg,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1) as actual_total,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1 AND rating = 5) as actual_5_star,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1 AND rating = 4) as actual_4_star,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1 AND rating = 3) as actual_3_star,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1 AND rating = 2) as actual_2_star,
    (SELECT COUNT(*) FROM gym_coach_review WHERE coach_id = c.id AND status = 1 AND rating = 1) as actual_1_star
FROM gym_coach c
LEFT JOIN gym_coach_review_statistics s ON c.id = s.coach_id
LEFT JOIN user u ON c.user_id = u.id
ORDER BY c.id;

-- 2. 重新计算并更新所有教练的统计数据
SELECT 
    '=== 开始更新统计数据 ===' as info;

-- 先删除所有统计数据（如果需要的话，可以注释掉这行）
-- DELETE FROM gym_coach_review_statistics;

-- 为每个有评价的教练重新计算统计数据
INSERT INTO gym_coach_review_statistics (
    coach_id, 
    total_reviews, 
    average_rating, 
    rating_5_count, 
    rating_4_count, 
    rating_3_count, 
    rating_2_count, 
    rating_1_count, 
    reply_rate,
    last_review_time
)
SELECT 
    coach_id,
    COUNT(*) as total_reviews,
    ROUND(AVG(rating), 2) as average_rating,
    SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) as rating_5_count,
    SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) as rating_4_count,
    SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) as rating_3_count,
    SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) as rating_2_count,
    SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) as rating_1_count,
    ROUND(
        SUM(CASE WHEN reply IS NOT NULL AND reply != '' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 
        2
    ) as reply_rate,
    MAX(create_time) as last_review_time
FROM gym_coach_review
WHERE status = 1  -- 1表示正常状态
GROUP BY coach_id
ON DUPLICATE KEY UPDATE
    total_reviews = VALUES(total_reviews),
    average_rating = VALUES(average_rating),
    rating_5_count = VALUES(rating_5_count),
    rating_4_count = VALUES(rating_4_count),
    rating_3_count = VALUES(rating_3_count),
    rating_2_count = VALUES(rating_2_count),
    rating_1_count = VALUES(rating_1_count),
    reply_rate = VALUES(reply_rate),
    last_review_time = VALUES(last_review_time),
    update_time = NOW();

-- 3. 更新教练表中的平均评分
UPDATE gym_coach c
LEFT JOIN (
    SELECT 
        coach_id,
        ROUND(AVG(rating), 2) as avg_rating
    FROM gym_coach_review
    WHERE status = 1
    GROUP BY coach_id
) r ON c.id = r.coach_id
SET c.rating = COALESCE(r.avg_rating, 0);

-- 4. 验证更新结果
SELECT 
    '=== 更新后的统计数据 ===' as info;

SELECT 
    c.id as coach_id,
    u.nickname as coach_name,
    s.total_reviews,
    s.average_rating,
    s.rating_5_count as '5星',
    s.rating_4_count as '4星',
    s.rating_3_count as '3星',
    s.rating_2_count as '2星',
    s.rating_1_count as '1星',
    s.reply_rate,
    s.last_review_time
FROM gym_coach c
LEFT JOIN gym_coach_review_statistics s ON c.id = s.coach_id
LEFT JOIN user u ON c.user_id = u.id
WHERE s.total_reviews > 0
ORDER BY c.id;

-- 5. 验证实际评价数据
SELECT 
    '=== 实际评价明细 ===' as info;

SELECT 
    r.id,
    u.nickname as user_name,
    c_user.nickname as coach_name,
    r.rating,
    LEFT(r.content, 50) as content,
    r.status,
    r.create_time
FROM gym_coach_review r
LEFT JOIN user u ON r.user_id = u.id
LEFT JOIN gym_coach c ON r.coach_id = c.id
LEFT JOIN user c_user ON c.user_id = c_user.id
WHERE r.status = 1
ORDER BY r.coach_id, r.create_time DESC;

SELECT '统计数据修复完成！' as message;
