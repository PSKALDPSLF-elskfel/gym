-- ==========================================
-- 小程序通知问题诊断和修复脚本
-- ==========================================

-- 1. 检查当前系统中的用户及其类型
SELECT '=== 检查用户表数据 ===' AS info;
SELECT 
    id,
    name,
    username,
    user_type AS '用户类型',
    status AS '状态',
    create_time AS '创建时间'
FROM `user`
ORDER BY id;

-- 2. 统计各类型用户数量
SELECT '=== 用户类型统计 ===' AS info;
SELECT 
    user_type AS '用户类型',
    COUNT(*) AS '数量',
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS '正常用户',
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS '禁用用户'
FROM `user`
GROUP BY user_type;

-- 3. 检查通知表中的数据
SELECT '=== 检查通知表数据 ===' AS info;
SELECT 
    id,
    notification_type AS '通知类型',
    title AS '标题',
    target_user_type AS '目标用户类型',
    target_user_id AS '目标用户ID',
    status AS '状态',
    send_time AS '发送时间',
    create_time AS '创建时间'
FROM `sys_notification`
ORDER BY create_time DESC
LIMIT 20;

-- 4. 检查通知读取记录表
SELECT '=== 检查通知读取记录 ===' AS info;
SELECT 
    nr.id,
    nr.notification_id AS '通知ID',
    nr.user_id AS '用户ID',
    u.name AS '用户名',
    u.user_type AS '用户类型',
    nr.is_read AS '是否已读',
    nr.create_time AS '创建时间'
FROM `sys_notification_read` nr
LEFT JOIN `user` u ON nr.user_id = u.id
ORDER BY nr.create_time DESC
LIMIT 20;

-- 5. 检查哪些通知没有创建读取记录
SELECT '=== 已发送但没有读取记录的通知 ===' AS info;
SELECT 
    n.id,
    n.title AS '标题',
    n.target_user_type AS '目标用户类型',
    n.target_user_id AS '目标用户ID',
    n.status AS '状态',
    n.send_time AS '发送时间',
    COUNT(nr.id) AS '读取记录数'
FROM `sys_notification` n
LEFT JOIN `sys_notification_read` nr ON n.id = nr.notification_id
WHERE n.status = 1  -- 已发送
GROUP BY n.id
HAVING COUNT(nr.id) = 0
ORDER BY n.create_time DESC;

-- 6. 为已发送但缺少读取记录的通知补充读取记录
-- 注意：这个操作会为所有符合条件的用户创建读取记录

-- 获取所有已发送但没有读取记录的通知
SELECT '=== 开始为缺失的通知创建读取记录 ===' AS info;

-- 为目标用户类型为 USER 的通知创建读取记录
INSERT INTO `sys_notification_read` (`notification_id`, `user_id`, `is_read`, `create_time`, `update_time`)
SELECT 
    n.id,
    u.id,
    0,
    NOW(),
    NOW()
FROM `sys_notification` n
CROSS JOIN `user` u
WHERE n.status = 1  -- 已发送
  AND n.target_user_type = 'USER'  -- 目标是小程序用户
  AND (n.target_user_id IS NULL OR n.target_user_id = u.id)  -- 全部用户或指定用户
  AND u.user_type = 'USER'  -- 用户类型是USER
  AND u.status = 1  -- 正常状态
  AND NOT EXISTS (
    -- 避免重复插入
    SELECT 1 FROM `sys_notification_read` nr 
    WHERE nr.notification_id = n.id AND nr.user_id = u.id
  );

-- 为目标用户类型为 ALL 的通知创建读取记录
INSERT INTO `sys_notification_read` (`notification_id`, `user_id`, `is_read`, `create_time`, `update_time`)
SELECT 
    n.id,
    u.id,
    0,
    NOW(),
    NOW()
FROM `sys_notification` n
CROSS JOIN `user` u
WHERE n.status = 1  -- 已发送
  AND n.target_user_type = 'ALL'  -- 目标是所有用户
  AND u.status = 1  -- 正常状态
  AND NOT EXISTS (
    -- 避免重复插入
    SELECT 1 FROM `sys_notification_read` nr 
    WHERE nr.notification_id = n.id AND nr.user_id = u.id
  );

-- 为目标用户类型为 COACH 的通知创建读取记录
INSERT INTO `sys_notification_read` (`notification_id`, `user_id`, `is_read`, `create_time`, `update_time`)
SELECT 
    n.id,
    u.id,
    0,
    NOW(),
    NOW()
FROM `sys_notification` n
CROSS JOIN `user` u
WHERE n.status = 1  -- 已发送
  AND n.target_user_type = 'COACH'  -- 目标是教练
  AND (n.target_user_id IS NULL OR n.target_user_id = u.id)
  AND u.user_type = 'COACH'  -- 用户类型是教练
  AND u.status = 1  -- 正常状态
  AND NOT EXISTS (
    SELECT 1 FROM `sys_notification_read` nr 
    WHERE nr.notification_id = n.id AND nr.user_id = u.id
  );

-- 为目标用户类型为 ADMIN 的通知创建读取记录
INSERT INTO `sys_notification_read` (`notification_id`, `user_id`, `is_read`, `create_time`, `update_time`)
SELECT 
    n.id,
    u.id,
    0,
    NOW(),
    NOW()
FROM `sys_notification` n
CROSS JOIN `user` u
WHERE n.status = 1  -- 已发送
  AND n.target_user_type = 'ADMIN'  -- 目标是管理员
  AND (n.target_user_id IS NULL OR n.target_user_id = u.id)
  AND u.user_type = 'ADMIN'  -- 用户类型是管理员
  AND u.status = 1  -- 正常状态
  AND NOT EXISTS (
    SELECT 1 FROM `sys_notification_read` nr 
    WHERE nr.notification_id = n.id AND nr.user_id = u.id
  );

SELECT '=== 修复完成 ===' AS info;

-- 7. 验证修复结果
SELECT '=== 验证修复结果 ===' AS info;
SELECT 
    n.id,
    n.title AS '标题',
    n.target_user_type AS '目标类型',
    COUNT(nr.id) AS '读取记录数',
    SUM(CASE WHEN nr.is_read = 1 THEN 1 ELSE 0 END) AS '已读数',
    SUM(CASE WHEN nr.is_read = 0 THEN 1 ELSE 0 END) AS '未读数'
FROM `sys_notification` n
LEFT JOIN `sys_notification_read` nr ON n.id = nr.notification_id
WHERE n.status = 1
GROUP BY n.id
ORDER BY n.create_time DESC
LIMIT 20;

-- 8. 查看小程序用户的通知
SELECT '=== 小程序用户的通知列表 ===' AS info;
SELECT 
    u.id AS '用户ID',
    u.name AS '用户名',
    u.user_type AS '用户类型',
    COUNT(nr.id) AS '通知总数',
    SUM(CASE WHEN nr.is_read = 0 THEN 1 ELSE 0 END) AS '未读通知',
    SUM(CASE WHEN nr.is_read = 1 THEN 1 ELSE 0 END) AS '已读通知'
FROM `user` u
LEFT JOIN `sys_notification_read` nr ON u.id = nr.user_id
WHERE u.user_type = 'USER' AND u.status = 1
GROUP BY u.id
ORDER BY u.id;
