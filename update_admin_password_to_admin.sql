-- ==========================================
-- 将 admin 账号密码修改为 admin
-- ==========================================
-- 说明：
-- 密码明文：admin
-- 密码密文（BCrypt）：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lFOy2PpGJP65bIbTa
-- ==========================================

USE gym;

-- 更新 admin 账号密码为 admin
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lFOy2PpGJP65bIbTa',
    `update_time` = NOW()
WHERE `username` = 'admin';

-- 验证更新结果
SELECT 
    id,
    username,
    user_type AS '用户类型',
    nickname AS '昵称',
    email AS '邮箱',
    '密码已更新为: admin' AS '说明'
FROM `user`
WHERE username = 'admin';

-- ==========================================
-- 执行完成后，可以使用以下账号登录：
-- 用户名：admin
-- 密码：admin
-- ==========================================
