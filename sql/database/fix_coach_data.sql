-- 修复教练数据问题
-- 为已存在的教练用户添加 gym_coach 记录

-- 1. 为 lw1 教练（userId=8）添加 gym_coach 记录
INSERT INTO `gym_coach` (`user_id`, `specialty`, `certificate`, `introduction`, `rating`, `status`, `create_time`, `update_time`)
SELECT 8, '综合训练', '[]', '新加入的教练', 0.0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `gym_coach` WHERE `user_id` = 8);

-- 2. 如果 admin 用户需要作为教练，需要先将其用户类型改为 COACH，然后添加教练记录
-- UPDATE `user` SET `user_type` = 'COACH' WHERE `id` = 1 AND `user_type` = 'ADMIN';
-- INSERT INTO `gym_coach` (`user_id`, `specialty`, `certificate`, `introduction`, `rating`, `status`, `create_time`, `update_time`)
-- SELECT 1, '综合管理', '[]', '系统管理员兼教练', 0.0, 1, NOW(), NOW()
-- WHERE NOT EXISTS (SELECT 1 FROM `gym_coach` WHERE `user_id` = 1);

-- 3. 查询所有教练用户及其对应的 gym_coach 记录（用于验证）
SELECT 
    u.id AS user_id,
    u.username,
    u.nickname,
    u.user_type,
    gc.id AS coach_id,
    gc.specialty,
    gc.status AS coach_status
FROM `user` u
LEFT JOIN `gym_coach` gc ON u.id = gc.user_id
WHERE u.user_type = 'COACH'
ORDER BY u.id;

-- 4. 查询缺少 gym_coach 记录的教练用户
SELECT 
    u.id AS user_id,
    u.username,
    u.nickname,
    u.user_type
FROM `user` u
LEFT JOIN `gym_coach` gc ON u.id = gc.user_id
WHERE u.user_type = 'COACH' AND gc.id IS NULL;
