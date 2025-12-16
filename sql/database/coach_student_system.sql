-- 教练学员关系表
CREATE TABLE IF NOT EXISTS `gym_coach_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员用户ID',
  `remark` text COMMENT '教练备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coach_student` (`coach_id`, `student_id`),
  KEY `idx_coach_id` (`coach_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练学员关系表';
