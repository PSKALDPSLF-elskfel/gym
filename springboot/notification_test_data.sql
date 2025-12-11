-- 系统通知功能测试数据
-- 执行前请确保sys_notification和sys_notification_read表已创建

-- 清空测试数据（谨慎使用）
-- TRUNCATE TABLE sys_notification_read;
-- TRUNCATE TABLE sys_notification;

-- 1. 系统维护通知（群发给所有用户）
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`, `remark`) 
VALUES 
('SYSTEM', '系统维护通知', '本系统将于本周日凌晨2:00-4:00进行系统维护，届时将无法访问，请提前安排好您的预约计划。', 
 'ALL', NULL, NULL, NULL, '/images/system.png', 2, 1, NOW(), 1, '系统通知测试');

-- 2. 课程预约成功通知（发送给用户ID为1的用户）
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`) 
VALUES 
('BOOKING', '课程预约成功', '您已成功预约瑜伽基础课，上课时间：2025-12-15 15:00-16:00，请提前到达。', 
 'USER', 1, '25', 'COURSE_BOOKING', '/images/booking.png', 1, 1, NOW(), 1);

-- 3. 会员即将到期提醒
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`) 
VALUES 
('MEMBERSHIP', '会员即将到期', '您的黄金会员将于3天后到期，请及时续费以继续享受会员权益。', 
 'USER', 1, NULL, 'MEMBERSHIP', '/images/vip.png', 1, 1, NOW(), 1);

-- 4. 器材维护通知（发送给所有普通用户）
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`) 
VALUES 
('EQUIPMENT', '器材维护通知', '跑步机A3将于今日下午进行维护保养，预计维护时间2小时，给您带来不便敬请谅解。', 
 'USER', NULL, '7', 'EQUIPMENT', '/images/equipment.png', 0, 1, NOW(), 1);

-- 5. 课程时间变更通知
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`) 
VALUES 
('COURSE', '课程时间变更', '您预约的瑜伽基础课时间已变更为2025-12-16 14:00-15:00，请注意时间变化。', 
 'USER', 1, '25', 'COURSE_SCHEDULE', '/images/course.png', 2, 1, NOW(), 1);

-- 6. 草稿通知（定时发送）
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `scheduled_time`, 
 `is_read_required`, `remark`) 
VALUES 
('SYSTEM', '新年优惠活动', '新年特惠！所有会员套餐8折优惠，活动时间：2025-12-20至2025-12-31。', 
 'ALL', NULL, NULL, NULL, '/images/activity.png', 1, 2, '2025-12-19 09:00:00', 
 1, '定时发送测试');

-- 创建通知读取记录（为用户ID为1的用户创建）
-- 注意：这里假设sys_notification表的自增ID从1开始，实际使用时需要根据插入后的ID调整

-- 为用户1创建所有通知的读取记录（模拟通知已发送给该用户）
INSERT INTO `sys_notification_read` 
(`notification_id`, `user_id`, `read_time`, `is_read`) 
SELECT id, 1, NULL, 0 
FROM `sys_notification` 
WHERE `status` = 1 AND (`target_user_id` = 1 OR `target_user_id` IS NULL);

-- 标记第一条通知为已读（系统维护通知）
UPDATE `sys_notification_read` 
SET `is_read` = 1, `read_time` = DATE_SUB(NOW(), INTERVAL 1 HOUR)
WHERE `notification_id` = (SELECT MIN(id) FROM `sys_notification` WHERE `notification_type` = 'SYSTEM')
AND `user_id` = 1;

-- 查询验证数据
SELECT 
    n.id,
    n.notification_type,
    n.title,
    n.target_user_type,
    n.target_user_id,
    n.priority,
    n.status,
    n.send_time,
    COUNT(nr.id) as read_record_count,
    SUM(CASE WHEN nr.is_read = 1 THEN 1 ELSE 0 END) as read_count,
    SUM(CASE WHEN nr.is_read = 0 THEN 1 ELSE 0 END) as unread_count
FROM `sys_notification` n
LEFT JOIN `sys_notification_read` nr ON n.id = nr.notification_id
GROUP BY n.id
ORDER BY n.create_time DESC;

-- 查询用户1的未读通知数量
SELECT COUNT(*) as unread_count
FROM `sys_notification_read`
WHERE `user_id` = 1 AND `is_read` = 0;

-- 查询用户1的通知列表（含已读状态）
SELECT 
    n.id,
    n.notification_type,
    n.title,
    n.content,
    n.priority,
    n.send_time,
    nr.is_read,
    nr.read_time
FROM `sys_notification` n
INNER JOIN `sys_notification_read` nr ON n.id = nr.notification_id
WHERE nr.user_id = 1
ORDER BY n.send_time DESC;
