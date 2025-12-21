-- ====================================================================
-- 教练端训练计划功能测试脚本
-- 创建时间: 2025-12-19
-- 说明: 用于验证训练计划功能是否正常实现
-- ====================================================================

-- 1. 检查训练计划数据
SELECT 
    COUNT(*) as '训练计划总数',
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as '进行中',
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as '已结束'
FROM gym_training_plan;

-- 2. 检查各教练的训练计划数量
SELECT 
    c.id as '教练ID',
    u.nickname as '教练名称',
    COUNT(tp.id) as '训练计划数',
    SUM(CASE WHEN tp.status = 1 THEN 1 ELSE 0 END) as '进行中',
    SUM(CASE WHEN tp.status = 0 THEN 1 ELSE 0 END) as '已结束'
FROM gym_coach c
LEFT JOIN user u ON c.user_id = u.id
LEFT JOIN gym_training_plan tp ON tp.coach_id = c.id
GROUP BY c.id, u.nickname
ORDER BY c.id;

-- 3. 检查教练学员关系数据
SELECT 
    coach_id as '教练ID',
    COUNT(*) as '学员数量'
FROM gym_coach_student
GROUP BY coach_id
ORDER BY coach_id;

-- 4. 检查训练动作库
SELECT 
    COUNT(*) as '训练动作总数',
    COUNT(DISTINCT category) as '动作分类数'
FROM gym_training_action;

-- 5. 检查训练计划模板
SELECT 
    COUNT(*) as '模板总数',
    SUM(CASE WHEN is_public = 1 THEN 1 ELSE 0 END) as '系统模板',
    SUM(CASE WHEN is_public = 0 THEN 1 ELSE 0 END) as '私有模板'
FROM gym_training_plan_template;

-- 6. 查看教练1(李教练)的学员和训练计划
SELECT 
    u.id as '学员ID',
    u.nickname as '学员名称',
    cs.remark as '备注',
    COUNT(tp.id) as '训练计划数'
FROM gym_coach_student cs
LEFT JOIN user u ON cs.student_id = u.id
LEFT JOIN gym_training_plan tp ON tp.user_id = u.id AND tp.coach_id = cs.coach_id
WHERE cs.coach_id = 1
GROUP BY u.id, u.nickname, cs.remark
ORDER BY u.id;

-- 7. 查看教练2(王教练)的学员和训练计划
SELECT 
    u.id as '学员ID',
    u.nickname as '学员名称',
    cs.remark as '备注',
    COUNT(tp.id) as '训练计划数'
FROM gym_coach_student cs
LEFT JOIN user u ON cs.student_id = u.id
LEFT JOIN gym_training_plan tp ON tp.user_id = u.id AND tp.coach_id = cs.coach_id
WHERE cs.coach_id = 2
GROUP BY u.id, u.nickname, cs.remark
ORDER BY u.id;

-- 8. 查看训练计划明细统计
SELECT 
    tp.id as '计划ID',
    tp.name as '计划名称',
    u.nickname as '学员',
    COUNT(tpd.id) as '动作数量'
FROM gym_training_plan tp
LEFT JOIN user u ON tp.user_id = u.id
LEFT JOIN gym_training_plan_detail tpd ON tpd.plan_id = tp.id
GROUP BY tp.id, tp.name, u.nickname
ORDER BY tp.id;

-- ====================================================================
-- 预期结果:
-- 1. 训练计划总数应该 > 0 (如果已导入测试数据)
-- 2. 三个教练都应该有学员
-- 3. 教练1有12个学员,教练2有11个学员,教练3有10个学员
-- 4. 训练动作库应该有6个动作
-- 5. 训练计划模板应该有3个系统模板
-- ====================================================================
