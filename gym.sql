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

 Date: 11/12/2025 16:30:12
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '体测数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_body_test
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach
-- ----------------------------

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
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440001', '瑜伽基础课', 1, NULL, '适合初学者的瑜伽课程，帮助放松身心，提高柔韧性', '/files/bussiness/course/1761619290737.png', 60, 20, 68.00, 1, '2025-10-17 09:19:31', '2025-10-28 10:41:32');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440002', '高强度间歇训练(HIIT)', 3, NULL, '高效燃脂训练，提高心肺功能和肌肉力量', '/images/hiit.jpg', 45, 15, 88.00, 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440003', '普拉提塑形', 4, NULL, '核心力量训练，改善体态，塑造完美身形', '/images/pilates.jpg', 50, 12, 78.00, 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440004', '动感单车', 5, NULL, '音乐节拍配合，燃烧卡路里，释放压力', '/images/spinning.jpg', 45, 25, 58.00, 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440005', '力量训练基础', 2, NULL, '器械使用指导，安全有效的力量训练', '/images/strength.jpg', 60, 10, 98.00, 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');
INSERT INTO `gym_course` VALUES ('550e8400-e29b-41d4-a716-446655440006', '拉伸放松课', 6, NULL, '运动后恢复，肌肉放松，预防运动损伤', '/images/stretching.jpg', 30, 15, 38.00, 1, '2025-10-17 09:19:31', '2025-10-17 09:19:31');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_booking
-- ----------------------------

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
INSERT INTO `gym_course_category` VALUES (1, '瑜伽课程', '各类瑜伽课程', '/images/category/yoga.png', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_course_category` VALUES (2, '力量训练', '器械和力量训练课程', '/images/category/strength.png', 2, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_course_category` VALUES (3, '有氧运动', '有氧健身课程', '/images/category/cardio.png', 3, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_course_category` VALUES (4, '普拉提', '普拉提塑形课程', '/images/category/pilates.png', 4, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_course_category` VALUES (5, '动感单车', '动感单车课程', '/images/category/spinning.png', 5, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `gym_course_category` VALUES (6, '拉伸放松', '拉伸恢复课程', '/images/category/stretching.png', 6, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程时间安排表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程签到表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_booking
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材维护记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_maintenance
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '器材排队表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_queue
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会员套餐表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan
-- ----------------------------

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
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_action_id`(`action_id` ASC) USING BTREE,
  CONSTRAINT `gym_training_plan_detail_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `gym_training_plan` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_training_plan_detail_ibfk_2` FOREIGN KEY (`action_id`) REFERENCES `gym_training_action` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_training_plan_detail
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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户会员记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_user_membership
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.name', '健身房预约管理系统', '系统名称', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (2, 'system.version', '1.0.0', '系统版本', '系统设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (3, 'booking.advance.days', '7', '可提前预约天数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (4, 'booking.cancel.hours', '2', '取消预约最少提前小时数', '预约设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (5, 'membership.gold.discount', '0.95', '黄金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');
INSERT INTO `sys_config` VALUES (6, 'membership.platinum.discount', '0.90', '铂金会员折扣率', '会员设置', 1, 1, '2025-12-05 14:00:00', '2025-12-05 14:00:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表-精简版' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file_info
-- ----------------------------
INSERT INTO `sys_file_info` VALUES (36, 'logo.png', '/files/bussiness/course/1761618881517.png', 7804, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440001', 'cover_image', 0, '2025-10-28 10:34:42');
INSERT INTO `sys_file_info` VALUES (37, 'logo.png', '/files/bussiness/course/1761619290737.png', 7804, 'IMG', 'COURSE', '550e8400-e29b-41d4-a716-446655440001', 'cover_image', 0, '2025-10-28 10:41:31');
INSERT INTO `sys_file_info` VALUES (38, 'KccBwJzJCFk746d008584defb0e297c6f6819e149d8d.jpg', '/files/bussiness/user_avatar/1761634887253.jpg', 84624, 'IMG', 'USER_AVATAR', '1', 'avatar', 0, '2025-10-28 15:01:27');
INSERT INTO `sys_file_info` VALUES (39, '微信图片_20251015184850_31_2.jpg', '/files/bussiness/user_avatar/1761636869026.jpg', 84624, 'IMG', 'USER_AVATAR', '5', 'avatar', 0, '2025-10-28 15:34:29');
INSERT INTO `sys_file_info` VALUES (40, '微信图片_20230727231054.jpg', '/files/bussiness/user_avatar/1764919899218.jpg', 40703, 'IMG', 'USER_AVATAR', '5', 'avatar', 0, '2025-12-05 15:31:39');

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统一用户表（包含小程序用户和管理员）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ADMIN', '系统管理员', '/files/bussiness/user_avatar/1764919899218.jpg', '13900139001', 0, NULL, 'admin', '$2a$10$0EB8hAzCT25cUhNeXDOhkujD./0TYjjiENnpirImJg4q4MeE/pKPa', 'admin@gym.com', 1, '2025-10-17 09:19:31', '2025-12-05 15:31:40');
INSERT INTO `user` VALUES (2, 'ADMIN', '健身房经理', NULL, '13900139002', 0, NULL, 'manager', '$10$lqnSaaWggBoGo5BftUKuX.hmQzk3yNF4UqC5H47AeySo5KOEg6MEC', 'manager@gym.com', 1, '2025-10-17 09:19:31', '2025-10-28 09:22:58');
INSERT INTO `user` VALUES (3, 'USER', NULL, NULL, '', 0, NULL, 'pgl', '$2a$10$TUbrMzc.bApp3K4aQzU25OFbcceKhIlboEBRc13638Wgxp9jVLefq', '2254894294@qq.com', 1, '2025-12-05 15:21:02', '2025-12-05 15:21:02');

SET FOREIGN_KEY_CHECKS = 1;
