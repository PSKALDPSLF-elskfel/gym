# 学员管理功能 - API测试文档

## 📋 文档说明
- **功能模块**: 教练学员管理
- **开发日期**: 2025-12-11
- **API前缀**: `/api/coach/students`
- **权限要求**: 教练角色（COACH）

---

## 🔐 认证说明

所有接口都需要在请求头中携带JWT Token：
```
Authorization: Bearer {token}
```

---

## 📡 API接口列表

### 1. 查询我的学员列表（分页）

**接口**: `GET /api/coach/students/my`

**描述**: 教练查询自己的学员列表，支持搜索和筛选

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 搜索关键词（昵称或手机号） |
| memberType | Integer | 否 | 会员类型筛选（0-普通，1-黄金，2-铂金） |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 页大小，默认10 |

**请求示例**:
```bash
GET /api/coach/students/my?keyword=张三&memberType=1&pageNum=1&pageSize=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "userId": 10,
        "nickname": "张三",
        "avatar": "https://example.com/avatar.jpg",
        "phone": "13800138000",
        "memberType": 1,
        "memberTypeName": "黄金会员",
        "memberExpireTime": "2025-12-31T23:59:59",
        "isMemberValid": true,
        "coachRemark": "训练认真，进步明显",
        "trainingPlanCount": 3,
        "latestPlanName": "增肌训练计划V2",
        "latestTrainingTime": "2025-12-01T10:00:00",
        "createTime": "2025-10-01T09:00:00"
      }
    ],
    "total": 25,
    "size": 10,
    "current": 1,
    "pages": 3
  }
}
```

---

### 2. 获取学员详细信息

**接口**: `GET /api/coach/students/{userId}/detail`

**描述**: 查看学员的详细信息、体测记录和训练计划

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 学员用户ID |

**请求示例**:
```bash
GET /api/coach/students/10/detail
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 10,
    "nickname": "张三",
    "avatar": "https://example.com/avatar.jpg",
    "phone": "13800138000",
    "memberType": 1,
    "memberTypeName": "黄金会员",
    "memberExpireTime": "2025-12-31T23:59:59",
    "isMemberValid": true,
    "coachRemark": "训练认真，进步明显",
    "createTime": "2025-10-01T09:00:00",
    "latestBodyTest": {
      "id": 5,
      "userId": 10,
      "height": 175.0,
      "weight": 75.5,
      "bmi": 24.6,
      "bodyFat": 18.5,
      "muscleMass": 35.2,
      "visceralFat": 8,
      "basalMetabolism": 1680,
      "testTime": "2025-12-01T14:00:00",
      "testerId": 2,
      "remark": "体脂率下降2%",
      "createTime": "2025-12-01T14:05:00"
    },
    "bodyTestHistory": [
      {
        "id": 5,
        "testTime": "2025-12-01T14:00:00",
        "weight": 75.5,
        "bodyFat": 18.5
      },
      {
        "id": 4,
        "testTime": "2025-11-01T14:00:00",
        "weight": 77.0,
        "bodyFat": 20.5
      }
    ],
    "trainingPlans": [
      {
        "id": 15,
        "userId": 10,
        "coachId": 2,
        "name": "增肌训练计划V2",
        "goal": "增肌",
        "startDate": "2025-12-01",
        "endDate": "2026-01-31",
        "status": 1,
        "remark": "针对胸肌和背部",
        "createTime": "2025-12-01T10:00:00"
      }
    ],
    "totalTrainingPlans": 3,
    "activeTrainingPlans": 1
  }
}
```

---

### 3. 更新学员备注

**接口**: `PUT /api/coach/students/remark`

**描述**: 教练为学员添加或修改备注

**请求体**:
```json
{
  "userId": 10,
  "remark": "训练态度积极，建议增加力量训练强度"
}
```

**请求参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 学员用户ID |
| remark | String | 否 | 备注内容，最大500字符 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 4. 查询学员体测历史

**接口**: `GET /api/coach/students/{userId}/body-tests`

**描述**: 查看学员的所有体测记录

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 学员用户ID |

**请求示例**:
```bash
GET /api/coach/students/10/body-tests
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 5,
      "userId": 10,
      "height": 175.0,
      "weight": 75.5,
      "bmi": 24.6,
      "bodyFat": 18.5,
      "muscleMass": 35.2,
      "visceralFat": 8,
      "basalMetabolism": 1680,
      "testTime": "2025-12-01T14:00:00",
      "testerId": 2,
      "remark": "体脂率下降2%",
      "createTime": "2025-12-01T14:05:00"
    },
    {
      "id": 4,
      "userId": 10,
      "height": 175.0,
      "weight": 77.0,
      "bmi": 25.1,
      "bodyFat": 20.5,
      "muscleMass": 34.0,
      "visceralFat": 9,
      "basalMetabolism": 1650,
      "testTime": "2025-11-01T14:00:00",
      "testerId": 2,
      "remark": "初次体测",
      "createTime": "2025-11-01T14:05:00"
    }
  ]
}
```

---

### 5. 查询学员训练计划

**接口**: `GET /api/coach/students/{userId}/training-plans`

