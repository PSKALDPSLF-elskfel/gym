-- 修复 gym_training_plan_detail 表缺失的字段
-- 添加 execution_note, actual_sets, actual_reps 字段

USE gym;

-- 添加执行备注字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN `execution_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执行备注' AFTER `complete_time`;

-- 添加实际完成组数字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN `actual_sets` int NULL DEFAULT NULL COMMENT '实际完成组数' AFTER `execution_note`;

-- 添加实际完成次数字段
ALTER TABLE `gym_training_plan_detail` 
ADD COLUMN `actual_reps` int NULL DEFAULT NULL COMMENT '实际完成次数' AFTER `actual_sets`;
