/*
 健身房教练排班管理系统数据库设计
 功能说明：
 - 教练排班表：记录教练的工作日期、时间和工作类型
 - 排班申请表：教练申请调休、加班、换班等
 - 排班记录表：教练实际工作的打卡记录
 - 排班规则表：系统配置的排班规则
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gym_coach_schedule
-- 教练排班表 - 记录教练计划的工作排班
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_schedule`;
CREATE TABLE `gym_coach_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `work_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NORMAL' COMMENT '工作类型：NORMAL-正常排班,OVERTIME-加班,SHIFT-轮班,HOLIDAY-休息',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作地点',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-已取消,1-正常,2-已完成',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者ID(管理员)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_coach_date_time` (`coach_id`, `work_date`, `start_time`, `end_time`) USING BTREE COMMENT '同一教练同一天同一时间段唯一',
  KEY `idx_coach_id` (`coach_id` ASC) USING BTREE,
  KEY `idx_work_date` (`work_date` ASC) USING BTREE,
  KEY `idx_work_type` (`work_type` ASC) USING BTREE,
  KEY `idx_status` (`status` ASC) USING BTREE,
  KEY `idx_coach_date` (`coach_id` ASC, `work_date` ASC) USING BTREE,
  CONSTRAINT `gym_coach_schedule_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gym_schedule_request
-- 排班申请表 - 教练提交的调休、加班、换班申请
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_request`;
CREATE TABLE `gym_schedule_request` (
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
  KEY `idx_coach_id` (`coach_id` ASC) USING BTREE,
  KEY `idx_request_type` (`request_type` ASC) USING BTREE,
  KEY `idx_status` (`status` ASC) USING BTREE,
  KEY `idx_target_date` (`target_date` ASC) USING BTREE,
  KEY `idx_coach_status` (`coach_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_request_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_request_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_request_ibfk_3` FOREIGN KEY (`exchange_with_coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gym_schedule_record
-- 排班打卡记录表 - 记录教练实际的工作打卡信息
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_record`;
CREATE TABLE `gym_schedule_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '打卡入场时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '打卡离场时间',
  `check_in_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入场地点',
  `check_out_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '离场地点',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '打卡状态：0-未打卡,1-已打卡入场,2-已打卡离场,3-迟到,4-早退,5-缺勤',
  `attendance_score` int DEFAULT 100 COMMENT '出勤评分(0-100)',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_schedule_id` (`schedule_id`) USING BTREE,
  KEY `idx_coach_id` (`coach_id` ASC) USING BTREE,
  KEY `idx_check_in_time` (`check_in_time` ASC) USING BTREE,
  KEY `idx_status` (`status` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_record_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `gym_coach_schedule` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_schedule_record_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gym_schedule_rule
-- 排班规则表 - 系统级的排班配置规则
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_rule`;
CREATE TABLE `gym_schedule_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则类型：WORK_TIME_LIMIT(工作时间限制),BREAK_TIME_RULE(休息规则),LATE_RULE(迟到规则)',
  `rule_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则值(JSON格式)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '规则说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_rule_type` (`rule_type`) USING BTREE,
  KEY `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gym_coach_leave
-- 教练请假表 - 记录教练的请假信息(年假、病假等)
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach_leave`;
CREATE TABLE `gym_coach_leave` (
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
  KEY `idx_coach_id` (`coach_id` ASC) USING BTREE,
  KEY `idx_leave_type` (`leave_type` ASC) USING BTREE,
  KEY `idx_status` (`status` ASC) USING BTREE,
  KEY `idx_date_range` (`start_date` ASC, `end_date` ASC) USING BTREE,
  CONSTRAINT `gym_coach_leave_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `gym_coach_leave_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练请假表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gym_schedule_statistics
-- 排班统计表 - 用于缓存和统计排班数据
-- ----------------------------
DROP TABLE IF EXISTS `gym_schedule_statistics`;
CREATE TABLE `gym_schedule_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `statistics_date` date NOT NULL COMMENT '统计日期(月份)',
  `total_hours` decimal(10, 2) DEFAULT 0.00 COMMENT '总工作时数',
  `normal_hours` decimal(10, 2) DEFAULT 0.00 COMMENT '正常工作时数',
  `overtime_hours` decimal(10, 2) DEFAULT 0.00 COMMENT '加班时数',
  `work_days` int DEFAULT 0 COMMENT '工作天数',
  `absent_days` int DEFAULT 0 COMMENT '缺勤天数',
  `late_days` int DEFAULT 0 COMMENT '迟到天数',
  `leave_days` int DEFAULT 0 COMMENT '请假天数',
  `average_attendance_score` int DEFAULT 100 COMMENT '平均出勤评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_coach_date` (`coach_id` ASC, `statistics_date` ASC) USING BTREE,
  KEY `idx_coach_id` (`coach_id` ASC) USING BTREE,
  CONSTRAINT `gym_schedule_statistics_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `gym_coach` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 初始化排班规则数据
-- ----------------------------
INSERT INTO `gym_schedule_rule` (`rule_name`, `rule_type`, `rule_value`, `description`, `status`) 
VALUES 
  ('正常工作时间', 'WORK_TIME_LIMIT', '{"max_hours_per_day":10,"max_hours_per_week":48}', '每天最多工作10小时，每周最多48小时', 1),
  ('休息规则', 'BREAK_TIME_RULE', '{"min_rest_hours":11,"max_work_days_continuous":6}', '工作日之间最少休息11小时，连续工作最多6天', 1),
  ('迟到规则', 'LATE_RULE', '{"late_threshold_minutes":15,"deduction_points":10}', '迟到超过15分钟扣10分', 1);

SET FOREIGN_KEY_CHECKS = 1;
