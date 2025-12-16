/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : gym

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 12/12/2025 16:00:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `tag_list` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价标签(JSON数组)，如["专业","耐心","认真负责"]',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_review
-- ----------------------------
-- 示例数据
INSERT INTO `gym_coach_review` VALUES (1, 3, 1, NULL, NULL, 2, 5, '[\"专业\",\"耐心\",\"认真负责\"]', '李教练非常专业，课程讲解很细致，动作示范标准，每次上课都能学到很多东西！', NULL, 0, '感谢您的认可，我会继续努力为大家提供更优质的教学服务！', '2025-12-06 10:30:00', 0, 1, '2025-12-05 18:20:00', '2025-12-06 10:30:00');
INSERT INTO `gym_coach_review` VALUES (2, 3, 2, NULL, NULL, 2, 5, '[\"效果显著\",\"幽默风趣\",\"激励性强\"]', '王教练的力量训练课程效果太棒了！跟着练了一个月，力量提升明显，而且教练很会调动气氛。', NULL, 0, NULL, NULL, 0, 1, '2025-12-07 15:45:00', '2025-12-07 15:45:00');
INSERT INTO `gym_coach_review` VALUES (3, 3, 3, NULL, NULL, 2, 4, '[\"活力四射\",\"音乐选得好\"]', '动感单车课太燃了！教练很有活力，音乐也选得很棒，每次上完课都大汗淋漓，减脂效果很好。', NULL, 0, NULL, NULL, 0, 1, '2025-12-08 20:10:00', '2025-12-08 20:10:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练评价统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach_review_statistics
-- ----------------------------
-- 示例统计数据
INSERT INTO `gym_coach_review_statistics` VALUES (1, 1, 1, 5.00, 1, 0, 0, 0, 0, 100.00, '2025-12-05 18:20:00', '2025-12-06 10:30:00');
INSERT INTO `gym_coach_review_statistics` VALUES (2, 2, 1, 5.00, 1, 0, 0, 0, 0, 0.00, '2025-12-07 15:45:00', '2025-12-07 15:45:00');
INSERT INTO `gym_coach_review_statistics` VALUES (3, 3, 1, 4.00, 0, 1, 0, 0, 0, 0.00, '2025-12-08 20:10:00', '2025-12-08 20:10:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_review_tag
-- ----------------------------
-- 正面标签
INSERT INTO `gym_review_tag` VALUES (1, '专业', 1, NULL, 1, 1, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (2, '耐心', 1, NULL, 2, 1, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (3, '认真负责', 1, NULL, 3, 1, 1, '2025-12-05 14:00:00', '2025-12-05 18:20:00');
INSERT INTO `gym_review_tag` VALUES (4, '效果显著', 1, NULL, 4, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (5, '幽默风趣', 1, NULL, 5, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (6, '激励性强', 1, NULL, 6, 1, 1, '2025-12-05 14:00:00', '2025-12-07 15:45:00');
INSERT INTO `gym_review_tag` VALUES (7, '活力四射', 1, NULL, 7, 1, 1, '2025-12-05 14:00:00', '2025-12-08 20:10:00');
INSERT INTO `gym_review_tag` VALUES (8, '音乐选得好', 1, NULL, 8, 1, 1, '2025-12-05 14:00:00', '2025-12-08 20:10:00');
INSERT INTO `gym_review_tag` VALUES (9, '动作标准', 1, NULL, 9, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (10, '讲解清晰', 1, NULL, 10, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (11, '体贴细心', 1, NULL, 11, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (12, '知识渊博', 1, NULL, 12, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

-- 负面标签
INSERT INTO `gym_review_tag` VALUES (13, '态度冷淡', 2, NULL, 50, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (14, '讲解不清', 2, NULL, 51, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (15, '经常迟到', 2, NULL, 52, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_review_tag` VALUES (16, '不够专业', 2, NULL, 53, 0, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

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

SET FOREIGN_KEY_CHECKS = 1;
