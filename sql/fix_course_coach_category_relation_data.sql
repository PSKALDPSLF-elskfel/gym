-- ======================================
-- 课程管理功能三端优化 - 数据修复脚本
-- 功能：为现有课程分配教练和分类
-- 日期：2025-12-20
-- ======================================

-- 1. 为课程分配教练
-- 将瑜伽类课程分配给教练ID=1 (李敏,瑜伽教练)
UPDATE gym_course 
SET coach_id = 1 
WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440001',  -- 瑜伽基础课
    '550e8400-e29b-41d4-a716-446655440003'   -- 普拉提塑形
);

-- 将力量训练课程分配给教练ID=2 (王强,力量训练教练)
UPDATE gym_course 
SET coach_id = 2 
WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440002',  -- 高强度间歇训练
    '550e8400-e29b-41d4-a716-446655440005'   -- 力量训练基础
);

-- 将有氧课程分配给教练ID=3 (张华,有氧健身教练)
UPDATE gym_course 
SET coach_id = 3 
WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440004',  -- 动感单车
    '550e8400-e29b-41d4-a716-446655440006'   -- 拉伸放松课
);

-- 2. 为课程分配分类
-- 瑜伽课程 → 瑜伽课程分类(id=1)
UPDATE gym_course 
SET category_id = 1 
WHERE id = '550e8400-e29b-41d4-a716-446655440001';

-- 力量训练 → 力量训练分类(id=2)
UPDATE gym_course 
SET category_id = 2 
WHERE id = '550e8400-e29b-41d4-a716-446655440005';

-- HIIT → 有氧运动分类(id=3)
UPDATE gym_course 
SET category_id = 3 
WHERE id = '550e8400-e29b-41d4-a716-446655440002';

-- 普拉提 → 普拉提分类(id=4)
UPDATE gym_course 
SET category_id = 4 
WHERE id = '550e8400-e29b-41d4-a716-446655440003';

-- 动感单车 → 动感单车分类(id=5)
UPDATE gym_course 
SET category_id = 5 
WHERE id = '550e8400-e29b-41d4-a716-446655440004';

-- 拉伸放松 → 拉伸放松分类(id=6)
UPDATE gym_course 
SET category_id = 6 
WHERE id = '550e8400-e29b-41d4-a716-446655440006';

-- 3. 验证修复结果
SELECT 
    c.id AS 课程ID,
    c.name AS 课程名称,
    c.coach_id AS 教练ID,
    u.nickname AS 教练姓名,
    c.category_id AS 分类ID,
    cc.name AS 分类名称,
    c.status AS 状态
FROM gym_course c
LEFT JOIN gym_coach gc ON c.coach_id = gc.id
LEFT JOIN user u ON gc.user_id = u.id
LEFT JOIN gym_course_category cc ON c.category_id = cc.id
ORDER BY c.create_time DESC;

-- 4. 统计修复情况
SELECT 
    COUNT(*) AS 总课程数,
    SUM(CASE WHEN coach_id IS NOT NULL THEN 1 ELSE 0 END) AS 已分配教练课程数,
    SUM(CASE WHEN category_id IS NOT NULL THEN 1 ELSE 0 END) AS 已分配分类课程数,
    SUM(CASE WHEN coach_id IS NULL THEN 1 ELSE 0 END) AS 未分配教练课程数,
    SUM(CASE WHEN category_id IS NULL THEN 1 ELSE 0 END) AS 未分配分类课程数
FROM gym_course;