**描述**: 查看学员的所有训练计划

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 学员用户ID |

**请求示例**:
```bash
GET /api/coach/students/10/training-plans
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 15,
      "userId": 10,
      "coachId": 2,
      "name": "增肌训练计划V2",
      "goal": "增肌",
      "startDate": "2025-12-01",
      "endDate": "2026-01-31",
      "status": 1,
      "remark": "针对胸肌和背部",
      "createTime": "2025-12-01T10:00:00",
      "updateTime": "2025-12-01T10:00:00"
    },
    {
      "id": 12,
      "userId": 10,
      "coachId": 2,
      "name": "增肌训练计划V1",
      "goal": "增肌",
      "startDate": "2025-10-01",
      "endDate": "2025-11-30",
      "status": 0,
      "remark": "已完成",
      "createTime": "2025-10-01T09:00:00",
      "updateTime": "2025-12-01T00:00:00"
    }
  ]
}
```

---

## 🧪 测试步骤

### 前置条件
1. 确保数据库中已创建 `gym_coach_student` 表
2. 确保有教练用户（user_type=COACH）
3. 确保有学员数据和训练计划数据

### 测试用例

#### 测试用例1：查询学员列表
```bash
# 使用Postman或curl
curl -X GET "http://localhost:8080/api/coach/students/my?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer {your_token}"
```

**预期结果**:
- 返回该教练的学员列表
- 分页信息正确
- 学员信息完整

#### 测试用例2：搜索学员
```bash
curl -X GET "http://localhost:8080/api/coach/students/my?keyword=张三" \
  -H "Authorization: Bearer {your_token}"
```

**预期结果**:
- 返回昵称或手机号包含"张三"的学员

#### 测试用例3：筛选会员类型
```bash
curl -X GET "http://localhost:8080/api/coach/students/my?memberType=1" \
  -H "Authorization: Bearer {your_token}"
```

**预期结果**:
- 仅返回黄金会员

#### 测试用例4：查看学员详情
```bash
curl -X GET "http://localhost:8080/api/coach/students/10/detail" \
  -H "Authorization: Bearer {your_token}"
```

**预期结果**:
- 返回学员完整信息
- 包含最近体测数据
- 包含训练计划列表

#### 测试用例5：更新学员备注
```bash
curl -X PUT "http://localhost:8080/api/coach/students/remark" \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 10,
    "remark": "训练态度积极，建议增加力量训练强度"
  }'
```

**预期结果**:
- 更新成功
- 再次查询学员详情，备注已更新

#### 测试用例6：无权限访问
```bash
# 使用非教练用户token
curl -X GET "http://localhost:8080/api/coach/students/my" \
  -H "Authorization: Bearer {user_token}"
```

**预期结果**:
- 返回错误：当前用户不是教练

---

## 🐛 常见问题

### 问题1: "当前用户不是教练"
**原因**: 
- Token对应的用户在 `gym_coach` 表中没有记录
- 用户类型不是COACH

**解决方案**:
1. 检查用户是否已创建教练记录
2. 使用管理后台创建教练信息

### 问题2: "无权查看该学员信息"
**原因**: 
- 该学员没有该教练创建的训练计划

**解决方案**:
- 教练需要先为学员创建训练计划，才能查看该学员信息

### 问题3: 返回学员列表为空
**原因**: 
- 该教练还没有创建任何训练计划
- 没有学员数据

**解决方案**:
1. 为学员创建训练计划
2. 检查数据库中是否有训练计划数据

---

## 📊 数据库检查

### 检查教练记录
```sql
SELECT * FROM gym_coach WHERE user_id = {userId};
```

### 检查训练计划
```sql
SELECT * FROM gym_training_plan WHERE coach_id = {coachId};
```

### 检查学员关系
```sql
SELECT * FROM gym_coach_student WHERE coach_id = {coachId};
```

---

## ✅ 验证清单

- [ ] 学员列表分页查询正常
- [ ] 关键词搜索功能正常
- [ ] 会员类型筛选正常
- [ ] 学员详情查询正常
- [ ] 体测记录展示正常
- [ ] 训练计划展示正常
- [ ] 备注更新功能正常
- [ ] 权限验证正常
- [ ] 错误提示友好
- [ ] 数据统计准确

---

## 📝 开发总结

### 已完成功能
1. ✅ 创建数据库表 `gym_coach_student`
2. ✅ 创建实体类 `GymCoachStudent`
3. ✅ 创建Mapper接口 `GymCoachStudentMapper`
4. ✅ 创建DTO（`StudentDTO`, `StudentDetailDTO`, `StudentRemarkUpdateDTO`）
5. ✅ 创建Service层 `CoachStudentService`
6. ✅ 创建Controller层 `CoachStudentController`
7. ✅ 实现5个核心接口
8. ✅ 添加权限验证
9. ✅ 添加Swagger文档

### 技术特点
- 基于训练计划关联查询学员
- 支持分页和搜索
- 完整的权限验证
- 丰富的学员信息展示
- 体测历史趋势分析
- 训练计划统计

---

**开发完成时间**: 2025-12-11  
**文档版本**: v1.0
