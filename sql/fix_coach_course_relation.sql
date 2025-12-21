-- ======================================================
-- 修复教练端课程管理 - 为课程分配教练
-- ======================================================
-- 说明:
-- 1. 根据教练专业领域为课程分配相应教练
-- 2. 教练1(user_id=4): 瑜伽、普拉提
-- 3. 教练2(user_id=5): 力量训练
-- 4. 教练3(user_id=6): 有氧、动感单车
-- ======================================================

-- 为瑜伽基础课分配教练1(瑜伽专业)
UPDATE `gym_course` 
SET `coach_id` = 1 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440001';

-- 为高强度间歇训练(HIIT)分配教练3(有氧专业)
UPDATE `gym_course` 
SET `coach_id` = 3 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440002';

-- 为普拉提塑形分配教练1(普拉提专业)
UPDATE `gym_course` 
SET `coach_id` = 1 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440003';

-- 为动感单车分配教练3(动感单车专业)
UPDATE `gym_course` 
SET `coach_id` = 3 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440004';

-- 为力量训练基础分配教练2(力量训练专业)
UPDATE `gym_course` 
SET `coach_id` = 2 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440005';

-- 为拉伸放松课分配教练1(柔韧性训练专业)
UPDATE `gym_course` 
SET `coach_id` = 1 
WHERE `id` = '550e8400-e29b-41d4-a716-446655440006';

-- 验证更新结果
SELECT 
    c.id,
    c.name AS '课程名称',
    c.coach_id AS '教练ID',
    CASE c.coach_id
        WHEN 1 THEN '李教练(瑜伽/普拉提)'
        WHEN 2 THEN '王教练(力量训练)'
        WHEN 3 THEN '张教练(有氧/单车)'
        ELSE '未分配'
    END AS '教练姓名',
    cc.name AS '课程分类'
FROM `gym_course` c
LEFT JOIN `gym_course_category` cc ON c.category_id = cc.id
ORDER BY c.coach_id;
