-- ==========================================
-- 系统配置初始化数据
-- ==========================================
-- 说明：
-- 本脚本用于初始化系统配置数据，包括微信配置、文件上传配置和系统配置
-- ==========================================

USE gym;

-- ==========================================
-- 1. 微信配置
-- ==========================================

-- 小程序AppID
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.appid', 'wxdae24e45d0dcbaa6', '小程序AppID', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 小程序AppSecret
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.appsecret', '', '小程序AppSecret', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 商户号
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.pay.mchid', '', '微信支付商户号', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- API V3密钥
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.pay.apiv3key', '', 'API V3密钥', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 支付回调地址
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.pay.notifyurl', 'https://yourdomain.com/api/pay/notify', '支付回调地址', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 商户私钥路径
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('wechat.pay.privatekeypath', 'classpath:apiclient_key.pem', '商户私钥路径', '微信配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- ==========================================
-- 2. 文件上传配置
-- ==========================================

-- 文件存储路径
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.upload.path', './files', '文件存储路径', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 访问路径前缀
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.access.prefix', '/files', '访问路径前缀', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 最大文件大小(MB)
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.upload.maxsize', '10', '最大文件大小(MB)', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 允许的文件类型
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.upload.allowedtypes', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许的文件类型', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 图片压缩
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.image.compress', 'true', '图片压缩', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 图片质量
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('file.image.quality', '80', '图片质量(10-100)', '文件配置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- ==========================================
-- 3. 系统配置
-- ==========================================

-- 系统名称
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('system.name', '健身房预约管理系统', '系统名称', '系统设置', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 系统版本
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('system.version', '1.0.0', '系统版本', '系统设置', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 系统描述
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('system.description', '基于Spring Boot + Vue3 + UniApp的健身房综合管理系统', '系统描述', '系统设置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 会话超时时间(分钟)
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('system.session.timeout', '120', '会话超时时间(分钟)', '系统设置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- 密码复杂度检查
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `config_group`, `is_system`, `status`, `create_time`, `update_time`)
VALUES ('system.password.check', 'false', '密码复杂度检查', '系统设置', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `description` = VALUES(`description`),
  `config_group` = VALUES(`config_group`),
  `update_time` = NOW();

-- ==========================================
-- 验证插入结果
-- ==========================================
SELECT 
    config_key AS '配置键',
    config_value AS '配置值',
    description AS '描述',
    config_group AS '配置分组',
    CASE 
        WHEN is_system = 1 THEN '系统内置'
        ELSE '自定义'
    END AS '类型',
    CASE 
        WHEN status = 1 THEN '启用'
        ELSE '禁用'
    END AS '状态'
FROM `sys_config`
WHERE config_group IN ('微信配置', '文件配置', '系统设置')
ORDER BY config_group, id;

-- ==========================================
-- 执行完成！
-- ==========================================
