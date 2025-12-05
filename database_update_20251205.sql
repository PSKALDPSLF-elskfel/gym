-- ========================================
-- 健身房预约系统 - 数据库更新脚本
-- 日期: 2025-12-05
-- 描述: 新增系统配置表
-- ========================================

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `config_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置分组',
  `is_system` tinyint NULL DEFAULT 0 COMMENT '是否系统内置：0-否，1-是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_group`(`config_group` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config (示例配置)
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.name', '健身房预约管理系统', '系统名称', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (2, 'system.version', '1.0.0', '系统版本', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (3, 'booking.advance.days', '7', '可提前预约天数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (4, 'booking.cancel.hours', '2', '取消预约最少提前小时数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (5, 'membership.gold.discount', '0.95', '黄金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (6, 'membership.platinum.discount', '0.90', '铂金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
