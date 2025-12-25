/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : localhost:3306
 Source Schema         : gym

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 21/12/2025 17:32:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gym_announcement
-- ----------------------------
DROP TABLE IF EXISTS `gym_announcement`;
CREATE TABLE `gym_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_announcement
-- ----------------------------
INSERT INTO `gym_announcement` VALUES (1, '欢迎使用健身房预约系统', '欢迎使用本系统进行课程预约，铂金会员享受9折优惠，黄金会员享匷95折优惠！！', 1, '2025-10-28 13:26:36', '2025-10-28 14:45:39');
INSERT INTO `gym_announcement` VALUES (2, '系统维护通知', '本系统将于本周日凌晨2:00-4:00进行系统维护，届时将无法访问，请提前安排好您的预约计划。', 1, '2025-10-28 13:26:36', '2025-10-28 13:26:36');
INSERT INTO `gym_announcement` VALUES (3, '新课程上线通知', '本月新增瑜伽课程和普拉提课程，欢迎大家预约体验！', 1, '2025-10-28 13:26:36', '2025-10-28 13:26:36');

-- ----------------------------
-- Table structure for gym_body_test
-- ----------------------------
DROP TABLE IF EXISTS `gym_body_test`;
CREATE TABLE `gym_body_test`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '体测ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `bmi` decimal(4, 2) NULL DEFAULT NULL COMMENT 'BMI指数',
  `body_fat` decimal(4, 2) NULL DEFAULT NULL COMMENT '体脂率(%)',
  `muscle_mass` decimal(5, 2) NULL DEFAULT NULL COMMENT '肌肉量(kg)',
  `visceral_fat` int NULL DEFAULT NULL COMMENT '内脏脂肪等级',
  `basal_metabolism` int NULL DEFAULT NULL COMMENT '基础代谢(kcal)',
  `test_time` datetime NULL DEFAULT NULL COMMENT '测试时间',
  `tester_id` bigint NULL DEFAULT NULL COMMENT '测试人员ID(教练)',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_test_time`(`test_time` ASC) USING BTREE,
  INDEX `idx_tester_id`(`tester_id` ASC) USING BTREE,
  CONSTRAINT `gym_body_test_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '体测数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_body_test
-- ----------------------------
INSERT INTO `gym_body_test` VALUES (1, 3, 175.50, 72.00, 23.42, 18.50, 55.00, 5, 1680, '2025-12-10 14:30:00', 1, '初次体测，身体状况良好', '2025-12-10 14:00:00', '2025-12-10 14:30:00');
INSERT INTO `gym_body_test` VALUES (2, 1, 180.00, 78.00, 24.07, 20.00, 60.00, 6, 1750, '2025-12-08 15:00:00', 2, '复测结果，肌肉略有增长', '2025-12-08 14:30:00', '2025-12-08 15:00:00');

-- ----------------------------
-- Table structure for gym_coach
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach`;
CREATE TABLE `gym_coach`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教练ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业领域',
  `certificate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资质证书(JSON)',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `rating` decimal(2, 1) NULL DEFAULT 0.0 COMMENT '平均评分',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-离职，1-在职',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `gym_coach_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach
-- ----------------------------
INSERT INTO `gym_coach` VALUES (1, 4, '瑜伽、普拉提、柔韧性训练', '[{\"name\":\"瑜伽教练证\",\"level\":\"国家级\"},{\"name\":\"普拉提认证\",\"level\":\"高级\"}]', '10年以上瑜伽教学经验，专业的普拉提塑形教练，擅长帮助学员改善体态和增强柔韧性', 4.7, 1, '2025-10-17 09:19:31', '2025-12-20 20:56:39');
INSERT INTO `gym_coach` VALUES (2, 5, '力量训练、健美、运动表现', '[{\"name\":\"健身教练证\",\"level\":\"国家级\"},{\"name\":\"健美运动员\",\"level\":\"高级\"}]', '8年健身房工作经验，拥有健美运动员背景，专业的力量训练指导', 4.6, 1, '2025-10-17 09:19:31', '2025-12-20 20:56:39');
INSERT INTO `gym_coach` VALUES (3, 6, '有氧健身、动感单车、团课', '[{\"name\":\"有氧健身教练证\",\"level\":\"国家级\"},{\"name\":\"动感单车教练\",\"level\":\"高级\"}]', '6年有氧健身教学经验，活力四射的动感单车教练，深受学员欢迎', 4.4, 1, '2025-10-17 09:19:31', '2025-12-20 20:56:39');

