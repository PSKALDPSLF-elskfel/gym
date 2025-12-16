# 课程签到功能 - API测试文档

## 📚 功能概述

课程签到模块提供了完整的课程签到功能，支持教练手动为学员签到。

## 🔧 主要功能

1. **学员签到** - 教练为预约学员进行签到
2. **查询签到信息** - 在预约详情中显示签到时间和方式
3. **防重复签到** - 自动校验防止重复签到

## 📋 API接口

### 1. 学员签到

**接口地址:** `POST /course-schedule/{scheduleId}/sign-in`

**请求头:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数:**
- `scheduleId`: 排课ID

**请求体示例:**
```json
{
  "bookingId": 1,
  "signInType": 2
}
```

**字段说明:**
- `bookingId`: 预约ID - 必填
- `signInType`: 签到方式 - 可选，1-扫码，2-手动，默认2

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

**错误响应示例:**
```json
{
  "code": 400,
  "msg": "该学员已签到，请勿重复签到",
  "data": null
}
```

**可能的错误信息:**
- "预约不存在"
- "预约与排课不匹配"
- "预约已取消，无法签到"
- "预约已完成"
- "该学员已签到，请勿重复签到"
- "排课不存在"

---

### 2. 查询预约列表（包含签到信息）

**接口地址:** `GET /booking/schedule/{scheduleId}`

**请求头:**
```
Authorization: Bearer {token}
```

**路径参数:**
- `scheduleId`: 排课ID

**成功响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "userNickname": "张三",
      "userPhone": "13800138000",
      "userAvatar": "/avatar/user1.jpg",
      "scheduleId": 5,
      "courseName": "瑜伽基础课",
      "bookingTime": "2025-12-10T10:30:00",
      "status": 1,
      "statusDisplayName": "已预约",
      "checkInTime": "2025-12-12T09:00:00",
      "signInType": 2,
      "signInTypeDisplayName": "手动签到"
    },
    {
      "id": 2,
      "userId": 3,
      "userNickname": "李四",
      "userPhone": "13900139000",
      "scheduleId": 5,
      "courseName": "瑜伽基础课",
      "bookingTime": "2025-12-10T11:00:00",
      "status": 1,
      "statusDisplayName": "已预约",
      "checkInTime": null,
      "signInType": null,
      "signInTypeDisplayName": null
    }
  ]
}
```

**字段说明:**
- `checkInTime`: 签到时间，未签到时为null
- `signInType`: 签到方式，未签到时为null
- `signInTypeDisplayName`: 签到方式显示名称，未签到时为null

---

## 🧪 测试步骤

### 准备工作

1. **启动后端服务**
```bash
cd D:\健身房预约小程序\springboot
mvn spring-boot:run
```

2. **获取Token**
使用教练账号登录获取token：
```
POST /user/login
{
  "username": "coach1",
  "password": "password"
}
```

### 测试用例

#### 测试1: 为学员签到（成功）

**前提条件:**
- 确保有一个有效的排课
- 学员已预约该排课
- 学员未签到

**请求:**
```bash
curl -X POST http://localhost:9090/course-schedule/5/sign-in \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "signInType": 2
  }'
```

**预期结果:**
- 返回成功响应
- 数据库 `gym_course_sign_in` 表中新增一条记录
- 再次查询预约列表，该预约的 `checkInTime` 不为空

---

#### 测试2: 重复签到（失败）

**前提条件:**
- 学员已完成签到（执行测试1后）

**请求:**
```bash
curl -X POST http://localhost:9090/course-schedule/5/sign-in \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "signInType": 2
  }'
```

**预期结果:**
- 返回错误信息："该学员已签到，请勿重复签到"
- 数据库无新增记录

---

#### 测试3: 查询包含签到信息的预约列表

**请求:**
```bash
curl -X GET http://localhost:9090/booking/schedule/5 \
  -H "Authorization: Bearer {your_token}"
