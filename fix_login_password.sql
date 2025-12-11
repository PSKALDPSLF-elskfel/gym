-- ==========================================
-- 修复登录问题 - 更新测试账号密码
-- ==========================================
-- 说明：
-- 1. 更新 admin 账号密码为 123456
-- 2. 更新 manager 账号密码为 123456  
-- 3. 更新 pgl 账号密码为 123456
-- 
-- 密码加密算法：BCrypt
-- 明文密码：123456
-- 加密后密码：$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa
-- ==========================================

USE gym;

-- 更新 admin 账号密码（管理员）
UPDATE `user` 
SET `password` = '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa',
    `update_time` = NOW()
WHERE `username` = 'admin';

-- 更新 manager 账号密码（健身房经理）
UPDATE `user` 
SET `password` = '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa',
    `update_time` = NOW()
WHERE `username` = 'manager';

-- 更新 pgl 账号密码（普通用户）
UPDATE `user` 
SET `password` = '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa',
    `update_time` = NOW()
WHERE `username` = 'pgl';

-- 验证更新结果
SELECT 
    id,
    username,
    user_type AS '用户类型',
    nickname AS '昵称',
    email AS '邮箱',
    status AS '状态',
    update_time AS '更新时间'
FROM `user`
WHERE username IN ('admin', 'manager', 'pgl')
ORDER BY id;

-- ==========================================
-- 执行完成后，可以使用以下账号登录：
-- 账号：admin   密码：123456  (管理员)
-- 账号：manager 密码：123456  (健身房经理)
-- 账号：pgl     密码：123456  (普通用户)
-- ==========================================
