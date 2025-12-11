SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Create the database
DROP DATABASE IF EXISTS gym;
CREATE DATABASE gym;
USE gym;

-- Table structure for user
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '用户类型：USER小程序用户，ADMIN管理员，COACH教练',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称/管理员姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `member_type` tinyint NULL DEFAULT 0 COMMENT '会员类型：0-普通用户，1-黄金会员，2-铂金会员（仅小程序用户）',
  `member_expire_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间（仅小程序用户）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员密码（BCrypt加密）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_user_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_email`(`email` ASC) USING BTREE,
  INDEX `idx_user_member_type`(`member_type` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统一用户表（包含小程序用户和管理员）' ROW_FORMAT = DYNAMIC;

-- Insert sample data
INSERT INTO `user` VALUES (1, 'ADMIN', '系统管理员', '/files/bussiness/user_avatar/1764919899218.jpg', '13900139001', 0, NULL, 'admin', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'admin@gym.com', 1, '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `user` VALUES (2, 'ADMIN', '健身房经理', NULL, '13900139002', 0, NULL, 'manager', '$10$lqnSaaWggBoGo5BftUKuX.hmQzk3yNF4UqC5H47AeySo5KOEg6MEC', 'manager@gym.com', 1, '2025-10-17 09:19:31', '2025-10-28 09:22:58');
INSERT INTO `user` VALUES (5, 'USER', '张三', NULL, '13800138000', 0, NULL, 'testuser', '$2a$10$TUbrMzc.bApp3K4aQzU25OFbcceKhIlboEBRc13638Wgxp9jVLefq', 'test@qq.com', 1, '2025-12-05 15:21:02', '2025-12-05 15:21:02');

SET FOREIGN_KEY_CHECKS = 1;
