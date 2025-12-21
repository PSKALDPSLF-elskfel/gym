-- ==========================================
-- 通知管理测试数据检查和插入脚本
-- ==========================================

-- 1. 首先检查是否有通知数据
SELECT '=== 检查通知表数据 ===' AS info;
SELECT COUNT(*) AS notification_count FROM sys_notification;

-- 2. 如果没有数据，插入测试数据
-- 清空现有测试数据（可选，谨慎使用）
-- DELETE FROM sys_notification_read;
-- DELETE FROM sys_notification;

-- 3. 插入管理端通知测试数据
INSERT INTO `sys_notification` 
(`notification_type`, `title`, `content`, `target_user_type`, `target_user_id`, 
 `related_id`, `related_type`, `icon`, `priority`, `status`, `send_time`, 
 `is_read_required`, `remark`) 
VALUES 
-- 系统维护通知（群发给所有用户）
('SYSTEM', '系统维护通知', '本系统将于本周日凌晨2:00-4:00进行系统维护，届时将无法访问，请提前安排好您的预约计划。', 
 'ALL', NULL, NULL, NULL, '/images/system.png', 2, 1, NOW(), 1, '系统通知测试'),

-- 课程预约提醒
('BOOKING', '课程预约提醒', '您预约的课程即将开始，请提前15分钟到达健身房。', 
 'USER', NULL, NULL, 'COURSE_BOOKING', '/images/booking.png', 1, 1, NOW(), 1, '预约提醒'),

-- 会员到期提醒
('MEMBERSHIP', '会员即将到期', '您的会员将于3天后到期，请及时续费以继续享受会员权益。', 
 'USER', NULL, NULL, 'MEMBERSHIP', '/images/vip.png', 1, 1, NOW(), 1, '会员提醒'),

-- 器材维护通知
('EQUIPMENT', '器材维护通知', '部分健身器材将于今日下午进行维护保养，预计维护时间2小时，给您带来不便敬请谅解。', 
 'ALL', NULL, NULL, 'EQUIPMENT', '/images/equipment.png', 0, 1, NOW(), 1, '器材维护'),

-- 课程时间变更通知
('COURSE', '课程时间变更', '您预约的课程时间已变更，请注意查看最新时间安排。', 
 'USER', NULL, NULL, 'COURSE_SCHEDULE', '/images/course.png', 2, 1, NOW(), 1, '课程通知'),

-- 新年优惠活动
('SYSTEM', '新年优惠活动', '新年特惠！所有会员套餐8折优惠，活动时间有限，欢迎咨询。', 
 'ALL', NULL, NULL, NULL, '/images/activity.png', 1, 1, NOW(), 1, '营销活动'),

-- 教练请假通知
('COURSE', '教练请假通知', '由于教练临时请假，您预约的课程已取消，费用将原路退回。', 
 'COACH', NULL, NULL, 'COURSE', '/images/coach.png', 2, 1, NOW(), 1, '教练通知'),

-- 管理员系统升级通知
('SYSTEM', '系统功能升级', '系统已升级至新版本，新增多项功能，欢迎体验！', 
 'ADMIN', NULL, NULL, NULL, '/images/upgrade.png', 1, 1, NOW(), 1, '管理员通知'),

-- 草稿通知（未发送）
('SYSTEM', '春节放假通知', '春节期间健身房营业时间调整，具体时间请关注公告。', 
 'ALL', NULL, NULL, NULL, '/images/holiday.png', 1, 2, NULL, 1, '草稿-待发送'),

-- 定时发送通知
('MEMBERSHIP', '会员优惠提醒', '本月办理年卡可享受特别优惠，欢迎咨询前台。', 
 'ALL', NULL, NULL, 'MEMBERSHIP', '/images/promotion.png', 1, 1, NOW(), 1, '优惠活动');

-- 4. 验证插入结果
SELECT '=== 插入后的通知数据 ===' AS info;
SELECT 
    id,
    notification_type AS '通知类型',
    title AS '标题',
    target_user_type AS '目标用户',
    priority AS '优先级',
    status AS '状态',
    send_time AS '发送时间',
    create_time AS '创建时间'
FROM `sys_notification` 
ORDER BY create_time DESC
LIMIT 20;

-- 5. 统计各类型通知数量
SELECT '=== 通知类型统计 ===' AS info;
SELECT 
    notification_type AS '通知类型',
    COUNT(*) AS '数量',
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS '已发送',
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS '草稿'
FROM `sys_notification`
GROUP BY notification_type;

-- 6. 统计目标用户类型
SELECT '=== 目标用户类型统计 ===' AS info;
SELECT 
    target_user_type AS '目标用户类型',
    COUNT(*) AS '数量'
FROM `sys_notification`
GROUP BY target_user_type;
