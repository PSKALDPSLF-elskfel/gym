-- ============================================
-- 训练计划明细表扩展 - 添加执行记录字段
-- 创建时间: 2025-12-11
-- 功能: 支持训练计划小程序端功能
-- ============================================

USE gym_booking_system;

-- 检查并添加执行备注字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'gym_booking_system' 
    AND TABLE_NAME = 'gym_training_plan_detail' 
    AND COLUMN_NAME = 'execution_note'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE `gym_training_plan_detail` ADD COLUMN `execution_note` TEXT COMMENT ''执行备注'' AFTER `complete_time`',
    'SELECT ''Column execution_note already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加实际完成组数字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'gym_booking_system' 
    AND TABLE_NAME = 'gym_training_plan_detail' 
    AND COLUMN_NAME = 'actual_sets'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE `gym_training_plan_detail` ADD COLUMN `actual_sets` INT COMMENT ''实际完成组数'' AFTER `execution_note`',
    'SELECT ''Column actual_sets already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加实际完成次数字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'gym_booking_system' 
    AND TABLE_NAME = 'gym_training_plan_detail' 
    AND COLUMN_NAME = 'actual_reps'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE `gym_training_plan_detail` ADD COLUMN `actual_reps` INT COMMENT ''实际完成次数'' AFTER `actual_sets`',
    'SELECT ''Column actual_reps already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示修改后的表结构
DESC gym_training_plan_detail;

SELECT '训练计划明细表扩展完成!' AS message;
