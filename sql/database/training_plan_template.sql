-- 训练计划模板表
CREATE TABLE `gym_training_plan_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '教练ID(NULL表示系统模板)',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `goal` varchar(50) NULL DEFAULT NULL COMMENT '训练目标(减脂/增肌/塑形/康复)',
  `description` text COMMENT '模板说明',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难度：1-初级，2-中级，3-高级',
  `duration_days` int NULL DEFAULT 7 COMMENT '建议周期(天)',
  `is_public` tinyint DEFAULT 0 COMMENT '是否公开:0-私有,1-公开(系统模板)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `total_exercises` int NULL DEFAULT 0 COMMENT '总动作数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_goal`(`goal` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_template_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划模板表' ROW_FORMAT = DYNAMIC;

-- 训练计划模板明细表
CREATE TABLE `gym_training_plan_template_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `day_of_week` tinyint NULL DEFAULT NULL COMMENT '星期几(1-7)',
  `action_id` bigint NULL DEFAULT NULL COMMENT '动作ID',
  `sets` int NULL DEFAULT NULL COMMENT '组数',
  `reps` int NULL DEFAULT NULL COMMENT '次数',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `duration` int NULL DEFAULT NULL COMMENT '时长(分钟)',
  `rest_time` int NULL DEFAULT NULL COMMENT '组间休息(秒)',
  `description` varchar(200) NULL DEFAULT NULL COMMENT '说明',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_action_id`(`action_id` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_template_detail_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `gym_training_plan_template` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_training_plan_template_detail_ibfk_2` FOREIGN KEY (`action_id`) REFERENCES `gym_training_action` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划模板明细表' ROW_FORMAT = DYNAMIC;

-- 初始化系统模板 - 减脂计划
INSERT INTO `gym_training_plan_template` VALUES (1, NULL, '减脂塑形-初级计划', '减脂', '适合初学者的减脂计划，注重有氧运动和核心训练结合', 1, 28, 1, 1, 12, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (1, 1, 1, 1, 3, 12, 60.00, NULL, 60, '胸部训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (2, 1, 1, 2, 4, 8, 80.00, NULL, 90, '下肢训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (3, 1, 2, 5, 3, 20, NULL, 30, 60, '腹部核心训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (4, 1, 2, 6, 3, 30, NULL, 30, 45, '核心稳定性', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (5, 1, 3, 3, 4, 6, 50.00, NULL, 90, '背部训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (6, 1, 3, 4, 3, 12, 25.00, NULL, 60, '肩部训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (7, 1, 4, 5, 3, 20, NULL, 30, 45, '腹肌训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (8, 1, 4, 1, 3, 10, 50.00, NULL, 60, '胸部辅助', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (9, 1, 5, 2, 4, 10, 100.00, NULL, 90, '腿部强化', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (10, 1, 5, 6, 2, 30, NULL, 20, 45, '拉伸放松', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (11, 1, 6, 3, 3, 8, 60.00, NULL, 90, '背部强化', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (12, 1, 7, 5, 4, 25, NULL, 45, 45, '腹部终极训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');

-- 初始化系统模板 - 增肌计划
INSERT INTO `gym_training_plan_template` VALUES (2, NULL, '增肌塑形-中级计划', '增肌', '针对有一定训练基础的学员，专注肌肉增长和力量提升', 2, 28, 1, 1, 14, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (13, 2, 1, 1, 4, 8, 80.00, NULL, 90, '胸部主要训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (14, 2, 1, 4, 3, 10, 30.00, NULL, 60, '肩部辅助', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (15, 2, 2, 2, 4, 6, 120.00, NULL, 120, '深蹲训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (16, 2, 2, 6, 3, 40, NULL, 30, 45, '核心训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (17, 2, 3, 3, 4, 6, 60.00, NULL, 120, '背部主要训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (18, 2, 3, 4, 3, 12, 35.00, NULL, 60, '肩部强化', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (19, 2, 4, 1, 4, 10, 70.00, NULL, 90, '胸部辅助训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (20, 2, 4, 5, 3, 15, NULL, 20, 45, '腹肌训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (21, 2, 5, 2, 4, 8, 110.00, NULL, 120, '腿部强化', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (22, 2, 5, 3, 3, 5, 70.00, NULL, 90, '背部辅助', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (23, 2, 6, 1, 3, 6, 60.00, NULL, 90, '胸部修复', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (24, 2, 6, 4, 3, 12, 30.00, NULL, 60, '肩部修复', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (25, 2, 7, 5, 3, 20, NULL, 30, 60, '腹部训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (26, 2, 7, 6, 2, 30, NULL, 20, 45, '拉伸恢复', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');

-- 初始化系统模板 - 康复计划
INSERT INTO `gym_training_plan_template` VALUES (3, NULL, '运动康复-康复计划', '康复', '帮助用户从运动损伤中恢复，强调柔韧性和稳定性', 1, 21, 1, 1, 8, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (27, 3, 1, 6, 3, 45, NULL, 40, 45, '核心稳定性训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (28, 3, 1, 5, 2, 15, NULL, 20, 30, '腹部轻训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (29, 3, 2, 1, 2, 10, 30.00, NULL, 60, '胸部轻训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (30, 3, 3, 3, 2, 6, 20.00, NULL, 60, '背部轻训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (31, 3, 4, 4, 2, 12, 15.00, NULL, 45, '肩部恢复', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (32, 3, 5, 6, 4, 60, NULL, 50, 45, '全身拉伸放松', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (33, 3, 6, 5, 2, 10, NULL, 15, 30, '腹部恢复', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;
