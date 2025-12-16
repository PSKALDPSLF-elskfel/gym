# 系统通知API测试文档

## 测试环境
- 基础URL: `http://localhost:8080`
- 认证方式: JWT Bearer Token

## 前置准备

### 1. 获取管理员Token
```http
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

响应示例:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "userType": "ADMIN"
  }
}
```

### 2. 获取普通用户Token
```http
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "username": "user1",
  "password": "123456"
}
```

---

## 管理端接口测试

### 1. 发送系统通知（群发所有用户）

**请求:**
```http
POST http://localhost:8080/api/notifications/send
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "notificationType": "SYSTEM",
  "title": "系统维护通知",
  "content": "本系统将于今晚22:00-23:00进行系统升级维护，届时将无法访问，请提前安排。",
  "targetUserType": "ALL",
  "priority": 2,
  "icon": "/images/system.png",
  "isReadRequired": 1,
  "remark": "API测试"
}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

**验证:**
- ✅ 返回通知ID
- ✅ 数据库sys_notification表新增一条记录
- ✅ 为所有活跃用户创建sys_notification_read记录

---

### 2. 发送单用户通知

**请求:**
```http
POST http://localhost:8080/api/notifications/send
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "notificationType": "BOOKING",
  "title": "课程预约成功",
  "content": "您已成功预约瑜伽基础课，上课时间：2025-12-15 15:00-16:00",
  "targetUserType": "USER",
  "targetUserId": 1,
  "relatedId": "25",
  "relatedType": "COURSE_BOOKING",
  "priority": 1,
  "icon": "/images/booking.png",
  "isReadRequired": 1
}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 2
}
```

**验证:**
- ✅ 只为用户ID=1创建读取记录
- ✅ 其他用户查询不到该通知

---

### 3. 定时发送通知

**请求:**
```http
POST http://localhost:8080/api/notifications/send
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "notificationType": "MEMBERSHIP",
  "title": "会员优惠活动",
  "content": "新年特惠！所有会员套餐8折优惠，活动时间：2025-12-20至2025-12-31。",
  "targetUserType": "USER",
  "scheduledTime": "2025-12-19T09:00:00",
  "priority": 1,
  "isReadRequired": 1
}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 3
}
```

**验证:**
- ✅ 通知状态为2（草稿）
- ✅ scheduled_time字段已设置
- ✅ 不会立即创建读取记录

---

### 4. 批量发送通知

**请求:**
```http
POST http://localhost:8080/api/notifications/send/batch?userIds=1,2,3
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "notificationType": "EQUIPMENT",
  "title": "器材维护通知",
  "content": "跑步机A3将于今日下午进行维护，暂时无法使用。",
  "targetUserType": "USER",
  "priority": 0,
  "isReadRequired": 1
}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**验证:**
- ✅ 为用户1、2、3各创建一条通知记录
- ✅ 每条通知都有对应的读取记录

---

### 5. 查询所有通知（管理端）

**请求:**
```http
GET http://localhost:8080/api/notifications/admin/list?current=1&size=10&notificationType=SYSTEM&status=1
Authorization: Bearer {admin_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "notificationType": "SYSTEM",
        "notificationTypeDesc": "系统通知",
        "title": "系统维护通知",
        "content": "本系统将于今晚22:00-23:00进行系统升级维护...",
        "targetUserType": "ALL",
        "priority": 2,
        "priorityDesc": "高",
        "status": 1,
        "statusDesc": "已发送",
        "sendTime": "2025-12-11T10:00:00",
        "createTime": "2025-12-11T10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  }
}
```

**验证:**
- ✅ 支持分页
- ✅ 支持按类型筛选
- ✅ 支持按状态筛选

---

### 6. 删除通知（管理端）

**请求:**
```http
DELETE http://localhost:8080/api/notifications/admin/1
Authorization: Bearer {admin_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**验证:**
- ✅ 通知记录被物理删除
- ✅ 相关的读取记录也被删除

---

## 用户端接口测试

### 7. 获取我的通知列表

**请求:**
```http
GET http://localhost:8080/api/notifications/my?current=1&size=10&isRead=0
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 2,
        "notificationType": "BOOKING",
        "notificationTypeDesc": "预约提醒",
        "title": "课程预约成功",
        "content": "您已成功预约瑜伽基础课...",
        "relatedId": "25",
        "relatedType": "COURSE_BOOKING",
        "priority": 1,
        "priorityDesc": "中",
        "sendTime": "2025-12-11T10:05:00",
        "isRead": false,
        "readTime": null
      }
    ],
    "total": 5,
    "current": 1,
    "size": 10
  }
}
```

**验证:**
- ✅ 只显示当前用户的通知
- ✅ isRead=0时只显示未读通知
- ✅ isRead=1时只显示已读通知
- ✅ 不传isRead参数时显示所有通知

---

### 8. 获取未读通知数量

**请求:**
```http
GET http://localhost:8080/api/notifications/unread/count
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 4
}
```

**验证:**
- ✅ 返回未读通知的准确数量
- ✅ 可用于显示角标

---

### 9. 标记通知为已读

**请求:**
```http
PUT http://localhost:8080/api/notifications/2/read
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**验证:**
- ✅ sys_notification_read表的is_read更新为1
- ✅ read_time字段更新为当前时间
- ✅ 未读数量减1