-- ----------------------------
-- Table structure for gym_coach_leave
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_leave`;
CREATE TABLE `gym_coach_leave`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `leave_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假类型：ANNUAL(年假),SICK(病假),PERSONAL(事假),MATERNITY(产假),UNPAID(无薪假)',
  `start_date` date NOT NULL COMMENT '请假开始日期',
  `end_date` date NOT NULL COMMENT '请假结束日期',
  `duration_days` int NOT NULL COMMENT '请假天数',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请假原因',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态：0-待审批,1-已批准,2-已拒绝',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '批准人ID',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '批准时间',
  `approve_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '批准意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_leave_type`(`leave_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_date_range`(`start_date` ASC, `end_date` ASC) USING BTREE,
  INDEX `gym_coach_leave_ibfk_2`(`approver_id` ASC) USING BTREE,
  CONSTRAINT `gym_coach_leave_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_coach_leave_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练请假表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_leave
-- ----------------------------
INSERT INTO `gym_coach_leave` VALUES (1, 1, 'SICK', '2025-12-15', '2025-12-16', 2, '感冒发烧，需要休息', 1, 1, '2025-12-12 10:00:00', '已批准，按时复工', '2025-12-12 09:30:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_leave` VALUES (2, 2, 'ANNUAL', '2025-12-20', '2025-12-27', 8, '年度假期休息', 0, NULL, NULL, NULL, '2025-12-12 08:00:00', '2025-12-12 08:00:00');

-- ----------------------------
-- Table structure for gym_coach_review
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_review`;
CREATE TABLE `gym_coach_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '关联训练计划ID',
  `course_booking_id` bigint NULL DEFAULT NULL COMMENT '关联课程预约ID',
  `review_type` tinyint NOT NULL DEFAULT 1 COMMENT '评价类型：1-训练计划评价，2-课程评价',
  `rating` tinyint NOT NULL COMMENT '评分(1-5星)',
  `tag_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价标签(JSON数组)，如[\"专业\",\"耐心\",\"认真负责\"]',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片(JSON数组)',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已删除，1-正常，2-已隐藏',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_course_booking_id`(`course_booking_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `gym_coach_review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_coach_review_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_coach_review_ibfk_3` FOREIGN KEY (`plan_id`) REFERENCES `gym_training_plan` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `gym_coach_review_ibfk_4` FOREIGN KEY (`course_booking_id`) REFERENCES `gym_course_booking` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_review
-- ----------------------------
INSERT INTO `gym_coach_review` VALUES (1, 3, 1, NULL, NULL, 2, 5, '[\"专业\",\"耐心\",\"认真负责\"]', '李教练非常专业，课程讲解很细致，动作示范标准，每次上课都能学到很多东西！', NULL, 0, '感谢您的认可，我会继续努力为大家提供更优质的教学服务！', '2025-12-06 10:30:00', 0, 1, '2025-12-05 18:20:00', '2025-12-06 10:30:00');
INSERT INTO `gym_coach_review` VALUES (2, 3, 2, NULL, NULL, 2, 5, '[\"效果显著\",\"幽默风趣\",\"激励性强\"]', '王教练的力量训练课程效果太棒了！跟着练了一个月，力量提升明显，而且教练很会调动气氛。', NULL, 0, NULL, NULL, 0, 1, '2025-12-07 15:45:00', '2025-12-07 15:45:00');
INSERT INTO `gym_coach_review` VALUES (3, 3, 3, NULL, NULL, 2, 4, '[\"活力四射\",\"音乐选得好\"]', '动感单车课太燃了！教练很有活力，音乐也选得很棒，每次上完课都大汗淋漓，减脂效果很好。', NULL, 0, NULL, NULL, 0, 1, '2025-12-08 20:10:00', '2025-12-08 20:10:00');
INSERT INTO `gym_coach_review` VALUES (4, 9, 1, NULL, NULL, 2, 5, '[\"专业\",\"细致\",\"耐心\"]', '李教练讲解非常细致，每个动作都认真示范，我收获很大！', NULL, 0, '感谢认可，会继续加油！', '2025-12-12 14:00:00', 0, 1, '2025-12-11 10:00:00', '2025-12-12 14:00:00');
INSERT INTO `gym_coach_review` VALUES (5, 11, 1, NULL, NULL, 2, 5, '[\"专业\",\"热情\",\"认真负责\"]', '在李教练的指导下，我的柔韧性提升明显，身体舒展多了！', NULL, 0, NULL, NULL, 0, 1, '2025-12-10 15:30:00', '2025-12-10 15:30:00');
INSERT INTO `gym_coach_review` VALUES (6, 13, 1, NULL, NULL, 2, 4, '[\"耐心\",\"鼓励\",\"支持\"]', '作为新手，李教练给了我很多帮助和鼓励，让我有信心继续练下去！', NULL, 0, '谢谢信任，新手坚持就好！', '2025-12-12 16:00:00', 0, 1, '2025-12-11 11:00:00', '2025-12-12 16:00:00');
INSERT INTO `gym_coach_review` VALUES (7, 15, 1, NULL, NULL, 2, 5, '[\"专业\",\"挑战性强\",\"效果显著\"]', '李教练安排的高难度动作很有挑战，完成后很有成就感！', NULL, 0, NULL, NULL, 0, 1, '2025-12-09 18:00:00', '2025-12-09 18:00:00');
INSERT INTO `gym_coach_review` VALUES (8, 17, 1, NULL, NULL, 2, 5, '[\"专业\",\"热情\",\"用心\"]', '坚持跟李教练练习，身体变化真的很明显，太满意了！', NULL, 0, '继续保持，效果会更好！', '2025-12-08 14:00:00', 0, 1, '2025-12-07 10:00:00', '2025-12-08 14:00:00');
INSERT INTO `gym_coach_review` VALUES (9, 19, 1, NULL, NULL, 2, 5, '[\"专业\",\"个性化\",\"效果好\"]', '作为铂金会员，李教练给我制定的一对一计划效果非常显著！', NULL, 0, NULL, NULL, 0, 1, '2025-12-11 17:00:00', '2025-12-11 17:00:00');
INSERT INTO `gym_coach_review` VALUES (10, 21, 1, NULL, NULL, 2, 4, '[\"热情\",\"耐心\",\"支持\"]', '新加入就得到李教练的专业指导，感觉很值！', NULL, 0, '欢迎加入，一起努力！', '2025-12-12 10:30:00', 0, 1, '2025-12-12 09:00:00', '2025-12-12 10:30:00');
INSERT INTO `gym_coach_review` VALUES (11, 23, 1, NULL, NULL, 2, 5, '[\"专业\",\"信任\",\"长期合作\"]', '多年跟着李教练，每次都有新的进步，非常信任！', NULL, 0, NULL, NULL, 0, 1, '2025-12-06 16:00:00', '2025-12-06 16:00:00');
INSERT INTO `gym_coach_review` VALUES (12, 25, 1, NULL, NULL, 2, 4, '[\"耐心\",\"细致\",\"鼓励\"]', '李教练从零开始教我，现在我已经初见成效了！', NULL, 0, '坚持就是胜利！', '2025-12-10 18:00:00', 0, 1, '2025-12-09 14:00:00', '2025-12-10 18:00:00');
INSERT INTO `gym_coach_review` VALUES (13, 27, 1, NULL, NULL, 2, 5, '[\"专业\",\"高强度\",\"效果显著\"]', '铂金会员参加李教练的高强度课程，效果真的很好！', NULL, 0, 'ok', '2025-12-21 08:29:51', 0, 1, '2025-12-12 12:00:00', '2025-12-12 12:00:00');
INSERT INTO `gym_coach_review` VALUES (14, 10, 2, NULL, NULL, 2, 5, '[\"专业\",\"有天赋\",\"进度快\"]', '王教练的力量训练课程很系统，我的进度超出预期！', NULL, 0, '继续加油，潜力很大！', '2025-12-12 15:00:00', 0, 1, '2025-12-11 14:00:00', '2025-12-12 15:00:00');
INSERT INTO `gym_coach_review` VALUES (15, 12, 2, NULL, NULL, 2, 5, '[\"耐心\",\"细致\",\"有效果\"]', '在王教练的坚持指导下，力量水平提升了很多！', NULL, 0, NULL, NULL, 0, 1, '2025-12-10 19:00:00', '2025-12-10 19:00:00');
INSERT INTO `gym_coach_review` VALUES (16, 14, 2, NULL, NULL, 2, 4, '[\"专业\",\"高效\",\"激励\"]', '王教练的课程安排很科学，效果明显，强烈推荐！', NULL, 0, '谢谢支持，会继续优化！', '2025-12-11 16:30:00', 0, 1, '2025-12-11 15:00:00', '2025-12-11 16:30:00');
INSERT INTO `gym_coach_review` VALUES (17, 16, 2, NULL, NULL, 2, 5, '[\"热心\",\"耐心\",\"进度快\"]', '新手跟王教练学习，进度特别快，感觉学到了很多！', NULL, 0, NULL, NULL, 0, 1, '2025-12-09 17:00:00', '2025-12-09 17:00:00');
INSERT INTO `gym_coach_review` VALUES (18, 18, 2, NULL, NULL, 2, 4, '[\"专业\",\"定期\",\"稳定效果\"]', '作为黄金会员，王教练的定期指导让我进度很稳定！', NULL, 0, '坚持就会有好结果！', '2025-12-12 11:00:00', 0, 1, '2025-12-11 13:00:00', '2025-12-12 11:00:00');
INSERT INTO `gym_coach_review` VALUES (19, 20, 2, NULL, NULL, 2, 5, '[\"专业\",\"小组课\",\"氛围好\"]', '参加王教练的小组课程，大家互相鼓励，效果很棒！', NULL, 0, NULL, NULL, 0, 1, '2025-12-08 19:30:00', '2025-12-08 19:30:00');
INSERT INTO `gym_coach_review` VALUES (20, 22, 2, NULL, NULL, 2, 4, '[\"经验丰富\",\"调整计划\",\"专业\"]', '王教练很专业，帮我突破力量瓶颈，调整了训练计划！', NULL, 0, '继续突破，更高目标！', '2025-12-12 13:30:00', 0, 1, '2025-12-12 10:00:00', '2025-12-12 13:30:00');
INSERT INTO `gym_coach_review` VALUES (21, 24, 2, NULL, NULL, 2, 5, '[\"专业\",\"用心\",\"效果显著\"]', '坚持跟王教练练习，肌肉线条改善明显，非常满意！', NULL, 0, NULL, NULL, 0, 1, '2025-12-07 18:00:00', '2025-12-07 18:00:00');
INSERT INTO `gym_coach_review` VALUES (22, 26, 2, NULL, NULL, 2, 5, '[\"专业\",\"自律\",\"成效显著\"]', '高级会员也推荐王教练，他的专业度和耐心真的一流！', NULL, 0, '感谢信任，继续努力！', '2025-12-06 17:00:00', 0, 1, '2025-12-05 15:00:00', '2025-12-06 17:00:00');
INSERT INTO `gym_coach_review` VALUES (23, 28, 2, NULL, NULL, 2, 4, '[\"热情\",\"个性化\",\"支持\"]', '新加入就享受王教练的个性化训练计划，感觉很值得！', NULL, 0, NULL, NULL, 0, 1, '2025-12-12 14:30:00', '2025-12-12 14:30:00');
INSERT INTO `gym_coach_review` VALUES (24, 30, 3, NULL, NULL, 2, 5, '[\"活力四射\",\"音乐好\",\"燃脂\"]', '张教练的动感单车课程太棒了，节奏感十足，燃脂效果很好！', NULL, 0, '继续坚持，效果会更好！', '2025-12-12 16:30:00', 0, 1, '2025-12-11 18:00:00', '2025-12-12 16:30:00');
INSERT INTO `gym_coach_review` VALUES (25, 31, 3, NULL, NULL, 2, 5, '[\"热情\",\"有氧\",\"团课氛围\"]', '铂金会员参加张教练的有氧团课，大家一起运动氛围特别好！', NULL, 0, NULL, NULL, 0, 1, '2025-12-10 20:00:00', '2025-12-10 20:00:00');
INSERT INTO `gym_coach_review` VALUES (26, 32, 3, NULL, NULL, 2, 4, '[\"基础培训\",\"耐心\",\"专业\"]', '新手在张教练的基础培训下，已经掌握了正确的运动方法！', NULL, 0, '好好学，坚持就有成果！', '2025-12-12 17:00:00', 0, 1, '2025-12-11 15:00:00', '2025-12-12 17:00:00');
INSERT INTO `gym_coach_review` VALUES (27, 33, 3, NULL, NULL, 2, 5, '[\"热心\",\"鼓励\",\"升级计划\"]', '张教练激励我升级黄金会员，相信自己的潜力很重要！', NULL, 0, NULL, NULL, 0, 1, '2025-12-09 19:00:00', '2025-12-09 19:00:00');
INSERT INTO `gym_coach_review` VALUES (28, 34, 3, NULL, NULL, 2, 4, '[\"定期参加\",\"专业\",\"有效\"]', '作为黄金会员，定期参加张教练的有氧课程，效果很稳定！', NULL, 0, '坚持参加，身体会更好！', '2025-12-11 17:30:00', 0, 1, '2025-12-10 14:00:00', '2025-12-11 17:30:00');
INSERT INTO `gym_coach_review` VALUES (29, 35, 3, NULL, NULL, 2, 5, '[\"高强度\",\"间歇训练\",\"挑战\"]', '参加张教练的高强度间歇训练课程，很有挑战，也很有收获！', NULL, 0, NULL, NULL, 0, 1, '2025-12-08 20:30:00', '2025-12-08 20:30:00');
INSERT INTO `gym_coach_review` VALUES (30, 36, 3, NULL, NULL, 2, 5, '[\"稳定\",\"进度好\",\"坚持\"]', '中级学员的进度稳定，张教练的课程设置很科学！', NULL, 0, '继续坚持，目标一定能达到！', '2025-12-12 15:30:00', 0, 1, '2025-12-11 16:00:00', '2025-12-12 15:30:00');
INSERT INTO `gym_coach_review` VALUES (31, 37, 3, NULL, NULL, 2, 4, '[\"热情\",\"鼓励\",\"进步快\"]', '新加入的学员在张教练的鼓励下，进度快得惊人！', NULL, 0, NULL, NULL, 0, 1, '2025-12-12 18:00:00', '2025-12-12 18:00:00');
INSERT INTO `gym_coach_review` VALUES (32, 38, 3, NULL, NULL, 2, 5, '[\"出色表现\",\"单车课\",\"优秀\"]', '黄金会员在张教练的动感单车课上表现出色，很励志！', NULL, 0, '表现很好，继续加油！', '2025-12-07 19:00:00', 0, 1, '2025-12-06 17:00:00', '2025-12-07 19:00:00');
INSERT INTO `gym_coach_review` VALUES (33, 29, 3, NULL, NULL, 2, 4, '[\"新手\",\"多节课\",\"进步\"]', '新手参加了多节张教练的课程，每次都有新收获！', NULL, 0, NULL, NULL, 0, 1, '2025-12-09 20:00:00', '2025-12-09 20:00:00');
INSERT INTO `gym_coach_review` VALUES (35, 39, 3, NULL, NULL, 2, 3, '[\"专业\",\"音乐选得好\",\"认真负责\"]', '好好好', NULL, 0, NULL, NULL, 0, 1, '2025-12-20 21:00:30', '2025-12-20 21:00:30');

-- ----------------------------
-- Table structure for gym_coach_review_statistics
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_review_statistics`;
CREATE TABLE `gym_coach_review_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `total_reviews` int NULL DEFAULT 0 COMMENT '总评价数',
  `average_rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '平均评分',
  `rating_5_count` int NULL DEFAULT 0 COMMENT '5星评价数',
  `rating_4_count` int NULL DEFAULT 0 COMMENT '4星评价数',
  `rating_3_count` int NULL DEFAULT 0 COMMENT '3星评价数',
  `rating_2_count` int NULL DEFAULT 0 COMMENT '2星评价数',
  `rating_1_count` int NULL DEFAULT 0 COMMENT '1星评价数',
  `reply_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '回复率(%)',
  `last_review_time` datetime NULL DEFAULT NULL COMMENT '最后评价时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `gym_coach_review_statistics_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练评价统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_review_statistics
-- ----------------------------
INSERT INTO `gym_coach_review_statistics` VALUES (1, 1, 11, 4.73, 8, 3, 0, 0, 0, 64.00, '2025-12-12 12:00:00', '2025-12-20 20:56:39');
INSERT INTO `gym_coach_review_statistics` VALUES (2, 2, 11, 4.64, 7, 4, 0, 0, 0, 45.45, '2025-12-12 14:30:00', '2025-12-20 20:56:39');
INSERT INTO `gym_coach_review_statistics` VALUES (3, 3, 12, 4.42, 6, 5, 1, 0, 0, 42.00, '2025-12-20 21:00:30', '2025-12-20 20:56:39');

-- ----------------------------
-- Table structure for gym_coach_schedule
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_schedule`;
CREATE TABLE `gym_coach_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `work_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'NORMAL' COMMENT '工作类型：NORMAL-正常排班,OVERTIME-加班,SHIFT-轮班,HOLIDAY-休息',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作地点',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-已取消,1-正常,2-已完成',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者ID(管理员)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_coach_date_time`(`coach_id` ASC, `work_date` ASC, `start_time` ASC, `end_time` ASC) USING BTREE COMMENT '同一教练同一天同一时间段唯一',
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_work_date`(`work_date` ASC) USING BTREE,
  INDEX `idx_work_type`(`work_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_coach_date`(`coach_id` ASC, `work_date` ASC) USING BTREE,
  CONSTRAINT `gym_coach_schedule_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_schedule
-- ----------------------------
INSERT INTO `gym_coach_schedule` VALUES (1, 1, '2025-12-13', '09:00:00', '12:00:00', 'NORMAL', '健身房一楼', 1, '周六上午瑜伽课', 1, '2025-12-12 16:00:00', '2025-12-12 16:00:00');
INSERT INTO `gym_coach_schedule` VALUES (2, 1, '2025-12-13', '14:00:00', '17:00:00', 'NORMAL', '健身房一楼', 1, '周六下午普拉提课', 1, '2025-12-12 16:00:00', '2025-12-12 16:00:00');
INSERT INTO `gym_coach_schedule` VALUES (3, 2, '2025-12-14', '08:00:00', '11:00:00', 'NORMAL', '健身房二楼力量区', 1, '周日上午力量训练课', 1, '2025-12-12 16:00:00', '2025-12-12 16:00:00');
INSERT INTO `gym_coach_schedule` VALUES (4, 2, '2025-12-14', '19:00:00', '21:00:00', 'NORMAL', '健身房二楼力量区', 1, '周日晚上力量训练课', 1, '2025-12-12 16:00:00', '2025-12-12 16:00:00');
INSERT INTO `gym_coach_schedule` VALUES (5, 3, '2025-12-13', '18:00:00', '19:30:00', 'NORMAL', '健身房二楼单车区', 1, '周六晚上动感单车课', 1, '2025-12-12 16:00:00', '2025-12-12 16:00:00');

-- ----------------------------
-- Table structure for gym_coach_student
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_student`;
CREATE TABLE `gym_coach_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员用户ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_coach_student`(`coach_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练学员关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_student
-- ----------------------------
INSERT INTO `gym_coach_student` VALUES (1, 1, 3, '学员基础薄弱，需要耐心指导，已安排个人训练计划', '2025-12-10 14:00:00', '2025-12-10 14:00:00');
INSERT INTO `gym_coach_student` VALUES (2, 1, 7, '学员进度快，可以加大训练强度', '2025-12-11 10:00:00', '2025-12-11 10:00:00');
INSERT INTO `gym_coach_student` VALUES (3, 2, 1, '会员很活跃，坚持训练，效果显著', '2025-12-09 09:00:00', '2025-12-09 09:00:00');
INSERT INTO `gym_coach_student` VALUES (4, 1, 9, '李欣怡：学员态度认真，坚持参加课程，进度明显', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (5, 1, 11, '张梦琪：柔韧性训练效果显著，已达到预期目标', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (6, 1, 13, '陈思彤：新手学员，需要更多基础指导和鼓励', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (7, 1, 15, '黄佳琪：高级学员，可以尝试更难的动作组合', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (8, 1, 17, '周欣悦：坚持到底，体态改善明显，已续费年卡', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (9, 1, 19, '孙思琦：铂金会员，接受一对一指导效果很好', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (10, 1, 21, '郑欣妍：新加入会员，目标明确，需要制定专项计划', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (11, 1, 23, '高小琳：长期会员，信任度高，配合度良好', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (12, 1, 25, '邱思玥：初级学员，从零开始，进度稳定', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (13, 1, 27, '傅思诺：铂金会员，接受高强度训练，效果显著', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (14, 2, 10, '王晓婷：力量训练有天赋，进度超预期', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (15, 2, 12, '刘晓芳：坚持训练，力量水平稳步提升', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (16, 2, 14, '杨娜娜：训练积极性高，已达到中级水平', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (17, 2, 16, '吴梦雨：新手学员，学习能力强，进度快', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (18, 2, 18, '徐晓美：黄金会员，定期接受指导，效果稳定', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (19, 2, 20, '何佳怡：铂金会员，参加小组课程，表现突出', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (20, 2, 22, '罗思语：中级学员，力量突破瓶颈，需要调整计划', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (21, 2, 24, '林晓晓：坚持训练，肌肉线条明显改善', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (22, 2, 26, '曾梦雪：高级会员，自律性强，成效显著', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (23, 2, 28, '蒋欣妤：新加入学员，接受个性化训练计划', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (24, 3, 30, '韦思琪：动感单车课程表现优秀，坚持参加', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (25, 3, 31, '唐晓敏：铂金会员，参加有氧团课，热情高涨', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (26, 3, 32, '苏思欣：新手，已完成基础有氧课程培训', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (27, 3, 33, '许欣怡：初级学员，准备升级黄金会员', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (28, 3, 34, '冯思琳：黄金会员，定期参加有氧课程', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (29, 3, 35, '卢梦琪：铂金会员，参加高强度间歇训练课程', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (30, 3, 36, '叶晓雪：中级学员，进度稳定，坚持参加课程', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (31, 3, 37, '樊思语：新加入学员，热情高，进步快', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (32, 3, 38, '葛欣琳：黄金会员，动感单车表现出色', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_coach_student` VALUES (33, 3, 29, '谢晓嘉：新手，已参加多节课程，持续进步', '2025-12-12 10:00:00', '2025-12-12 10:00:00');

-- ----------------------------
-- Table structure for gym_course
-- ----------------------------
DROP TABLE IF EXISTS `gym_course`;
CREATE TABLE `gym_course`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程ID（UUID）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `category_id` bigint NULL DEFAULT NULL COMMENT '课程分类ID',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '教练ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片',
  `duration` int NOT NULL COMMENT '课程时长（分钟）',
  `max_participants` int NOT NULL COMMENT '最大参与人数',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '课程价格',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_status`(`status` ASC) USING BTREE,
  INDEX `idx_course_name`(`name` ASC) USING BTREE,
  INDEX `idx_course_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_course_coach_id`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course
-- ----------------------------
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440001', '瑜伽基础课', 1, 1, '适合初学者的瑜伽课程，帮助放松身心，提高柔韧性', '/files/bussiness/course/1766219574497.jpg', 60, 20, 68.00, 1, '2025-12-17 09:19:31', '2025-12-20 16:32:56');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440002', '高强度间歇训练(HIIT)', 3, 2, '高效燃脂训练，提高心肺功能和肌肉力量', '/files/bussiness/course/1766219564065.png', 45, 15, 88.00, 1, '2025-12-18 09:19:31', '2025-12-20 16:32:45');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440003', '普拉提塑形', 4, 1, '核心力量训练，改善体态，塑造完美身形', '/files/bussiness/course/1766219551439.jpg', 50, 12, 78.00, 1, '2025-12-19 09:19:31', '2025-12-20 16:32:32');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440004', '动感单车', 5, 3, '音乐节拍配合，燃烧卡路里，释放压力', '/files/bussiness/course/1766219588034.png', 45, 25, 58.00, 1, '2025-12-10 09:19:31', '2025-12-20 16:33:09');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440005', '力量训练基础', 2, 2, '器械使用指导，安全有效的力量训练', '/files/bussiness/course/1766276361852.jpg', 60, 10, 98.00, 1, '2025-12-30 09:19:31', '2025-12-21 08:19:24');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440006', '拉伸放松课', 6, 3, '运动后恢复，肌肉放松，预防运动损伤', '/files/bussiness/course/1766219539658.jpg', 30, 15, 38.00, 1, '2025-12-22 09:19:31', '2025-12-20 16:32:21');

-- ----------------------------
-- Table structure for gym_course_booking
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_booking`;
CREATE TABLE `gym_course_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `schedule_id` bigint NOT NULL COMMENT '排课ID',
  `booking_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `original_price` decimal(10, 2) NOT NULL COMMENT '原价',
  `actual_price` decimal(10, 2) NOT NULL COMMENT '实际支付价格',
  `discount_rate` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '折扣率',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已取消，1-已预约，2-已完成',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_booking_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_booking_schedule_id`(`schedule_id` ASC) USING BTREE,
  INDEX `idx_booking_status`(`status` ASC) USING BTREE,
  INDEX `idx_booking_time`(`booking_time` ASC) USING BTREE,
  CONSTRAINT `gym_course_booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_course_booking_ibfk_2` FOREIGN KEY (`schedule_id`) REFERENCES `gym_course_schedule` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_booking
-- ----------------------------
INSERT INTO `gym_course_booking` VALUES (1, 3, 25, '2025-12-12 10:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-12 09:55:00', '2025-12-12 09:55:00');
INSERT INTO `gym_course_booking` VALUES (2, 1, 26, '2025-12-12 11:30:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-12 11:25:00', '2025-12-12 11:25:00');
INSERT INTO `gym_course_booking` VALUES (3, 7, 27, '2025-12-12 12:00:00', 68.00, 68.00, 1.00, 1, NULL, '2025-12-12 11:55:00', '2025-12-12 11:55:00');
INSERT INTO `gym_course_booking` VALUES (4, 9, 28, '2025-12-13 10:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-13 09:30:00', '2025-12-13 09:30:00');
INSERT INTO `gym_course_booking` VALUES (5, 11, 29, '2025-12-13 14:00:00', 68.00, 54.40, 0.80, 1, NULL, '2025-12-13 13:30:00', '2025-12-13 13:30:00');
INSERT INTO `gym_course_booking` VALUES (6, 13, 30, '2025-12-14 10:00:00', 68.00, 68.00, 1.00, 1, NULL, '2025-12-14 09:45:00', '2025-12-14 09:45:00');
INSERT INTO `gym_course_booking` VALUES (7, 15, 31, '2025-12-14 15:00:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-14 14:30:00', '2025-12-14 14:30:00');
INSERT INTO `gym_course_booking` VALUES (8, 17, 32, '2025-12-15 10:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-15 09:50:00', '2025-12-15 09:50:00');
INSERT INTO `gym_course_booking` VALUES (9, 19, 33, '2025-12-15 14:00:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-15 13:45:00', '2025-12-15 13:45:00');
INSERT INTO `gym_course_booking` VALUES (10, 21, 34, '2025-12-16 10:00:00', 68.00, 68.00, 1.00, 1, NULL, '2025-12-16 09:55:00', '2025-12-16 09:55:00');
INSERT INTO `gym_course_booking` VALUES (11, 23, 35, '2025-12-16 15:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-16 14:40:00', '2025-12-16 14:40:00');
INSERT INTO `gym_course_booking` VALUES (12, 25, 36, '2025-12-17 10:00:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-17 09:40:00', '2025-12-17 09:40:00');
INSERT INTO `gym_course_booking` VALUES (13, 27, 37, '2025-12-17 14:00:00', 68.00, 68.00, 1.00, 1, NULL, '2025-12-17 13:50:00', '2025-12-17 13:50:00');
INSERT INTO `gym_course_booking` VALUES (14, 10, 28, '2025-12-13 11:00:00', 98.00, 98.00, 1.00, 1, NULL, '2025-12-13 10:45:00', '2025-12-13 10:45:00');
INSERT INTO `gym_course_booking` VALUES (15, 12, 29, '2025-12-13 16:00:00', 98.00, 88.20, 0.90, 1, NULL, '2025-12-13 15:30:00', '2025-12-13 15:30:00');
INSERT INTO `gym_course_booking` VALUES (16, 14, 30, '2025-12-14 11:00:00', 98.00, 93.10, 0.95, 1, NULL, '2025-12-14 10:50:00', '2025-12-14 10:50:00');
INSERT INTO `gym_course_booking` VALUES (17, 16, 31, '2025-12-14 16:00:00', 98.00, 88.20, 0.90, 1, NULL, '2025-12-14 15:40:00', '2025-12-14 15:40:00');
INSERT INTO `gym_course_booking` VALUES (18, 18, 32, '2025-12-15 11:00:00', 98.00, 98.00, 1.00, 1, NULL, '2025-12-15 10:40:00', '2025-12-15 10:40:00');
INSERT INTO `gym_course_booking` VALUES (19, 20, 33, '2025-12-15 16:00:00', 98.00, 88.20, 0.90, 1, NULL, '2025-12-15 15:35:00', '2025-12-15 15:35:00');
INSERT INTO `gym_course_booking` VALUES (20, 22, 34, '2025-12-16 11:00:00', 98.00, 93.10, 0.95, 1, NULL, '2025-12-16 10:45:00', '2025-12-16 10:45:00');
INSERT INTO `gym_course_booking` VALUES (21, 24, 35, '2025-12-16 16:00:00', 98.00, 88.20, 0.90, 1, NULL, '2025-12-16 15:50:00', '2025-12-16 15:50:00');
INSERT INTO `gym_course_booking` VALUES (22, 26, 36, '2025-12-17 11:00:00', 98.00, 98.00, 1.00, 1, NULL, '2025-12-17 10:35:00', '2025-12-17 10:35:00');
INSERT INTO `gym_course_booking` VALUES (23, 28, 37, '2025-12-17 16:00:00', 98.00, 88.20, 0.90, 1, NULL, '2025-12-17 15:45:00', '2025-12-17 15:45:00');
INSERT INTO `gym_course_booking` VALUES (24, 30, 38, '2025-12-13 18:30:00', 58.00, 58.00, 1.00, 1, NULL, '2025-12-13 18:00:00', '2025-12-13 18:00:00');
INSERT INTO `gym_course_booking` VALUES (25, 31, 39, '2025-12-14 18:30:00', 58.00, 52.20, 0.90, 1, NULL, '2025-12-14 18:15:00', '2025-12-14 18:15:00');
INSERT INTO `gym_course_booking` VALUES (26, 32, 25, '2025-12-13 09:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-13 08:45:00', '2025-12-13 08:45:00');
INSERT INTO `gym_course_booking` VALUES (27, 33, 26, '2025-12-14 09:00:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-14 08:50:00', '2025-12-14 08:50:00');
INSERT INTO `gym_course_booking` VALUES (28, 34, 27, '2025-12-15 09:00:00', 68.00, 68.00, 1.00, 1, NULL, '2025-12-15 08:40:00', '2025-12-15 08:40:00');
INSERT INTO `gym_course_booking` VALUES (29, 35, 38, '2025-12-15 18:30:00', 58.00, 58.00, 1.00, 1, NULL, '2025-12-15 18:20:00', '2025-12-15 18:20:00');
INSERT INTO `gym_course_booking` VALUES (30, 36, 39, '2025-12-16 18:30:00', 58.00, 52.20, 0.90, 1, NULL, '2025-12-16 18:10:00', '2025-12-16 18:10:00');
INSERT INTO `gym_course_booking` VALUES (31, 37, 25, '2025-12-16 09:00:00', 68.00, 64.60, 0.95, 1, NULL, '2025-12-16 08:45:00', '2025-12-16 08:45:00');
INSERT INTO `gym_course_booking` VALUES (32, 38, 26, '2025-12-17 09:00:00', 68.00, 61.20, 0.90, 1, NULL, '2025-12-17 08:50:00', '2025-12-17 08:50:00');
INSERT INTO `gym_course_booking` VALUES (33, 29, 27, '2025-12-17 18:30:00', 58.00, 58.00, 1.00, 1, NULL, '2025-12-17 18:15:00', '2025-12-17 18:15:00');
INSERT INTO `gym_course_booking` VALUES (34, 39, 40, '2025-12-20 16:38:42', 98.00, 98.00, 1.00, 1, NULL, '2025-12-20 16:38:42', '2025-12-20 16:38:42');

-- ----------------------------
-- Table structure for gym_course_category
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_category`;
CREATE TABLE `gym_course_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_category
-- ----------------------------
INSERT INTO `gym_course_category` VALUES (1, '瑜伽课程', '各类瑜伽课程', '/files/bussiness/course/1766219574497.jpg', 1, 1, '2025-12-05 14:00:00', '2025-12-21 15:15:54');
INSERT INTO `gym_course_category` VALUES (2, '力量训练', '器械和力量训练课程', '/files/bussiness/course/1766219574497.jpg', 2, 1, '2025-12-05 14:00:00', '2025-12-21 15:15:56');
INSERT INTO `gym_course_category` VALUES (3, '有氧运动', '有氧健身课程', '/files/bussiness/course/1766219574497.jpg', 3, 1, '2025-12-05 14:00:00', '2025-12-21 15:15:57');
INSERT INTO `gym_course_category` VALUES (4, '普拉提', '普拉提塑形课程', '/files/bussiness/course/1766219574497.jpg', 4, 1, '2025-12-05 14:00:00', '2025-12-21 15:15:59');
INSERT INTO `gym_course_category` VALUES (5, '动感单车', '动感单车课程', '/files/bussiness/course/1766219574497.jpg', 5, 1, '2025-12-05 14:00:00', '2025-12-21 15:16:00');
INSERT INTO `gym_course_category` VALUES (6, '拉伸放松', '拉伸恢复课程', '/files/bussiness/course/1766219574497.jpg', 6, 1, '2025-12-05 14:00:00', '2025-12-21 15:22:24');

-- ----------------------------
-- Table structure for gym_course_schedule
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_schedule`;
CREATE TABLE `gym_course_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排课ID',
  `course_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程ID（UUID）',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_participants` int NOT NULL COMMENT '最大参与人数',
  `current_participants` int NULL DEFAULT 0 COMMENT '当前参与人数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-取消，1-正常，2-已满',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_schedule_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_schedule_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_schedule_status`(`status` ASC) USING BTREE,
  CONSTRAINT `gym_course_schedule_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `gym_course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程时间安排表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_schedule
-- ----------------------------
INSERT INTO `gym_course_schedule` VALUES (25, '550e8400-e29b-41d4-a716-446655440001', '2025-10-28 15:00:00', '2025-10-28 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (26, '550e8400-e29b-41d4-a716-446655440001', '2025-10-29 15:00:00', '2025-10-29 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (27, '550e8400-e29b-41d4-a716-446655440001', '2025-10-30 15:00:00', '2025-10-30 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (28, '550e8400-e29b-41d4-a716-446655440001', '2025-11-04 15:00:00', '2025-11-04 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (29, '550e8400-e29b-41d4-a716-446655440001', '2025-11-05 15:00:00', '2025-11-05 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (30, '550e8400-e29b-41d4-a716-446655440001', '2025-11-06 15:00:00', '2025-11-06 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (31, '550e8400-e29b-41d4-a716-446655440001', '2025-11-11 15:00:00', '2025-11-11 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (32, '550e8400-e29b-41d4-a716-446655440001', '2025-11-12 15:00:00', '2025-11-12 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (33, '550e8400-e29b-41d4-a716-446655440001', '2025-11-13 15:00:00', '2025-11-13 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (34, '550e8400-e29b-41d4-a716-446655440001', '2025-11-18 15:00:00', '2025-11-18 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (35, '550e8400-e29b-41d4-a716-446655440001', '2025-11-19 15:00:00', '2025-11-19 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (36, '550e8400-e29b-41d4-a716-446655440001', '2025-11-20 15:00:00', '2025-11-20 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (37, '550e8400-e29b-41d4-a716-446655440001', '2025-11-25 15:00:00', '2025-11-25 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (38, '550e8400-e29b-41d4-a716-446655440001', '2025-11-26 15:00:00', '2025-11-26 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (39, '550e8400-e29b-41d4-a716-446655440001', '2025-11-27 15:00:00', '2025-11-27 18:00:00', 20, 0, 1, '2025-10-28 11:57:56', '2025-10-28 11:57:56');
INSERT INTO `gym_course_schedule` VALUES (40, '550e8400-e29b-41d4-a716-446655440005', '2025-12-22 18:46:35', '2025-12-22 21:46:42', 10, 1, 1, '2025-12-19 18:46:52', '2025-12-19 18:46:52');

-- ----------------------------
-- Table structure for gym_course_sign_in
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_sign_in`;
CREATE TABLE `gym_course_sign_in`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `booking_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `schedule_id` bigint NOT NULL COMMENT '排课ID',
  `sign_in_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `sign_in_type` tinyint NULL DEFAULT 1 COMMENT '签到方式：1-扫码，2-手动',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID(教练/管理员)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_booking_id`(`booking_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_schedule_id`(`schedule_id` ASC) USING BTREE,
  INDEX `idx_sign_in_time`(`sign_in_time` ASC) USING BTREE,
  CONSTRAINT `gym_course_sign_in_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `gym_course_booking` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_course_sign_in_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_course_sign_in_ibfk_3` FOREIGN KEY (`schedule_id`) REFERENCES `gym_course_schedule` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程签到表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_sign_in
-- ----------------------------

-- ----------------------------
-- Table structure for gym_equipment
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment`;
CREATE TABLE `gym_equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '器材名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '器材编号',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '器材类型(有氧/力量/自由重量)',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '品牌',
  `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '型号',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置',
  `purchase_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `warranty_expire` date NULL DEFAULT NULL COMMENT '保修到期日',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态:0-故障,1-正常,2-维护中,3-报废',
  `current_user_id` bigint NULL DEFAULT NULL COMMENT '当前使用者ID',
  `usage_start_time` datetime NULL DEFAULT NULL COMMENT '使用开始时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_current_user`(`current_user_id` ASC) USING BTREE,
  CONSTRAINT `gym_equipment_ibfk_1` FOREIGN KEY (`current_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment
-- ----------------------------
INSERT INTO `gym_equipment` VALUES (1, '跑步机A1', 'TM-001', '有氧', 'Life Fitness', 'Integrity', '一楼有氧区', '2024-01-15', '2026-01-15', 1, NULL, NULL, '高端跑步机，电动斜坡功能', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (2, '跑步机A2', 'TM-002', '有氧', 'Life Fitness', 'Integrity', '一楼有氧区', '2024-01-15', '2026-01-15', 1, NULL, NULL, '高端跑步机，电动斜坡功能', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (3, '动感单车B1', 'SC-001', '有氧', 'Peloton', 'Digital', '二楼单车区', '2024-02-20', '2026-02-20', 1, NULL, NULL, '智能动感单车', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (4, '杠铃架C1', 'RK-001', '力量', 'Rogue', 'Monolift', '三楼力量区', '2024-03-10', '2027-03-10', 1, NULL, NULL, '专业杠铃架，可调节高度', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (5, '哑铃组D1', 'DB-001', '自由重量', 'Bowflex', 'SelectTech', '三楼力量区', '2024-04-05', '2026-04-05', 1, NULL, NULL, '可调式哑铃，10-90磅', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (6, '腿部推蹬机E1', 'LP-001', '力量', 'Hammer Strength', 'Plate Loaded', '二楼力量区', '2024-05-12', '2026-05-12', 1, NULL, NULL, '腿部训练专用器械', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (7, '跑步机A3', 'TM-003', '有氧', 'Life Fitness', 'Integrity', '一楼有氧区', '2024-06-18', '2026-06-18', 0, NULL, NULL, '故障待修，显示屏不亮', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (8, '椭圆机F1', 'EM-001', '有氧', 'Precor', 'Elliptical', '一楼有氧区', '2024-07-22', '2026-07-22', 1, NULL, NULL, '低冲击有氧运动机器', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (9, '卧推架G1', 'BP-001', '力量', 'Rogue', 'Flat Bench', '二楼力量区', '2024-08-30', '2027-08-30', 1, NULL, NULL, '可调节卧推凳', '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `gym_equipment` VALUES (10, '龙门架H1', 'CS-001', '力量', 'Ironmaster', 'Multifunctional', '三楼力量区', '2024-09-14', '2027-09-14', 1, NULL, NULL, '多功能训练架', '2025-10-17 09:19:31', '2025-12-05 15:31:40');

-- ----------------------------
-- Table structure for gym_equipment_booking
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment_booking`;
CREATE TABLE `gym_equipment_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `equipment_id` bigint NOT NULL COMMENT '器材ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `actual_start_time` datetime NULL DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime NULL DEFAULT NULL COMMENT '实际结束时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态:0-已取消,1-预约中,2-使用中,3-已完成,4-超时未使用',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `gym_equipment_booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_equipment_booking_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `gym_equipment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_booking
-- ----------------------------
INSERT INTO `gym_equipment_booking` VALUES (1, 3, 1, '2025-12-13 09:00:00', '2025-12-13 10:00:00', '2025-12-13 09:05:00', '2025-12-13 10:05:00', 3, NULL, NULL, '2025-12-12 20:00:00', '2025-12-12 20:00:00');
INSERT INTO `gym_equipment_booking` VALUES (2, 1, 3, '2025-12-14 15:00:00', '2025-12-14 15:45:00', NULL, NULL, 1, NULL, NULL, '2025-12-12 18:30:00', '2025-12-12 18:30:00');
INSERT INTO `gym_equipment_booking` VALUES (3, 7, 2, '2025-12-13 17:00:00', '2025-12-13 17:30:00', NULL, NULL, 1, NULL, NULL, '2025-12-12 16:15:00', '2025-12-12 16:15:00');

-- ----------------------------
-- Table structure for gym_equipment_maintenance
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment_maintenance`;
CREATE TABLE `gym_equipment_maintenance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '维护ID',
  `equipment_id` bigint NOT NULL COMMENT '器材ID',
  `maintenance_type` tinyint NOT NULL COMMENT '类型:1-保养,2-维修,3-检查',
  `maintenance_date` datetime NOT NULL COMMENT '维护时间',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '维护内容',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '费用',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '维护人员',
  `next_maintenance_date` date NULL DEFAULT NULL COMMENT '下次维护日期',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `idx_maintenance_date`(`maintenance_date` ASC) USING BTREE,
  INDEX `idx_maintenance_type`(`maintenance_type` ASC) USING BTREE,
  CONSTRAINT `gym_equipment_maintenance_ibfk_1` FOREIGN KEY (`equipment_id`) REFERENCES `gym_equipment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材维护记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_maintenance
-- ----------------------------
INSERT INTO `gym_equipment_maintenance` VALUES (1, 7, 2, '2025-12-10 10:00:00', '显示屏维修，更换LED屏幕', 450.00, '张师傅', '2025-12-20', '设备故障，需要维修', '2025-12-10 09:45:00', '2025-12-11 08:30:00');
INSERT INTO `gym_equipment_maintenance` VALUES (2, 1, 1, '2025-12-08 14:00:00', '定期保养，检查电机和传送带', 150.00, '李师傅', '2025-12-22', '正常保养', '2025-12-08 13:30:00', '2025-12-08 14:00:00');
INSERT INTO `gym_equipment_maintenance` VALUES (3, 3, 3, '2025-12-05 10:30:00', '踏板调整，刹车片更换', 280.00, '王师傅', '2025-12-19', '定期维护', '2025-12-05 10:00:00', '2025-12-05 10:30:00');
INSERT INTO `gym_equipment_maintenance` VALUES (4, 2, 1, '2025-12-01 15:00:00', '跑带张力调整，润滑处理', 100.00, '张师傅', '2025-12-15', '预防性维护', '2025-12-01 14:45:00', '2025-12-01 15:00:00');
INSERT INTO `gym_equipment_maintenance` VALUES (5, 4, 2, '2025-11-28 09:00:00', '安全销检查，动作平顺性检验', 0.00, '李师傅', '2025-12-26', '定期检查', '2025-11-28 08:30:00', '2025-11-28 09:00:00');

-- ----------------------------
-- Table structure for gym_equipment_queue
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment_queue`;
CREATE TABLE `gym_equipment_queue`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `equipment_id` bigint NOT NULL COMMENT '器材ID',
  `queue_number` int NOT NULL COMMENT '排队号',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `call_time` datetime NULL DEFAULT NULL COMMENT '叫号时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态:0-已取消,1-排队中,2-已叫号,3-已完成',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment_status`(`equipment_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_queue_number`(`queue_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_join_time`(`join_time` ASC) USING BTREE,
  CONSTRAINT `gym_equipment_queue_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_equipment_queue_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `gym_equipment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材排队表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_queue
-- ----------------------------
INSERT INTO `gym_equipment_queue` VALUES (1, 3, 4, 1, '2025-12-13 14:00:00', '2025-12-13 14:10:00', 2, NULL, NULL, '2025-12-13 14:00:00', '2025-12-13 14:10:00');
INSERT INTO `gym_equipment_queue` VALUES (2, 1, 6, 2, '2025-12-13 14:05:00', NULL, 1, NULL, NULL, '2025-12-13 14:05:00', '2025-12-13 14:05:00');
INSERT INTO `gym_equipment_queue` VALUES (3, 7, 5, 3, '2025-12-13 14:15:00', NULL, 1, NULL, NULL, '2025-12-13 14:15:00', '2025-12-13 14:15:00');

-- ----------------------------
-- Table structure for gym_membership_package
-- ----------------------------
DROP TABLE IF EXISTS `gym_membership_package`;
CREATE TABLE `gym_membership_package`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '套餐名称',
  `member_type` tinyint NOT NULL COMMENT '会员类型：1-黄金会员，2-铂金会员',
  `duration_days` int NOT NULL COMMENT '有效天数',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '套餐描述',
  `benefits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '会员权益描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_package_member_type`(`member_type` ASC) USING BTREE,
  INDEX `idx_package_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会员套餐表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_membership_package
-- ----------------------------
INSERT INTO `gym_membership_package` VALUES (1, '黄金会员月卡', 1, 30, 199.00, '黄金会员月卡，享受健身房训练权限', '健身房自主训练权限+课程九五折优惠', 1, '2025-10-17 09:19:31', '2025-10-28 15:15:48');
INSERT INTO `gym_membership_package` VALUES (2, '黄金会员季卡', 1, 90, 499.00, '黄金会员季卡，享受健身房训练权限', '健身房自主训练权限+课程九五折优惠', 1, '2025-10-17 09:19:31', '2025-10-28 15:15:51');
INSERT INTO `gym_membership_package` VALUES (3, '黄金会员年卡', 1, 365, 1699.00, '黄金会员年卡，享受健身房训练权限', '健身房自主训练权限+课程九五折优惠', 1, '2025-10-17 09:19:31', '2025-10-28 15:15:52');
INSERT INTO `gym_membership_package` VALUES (4, '铂金会员月卡', 2, 30, 299.00, '铂金会员月卡，享受健身房训练权限+课程九折优惠', '健身房自主训练权限+课程九折优惠', 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_membership_package` VALUES (5, '铂金会员季卡', 2, 90, 799.00, '铂金会员季卡，享受健身房训练权限+课程九折优惠', '健身房自主训练权限+课程九折优惠', 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_membership_package` VALUES (6, '铂金会员年卡', 2, 365, 2699.00, '铂金会员年卡，享受健身房训练权限+课程九折优惠', '健身房自主训练权限+课程九折优惠', 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');

-- ----------------------------
-- Table structure for gym_review_helpful
-- ----------------------------
DROP TABLE IF EXISTS `gym_review_helpful`;
CREATE TABLE `gym_review_helpful`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `review_id` bigint NOT NULL COMMENT '评价ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `is_helpful` tinyint NULL DEFAULT 1 COMMENT '是否有帮助：0-无帮助，1-有帮助',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_review_user`(`review_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `gym_review_helpful_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `gym_coach_review` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_review_helpful_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_review_helpful
-- ----------------------------

-- ----------------------------
-- Table structure for gym_review_tag
-- ----------------------------
DROP TABLE IF EXISTS `gym_review_tag`;
CREATE TABLE `gym_review_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_type` tinyint NOT NULL DEFAULT 1 COMMENT '标签类型：1-正面标签，2-负面标签',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `usage_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_name`(`tag_name` ASC) USING BTREE,
  INDEX `idx_tag_type`(`tag_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_review_tag
-- ----------------------------
INSERT INTO `gym_review_tag` VALUES (1, '专业', 1, NULL, 1, 2, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (2, '耐心', 1, NULL, 2, 1, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (3, '认真负责', 1, NULL, 3, 2, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (4, '效果显著', 1, NULL, 4, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (5, '幽默风趣', 1, NULL, 5, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (6, '激励性强', 1, NULL, 6, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (7, '活力四射', 1, NULL, 7, 1, 1, '2025-12-05 14:00:00', '2025-12-08 20:10:00');
INSERT INTO `gym_review_tag` VALUES (8, '音乐选得好', 1, NULL, 8, 2, 1, '2025-12-05 14:00:00', '2025-12-08 20:10:00');
INSERT INTO `gym_review_tag` VALUES (9, '动作标准', 1, NULL, 9, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (10, '讲解清晰', 1, NULL, 10, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (11, '体贴细心', 1, NULL, 11, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (12, '知识渊博', 1, NULL, 12, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (13, '态度冷淡', 2, NULL, 50, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (14, '讲解不清', 2, NULL, 51, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (15, '经常迟到', 2, NULL, 52, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (16, '不够专业', 2, NULL, 53, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

-- ----------------------------
-- Table structure for gym_schedule_record
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_record`;
CREATE TABLE `gym_schedule_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '打卡入场时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '打卡离场时间',
  `check_in_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入场地点',
  `check_out_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '离场地点',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '打卡状态：0-未打卡,1-已打卡入场,2-已打卡离场,3-迟到,4-早退,5-缺勤',
  `attendance_score` int NULL DEFAULT 100 COMMENT '出勤评分(0-100)',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_schedule_id`(`schedule_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_check_in_time`(`check_in_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_record_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `gym_coach_schedule` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_record_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_schedule_record
-- ----------------------------
INSERT INTO `gym_schedule_record` VALUES (1, 1, 1, '2025-12-13 08:55:00', '2025-12-13 12:10:00', '健身房一楼', '健身房一楼', 2, 100, '正常打卡', '2025-12-13 08:55:00', '2025-12-13 12:10:00');
INSERT INTO `gym_schedule_record` VALUES (2, 2, 1, '2025-12-13 14:05:00', '2025-12-13 17:05:00', '健身房一楼', '健身房一楼', 2, 100, '正常打卡', '2025-12-13 14:05:00', '2025-12-13 17:05:00');
INSERT INTO `gym_schedule_record` VALUES (3, 3, 2, '2025-12-14 08:15:00', NULL, '健身房二楼力量区', NULL, 3, 95, '迟到15分钟', '2025-12-14 08:15:00', '2025-12-14 08:15:00');
INSERT INTO `gym_schedule_record` VALUES (4, 4, 2, '2025-12-14 19:00:00', '2025-12-14 20:50:00', '健身房二楼力量区', '健身房二楼力量区', 4, 90, '早退10分钟', '2025-12-14 19:00:00', '2025-12-14 20:50:00');
INSERT INTO `gym_schedule_record` VALUES (5, 5, 3, '2025-12-13 17:58:00', '2025-12-13 19:32:00', '健身房二楼单车区', '健身房二楼单车区', 2, 100, '正常打卡', '2025-12-13 17:58:00', '2025-12-13 19:32:00');

-- ----------------------------
-- Table structure for gym_schedule_request
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_request`;
CREATE TABLE `gym_schedule_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `request_type` tinyint NOT NULL COMMENT '申请类型：1-调休(请假),2-加班,3-换班',
  `target_date` date NOT NULL COMMENT '目标日期(调休/加班的日期)',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '申请理由',
  `exchange_with_coach_id` bigint NULL DEFAULT NULL COMMENT '换班对象教练ID(仅当type=3时有效)',
  `exchange_schedule_id` bigint NULL DEFAULT NULL COMMENT '被交换的排班ID(仅当type=3时有效)',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态：0-待审批,1-已通过,2-已拒绝,3-已取消',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID(管理员)',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批意见',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件地址(如医疗证明)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_request_type`(`request_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_target_date`(`target_date` ASC) USING BTREE,
  INDEX `idx_coach_status`(`coach_id` ASC, `status` ASC) USING BTREE,
  INDEX `gym_schedule_request_ibfk_2`(`approver_id` ASC) USING BTREE,
  INDEX `gym_schedule_request_ibfk_3`(`exchange_with_coach_id` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_request_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_request_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_request_ibfk_3` FOREIGN KEY (`exchange_with_coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_schedule_request
-- ----------------------------
INSERT INTO `gym_schedule_request` VALUES (1, 1, 1, '2025-12-20', '身体不适，需要请假休息', NULL, NULL, 1, 1, '2025-12-18 10:00:00', '同意请假，注意身体', NULL, '2025-12-18 09:30:00', '2025-12-18 10:00:00');
INSERT INTO `gym_schedule_request` VALUES (2, 2, 2, '2025-12-25', '圣诞节想加班服务会员', NULL, NULL, 0, NULL, NULL, NULL, NULL, '2025-12-17 14:00:00', '2025-12-17 14:00:00');
INSERT INTO `gym_schedule_request` VALUES (3, 3, 3, '2025-12-21', '想和李教练换班，她同意了', 1, 1, 1, 1, '2025-12-19 15:00:00', '已批准换班申请', NULL, '2025-12-19 11:00:00', '2025-12-19 15:00:00');
INSERT INTO `gym_schedule_request` VALUES (4, 1, 1, '2025-12-22', '家里有事需要调休', NULL, NULL, 2, 1, '2025-12-20 09:00:00', '当天课程已满，无法批准', NULL, '2025-12-19 16:30:00', '2025-12-20 09:00:00');

-- ----------------------------
-- Table structure for gym_schedule_rule
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_rule`;
CREATE TABLE `gym_schedule_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则类型：WORK_TIME_LIMIT(工作时间限制),BREAK_TIME_RULE(休息规则),LATE_RULE(迟到规则)',
  `rule_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则值(JSON格式)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '规则说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rule_type`(`rule_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_schedule_rule
-- ----------------------------
INSERT INTO `gym_schedule_rule` VALUES (1, '正常工作时间', 'WORK_TIME_LIMIT', '{\"max_hours_per_day\":10,\"max_hours_per_week\":48}', '每天最多工作10小时，每周最多48小时', 1, '2025-12-11 21:34:41', '2025-12-11 21:34:41');
INSERT INTO `gym_schedule_rule` VALUES (2, '休息规则', 'BREAK_TIME_RULE', '{\"min_rest_hours\":11,\"max_work_days_continuous\":6}', '工作日之间最少休息11小时，连续工作最多6天', 1, '2025-12-11 21:34:41', '2025-12-11 21:34:41');
INSERT INTO `gym_schedule_rule` VALUES (3, '迟到规则', 'LATE_RULE', '{\"late_threshold_minutes\":15,\"deduction_points\":10}', '迟到超过15分钟扣10分', 1, '2025-12-11 21:34:41', '2025-12-11 21:34:41');

-- ----------------------------
-- Table structure for gym_schedule_statistics
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_statistics`;
CREATE TABLE `gym_schedule_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `statistics_date` date NOT NULL COMMENT '统计日期(月份)',
  `total_hours` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '总工作时数',
  `normal_hours` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '正常工作时数',
  `overtime_hours` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '加班时数',
  `work_days` int NULL DEFAULT 0 COMMENT '工作天数',
  `absent_days` int NULL DEFAULT 0 COMMENT '缺勤天数',
  `late_days` int NULL DEFAULT 0 COMMENT '迟到天数',
  `leave_days` int NULL DEFAULT 0 COMMENT '请假天数',
  `average_attendance_score` int NULL DEFAULT 100 COMMENT '平均出勤评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_coach_date`(`coach_id` ASC, `statistics_date` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_statistics_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_schedule_statistics
-- ----------------------------
INSERT INTO `gym_schedule_statistics` VALUES (1, 1, '2025-12-01', 168.50, 160.00, 8.50, 22, 0, 1, 2, 98, '2025-12-01 00:00:00', '2025-12-14 00:00:00');
INSERT INTO `gym_schedule_statistics` VALUES (2, 2, '2025-12-01', 184.00, 176.00, 8.00, 23, 0, 2, 0, 96, '2025-12-01 00:00:00', '2025-12-14 00:00:00');
INSERT INTO `gym_schedule_statistics` VALUES (3, 3, '2025-12-01', 156.00, 156.00, 0.00, 20, 0, 0, 0, 100, '2025-12-01 00:00:00', '2025-12-14 00:00:00');
INSERT INTO `gym_schedule_statistics` VALUES (4, 1, '2025-11-01', 176.00, 168.00, 8.00, 24, 1, 0, 3, 95, '2025-11-01 00:00:00', '2025-11-30 23:59:59');
INSERT INTO `gym_schedule_statistics` VALUES (5, 2, '2025-11-01', 192.00, 184.00, 8.00, 25, 0, 1, 0, 97, '2025-11-01 00:00:00', '2025-11-30 23:59:59');
INSERT INTO `gym_schedule_statistics` VALUES (6, 3, '2025-11-01', 144.00, 144.00, 0.00, 18, 0, 1, 0, 100, '2025-11-01 00:00:00', '2025-11-30 23:59:59');

-- ----------------------------
-- Table structure for gym_training_action
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_action`;
CREATE TABLE `gym_training_action`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动作ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '动作分类(胸/背/腿/肩/臂/腹/核心/全身)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '动作描述',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '示范视频URL',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '示范图片URL',
  `difficulty` tinyint NULL DEFAULT NULL COMMENT '难度：1-简单，2-中等，3-困难',
  `target_muscle` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标肌群',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练动作库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_action
-- ----------------------------
INSERT INTO `gym_training_action` VALUES (1, '平板卧推', '胸', '经典胸部训练动作', NULL, NULL, 2, '胸大肌', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_action` VALUES (2, '深蹲', '腿', '下肢力量训练', NULL, NULL, 2, '股四头肌、臀大肌', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_action` VALUES (3, '引体向上', '背', '背部训练经典动作', NULL, NULL, 3, '背阔肌', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_action` VALUES (4, '哑铃推举', '肩', '肩部力量训练', NULL, NULL, 2, '三角肌', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_action` VALUES (5, '仰卧起坐', '腹', '腹部核心训练', NULL, NULL, 1, '腹直肌', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_action` VALUES (6, '平板支撑', '核心', '核心稳定性训练', NULL, NULL, 1, '核心肌群', 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

-- ----------------------------
-- Table structure for gym_training_plan
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan`;
CREATE TABLE `gym_training_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '教练ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
  `goal` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '训练目标(减脂/增肌/塑形/康复)',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已结束，1-进行中',
  `version` int NULL DEFAULT 1 COMMENT '版本号',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan
-- ----------------------------
INSERT INTO `gym_training_plan` VALUES (1, 3, 1, '瑜伽入门计划', '塑形', '2025-12-01', '2026-01-31', 1, 1, '专注于柔韧性和体态改善', '2025-12-01 10:00:00', '2025-12-01 10:00:00');
INSERT INTO `gym_training_plan` VALUES (2, 3, 1, '普拉提进阶计划', '塑形', '2025-11-01', '2025-11-30', 0, 1, '核心力量训练已完成', '2025-11-01 09:00:00', '2025-12-01 00:00:00');
INSERT INTO `gym_training_plan` VALUES (3, 7, 1, '减脂塑形计划', '减脂', '2025-12-05', '2026-02-05', 1, 1, '结合瑜伽和有氧运动', '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_training_plan` VALUES (4, 9, 1, '柔韧性提升计划', '塑形', '2025-12-10', '2026-01-10', 1, 1, '初级学员,循序渐进', '2025-12-10 10:00:00', '2025-12-10 10:00:00');
INSERT INTO `gym_training_plan` VALUES (5, 11, 1, '体态矫正计划', '康复', '2025-11-20', '2025-12-20', 1, 1, '改善体态和增强柔韧性', '2025-11-20 15:00:00', '2025-11-20 15:00:00');
INSERT INTO `gym_training_plan` VALUES (6, 13, 1, '瑜伽基础计划', '塑形', '2025-12-08', '2026-01-08', 1, 1, '新手学员基础训练', '2025-12-08 11:00:00', '2025-12-08 11:00:00');
INSERT INTO `gym_training_plan` VALUES (7, 15, 1, '高级瑜伽计划', '塑形', '2025-12-03', '2026-02-03', 1, 1, '高难度动作组合', '2025-12-03 09:00:00', '2025-12-03 09:00:00');
INSERT INTO `gym_training_plan` VALUES (8, 17, 1, '全面塑形计划V2', '塑形', '2025-12-12', '2026-02-12', 1, 1, '续费后的新计划', '2025-12-12 10:00:00', '2025-12-12 10:00:00');
INSERT INTO `gym_training_plan` VALUES (9, 19, 1, '一对一定制计划', '增肌', '2025-12-06', '2026-01-06', 1, 1, '铂金会员专属定制', '2025-12-06 14:00:00', '2025-12-06 14:00:00');
INSERT INTO `gym_training_plan` VALUES (10, 21, 1, '减脂塑形专项计划', '减脂', '2025-12-11', '2026-01-11', 1, 1, '新会员专项计划', '2025-12-11 10:00:00', '2025-12-11 10:00:00');
INSERT INTO `gym_training_plan` VALUES (11, 23, 1, '普拉提长期计划', '塑形', '2025-10-15', '2025-12-15', 1, 1, '长期会员持续训练', '2025-10-15 09:00:00', '2025-10-15 09:00:00');
INSERT INTO `gym_training_plan` VALUES (12, 25, 1, '零基础瑜伽计划', '塑形', '2025-12-09', '2026-01-09', 1, 1, '从零开始系统训练', '2025-12-09 14:00:00', '2025-12-09 14:00:00');
INSERT INTO `gym_training_plan` VALUES (13, 27, 1, '高强度塑形计划', '塑形', '2025-12-07', '2026-02-07', 1, 1, '铂金会员高强度训练', '2025-12-07 10:00:00', '2025-12-07 10:00:00');
INSERT INTO `gym_training_plan` VALUES (14, 1, 2, '增肌力量计划V3', '增肌', '2025-12-01', '2026-01-31', 1, 1, '铂金会员第三期计划', '2025-12-01 08:00:00', '2025-12-01 08:00:00');
INSERT INTO `gym_training_plan` VALUES (15, 1, 2, '增肌力量计划V2', '增肌', '2025-10-01', '2025-11-30', 0, 1, '第二期已完成', '2025-10-01 08:00:00', '2025-12-01 00:00:00');
INSERT INTO `gym_training_plan` VALUES (16, 10, 2, '力量训练进阶计划', '增肌', '2025-12-10', '2026-01-10', 1, 1, '有天赋的学员,进度快', '2025-12-10 09:00:00', '2025-12-10 09:00:00');
INSERT INTO `gym_training_plan` VALUES (17, 12, 2, '力量提升计划', '增肌', '2025-11-25', '2025-12-25', 1, 1, '持续稳定提升', '2025-11-25 10:00:00', '2025-11-25 10:00:00');
INSERT INTO `gym_training_plan` VALUES (18, 14, 2, '中级力量训练', '增肌', '2025-12-05', '2026-01-05', 1, 1, '已达到中级水平', '2025-12-05 11:00:00', '2025-12-05 11:00:00');
INSERT INTO `gym_training_plan` VALUES (19, 16, 2, '新手力量入门', '增肌', '2025-12-08', '2026-01-08', 1, 1, '新手学员快速进步', '2025-12-08 14:00:00', '2025-12-08 14:00:00');
INSERT INTO `gym_training_plan` VALUES (20, 18, 2, '稳定增肌计划', '增肌', '2025-12-03', '2026-01-03', 1, 1, '黄金会员定期指导', '2025-12-03 10:00:00', '2025-12-03 10:00:00');
INSERT INTO `gym_training_plan` VALUES (21, 20, 2, '小组力量训练', '增肌', '2025-12-06', '2026-01-06', 1, 1, '小组课程表现突出', '2025-12-06 15:00:00', '2025-12-06 15:00:00');
INSERT INTO `gym_training_plan` VALUES (22, 22, 2, '力量突破计划', '增肌', '2025-12-09', '2026-01-09', 1, 1, '突破瓶颈专项计划', '2025-12-09 10:00:00', '2025-12-09 10:00:00');
INSERT INTO `gym_training_plan` VALUES (23, 24, 2, '肌肉线条塑造', '增肌', '2025-11-20', '2025-12-20', 1, 1, '改善肌肉线条', '2025-11-20 14:00:00', '2025-11-20 14:00:00');
INSERT INTO `gym_training_plan` VALUES (24, 26, 2, '高级力量训练', '增肌', '2025-12-04', '2026-02-04', 1, 1, '高级会员自律训练', '2025-12-04 09:00:00', '2025-12-04 09:00:00');
INSERT INTO `gym_training_plan` VALUES (25, 28, 2, '个性化增肌计划', '增肌', '2025-12-11', '2026-01-11', 1, 1, '新学员定制计划', '2025-12-11 15:00:00', '2025-12-11 15:00:00');
INSERT INTO `gym_training_plan` VALUES (26, 30, 3, '有氧燃脂计划', '减脂', '2025-12-05', '2026-01-05', 1, 1, '动感单车为主', '2025-12-05 18:00:00', '2025-12-05 18:00:00');
INSERT INTO `gym_training_plan` VALUES (27, 31, 3, '团课减脂计划', '减脂', '2025-12-08', '2026-01-08', 1, 1, '铂金会员团课专属', '2025-12-08 17:00:00', '2025-12-08 17:00:00');
INSERT INTO `gym_training_plan` VALUES (28, 32, 3, '基础有氧训练', '减脂', '2025-12-10', '2026-01-10', 1, 1, '新手基础培训', '2025-12-10 19:00:00', '2025-12-10 19:00:00');
INSERT INTO `gym_training_plan` VALUES (29, 33, 3, '有氧进阶计划', '减脂', '2025-12-06', '2026-01-06', 1, 1, '准备升级黄金会员', '2025-12-06 18:30:00', '2025-12-06 18:30:00');
INSERT INTO `gym_training_plan` VALUES (30, 34, 3, '定期有氧课程', '减脂', '2025-11-28', '2025-12-28', 1, 1, '黄金会员定期课程', '2025-11-28 18:00:00', '2025-11-28 18:00:00');
INSERT INTO `gym_training_plan` VALUES (31, 35, 3, 'HIIT高强度训练', '减脂', '2025-12-07', '2026-01-07', 1, 1, '高强度间歇训练', '2025-12-07 19:00:00', '2025-12-07 19:00:00');
INSERT INTO `gym_training_plan` VALUES (32, 36, 3, '中级有氧计划', '减脂', '2025-12-09', '2026-01-09', 1, 1, '中级学员稳定训练', '2025-12-09 18:00:00', '2025-12-09 18:00:00');
INSERT INTO `gym_training_plan` VALUES (33, 37, 3, '新手燃脂计划', '减脂', '2025-12-11', '2026-01-11', 1, 1, '新学员热情高', '2025-12-11 19:00:00', '2025-12-11 19:00:00');
INSERT INTO `gym_training_plan` VALUES (34, 38, 3, '单车减脂计划', '减脂', '2025-12-04', '2026-01-04', 1, 1, '动感单车表现出色', '2025-12-04 18:00:00', '2025-12-04 18:00:00');
INSERT INTO `gym_training_plan` VALUES (35, 29, 3, '综合有氧训练', '减脂', '2025-12-12', '2026-01-12', 1, 1, '多节课程持续进步', '2025-12-12 18:30:00', '2025-12-12 18:30:00');
INSERT INTO `gym_training_plan` VALUES (36, 39, NULL, '减脂塑形-初级计划 - 我的计划', '减脂', '2025-12-21', '2026-01-18', 1, 1, '基于模板：减脂塑形-初级计划', '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan` VALUES (37, 39, NULL, '运动康复-康复计划 - 我的计划', '康复', '2025-12-21', '2026-01-11', 1, 1, '基于模板：运动康复-康复计划', '2025-12-21 17:23:16', '2025-12-21 17:23:16');

-- ----------------------------
-- Table structure for gym_training_plan_detail
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan_detail`;
CREATE TABLE `gym_training_plan_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `day_of_week` tinyint NULL DEFAULT NULL COMMENT '星期几(1-7)',
  `action_id` bigint NULL DEFAULT NULL COMMENT '动作ID',
  `sets` int NULL DEFAULT NULL COMMENT '组数',
  `reps` int NULL DEFAULT NULL COMMENT '次数',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `duration` int NULL DEFAULT NULL COMMENT '时长(分钟)',
  `rest_time` int NULL DEFAULT NULL COMMENT '组间休息(秒)',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_completed` tinyint NULL DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `execution_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执行备注',
  `actual_sets` int NULL DEFAULT NULL COMMENT '实际完成组数',
  `actual_reps` int NULL DEFAULT NULL COMMENT '实际完成次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_action_id`(`action_id` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_detail_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `gym_training_plan` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_training_plan_detail_ibfk_2` FOREIGN KEY (`action_id`) REFERENCES `gym_training_action` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan_detail
-- ----------------------------
INSERT INTO `gym_training_plan_detail` VALUES (1, 36, 1, 1, 3, 12, 60.00, NULL, 60, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (2, 36, 1, 2, 4, 8, 80.00, NULL, 90, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (3, 36, 2, 5, 3, 20, NULL, 30, 60, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (4, 36, 2, 6, 3, 30, NULL, 30, 45, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (5, 36, 3, 3, 4, 6, 50.00, NULL, 90, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (6, 36, 3, 4, 3, 12, 25.00, NULL, 60, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (7, 36, 4, 5, 3, 20, NULL, 30, 45, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (8, 36, 4, 1, 3, 10, 50.00, NULL, 60, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (9, 36, 5, 2, 4, 10, 100.00, NULL, 90, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (10, 36, 5, 6, 2, 30, NULL, 20, 45, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (11, 36, 6, 3, 3, 8, 60.00, NULL, 90, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (12, 36, 7, 5, 4, 25, NULL, 45, 45, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:17:28', '2025-12-21 17:17:28');
INSERT INTO `gym_training_plan_detail` VALUES (13, 37, 1, 6, 3, 45, NULL, 40, 45, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (14, 37, 1, 5, 2, 15, NULL, 20, 30, 2, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (15, 37, 2, 1, 2, 10, 30.00, NULL, 60, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (16, 37, 3, 3, 2, 6, 20.00, NULL, 60, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (17, 37, 4, 4, 2, 12, 15.00, NULL, 45, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (18, 37, 5, 6, 4, 60, NULL, 50, 45, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');
INSERT INTO `gym_training_plan_detail` VALUES (19, 37, 6, 5, 2, 10, NULL, 15, 30, 1, 0, NULL, NULL, NULL, NULL, '2025-12-21 17:23:16', '2025-12-21 17:23:16');

-- ----------------------------
-- Table structure for gym_training_plan_template
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan_template`;
CREATE TABLE `gym_training_plan_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '教练ID(NULL表示系统模板)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `goal` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '训练目标(减脂/增肌/塑形/康复)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板说明',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难度：1-初级，2-中级，3-高级',
  `duration_days` int NULL DEFAULT 7 COMMENT '建议周期(天)',
  `is_public` tinyint NULL DEFAULT 0 COMMENT '是否公开:0-私有,1-公开(系统模板)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `total_exercises` int NULL DEFAULT 0 COMMENT '总动作数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_goal`(`goal` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_template_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan_template
-- ----------------------------
INSERT INTO `gym_training_plan_template` VALUES (1, NULL, '减脂塑形-初级计划', '减脂', '适合初学者的减脂计划，注重有氧运动和核心训练结合', 1, 28, 1, 1, 12, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template` VALUES (2, NULL, '增肌塑形-中级计划', '增肌', '针对有一定训练基础的学员，专注肌肉增长和力量提升', 2, 28, 1, 1, 14, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template` VALUES (3, NULL, '运动康复-康复计划', '康复', '帮助用户从运动损伤中恢复，强调柔韧性和稳定性', 1, 21, 1, 1, 8, '2025-12-11 00:00:00', '2025-12-11 00:00:00');

-- ----------------------------
-- Table structure for gym_training_plan_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan_template_detail`;
CREATE TABLE `gym_training_plan_template_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `day_of_week` tinyint NULL DEFAULT NULL COMMENT '星期几(1-7)',
  `action_id` bigint NULL DEFAULT NULL COMMENT '动作ID',
  `sets` int NULL DEFAULT NULL COMMENT '组数',
  `reps` int NULL DEFAULT NULL COMMENT '次数',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `duration` int NULL DEFAULT NULL COMMENT '时长(分钟)',
  `rest_time` int NULL DEFAULT NULL COMMENT '组间休息(秒)',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '说明',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_action_id`(`action_id` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_template_detail_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `gym_training_plan_template` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_training_plan_template_detail_ibfk_2` FOREIGN KEY (`action_id`) REFERENCES `gym_training_action` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划模板明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan_template_detail
-- ----------------------------
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
INSERT INTO `gym_training_plan_template_detail` VALUES (27, 3, 1, 6, 3, 45, NULL, 40, 45, '核心稳定性训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (28, 3, 1, 5, 2, 15, NULL, 20, 30, '腹部轻训练', 2, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (29, 3, 2, 1, 2, 10, 30.00, NULL, 60, '胸部轻训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (30, 3, 3, 3, 2, 6, 20.00, NULL, 60, '背部轻训练', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (31, 3, 4, 4, 2, 12, 15.00, NULL, 45, '肩部恢复', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (32, 3, 5, 6, 4, 60, NULL, 50, 45, '全身拉伸放松', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');
INSERT INTO `gym_training_plan_template_detail` VALUES (33, 3, 6, 5, 2, 10, NULL, 15, 30, '腹部恢复', 1, '2025-12-11 00:00:00', '2025-12-11 00:00:00');

-- ----------------------------
-- Table structure for gym_user_achievement
-- ----------------------------
DROP TABLE IF EXISTS `gym_user_achievement`;
CREATE TABLE `gym_user_achievement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `achievement_id` bigint NOT NULL COMMENT '成就ID',
  `achieve_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '达成时间',
  `progress` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '当前进度（%）',
  `is_achieved` tinyint NULL DEFAULT 0 COMMENT '是否已达成：0-未达成，1-已达成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_achievement`(`user_id` ASC, `achievement_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_achievement_id`(`achievement_id` ASC) USING BTREE,
  INDEX `idx_is_achieved`(`is_achieved` ASC) USING BTREE,
  CONSTRAINT `gym_user_achievement_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_user_achievement_ibfk_2` FOREIGN KEY (`achievement_id`) REFERENCES `gym_workout_achievement` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户成就记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_user_achievement
-- ----------------------------

-- ----------------------------
-- Table structure for gym_user_membership
-- ----------------------------
DROP TABLE IF EXISTS `gym_user_membership`;
CREATE TABLE `gym_user_membership`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `package_id` bigint NOT NULL COMMENT '套餐ID',
  `member_type` tinyint NOT NULL COMMENT '会员类型：1-黄金会员，2-铂金会员',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `price` decimal(10, 2) NOT NULL COMMENT '购买价格',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已过期，1-使用中',
  `purchase_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_membership_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_membership_package_id`(`package_id` ASC) USING BTREE,
  INDEX `idx_membership_status`(`status` ASC) USING BTREE,
  INDEX `idx_membership_end_time`(`end_time` ASC) USING BTREE,
  CONSTRAINT `gym_user_membership_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_user_membership_ibfk_2` FOREIGN KEY (`package_id`) REFERENCES `gym_membership_package` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户会员记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_user_membership
-- ----------------------------
INSERT INTO `gym_user_membership` VALUES (3, 1, 1, 1, '2025-12-12 14:53:47', '2026-01-11 14:53:47', 199.00, 1, '2025-12-12 14:53:47', '2025-12-12 14:53:46', '2025-12-12 14:53:46');
INSERT INTO `gym_user_membership` VALUES (4, 3, 4, 2, '2025-11-15 10:00:00', '2025-12-15 10:00:00', 299.00, 1, '2025-11-15 10:00:00', '2025-11-15 09:55:00', '2025-11-15 09:55:00');
INSERT INTO `gym_user_membership` VALUES (5, 7, 2, 1, '2025-10-01 00:00:00', '2025-12-31 23:59:59', 499.00, 1, '2025-10-01 00:00:00', '2025-10-01 00:00:00', '2025-10-01 00:00:00');
INSERT INTO `gym_user_membership` VALUES (6, 1, 6, 2, '2025-09-01 00:00:00', '2026-09-01 00:00:00', 2699.00, 1, '2025-09-01 00:00:00', '2025-09-01 00:00:00', '2025-09-01 00:00:00');

-- ----------------------------
-- Table structure for gym_workout_achievement
-- ----------------------------
DROP TABLE IF EXISTS `gym_workout_achievement`;
CREATE TABLE `gym_workout_achievement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成就ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成就名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '成就描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '成就图标',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成就类型：DURATION-时长，CALORIES-热量，DISTANCE-距离，FREQUENCY-频率，STREAK-连续',
  `condition_value` int NOT NULL COMMENT '达成条件值',
  `condition_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '条件单位（分钟、千卡、公里、天等）',
  `points` int NULL DEFAULT 0 COMMENT '获得积分',
  `level` tinyint NULL DEFAULT 1 COMMENT '成就等级：1-青铜，2-白银，3-黄金，4-钻石',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动成就表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_achievement
-- ----------------------------
INSERT INTO `gym_workout_achievement` VALUES (1, '初出茅庐', '完成第一次运动记录', '/images/achievement/first.png', 'FREQUENCY', 1, '次', 10, 1, 1, 1, '2025-12-12 15:31:05', '2025-12-12 15:31:05');
INSERT INTO `gym_workout_achievement` VALUES (2, '持之以恒', '连续运动7天', '/images/achievement/streak7.png', 'STREAK', 7, '天', 50, 2, 2, 1, '2025-12-12 15:31:05', '2025-12-12 15:31:05');
INSERT INTO `gym_workout_achievement` VALUES (3, '坚持不懈', '连续运动30天', '/images/achievement/streak30.png', 'STREAK', 30, '天', 200, 3, 3, 1, '2025-12-12 15:31:05', '2025-12-12 15:31:05');

-- ----------------------------
-- Table structure for gym_workout_daily_stats
-- ----------------------------
DROP TABLE IF EXISTS `gym_workout_daily_stats`;
CREATE TABLE `gym_workout_daily_stats`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_duration` int NULL DEFAULT 0 COMMENT '总运动时长（分钟）',
  `total_calories` int NULL DEFAULT 0 COMMENT '总消耗热量（千卡）',
  `total_distance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '总运动距离（公里）',
  `total_steps` int NULL DEFAULT 0 COMMENT '总步数',
  `workout_count` int NULL DEFAULT 0 COMMENT '运动次数',
  `cardio_duration` int NULL DEFAULT 0 COMMENT '有氧运动时长（分钟）',
  `strength_duration` int NULL DEFAULT 0 COMMENT '力量训练时长（分钟）',
  `flexibility_duration` int NULL DEFAULT 0 COMMENT '柔韧性训练时长（分钟）',
  `is_rest_day` tinyint NULL DEFAULT 0 COMMENT '是否休息日：0-否，1-是',
  `plan_completion_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '计划完成率（%，如果有训练计划）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `stat_date` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
  CONSTRAINT `gym_workout_daily_stats_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '每日运动数据统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_daily_stats
-- ----------------------------
INSERT INTO `gym_workout_daily_stats` VALUES (1, 3, '2025-12-10', 60, 450, 5.00, 8000, 1, 30, 30, 0, 0, 100.00, '2025-12-10 14:00:00', '2025-12-10 14:00:00');
INSERT INTO `gym_workout_daily_stats` VALUES (2, 1, '2025-12-12', 90, 650, 8.00, 12000, 2, 45, 45, 0, 0, 95.50, '2025-12-12 09:00:00', '2025-12-12 17:30:00');
INSERT INTO `gym_workout_daily_stats` VALUES (3, 7, '2025-12-11', 120, 800, 10.00, 15000, 2, 60, 60, 0, 0, 100.00, '2025-12-11 08:00:00', '2025-12-11 20:30:00');
INSERT INTO `gym_workout_daily_stats` VALUES (4, 1, '2025-12-01', 60, 480, 8.00, 10000, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (5, 1, '2025-12-02', 100, 600, 0.00, 0, 2, 0, 100, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (7, 1, '2025-12-03', 90, 550, 3.00, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (8, 1, '2025-12-04', 60, 600, 0.00, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (9, 1, '2025-12-05', 60, 280, 6.00, 8000, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (10, 1, '2025-12-06', 60, 400, 0.00, 0, 1, 0, 60, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (11, 1, '2025-12-07', 90, 520, 15.00, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (12, 1, '2025-12-08', 60, 480, 8.50, 10500, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (13, 1, '2025-12-09', 85, 600, 0.00, 0, 2, 0, 85, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (15, 1, '2025-12-10', 60, 380, 0.00, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (16, 1, '2025-12-11', 60, 420, 2.50, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (17, 1, '2025-12-13', 60, 520, 9.00, 11000, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (18, 1, '2025-12-14', 90, 580, 0.00, 0, 2, 0, 90, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (20, 1, '2025-12-15', 60, 580, 0.00, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (21, 1, '2025-12-16', 60, 290, 6.50, 8500, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (22, 1, '2025-12-17', 95, 740, 0.00, 0, 2, 0, 95, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (24, 1, '2025-12-18', 90, 530, 16.00, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (25, 1, '2025-12-19', 90, 560, 3.50, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (26, 1, '2025-12-20', 60, 490, 8.80, 10800, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (27, 1, '2025-12-21', 85, 570, 0.00, 0, 2, 0, 85, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (29, 3, '2025-12-05', 60, 460, 7.50, 9500, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (30, 3, '2025-12-08', 60, 590, 0.00, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (31, 3, '2025-12-12', 60, 410, 2.30, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (32, 3, '2025-12-15', 60, 360, 0.00, 0, 1, 0, 60, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (33, 3, '2025-12-18', 60, 470, 8.00, 10000, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (34, 7, '2025-12-05', 90, 510, 14.00, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (35, 7, '2025-12-08', 60, 340, 0.00, 0, 1, 0, 60, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (36, 7, '2025-12-14', 60, 480, 8.20, 10200, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (37, 7, '2025-12-17', 90, 540, 3.20, 0, 1, 90, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_daily_stats` VALUES (38, 7, '2025-12-20', 60, 600, 0.00, 0, 1, 60, 0, 0, 0, NULL, '2025-12-21 17:31:12', '2025-12-21 17:31:12');

-- ----------------------------
-- Table structure for gym_workout_detail
-- ----------------------------
DROP TABLE IF EXISTS `gym_workout_detail`;
CREATE TABLE `gym_workout_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `workout_record_id` bigint NOT NULL COMMENT '运动记录ID',
  `action_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作名称（如：杠铃深蹲、哑铃卧推）',
  `sets` int NULL DEFAULT NULL COMMENT '组数',
  `reps` int NULL DEFAULT NULL COMMENT '每组次数（或时长秒数）',
  `weight` decimal(6, 2) NULL DEFAULT NULL COMMENT '重量（kg）',
  `rest_time` int NULL DEFAULT NULL COMMENT '组间休息时间（秒）',
  `actual_sets` int NULL DEFAULT NULL COMMENT '实际完成组数',
  `actual_reps` int NULL DEFAULT NULL COMMENT '实际完成次数',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序（动作顺序）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_workout_record_id`(`workout_record_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `gym_workout_detail_ibfk_1` FOREIGN KEY (`workout_record_id`) REFERENCES `gym_workout_record` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动详情表（力量训练动作明细）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_detail
-- ----------------------------

-- ----------------------------
-- Table structure for gym_workout_record
-- ----------------------------
DROP TABLE IF EXISTS `gym_workout_record`;
CREATE TABLE `gym_workout_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `workout_type_id` bigint NULL DEFAULT NULL COMMENT '运动类型ID',
  `workout_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运动类型名称（冗余字段，防止类型删除后丢失）',
  `workout_date` date NOT NULL COMMENT '运动日期',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration` int NOT NULL COMMENT '运动时长（分钟）',
  `intensity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'MEDIUM' COMMENT '运动强度：LOW-低，MEDIUM-中，HIGH-高',
  `calories` int NULL DEFAULT 0 COMMENT '消耗热量（千卡kcal）',
  `distance` decimal(10, 2) NULL DEFAULT NULL COMMENT '运动距离（公里km，仅适用于有氧运动）',
  `steps` int NULL DEFAULT NULL COMMENT '步数（仅适用于步行、跑步等）',
  `heart_rate_avg` int NULL DEFAULT NULL COMMENT '平均心率（次/分钟）',
  `heart_rate_max` int NULL DEFAULT NULL COMMENT '最大心率（次/分钟）',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '运动照片（JSON数组，存储图片URL）',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '运动备注（感受、心情等）',
  `feeling` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '运动感受：GREAT-很好，GOOD-良好，NORMAL-一般，TIRED-疲惫，BAD-不适',
  `weather` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '天气情况',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '运动地点',
  `is_completed` tinyint NULL DEFAULT 1 COMMENT '是否完成：0-未完成（中途放弃），1-已完成',
  `training_plan_id` bigint NULL DEFAULT NULL COMMENT '关联的训练计划ID（如果是按计划训练）',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'MANUAL' COMMENT '数据来源：MANUAL-手动录入，PLAN-训练计划，DEVICE-设备同步',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_workout_date`(`workout_date` ASC) USING BTREE,
  INDEX `idx_user_date`(`user_id` ASC, `workout_date` ASC) USING BTREE,
  INDEX `idx_workout_type_id`(`workout_type_id` ASC) USING BTREE,
  INDEX `idx_training_plan_id`(`training_plan_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `gym_workout_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_workout_record_ibfk_2` FOREIGN KEY (`workout_type_id`) REFERENCES `gym_workout_type` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健身运动记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_record
-- ----------------------------
INSERT INTO `gym_workout_record` VALUES (1, 3, 1, '跑步', '2025-12-10', '2025-12-10 14:00:00', '2025-12-10 15:00:00', 60, 'MEDIUM', 450, 5.00, 8000, 120, 155, NULL, '感觉很好，跑步时心情舒畅', 'GREAT', '晴天', '健身房一楼', 1, NULL, 'MANUAL', '2025-12-10 15:05:00', '2025-12-10 15:05:00');
INSERT INTO `gym_workout_record` VALUES (2, 1, 7, '深蹲', '2025-12-12', '2025-12-12 09:30:00', '2025-12-12 11:00:00', 90, 'HIGH', 600, NULL, NULL, 140, 165, NULL, '今天深蹲进度有提升，完成了目标重量', 'GOOD', '多云', '健身房二楼', 1, NULL, 'MANUAL', '2025-12-12 11:05:00', '2025-12-12 11:05:00');
INSERT INTO `gym_workout_record` VALUES (3, 7, 6, '动感单车', '2025-12-11', '2025-12-11 08:00:00', '2025-12-11 09:30:00', 90, 'HIGH', 700, NULL, NULL, 155, 180, NULL, '跟着教练的节奏，整堂课燃脂效果很棒', 'GREAT', '晴天', '健身房二楼', 1, NULL, 'MANUAL', '2025-12-11 09:35:00', '2025-12-11 09:35:00');
INSERT INTO `gym_workout_record` VALUES (4, 1, 3, '游泳', '2025-12-12', '2025-12-12 15:00:00', '2025-12-12 16:30:00', 90, 'MEDIUM', 500, 2.00, NULL, 130, 150, NULL, '水温适宜，游泳时很放松', 'GOOD', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-12 16:35:00', '2025-12-12 16:35:00');
INSERT INTO `gym_workout_record` VALUES (10, 1, 1, '跑步', '2025-12-01', '2025-12-01 07:00:00', '2025-12-01 08:00:00', 60, 'MEDIUM', 480, 8.00, 10000, 135, 160, NULL, '早晨跑步，状态不错', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (11, 1, 7, '深蹲', '2025-12-02', '2025-12-02 18:00:00', '2025-12-02 19:00:00', 60, 'HIGH', 350, NULL, NULL, 130, 155, NULL, '力量训练日，腿部训练', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (12, 1, 8, '卧推', '2025-12-02', '2025-12-02 19:10:00', '2025-12-02 19:50:00', 40, 'HIGH', 250, NULL, NULL, 125, 150, NULL, '胸部训练，感觉良好', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (13, 1, 3, '游泳', '2025-12-03', '2025-12-03 15:00:00', '2025-12-03 16:30:00', 90, 'MEDIUM', 550, 3.00, NULL, 120, 145, NULL, '游泳放松，水温适宜', 'GREAT', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (14, 1, 6, '动感单车', '2025-12-04', '2025-12-04 18:30:00', '2025-12-04 19:30:00', 60, 'HIGH', 600, NULL, NULL, 155, 180, NULL, '跟着节奏，燃脂效果好', 'GREAT', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (15, 1, 2, '快走', '2025-12-05', '2025-12-05 07:30:00', '2025-12-05 08:30:00', 60, 'LOW', 280, 6.00, 8000, 110, 130, NULL, '轻松快走，舒缓压力', 'GOOD', '阴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (16, 1, 9, '硬拉', '2025-12-06', '2025-12-06 18:00:00', '2025-12-06 19:00:00', 60, 'HIGH', 400, NULL, NULL, 140, 165, NULL, '全身力量训练', 'TIRED', '晴天', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (17, 1, 4, '骑行', '2025-12-07', '2025-12-07 08:00:00', '2025-12-07 09:30:00', 90, 'MEDIUM', 520, 15.00, NULL, 130, 155, NULL, '周末骑行，天气很好', 'GREAT', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (18, 1, 1, '跑步', '2025-12-08', '2025-12-08 07:00:00', '2025-12-08 08:00:00', 60, 'MEDIUM', 480, 8.50, 10500, 138, 162, NULL, '继续坚持晨跑', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (19, 1, 10, '引体向上', '2025-12-09', '2025-12-09 18:00:00', '2025-12-09 18:40:00', 40, 'HIGH', 280, NULL, NULL, 125, 148, NULL, '背部训练日', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (20, 1, 7, '深蹲', '2025-12-09', '2025-12-09 18:45:00', '2025-12-09 19:30:00', 45, 'HIGH', 320, NULL, NULL, 132, 155, NULL, '腿部训练', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (21, 1, 5, '椭圆机', '2025-12-10', '2025-12-10 19:00:00', '2025-12-10 20:00:00', 60, 'MEDIUM', 380, NULL, NULL, 128, 150, NULL, '低冲击有氧训练', 'GOOD', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (22, 1, 3, '游泳', '2025-12-11', '2025-12-11 15:00:00', '2025-12-11 16:00:00', 60, 'MEDIUM', 420, 2.50, NULL, 118, 142, NULL, '游泳训练', 'GOOD', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (23, 1, 1, '跑步', '2025-12-13', '2025-12-13 07:00:00', '2025-12-13 08:00:00', 60, 'HIGH', 520, 9.00, 11000, 145, 170, NULL, '提速训练，感觉很棒', 'GREAT', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (24, 1, 8, '卧推', '2025-12-14', '2025-12-14 18:00:00', '2025-12-14 18:50:00', 50, 'HIGH', 300, NULL, NULL, 128, 152, NULL, '上肢训练日', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (25, 1, 10, '引体向上', '2025-12-14', '2025-12-14 19:00:00', '2025-12-14 19:40:00', 40, 'HIGH', 280, NULL, NULL, 126, 148, NULL, '背部训练', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (26, 1, 6, '动感单车', '2025-12-15', '2025-12-15 09:00:00', '2025-12-15 10:00:00', 60, 'HIGH', 580, NULL, NULL, 152, 178, NULL, '周末燃脂训练', 'GREAT', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (27, 1, 2, '快走', '2025-12-16', '2025-12-16 07:30:00', '2025-12-16 08:30:00', 60, 'LOW', 290, 6.50, 8500, 112, 132, NULL, '恢复性训练', 'GOOD', '阴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (28, 1, 9, '硬拉', '2025-12-17', '2025-12-17 18:00:00', '2025-12-17 18:45:00', 45, 'HIGH', 380, NULL, NULL, 138, 162, NULL, '下肢力量强化', 'GOOD', '晴天', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (29, 1, 7, '深蹲', '2025-12-17', '2025-12-17 18:50:00', '2025-12-17 19:40:00', 50, 'HIGH', 360, NULL, NULL, 135, 158, NULL, '腿部训练', 'TIRED', '晴天', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (30, 1, 4, '骑行', '2025-12-18', '2025-12-18 08:00:00', '2025-12-18 09:30:00', 90, 'MEDIUM', 530, 16.00, NULL, 132, 156, NULL, '有氧耐力训练', 'GOOD', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (31, 1, 3, '游泳', '2025-12-19', '2025-12-19 15:00:00', '2025-12-19 16:30:00', 90, 'MEDIUM', 560, 3.50, NULL, 122, 146, NULL, '全身有氧训练', 'GREAT', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (32, 1, 1, '跑步', '2025-12-20', '2025-12-20 07:00:00', '2025-12-20 08:00:00', 60, 'MEDIUM', 490, 8.80, 10800, 140, 164, NULL, '晨跑继续', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (33, 1, 8, '卧推', '2025-12-21', '2025-12-21 10:00:00', '2025-12-21 10:50:00', 50, 'HIGH', 310, NULL, NULL, 130, 153, NULL, '上肢力量训练', 'GOOD', '晴天', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (34, 1, 10, '引体向上', '2025-12-21', '2025-12-21 11:00:00', '2025-12-21 11:35:00', 35, 'HIGH', 260, NULL, NULL, 124, 147, NULL, '背部强化', 'GOOD', '晴天', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (40, 3, 1, '跑步', '2025-12-05', '2025-12-05 07:00:00', '2025-12-05 08:00:00', 60, 'MEDIUM', 460, 7.50, 9500, 132, 158, NULL, '晨跑训练', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (41, 3, 6, '动感单车', '2025-12-08', '2025-12-08 18:00:00', '2025-12-08 19:00:00', 60, 'HIGH', 590, NULL, NULL, 150, 175, NULL, '燃脂训练', 'GREAT', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (42, 3, 3, '游泳', '2025-12-12', '2025-12-12 15:00:00', '2025-12-12 16:00:00', 60, 'MEDIUM', 410, 2.30, NULL, 116, 140, NULL, '游泳训练', 'GOOD', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (43, 3, 7, '深蹲', '2025-12-15', '2025-12-15 18:00:00', '2025-12-15 19:00:00', 60, 'HIGH', 360, NULL, NULL, 135, 160, NULL, '腿部力量训练', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (44, 3, 1, '跑步', '2025-12-18', '2025-12-18 07:00:00', '2025-12-18 08:00:00', 60, 'MEDIUM', 470, 8.00, 10000, 135, 160, NULL, '晨跑训练', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (50, 7, 4, '骑行', '2025-12-05', '2025-12-05 08:00:00', '2025-12-05 09:30:00', 90, 'MEDIUM', 510, 14.00, NULL, 128, 152, NULL, '有氧骑行', 'GOOD', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (51, 7, 8, '卧推', '2025-12-08', '2025-12-08 18:00:00', '2025-12-08 19:00:00', 60, 'HIGH', 340, NULL, NULL, 130, 155, NULL, '胸部训练', 'GOOD', '多云', '健身房力量区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (52, 7, 1, '跑步', '2025-12-14', '2025-12-14 07:00:00', '2025-12-14 08:00:00', 60, 'MEDIUM', 480, 8.20, 10200, 137, 161, NULL, '晨跑训练', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (53, 7, 3, '游泳', '2025-12-17', '2025-12-17 15:00:00', '2025-12-17 16:30:00', 90, 'MEDIUM', 540, 3.20, NULL, 120, 144, NULL, '游泳训练', 'GREAT', '晴天', '健身房游泳池', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');
INSERT INTO `gym_workout_record` VALUES (54, 7, 6, '动感单车', '2025-12-20', '2025-12-20 18:00:00', '2025-12-20 19:00:00', 60, 'HIGH', 600, NULL, NULL, 153, 179, NULL, '高强度燃脂', 'GREAT', '晴天', '健身房有氧区', 1, NULL, 'MANUAL', '2025-12-21 17:31:12', '2025-12-21 17:31:12');

-- ----------------------------
-- Table structure for gym_workout_type
-- ----------------------------
DROP TABLE IF EXISTS `gym_workout_type`;
CREATE TABLE `gym_workout_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '运动类型ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运动类型名称',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运动大类：CARDIO-有氧运动，STRENGTH-力量训练，FLEXIBILITY-柔韧性训练，SPORTS-球类运动，OTHER-其他',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标URL',
  `met_value` decimal(4, 2) NULL DEFAULT NULL COMMENT 'MET代谢当量值（用于热量计算）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_type
-- ----------------------------
INSERT INTO `gym_workout_type` VALUES (1, '跑步', 'CARDIO', '/images/workout/running.png', 8.00, '有氧运动，提升心肺功能', 1, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (2, '快走', 'CARDIO', '/images/workout/walking.png', 3.50, '低强度有氧运动', 2, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (3, '游泳', 'CARDIO', '/images/workout/swimming.png', 7.00, '全身有氧运动', 3, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (4, '骑行', 'CARDIO', '/images/workout/cycling.png', 6.00, '有氧耐力训练', 4, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (5, '椭圆机', 'CARDIO', '/images/workout/elliptical.png', 5.00, '低冲击有氧运动', 5, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (6, '动感单车', 'CARDIO', '/images/workout/spinning.png', 8.50, '高强度有氧训练', 6, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (7, '深蹲', 'STRENGTH', '/images/workout/squat.png', 5.00, '下肢力量训练', 7, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (8, '卧推', 'STRENGTH', '/images/workout/bench_press.png', 4.00, '胸部力量训练', 8, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (9, '硬拉', 'STRENGTH', '/images/workout/deadlift.png', 6.00, '全身力量训练', 9, 1, '2025-12-12 15:31:04', '2025-12-12 15:31:04');
INSERT INTO `gym_workout_type` VALUES (10, '引体向上', 'STRENGTH', '/images/workout/pull_up.png', 4.50, '背部力量训练', 10, 1, '2025-12-12 15:31:05', '2025-12-12 15:31:05');

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
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.name', '健身房预约管理系统', '系统名称', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (2, 'system.version', '1.0.0', '系统版本', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (3, 'booking.advance.days', '7', '可提前预约天数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (4, 'booking.cancel.hours', '2', '取消预约最少提前小时数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (5, 'membership.gold.discount', '0.95', '黄金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (6, 'membership.platinum.discount', '0.90', '铂金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (7, 'wechat.appid', 'wxdae24e45d0dcbaa6', '?????ppID', '??????', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:46:36');
INSERT INTO `sys_config` VALUES (8, 'wechat.appsecret', '', '?????ppSecret', '??????', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:46:36');
INSERT INTO `sys_config` VALUES (9, 'wechat.pay.mchid', '', '微信支付商户号', '微信配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (10, 'wechat.pay.apiv3key', '', 'API V3密钥', '微信配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (11, 'wechat.pay.notifyurl', 'https://yourdomain.com/api/pay/notify', '支付回调地址', '微信配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (12, 'wechat.pay.privatekeypath', 'classpath:apiclient_key.pem', '商户私钥路径', '微信配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (13, 'file.upload.path', './files', '文件存储路径', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (14, 'file.access.prefix', '/files', '访问路径前缀', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (15, 'file.upload.maxsize', '10', '最大文件大小(MB)', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (16, 'file.upload.allowedtypes', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许的文件类型', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (17, 'file.image.compress', 'true', '图片压缩', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (18, 'file.image.quality', '80', '图片质量(10-100)', '文件配置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (21, 'system.description', '基于Spring Boot + Vue3 + UniApp的健身房综合管理系统', '系统描述', '系统设置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (22, 'system.session.timeout', '120', '会话超时时间(分钟)', '系统设置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');
INSERT INTO `sys_config` VALUES (23, 'system.password.check', 'false', '密码复杂度检查', '系统设置', 0, 1, '2025-12-12 16:44:02', '2025-12-12 16:44:02');

-- ----------------------------
-- Table structure for sys_file_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_info`;
CREATE TABLE `sys_file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件访问路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型(IMG/PDF/TXT/AUDIO/VIDEO)',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `business_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务对象ID',
  `business_field` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务字段名',
  `is_temp` tinyint(1) NULL DEFAULT 0 COMMENT '是否临时文件(0:否 1:是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_business_field`(`business_type` ASC, `business_id` ASC, `business_field` ASC) USING BTREE,
  INDEX `idx_is_temp`(`is_temp` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_file_path`(`file_path` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表-精简版' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file_info
-- ----------------------------
INSERT INTO `sys_file_info` VALUES (36, 'logo.png', '/files/bussiness/course/1761618881517.png', 7804, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440001', 'cover_image', 0, '2025-10-28 10:34:42');
INSERT INTO `sys_file_info` VALUES (37, 'logo.png', '/files/bussiness/course/1761619290737.png', 7804, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440001', 'cover_image', 0, '2025-10-28 10:41:31');
INSERT INTO `sys_file_info` VALUES (38, 'KccBwJzJCFk746d008584defb0e297c6f6819e149d8d.jpg', '/files/bussiness/user_avatar/1761634887253.jpg', 84624, 'IMG', 'USER_AVATAR', '1', 'avatar', 0, '2025-10-28 15:01:27');
INSERT INTO `sys_file_info` VALUES (39, '微信图片_20251015184850_31_2.jpg', '/files/bussiness/user_avatar/1761636869026.jpg', 84624, 'IMG', 'USER_AVATAR', '5', 'avatar', 0, '2025-10-28 15:34:29');
INSERT INTO `sys_file_info` VALUES (40, '微信图片_20230727231054.jpg', '/files/bussiness/user_avatar/1764919899218.jpg', 40703, 'IMG', 'USER_AVATAR', '5', 'avatar', 0, '2025-12-05 15:31:39');
INSERT INTO `sys_file_info` VALUES (41, '微信图片_20230727231054.jpg', '/files/bussiness/course/1766214401147.jpg', 40703, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440005', 'cover_image', 0, '2025-12-20 15:06:41');
INSERT INTO `sys_file_info` VALUES (42, '1761618669374.jpg', '/files/bussiness/course/1766219539658.jpg', 168829, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440006', 'cover_image', 0, '2025-12-20 16:32:20');
INSERT INTO `sys_file_info` VALUES (43, '1181597-rId21.jpg', '/files/bussiness/course/1766219551439.jpg', 189613, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440003', 'cover_image', 0, '2025-12-20 16:32:31');
INSERT INTO `sys_file_info` VALUES (44, '1xfavgp86qikr2nsmh9b7cd0ew4z5luo_PIC2018.png', '/files/bussiness/course/1766219564065.png', 1736562, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440002', 'cover_image', 0, '2025-12-20 16:32:44');
INSERT INTO `sys_file_info` VALUES (45, '5148.jpg_wh860.jpg', '/files/bussiness/course/1766219574497.jpg', 158186, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440001', 'cover_image', 0, '2025-12-20 16:32:55');
INSERT INTO `sys_file_info` VALUES (46, '1xfavgp86qikr2nsmh9b7cd0ew4z5luo_PIC2018.png', '/files/bussiness/course/1766219588034.png', 1736562, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440004', 'cover_image', 0, '2025-12-20 16:33:08');
INSERT INTO `sys_file_info` VALUES (47, '10389099752_1103739600.jpg', '/files/bussiness/course/1766276361852.jpg', 195440, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440005', 'cover_image', 0, '2025-12-21 08:19:22');
INSERT INTO `sys_file_info` VALUES (48, '1765962376426.jpg', '/files/bussiness/user_avatar/1766276383386.jpg', 40703, 'IMG', 'USER_AVATAR', '1', 'avatar', 0, '2025-12-21 08:19:43');

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notification_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型：SYSTEM-系统通知，BOOKING-预约提醒，COURSE-课程相关，EQUIPMENT-器材相关，MEMBERSHIP-会员相关',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `target_user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标用户类型：USER-小程序用户，ADMIN-管理员，COACH-教练，ALL-全部',
  `target_user_id` bigint NULL DEFAULT NULL COMMENT '目标用户ID（NULL表示发送给所有该类型用户）',
  `related_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联业务ID（如课程ID、预约ID等）',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联业务类型（COURSE、BOOKING、EQUIPMENT等）',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知图标URL',
  `priority` tinyint NULL DEFAULT 0 COMMENT '优先级：0-低，1-中，2-高',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已删除，1-已发送，2-草稿',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `scheduled_time` datetime NULL DEFAULT NULL COMMENT '定时发送时间（为空时立即发送）',
  `is_read_required` tinyint NULL DEFAULT 1 COMMENT '是否需要标记已读：0-否，1-是',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notification_type`(`notification_type` ASC) USING BTREE,
  INDEX `idx_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_target_user_type`(`target_user_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_related_id`(`related_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notification
-- ----------------------------
INSERT INTO `sys_notification` VALUES (1, 'SYSTEM', '系统维护通知', '本系统将于本周日凌晨2:00-4:00进行系统维护，届时将无法访问，请提前安排好您的预约计划。', 'ALL', NULL, NULL, NULL, NULL, 2, 1, '2025-12-12 16:30:00', NULL, 1, '重要维护通知', '2025-12-12 16:00:00', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (2, 'BOOKING', '课程预约确认', '您已成功预约瑜伽基础课，预约时间：2025-10-28 15:00-18:00', 'USER', 3, '550e8400-e29b-41d4-a716-446655440001', 'COURSE', NULL, 1, 1, '2025-12-12 10:05:00', NULL, 1, '自动确认', '2025-12-12 10:00:00', '2025-12-12 10:05:00');
INSERT INTO `sys_notification` VALUES (3, 'MEMBERSHIP', '会员即将过期提醒', '您的铂金会员将于2025-12-15过期，请及时续费以继续享受会员权益', 'USER', 3, NULL, NULL, NULL, 1, 1, '2025-12-12 14:00:00', NULL, 1, '续费提醒', '2025-12-12 14:00:00', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (4, 'EQUIPMENT', '器材维护通知', '跑步机A3已维修完成，可以正常使用，感谢您的耐心等待', 'ALL', NULL, '7', 'EQUIPMENT', NULL, 0, 1, '2025-12-12 08:30:00', NULL, 1, '维修完成通知', '2025-12-12 08:30:00', '2025-12-12 08:30:00');
INSERT INTO `sys_notification` VALUES (5, 'SYSTEM', '系统维护通知', '本系统将于本周日凌晨2:00-4:00进行系统维护，届时将无法访问，请提前安排好您的预约计划。', 'ALL', NULL, NULL, NULL, NULL, 2, 1, '2025-12-20 21:34:49', NULL, 1, '系统通知测试', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (6, 'BOOKING', '课程预约提醒', '您预约的课程即将开始，请提前15分钟到达健身房。', 'USER', NULL, NULL, 'COURSE_BOOKING', NULL, 1, 1, '2025-12-20 21:34:49', NULL, 1, '预约提醒', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (7, 'MEMBERSHIP', '会员即将到期', '您的会员将于3天后到期，请及时续费以继续享受会员权益。', 'USER', NULL, NULL, 'MEMBERSHIP', NULL, 1, 1, '2025-12-20 21:34:49', NULL, 1, '会员提醒', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (8, 'EQUIPMENT', '器材维护通知', '部分健身器材将于今日下午进行维护保养，预计维护时间2小时，给您带来不便敬请谅解。', 'ALL', NULL, NULL, 'EQUIPMENT', NULL, 0, 1, '2025-12-20 21:34:49', NULL, 1, '器材维护', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (9, 'COURSE', '课程时间变更', '您预约的课程时间已变更，请注意查看最新时间安排。', 'USER', NULL, NULL, 'COURSE_SCHEDULE', NULL, 2, 1, '2025-12-20 21:34:49', NULL, 1, '课程通知', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (10, 'SYSTEM', '新年优惠活动', '新年特惠！所有会员套餐8折优惠，活动时间有限，欢迎咨询。', 'ALL', NULL, NULL, NULL, NULL, 1, 1, '2025-12-20 21:34:49', NULL, 1, '营销活动', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (11, 'COURSE', '教练请假通知', '由于教练临时请假，您预约的课程已取消，费用将原路退回。', 'COACH', NULL, NULL, 'COURSE', NULL, 2, 1, '2025-12-20 21:34:49', NULL, 1, '教练通知', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (12, 'SYSTEM', '系统功能升级', '系统已升级至新版本，新增多项功能，欢迎体验！', 'ADMIN', NULL, NULL, NULL, NULL, 1, 1, '2025-12-20 21:34:49', NULL, 1, '管理员通知', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (13, 'SYSTEM', '春节放假通知', '春节期间健身房营业时间调整，具体时间请关注公告。', 'ALL', NULL, NULL, NULL, NULL, 1, 2, NULL, NULL, 1, '草稿-待发送', '2025-12-20 21:34:49', '2025-12-21 17:13:14');
INSERT INTO `sys_notification` VALUES (14, 'MEMBERSHIP', '会员优惠提醒', '本月办理年卡可享受特别优惠，欢迎咨询前台。', 'ALL', NULL, NULL, 'MEMBERSHIP', NULL, 1, 1, '2025-12-20 21:34:49', NULL, 1, '优惠活动', '2025-12-20 21:34:49', '2025-12-21 17:13:14');

-- ----------------------------
-- Table structure for sys_notification_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification_read`;
CREATE TABLE `sys_notification_read`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `notification_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime NULL DEFAULT NULL COMMENT '读取时间',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notification_user`(`notification_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_read_time`(`read_time` ASC) USING BTREE,
  CONSTRAINT `sys_notification_read_ibfk_1` FOREIGN KEY (`notification_id`) REFERENCES `sys_notification` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_notification_read_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 396 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户通知读取记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notification_read
-- ----------------------------
INSERT INTO `sys_notification_read` VALUES (1, 14, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (2, 10, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (3, 8, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (4, 5, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (5, 4, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (6, 1, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (7, 14, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (8, 10, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (9, 8, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (10, 5, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (11, 4, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (12, 1, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (13, 14, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (14, 10, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (15, 8, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (16, 5, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (17, 4, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (18, 1, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (19, 14, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (20, 10, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (21, 8, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (22, 5, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (23, 4, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (24, 1, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (25, 14, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (26, 10, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (27, 8, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (28, 5, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (29, 4, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (30, 1, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (31, 14, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (32, 10, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (33, 8, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (34, 5, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (35, 4, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (36, 1, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (37, 14, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (38, 10, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (39, 8, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (40, 5, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (41, 4, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (42, 1, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (43, 14, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (44, 10, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (45, 8, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (46, 5, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (47, 4, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (48, 1, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (49, 14, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (50, 10, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (51, 8, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (52, 5, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (53, 4, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (54, 1, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (55, 14, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (56, 10, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (57, 8, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (58, 5, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (59, 4, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (60, 1, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (61, 14, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (62, 10, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (63, 8, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (64, 5, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (65, 4, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (66, 1, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (67, 14, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (68, 10, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (69, 8, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (70, 5, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (71, 4, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (72, 1, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (73, 14, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (74, 10, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (75, 8, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (76, 5, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (77, 4, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (78, 1, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (79, 14, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (80, 10, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (81, 8, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (82, 5, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (83, 4, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (84, 1, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (85, 14, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (86, 10, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (87, 8, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (88, 5, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (89, 4, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (90, 1, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (91, 14, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (92, 10, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (93, 8, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (94, 5, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (95, 4, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (96, 1, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (97, 14, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (98, 10, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (99, 8, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (100, 5, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (101, 4, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (102, 1, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (103, 14, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (104, 10, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (105, 8, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (106, 5, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (107, 4, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (108, 1, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (109, 14, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (110, 10, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (111, 8, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (112, 5, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (113, 4, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (114, 1, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (115, 14, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (116, 10, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (117, 8, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (118, 5, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (119, 4, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (120, 1, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (121, 14, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (122, 10, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (123, 8, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (124, 5, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (125, 4, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (126, 1, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (127, 14, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (128, 10, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (129, 8, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (130, 5, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (131, 4, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (132, 1, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (133, 14, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (134, 10, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (135, 8, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (136, 5, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (137, 4, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (138, 1, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (139, 14, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (140, 10, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (141, 8, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (142, 5, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (143, 4, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (144, 1, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (145, 14, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (146, 10, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (147, 8, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (148, 5, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (149, 4, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (150, 1, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (151, 14, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (152, 10, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (153, 8, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (154, 5, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (155, 4, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (156, 1, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (157, 14, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (158, 10, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (159, 8, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (160, 5, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (161, 4, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (162, 1, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (163, 14, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (164, 10, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (165, 8, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (166, 5, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (167, 4, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (168, 1, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (169, 14, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (170, 10, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (171, 8, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (172, 5, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (173, 4, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (174, 1, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (175, 14, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (176, 10, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (177, 8, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (178, 5, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (179, 4, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (180, 1, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (181, 14, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (182, 10, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (183, 8, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (184, 5, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (185, 4, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (186, 1, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (187, 14, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (188, 10, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (189, 8, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (190, 5, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (191, 4, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (192, 1, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (193, 14, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (194, 10, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (195, 8, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (196, 5, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (197, 4, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (198, 1, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (199, 14, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (200, 10, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (201, 8, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (202, 5, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (203, 4, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (204, 1, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (205, 14, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (206, 10, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (207, 8, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (208, 5, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (209, 4, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (210, 1, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (211, 14, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (212, 10, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (213, 8, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (214, 5, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (215, 4, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (216, 1, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (217, 14, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (218, 10, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (219, 8, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (220, 5, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (221, 4, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (222, 1, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (223, 14, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (224, 10, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (225, 8, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (226, 5, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (227, 4, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (228, 1, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (229, 14, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (230, 10, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (231, 8, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (232, 5, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (233, 4, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (234, 1, 39, '2025-12-21 17:14:14', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (256, 9, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (257, 7, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (258, 6, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (259, 9, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (260, 7, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (261, 6, 7, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (262, 9, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (263, 7, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (264, 6, 9, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (265, 9, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (266, 7, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (267, 6, 10, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (268, 9, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (269, 7, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (270, 6, 11, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (271, 9, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (272, 7, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (273, 6, 12, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (274, 9, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (275, 7, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (276, 6, 13, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (277, 9, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (278, 7, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (279, 6, 14, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (280, 9, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (281, 7, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (282, 6, 15, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (283, 9, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (284, 7, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (285, 6, 16, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (286, 9, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (287, 7, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (288, 6, 17, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (289, 9, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (290, 7, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (291, 6, 18, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (292, 9, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (293, 7, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (294, 6, 19, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (295, 9, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (296, 7, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (297, 6, 20, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (298, 9, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (299, 7, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (300, 6, 21, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (301, 9, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (302, 7, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (303, 6, 22, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (304, 9, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (305, 7, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (306, 6, 23, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (307, 9, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (308, 7, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (309, 6, 24, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (310, 9, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (311, 7, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (312, 6, 25, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (313, 9, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (314, 7, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (315, 6, 26, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (316, 9, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (317, 7, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (318, 6, 27, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (319, 9, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (320, 7, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (321, 6, 28, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (322, 9, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (323, 7, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (324, 6, 29, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (325, 9, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (326, 7, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (327, 6, 30, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (328, 9, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (329, 7, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (330, 6, 31, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (331, 9, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (332, 7, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (333, 6, 32, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (334, 9, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (335, 7, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (336, 6, 33, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (337, 9, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (338, 7, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (339, 6, 34, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (340, 9, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (341, 7, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (342, 6, 35, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (343, 9, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (344, 7, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (345, 6, 36, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (346, 9, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (347, 7, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (348, 6, 37, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (349, 9, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (350, 7, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (351, 6, 38, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (352, 9, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (353, 7, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (354, 6, 39, '2025-12-21 17:14:26', 1, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (383, 2, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (384, 3, 3, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (386, 12, 1, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (387, 12, 2, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (389, 11, 4, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (390, 11, 5, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (391, 11, 6, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');
INSERT INTO `sys_notification_read` VALUES (392, 11, 8, NULL, 0, '2025-12-21 17:13:14', '2025-12-21 17:13:14');

-- ----------------------------
-- Table structure for user
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统一用户表（包含小程序用户和管理员）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ADMIN', '系统管理员', '/files/bussiness/user_avatar/1766276383386.jpg', '13900139001', 1, '2026-01-11 14:53:47', 'admin', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'admin@gym.com', 1, '2025-10-17 09:19:31', '2025-12-21 08:19:44');
INSERT INTO `user` VALUES (2, 'ADMIN', '健身房经理', '/files/bussiness/user_avatar/1766276383386.jpg', '13900139002', 0, NULL, 'manager', '$2a$10$lqnSaaWggBoGo5BftUKuX.hmQzk3yNF4UqC5H47AeySo5KOEg6MEC', 'manager@gym.com', 1, '2025-10-17 09:19:31', '2025-12-21 15:11:49');
INSERT INTO `user` VALUES (3, 'USER', '小明', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888888', 2, '2025-12-15 10:00:00', NULL, NULL, NULL, 1, '2025-12-05 15:21:02', '2025-12-21 15:13:26');
INSERT INTO `user` VALUES (4, 'COACH', '李教练', '/files/bussiness/user_avatar/1759645666149.jpg', '13900139004', 0, NULL, 'coach_li', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'coach_li@gym.com', 1, '2025-10-17 09:19:31', '2025-12-21 15:13:33');
INSERT INTO `user` VALUES (5, 'COACH', '王教练', '/files/bussiness/user_avatar/1759645666149.jpg', '13900139005', 0, NULL, 'coach_wang', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'coach_wang@gym.com', 1, '2025-10-17 09:19:31', '2025-12-21 15:13:38');
INSERT INTO `user` VALUES (6, 'COACH', '张教练', '/files/bussiness/user_avatar/1766276383386.jpg', '13900139006', 0, NULL, 'coach_zhang', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'coach_zhang@gym.com', 1, '2025-10-17 09:19:31', '2025-12-21 15:11:55');
INSERT INTO `user` VALUES (7, 'USER', '小红', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888889', 1, '2026-12-31 23:59:59', NULL, NULL, NULL, 1, '2025-12-11 10:00:00', '2025-12-21 15:11:57');
INSERT INTO `user` VALUES (8, 'COACH', 'lw', '/files/bussiness/user_avatar/1759644698565.jpg', '19878726645', 0, NULL, 'lw1', '$2a$10$EhkcCnHsF4UGmGfFG8aXouy/PgQyFBDwwGCVD2WMyGfLFS1HhuANq', '22548944@qq.com', 1, '2025-12-11 20:43:55', '2025-12-21 15:13:47');
INSERT INTO `user` VALUES (9, 'USER', '李欣怡', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888890', 1, '2026-01-10 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:00');
INSERT INTO `user` VALUES (10, 'USER', '王晓婷', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888891', 1, '2026-02-15 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:13:48');
INSERT INTO `user` VALUES (11, 'USER', '张梦琪', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888892', 2, '2026-03-20 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:03');
INSERT INTO `user` VALUES (12, 'USER', '刘晓芳', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888893', 1, '2026-01-25 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:13:55');
INSERT INTO `user` VALUES (13, 'USER', '陈思彤', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888894', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:07');
INSERT INTO `user` VALUES (14, 'USER', '杨娜娜', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888895', 1, '2026-02-01 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:13:57');
INSERT INTO `user` VALUES (15, 'USER', '黄佳琪', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888896', 2, '2026-04-10 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:09');
INSERT INTO `user` VALUES (16, 'USER', '吴梦雨', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888897', 1, '2026-03-05 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:13:59');
INSERT INTO `user` VALUES (17, 'USER', '周欣悦', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888898', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:11');
INSERT INTO `user` VALUES (18, 'USER', '徐晓美', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888899', 1, '2026-01-15 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:01');
INSERT INTO `user` VALUES (19, 'USER', '孙思琦', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888800', 2, '2026-02-28 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:13');
INSERT INTO `user` VALUES (20, 'USER', '何佳怡', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888801', 1, '2026-03-12 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:03');
INSERT INTO `user` VALUES (21, 'USER', '郑欣妍', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888802', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:25');
INSERT INTO `user` VALUES (22, 'USER', '罗思语', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888803', 1, '2026-01-20 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:04');
INSERT INTO `user` VALUES (23, 'USER', '高小琳', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888804', 2, '2026-04-05 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:24');
INSERT INTO `user` VALUES (24, 'USER', '林晓晓', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888805', 1, '2026-02-20 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:23');
INSERT INTO `user` VALUES (25, 'USER', '邱思玥', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888806', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:26');
INSERT INTO `user` VALUES (26, 'USER', '曾梦雪', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888807', 1, '2026-03-15 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:07');
INSERT INTO `user` VALUES (27, 'USER', '傅思诺', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888808', 2, '2026-01-30 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:21');
INSERT INTO `user` VALUES (28, 'USER', '蒋欣妤', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888809', 1, '2026-02-10 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:28');
INSERT INTO `user` VALUES (29, 'USER', '谢晓嘉', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888810', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:10');
INSERT INTO `user` VALUES (30, 'USER', '韦思琪', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888811', 1, '2026-03-25 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:11');
INSERT INTO `user` VALUES (31, 'USER', '唐晓敏', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888812', 2, '2026-04-15 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:32');
INSERT INTO `user` VALUES (32, 'USER', '苏思欣', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888813', 1, '2026-01-08 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:18');
INSERT INTO `user` VALUES (33, 'USER', '许欣怡', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888814', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:34');
INSERT INTO `user` VALUES (34, 'USER', '冯思琳', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888815', 1, '2026-02-05 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:35');
INSERT INTO `user` VALUES (35, 'USER', '卢梦琪', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888816', 2, '2026-03-30 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:36');
INSERT INTO `user` VALUES (36, 'USER', '叶晓雪', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888817', 1, '2026-01-22 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:37');
INSERT INTO `user` VALUES (37, 'USER', '樊思语', '/files/bussiness/user_avatar/1759644698565.jpg', '18888888818', 0, NULL, NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:14:16');
INSERT INTO `user` VALUES (38, 'USER', '葛欣琳', '/files/bussiness/user_avatar/1766276383386.jpg', '18888888819', 1, '2026-04-01 00:00:00', NULL, NULL, NULL, 1, '2025-12-12 10:00:00', '2025-12-21 15:12:39');
INSERT INTO `user` VALUES (39, 'USER', 'pgl', '/files/bussiness/user_avatar/1766276383386.jpg', '19878726696', 0, NULL, 'pgl', '$2a$10$F79W5Dij2SDgsvNEF2YF.OeI5D4nZGYJa301exvp6iPcMc0Sp3XY.', '2254894294@qq.com', 1, '2025-12-19 19:32:19', '2025-12-21 15:55:19');

-- ----------------------------
-- View structure for view_user_workout_summary
-- ----------------------------
DROP VIEW IF EXISTS `view_user_workout_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `view_user_workout_summary` AS select `u`.`id` AS `user_id`,`u`.`nickname` AS `nickname`,count(distinct `wr`.`id`) AS `total_workouts`,coalesce(sum(`wr`.`duration`),0) AS `total_duration`,coalesce(sum(`wr`.`calories`),0) AS `total_calories`,coalesce(sum(`wr`.`distance`),0) AS `total_distance`,coalesce(avg(`wr`.`duration`),0) AS `avg_duration`,count(distinct `wr`.`workout_date`) AS `workout_days`,max(`wr`.`workout_date`) AS `last_workout_date` from (`user` `u` left join `gym_workout_record` `wr` on(((`u`.`id` = `wr`.`user_id`) and (`wr`.`is_completed` = 1)))) where (`u`.`user_type` = 'USER') group by `u`.`id`,`u`.`nickname`;

-- ----------------------------
-- Procedure structure for sp_calculate_workout_streak
-- ----------------------------
DROP PROCEDURE IF EXISTS `sp_calculate_workout_streak`;
delimiter ;;
CREATE PROCEDURE `sp_calculate_workout_streak`(IN p_user_id BIGINT, OUT p_streak_days INT)
BEGIN
  DECLARE v_last_date DATE;
  DECLARE v_current_date DATE;
  DECLARE v_streak INT DEFAULT 0;
  DECLARE done INT DEFAULT 0;
  
  DECLARE date_cursor CURSOR FOR
    SELECT DISTINCT workout_date
    FROM gym_workout_record
    WHERE user_id = p_user_id AND is_completed = 1
    ORDER BY workout_date DESC;
  
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
  
  OPEN date_cursor;
  
  FETCH date_cursor INTO v_current_date;
  SET v_last_date = v_current_date;
  SET v_streak = 1;
  
  read_loop: LOOP
    FETCH date_cursor INTO v_current_date;
    IF done THEN
      LEAVE read_loop;
    END IF;
    
    -- 检查是否连续（相差1天）
    IF DATEDIFF(v_last_date, v_current_date) = 1 THEN
      SET v_streak = v_streak + 1;
      SET v_last_date = v_current_date;
    ELSE
      LEAVE read_loop;
    END IF;
  END LOOP;
  
  CLOSE date_cursor;
  
  SET p_streak_days = v_streak;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table gym_workout_record
-- ----------------------------
DROP TRIGGER IF EXISTS `after_workout_record_insert`;
delimiter ;;
CREATE TRIGGER `after_workout_record_insert` AFTER INSERT ON `gym_workout_record` FOR EACH ROW BEGIN
  DECLARE v_category VARCHAR(20);
  
  -- 获取运动类型分类
  SELECT category INTO v_category 
  FROM gym_workout_type 
  WHERE id = NEW.workout_type_id;
  
  -- 插入或更新每日统计
  INSERT INTO gym_workout_daily_stats (
    user_id, stat_date, total_duration, total_calories, total_distance, 
    total_steps, workout_count, cardio_duration, strength_duration, flexibility_duration
  ) VALUES (
    NEW.user_id, NEW.workout_date, NEW.duration, NEW.calories, 
    IFNULL(NEW.distance, 0), IFNULL(NEW.steps, 0), 1,
    IF(v_category = 'CARDIO', NEW.duration, 0),
    IF(v_category = 'STRENGTH', NEW.duration, 0),
    IF(v_category = 'FLEXIBILITY', NEW.duration, 0)
  )
  ON DUPLICATE KEY UPDATE
    total_duration = total_duration + NEW.duration,
    total_calories = total_calories + NEW.calories,
    total_distance = total_distance + IFNULL(NEW.distance, 0),
    total_steps = total_steps + IFNULL(NEW.steps, 0),
    workout_count = workout_count + 1,
    cardio_duration = cardio_duration + IF(v_category = 'CARDIO', NEW.duration, 0),
    strength_duration = strength_duration + IF(v_category = 'STRENGTH', NEW.duration, 0),
    flexibility_duration = flexibility_duration + IF(v_category = 'FLEXIBILITY', NEW.duration, 0);
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table gym_workout_record
-- ----------------------------
DROP TRIGGER IF EXISTS `after_workout_record_update`;
delimiter ;;
CREATE TRIGGER `after_workout_record_update` AFTER UPDATE ON `gym_workout_record` FOR EACH ROW BEGIN
  DECLARE v_old_category VARCHAR(20);
  DECLARE v_new_category VARCHAR(20);
  
  -- 获取旧的运动类型分类
  SELECT category INTO v_old_category 
  FROM gym_workout_type 
  WHERE id = OLD.workout_type_id;
  
  -- 获取新的运动类型分类
  SELECT category INTO v_new_category 
  FROM gym_workout_type 
  WHERE id = NEW.workout_type_id;
  
  -- 先减去旧数据
  UPDATE gym_workout_daily_stats SET
    total_duration = total_duration - OLD.duration,
    total_calories = total_calories - OLD.calories,
    total_distance = total_distance - IFNULL(OLD.distance, 0),
    total_steps = total_steps - IFNULL(OLD.steps, 0),
    cardio_duration = cardio_duration - IF(v_old_category = 'CARDIO', OLD.duration, 0),
    strength_duration = strength_duration - IF(v_old_category = 'STRENGTH', OLD.duration, 0),
    flexibility_duration = flexibility_duration - IF(v_old_category = 'FLEXIBILITY', OLD.duration, 0)
  WHERE user_id = OLD.user_id AND stat_date = OLD.workout_date;
  
  -- 再加上新数据
  UPDATE gym_workout_daily_stats SET
    total_duration = total_duration + NEW.duration,
    total_calories = total_calories + NEW.calories,
    total_distance = total_distance + IFNULL(NEW.distance, 0),
    total_steps = total_steps + IFNULL(NEW.steps, 0),
    cardio_duration = cardio_duration + IF(v_new_category = 'CARDIO', NEW.duration, 0),
    strength_duration = strength_duration + IF(v_new_category = 'STRENGTH', NEW.duration, 0),
    flexibility_duration = flexibility_duration + IF(v_new_category = 'FLEXIBILITY', NEW.duration, 0)
  WHERE user_id = NEW.user_id AND stat_date = NEW.workout_date;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table gym_workout_record
-- ----------------------------
DROP TRIGGER IF EXISTS `after_workout_record_delete`;
delimiter ;;
CREATE TRIGGER `after_workout_record_delete` AFTER DELETE ON `gym_workout_record` FOR EACH ROW BEGIN
  DECLARE v_category VARCHAR(20);
  
  -- 获取运动类型分类
  SELECT category INTO v_category 
  FROM gym_workout_type 
  WHERE id = OLD.workout_type_id;
  
  -- 更新每日统计
  UPDATE gym_workout_daily_stats SET
    total_duration = total_duration - OLD.duration,
    total_calories = total_calories - OLD.calories,
    total_distance = total_distance - IFNULL(OLD.distance, 0),
    total_steps = total_steps - IFNULL(OLD.steps, 0),
    workout_count = workout_count - 1,
    cardio_duration = cardio_duration - IF(v_category = 'CARDIO', OLD.duration, 0),
    strength_duration = strength_duration - IF(v_category = 'STRENGTH', OLD.duration, 0),
    flexibility_duration = flexibility_duration - IF(v_category = 'FLEXIBILITY', OLD.duration, 0)
  WHERE user_id = OLD.user_id AND stat_date = OLD.workout_date;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
