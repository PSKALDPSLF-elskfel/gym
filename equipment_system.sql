/*
 Equipment Management System Database Script
 健身房器材管理系统数据库脚本
 
 Date: 2025-12-11
 Version: 1.0
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gym_equipment
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment`;
CREATE TABLE `gym_equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '器材名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE COMMENT '器材编号',
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

SET FOREIGN_KEY_CHECKS = 1;
