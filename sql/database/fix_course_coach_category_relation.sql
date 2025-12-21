-- ========================================
-- 课程管理功能 - 教练和分类关联修复脚本
-- 创建时间: 2025-12-20
-- 功能: 为现有课程分配教练和分类
-- ========================================

-- 使用数据库
USE gym;

-- ========================================
-- 第一部分: 为课程分配教练
-- ========================================

-- 根据教练专长为课程分配对应的教练
-- 教练ID=1: 李敏 - 瑜伽、普拉提、柔韧性训练
-- 教练ID=2: 王强 - 力量训练、健美、运动表现  
-- 教练ID=3: 张华 - 有氧健身、动感单车、团课

-- 1. 瑜伽基础课 → 李敏教练(ID=1)
UPDATE gym_course 
SET coach_id = 1,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440001'
  AND coach_id IS NULL;

-- 2. 高强度间歇训练(HIIT) → 张华教练(ID=3) [有氧运动]
UPDATE gym_course 
SET coach_id = 3,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440002'
  AND coach_id IS NULL;

-- 3. 普拉提塑形 → 李敏教练(ID=1)
UPDATE gym_course 
SET coach_id = 1,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440003'
  AND coach_id IS NULL;

-- 4. 动感单车 → 张华教练(ID=3)
UPDATE gym_course 
SET coach_id = 3,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440004'
  AND coach_id IS NULL;

-- 5. 力量训练基础 → 王强教练(ID=2)
UPDATE gym_course 
SET coach_id = 2,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440005'
  AND coach_id IS NULL;

-- 6. 拉伸放松课 → 李敏教练(ID=1) [柔韧性训练]
UPDATE gym_course 
SET coach_id = 1,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440006'
  AND coach_id IS NULL;

-- ========================================
-- 第二部分: 为课程分配分类
-- ========================================

-- 根据课程名称和类型分配对应的分类
-- 分类ID=1: 瑜伽课程
-- 分类ID=2: 力量训练
-- 分类ID=3: 有氧运动
-- 分类ID=4: 普拉提
-- 分类ID=5: 动感单车
-- 分类ID=6: 拉伸放松

-- 1. 瑜伽基础课 → 瑜伽课程分类(ID=1)
UPDATE gym_course 
SET category_id = 1,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440001'
  AND category_id IS NULL;

-- 2. 高强度间歇训练(HIIT) → 有氧运动分类(ID=3)
UPDATE gym_course 
SET category_id = 3,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440002'
  AND category_id IS NULL;

-- 3. 普拉提塑形 → 普拉提分类(ID=4)
UPDATE gym_course 
SET category_id = 4,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440003'
  AND category_id IS NULL;

-- 4. 动感单车 → 动感单车分类(ID=5)
UPDATE gym_course 
SET category_id = 5,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440004'
  AND category_id IS NULL;

-- 5. 力量训练基础 → 力量训练分类(ID=2)
UPDATE gym_course 
SET category_id = 2,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440005'
  AND category_id IS NULL;

-- 6. 拉伸放松课 → 拉伸放松分类(ID=6)
UPDATE gym_course 
SET category_id = 6,
    update_time = NOW()
WHERE id = '550e8400-e29b-41d4-a716-446655440006'
  AND category_id IS NULL;

-- ========================================
-- 第三部分: 数据验证查询
-- ========================================

-- 查询所有课程的教练和分类关联情况
SELECT 
    c.id AS '课程ID',
    c.name AS '课程名称',
    c.coach_id AS '教练ID',
    u.nickname AS '教练姓名',
    coach.specialty AS '教练专长',
    c.category_id AS '分类ID',
    cat.name AS '分类名称',
    c.status AS '课程状态',
    CASE c.status 
        WHEN 0 THEN '下架' 
        WHEN 1 THEN '上架' 
        ELSE '未知' 
    END AS '状态说明'
FROM gym_course c
LEFT JOIN gym_coach coach ON c.coach_id = coach.id
LEFT JOIN user u ON coach.user_id = u.id
LEFT JOIN gym_course_category cat ON c.category_id = cat.id
ORDER BY c.create_time;

-- 统计课程分配情况
SELECT 
    '总课程数' AS '统计项',
    COUNT(*) AS '数量'
FROM gym_course
UNION ALL
SELECT 
    '已分配教练的课程',
    COUNT(*) 
FROM gym_course 
WHERE coach_id IS NOT NULL
UNION ALL
SELECT 
    '未分配教练的课程',
    COUNT(*) 
FROM gym_course 
WHERE coach_id IS NULL
UNION ALL
SELECT 
    '已分配分类的课程',
    COUNT(*) 
FROM gym_course 
WHERE category_id IS NOT NULL
UNION ALL
SELECT 
    '未分配分类的课程',
    COUNT(*) 
FROM gym_course 
WHERE category_id IS NULL;

-- ========================================
-- 执行完成提示
-- ========================================
SELECT '✓ 课程教练和分类关联修复完成！' AS '执行状态';
SELECT '请查看上方验证查询结果，确认所有课程都已正确分配教练和分类' AS '提示信息';