---

### 10. 全部标记为已读

**请求:**
```http
PUT http://localhost:8080/api/notifications/read/all
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**验证:**
- ✅ 当前用户所有未读通知标记为已读
- ✅ 未读数量变为0

---

### 11. 删除通知

**请求:**
```http
DELETE http://localhost:8080/api/notifications/2
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**验证:**
- ✅ 该用户的读取记录被删除
- ✅ 通知本身不删除（其他用户仍可见）
- ✅ 再次查询时该通知不出现在列表中

---

## 错误场景测试

### 12. 无效的通知类型

**请求:**
```http
POST http://localhost:8080/api/notifications/send
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "notificationType": "INVALID_TYPE",
  "title": "测试",
  "content": "测试内容"
}
```

**预期响应:**
```json
{
  "code": 400,
  "message": "无效的通知类型",
  "data": null
}
```

---

### 13. 未授权访问

**请求:**
```http
GET http://localhost:8080/api/notifications/my?current=1&size=10
```

**预期响应:**
```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

---

### 14. 标记不存在的通知

**请求:**
```http
PUT http://localhost:8080/api/notifications/99999/read
Authorization: Bearer {user_token}
```

**预期响应:**
```json
{
  "code": 400,
  "message": "通知读取记录不存在",
  "data": null
}
```

---

## 性能测试场景

### 15. 批量发送1000个用户

**测试步骤:**
1. 准备1000个用户ID列表
2. 调用批量发送接口
3. 记录响应时间
4. 验证数据库记录数量

**预期结果:**
- ✅ 响应时间 < 5秒
- ✅ 所有用户都收到通知
- ✅ 无数据丢失

---

### 16. 并发查询通知列表

**测试步骤:**
1. 使用JMeter或类似工具
2. 100个并发用户同时查询通知列表
3. 记录响应时间和成功率

**预期结果:**
- ✅ 成功率 100%
- ✅ 平均响应时间 < 500ms
- ✅ 无数据错乱

---

## 集成测试场景

### 17. 完整业务流程测试

**场景:** 用户预约课程后收到通知

1. 用户登录
2. 预约课程（触发通知发送）
3. 查询我的通知列表（应该看到预约成功通知）
4. 查看未读数量（应该增加1）
5. 点击通知查看详情（标记为已读）
6. 再次查询未读数量（应该减少1）

---

## 数据验证SQL

### 查询通知发送统计
```sql
SELECT 
    notification_type,
    COUNT(*) as total,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as sent,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as draft
FROM sys_notification
GROUP BY notification_type;
```

### 查询用户通知接收情况
```sql
SELECT 
    u.id as user_id,
    u.nickname,
    COUNT(nr.id) as total_notifications,
    SUM(CASE WHEN nr.is_read = 1 THEN 1 ELSE 0 END) as read_count,
    SUM(CASE WHEN nr.is_read = 0 THEN 1 ELSE 0 END) as unread_count
FROM user u
LEFT JOIN sys_notification_read nr ON u.id = nr.user_id
WHERE u.user_type = 'USER'
GROUP BY u.id, u.nickname
ORDER BY unread_count DESC;
```

### 查询通知打开率
```sql
SELECT 
    n.notification_type,
    COUNT(DISTINCT nr.notification_id) as sent_count,
    COUNT(DISTINCT CASE WHEN nr.is_read = 1 THEN nr.notification_id END) as read_count,
    ROUND(COUNT(DISTINCT CASE WHEN nr.is_read = 1 THEN nr.notification_id END) * 100.0 / 
          COUNT(DISTINCT nr.notification_id), 2) as open_rate
FROM sys_notification n
INNER JOIN sys_notification_read nr ON n.id = nr.notification_id
WHERE n.status = 1
GROUP BY n.notification_type;
```

---

## 注意事项

1. **Token有效期**: JWT Token通常有过期时间，测试时注意刷新Token
2. **数据清理**: 测试完成后清理测试数据，避免影响生产环境
3. **并发测试**: 进行并发测试时注意数据库连接池配置
4. **日志查看**: 测试失败时查看后端日志定位问题
5. **Swagger文档**: 启动后访问 `http://localhost:8080/swagger-ui.html` 查看完整API文档

---

**测试完成清单**

- [ ] 所有管理端接口测试通过
- [ ] 所有用户端接口测试通过
- [ ] 错误场景处理正确
- [ ] 性能测试达标
- [ ] 集成测试流程完整
- [ ] 数据验证SQL执行正常

**测试日期**: ___________  
**测试人**: ___________  
**测试环境**: ___________
