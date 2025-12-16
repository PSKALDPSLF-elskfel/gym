/*
 健身数据记录系统 - 数据库设计
 
 功能模块：
 1. 运动记录表 - 记录用户每次健身数据
 2. 运动类型表 - 预定义的运动类型分类
 3. 运动详情表 - 每次运动的详细动作记录
 4. 每日统计表 - 按日汇总的运动数据
 
 Date: 2025-12-12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_type - 预置常见运动类型
-- ----------------------------
INSERT INTO `gym_workout_type` VALUES (1, '跑步', 'CARDIO', '/images/workout/running.png', 8.00, '有氧运动，提升心肺功能', 1, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (2, '快走', 'CARDIO', '/images/workout/walking.png', 3.50, '低强度有氧运动', 2, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (3, '游泳', 'CARDIO', '/images/workout/swimming.png', 7.00, '全身有氧运动', 3, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (4, '骑行', 'CARDIO', '/images/workout/cycling.png', 6.00, '有氧耐力训练', 4, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (5, '椭圆机', 'CARDIO', '/images/workout/elliptical.png', 5.00, '低冲击有氧运动', 5, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (6, '动感单车', 'CARDIO', '/images/workout/spinning.png', 8.50, '高强度有氧训练', 6, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (7, '深蹲', 'STRENGTH', '/images/workout/squat.png', 5.00, '下肢力量训练', 7, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (8, '卧推', 'STRENGTH', '/images/workout/bench_press.png', 4.00, '胸部力量训练', 8, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (9, '硬拉', 'STRENGTH', '/images/workout/deadlift.png', 6.00, '全身力量训练', 9, 1, NOW(), NOW());
INSERT INTO `gym_workout_type` VALUES (10, '引体向上', 'STRENGTH', '/images/workout/pull_up.png', 4.50, '背部力量训练', 10, 1, NOW(), NOW());
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健身运动记录表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动详情表（力量训练动作明细）' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '每日运动数据统计表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '运动成就表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_workout_achievement - 预置成就
-- ----------------------------
INSERT INTO `gym_workout_achievement` VALUES (1, '初出茅庐', '完成第一次运动记录', '/images/achievement/first.png', 'FREQUENCY', 1, '次', 10, 1, 1, 1, NOW(), NOW());
INSERT INTO `gym_workout_achievement` VALUES (2, '持之以恒', '连续运动7天', '/images/achievement/streak7.png', 'STREAK', 7, '天', 50, 2, 2, 1, NOW(), NOW());
INSERT INTO `gym_workout_achievement` VALUES (3, '坚持不懈', '连续运动30天', '/images/achievement/streak30.png', 'STREAK', 30, '天', 200, 3, 3, 1, NOW(), NOW());
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户成就记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 视图：用户运动数据总览
-- ----------------------------
DROP VIEW IF EXISTS `view_user_workout_summary`;
CREATE VIEW `view_user_workout_summary` AS
SELECT 
  u.id AS user_id,
  u.nickname,
  COUNT(DISTINCT wr.id) AS total_workouts,
  COALESCE(SUM(wr.duration), 0) AS total_duration,
  COALESCE(SUM(wr.calories), 0) AS total_calories,
  COALESCE(SUM(wr.distance), 0) AS total_distance,
  COALESCE(AVG(wr.duration), 0) AS avg_duration,
  COUNT(DISTINCT wr.workout_date) AS workout_days,
  MAX(wr.workout_date) AS last_workout_date
FROM user u
LEFT JOIN gym_workout_record wr ON u.id = wr.user_id AND wr.is_completed = 1
WHERE u.user_type = 'USER'
GROUP BY u.id, u.nickname;

-- ----------------------------
-- 触发器：自动更新每日统计
-- ----------------------------
DELIMITER $$

DROP TRIGGER IF EXISTS `after_workout_record_insert`;
CREATE TRIGGER `after_workout_record_insert` AFTER INSERT ON `gym_workout_record`
FOR EACH ROW
BEGIN
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
END$$

DROP TRIGGER IF EXISTS `after_workout_record_update`;
CREATE TRIGGER `after_workout_record_update` AFTER UPDATE ON `gym_workout_record`
FOR EACH ROW
BEGIN
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
END$$

DROP TRIGGER IF EXISTS `after_workout_record_delete`;
CREATE TRIGGER `after_workout_record_delete` AFTER DELETE ON `gym_workout_record`
FOR EACH ROW
BEGIN
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
END$$

DELIMITER ;

-- ----------------------------
-- 存储过程：计算用户连续运动天数
-- ----------------------------
DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_calculate_workout_streak`;
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
END$$

DELIMITER ;

-- ----------------------------
-- 初始化测试数据（可选）
-- ----------------------------
-- 注意：实际使用时可删除或注释掉以下测试数据

-- 用户ID=3的测试数据
-- INSERT INTO `gym_workout_record` VALUES 
-- (1, 3, 1, '跑步', '2025-12-10', '2025-12-10 07:00:00', '2025-12-10 07:30:00', 30, 'MEDIUM', 250, 5.00, 6500, 145, 165, NULL, '早晨跑步，感觉不错', 'GOOD', '晴天', '健身房跑步机', 1, NULL, 'MANUAL', NOW(), NOW()),
-- (2, 3, 7, '深蹲', '2025-12-10', '2025-12-10 18:00:00', '2025-12-10 18:45:00', 45, 'HIGH', 180, NULL, NULL, 135, 158, NULL, '腿部训练日', 'TIRED', NULL, '健身房', 1, NULL, 'MANUAL', NOW(), NOW()),
-- (3, 3, 13, '瑜伽', '2025-12-11', '2025-12-11 19:00:00', '2025-12-11 20:00:00', 60, 'LOW', 120, NULL, NULL, 95, 110, NULL, '放松身心', 'GREAT', NULL, '瑜伽馆', 1, NULL, 'MANUAL', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
