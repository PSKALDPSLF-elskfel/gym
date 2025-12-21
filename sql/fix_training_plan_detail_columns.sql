-- ============================================
-- 训练计划明细表字段修复
-- 添加缺失的执行记录字段
-- ============================================

USE gym_booking_system;

-- 添加执行备注字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN IF NOT EXISTS `execution_note` TEXT COMMENT '执行备注' AFTER `complete_time`;

-- 添加实际完成组数字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN IF NOT EXISTS `actual_sets` INT COMMENT '实际完成组数' AFTER `execution_note`;

-- 添加实际完成次数字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN IF NOT EXISTS `actual_reps` INT COMMENT '实际完成次数' AFTER `actual_sets`;

-- 显示修改后的表结构
DESC gym_training_plan_detail;

SELECT '训练计划明细表字段修复完成!' AS message;