```

**预期结果:**
- 返回预约列表
- 已签到的预约包含 `checkInTime`、`signInType`、`signInTypeDisplayName` 字段
- 未签到的预约这些字段为null

---

#### 测试4: 为已取消预约签到（失败）

**前提条件:**
- 学员已取消预约

**请求:**
```bash
curl -X POST http://localhost:9090/course-schedule/5/sign-in \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 2,
    "signInType": 2
  }'
```

**预期结果:**
- 返回错误信息："预约已取消，无法签到"

---

#### 测试5: 预约ID与排课ID不匹配（失败）

**请求:**
```bash
curl -X POST http://localhost:9090/course-schedule/999/sign-in \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "signInType": 2
  }'
```

**预期结果:**
- 返回错误信息："预约与排课不匹配" 或 "排课不存在"

---

## 🔍 验证点

### 业务逻辑验证

1. **签到校验**
   - ✅ 预约必须存在
   - ✅ 预约必须属于指定的排课
   - ✅ 预约状态必须为"已预约"
   - ✅ 不能重复签到
   - ✅ 已取消的预约不能签到
   - ✅ 已完成的预约不能签到

2. **签到记录**
   - ✅ 记录签到时间
   - ✅ 记录签到方式（扫码/手动）
   - ✅ 记录操作人ID（教练/管理员）
   - ✅ 记录用户ID和排课ID

3. **数据完整性**
   - ✅ 查询预约列表时包含签到信息
   - ✅ 未签到的预约签到字段为null
   - ✅ 已签到的预约包含完整签到信息

---

## 📊 数据库表结构

### gym_course_sign_in 表

```sql
CREATE TABLE `gym_course_sign_in` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `booking_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `schedule_id` bigint NOT NULL COMMENT '排课ID',
  `sign_in_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `sign_in_type` tinyint NULL DEFAULT 1 COMMENT '签到方式：1-扫码，2-手动',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID(教练/管理员)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_booking_id` (`booking_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_sign_in_time` (`sign_in_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程签到表';
```

---

## 📝 开发总结

### 已完成功能

✅ **实体层** - `GymCourseSignIn` 实体类  
✅ **Mapper层** - `GymCourseSignInMapper`  
✅ **DTO层** - `CourseSignInDTO` 命令DTO  
✅ **响应DTO** - `CourseBookingDetailDTO` 添加签到字段  
✅ **Service层** - `CourseSignInService` 完整签到业务逻辑  
✅ **Controller层** - 签到接口  
✅ **数据关联** - 预约查询自动关联签到信息  

### 技术亮点

1. **完整的业务校验** - 多层次校验确保数据准确性
2. **防重复签到** - 自动检测避免重复操作
3. **批量查询优化** - 在查询预约列表时批量获取签到信息
4. **操作人记录** - 记录是哪个教练/管理员进行的签到操作
5. **灵活的签到方式** - 支持扫码和手动两种方式

### 与前端对接

前端已实现的签到页面需要的接口已全部就绪：

1. **GET /booking/schedule/{scheduleId}** - 获取预约列表（含签到信息）
2. **POST /course-schedule/{scheduleId}/sign-in** - 执行签到操作

前端调用示例（参考 `vue3/coach/src/api/course.js`）：
```javascript
// 已存在，无需修改
export const signInStudent = (scheduleId, bookingId) => {
  return request.post(`/course-schedule/${scheduleId}/sign-in`, { bookingId })
}
```

---

## ✅ 测试清单

- [ ] 正常签到
- [ ] 重复签到验证
- [ ] 已取消预约签到验证
- [ ] 预约与排课不匹配验证
- [ ] 查询预约列表包含签到信息
- [ ] 签到记录正确保存到数据库
- [ ] 操作人ID正确记录
- [ ] 签到时间自动生成
- [ ] 签到方式正确保存
- [ ] 批量查询性能测试

---

## 📱 Swagger API文档

启动后端服务后，可以访问在线API文档：

```
http://localhost:9090/doc.html
```

在文档中可以：
- ✅ 查看签到接口的详细说明
- ✅ 在线测试签到API
- ✅ 查看请求/响应示例

---

**最后更新**: 2025-12-12  
**文档版本**: v1.0
