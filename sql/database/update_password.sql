-- 更新admin用户密码为 123456
-- BCrypt加密后的密码
UPDATE user SET password = '$2a$10$N.zmdr9k7uOCQb1TPKgPe.SFbbyNnAZ1Gh9ECLchM9CIZ88UAp0gW' WHERE username = 'admin';

-- 或者创建一个新的教练用户
INSERT INTO user (user_type, name, username, password, email, member_type, status, create_time, update_time)
VALUES ('COACH', '教练张三', 'coach', '$2a$10$N.zmdr9k7uOCQb1TPKgPe.SFbbyNnAZ1Gh9ECLchM9CIZ88UAp0gW', 'coach@gym.com', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE password = '$2a$10$N.zmdr9k7uOCQb1TPKgPe.SFbbyNnAZ1Gh9ECLchM9CIZ88UAp0gW';

-- 密码: 123456
-- 说明: BCrypt每次生成的hash都不同，但都能验证相同的密码
