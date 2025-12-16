# 健身数据记录 API 测试文档

## 📋 文档说明

本文档提供健身数据记录模块的完整API测试用例，包括运动记录管理、运动类型查询和数据统计等功能。

---

## 🚀 API 接口列表

### 1. 运动记录管理

#### 1.1 创建运动记录

**接口地址**: `POST /workout/record`

**请求头**:
```json
{
  "Authorization": "Bearer {token}",
  "Content-Type": "application/json"
}
```

**请求示例1 - 有氧运动(跑步)**:
```json
{
  "workoutTypeId": 1,
  "workoutDate": "2025-12-12",
  "startTime": "2025-12-12 07:00:00",
  "endTime": "2025-12-12 07:30:00",
  "duration": 30,
  "intensity": "MEDIUM",
  "calories": 250,
  "distance": 5.0,
  "steps": 6500,
  "heartRateAvg": 145,
  "heartRateMax": 165,
  "note": "早晨跑步，感觉不错",
  "feeling": "GOOD",
  "weather": "晴天",
  "location": "健身房跑步机",
  "isCompleted": 1,
  "source": "MANUAL"
}
```

**请求示例2 - 力量训练(深蹲)**:
```json
{
  "workoutTypeId": 7,
  "workoutDate": "2025-12-12",
  "startTime": "2025-12-12 18:00:00",
  "endTime": "2025-12-12 18:45:00",
  "duration": 45,
  "intensity": "HIGH",
  "calories": 180,
  "heartRateAvg": 135,
  "heartRateMax": 158,
  "note": "腿部训练日",
  "feeling": "TIRED",
  "location": "健身房",
  "isCompleted": 1,
  "source": "MANUAL",
  "details": [
    {
      "actionName": "杠铃深蹲",
      "sets": 4,
      "reps": 12,
      "weight": 80.5,
      "restTime": 90,
      "actualSets": 4,
      "actualReps": 10,
      "note": "感觉良好",
      "sortOrder": 1
    },
    {
      "actionName": "腿举",
      "sets": 3,
      "reps": 15,
      "weight": 120.0,
      "restTime": 60,
      "actualSets": 3,
      "actualReps": 15,
      "note": "完成顺利",
      "sortOrder": 2
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

---

#### 1.2 更新运动记录

**接口地址**: `PUT /workout/record/{id}`

**请求头**:
```json
{
  "Authorization": "Bearer {token}",
  "Content-Type": "application/json"
}
```

**请求示例**:
```json
{
  "duration": 35,
  "calories": 280,
  "distance": 5.5,
  "note": "今天状态很好，多跑了5分钟",
  "feeling": "GREAT"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

#### 1.3 删除运动记录

**接口地址**: `DELETE /workout/record/{id}`

**请求头**:
```json
{
  "Authorization": "Bearer {token}"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

#### 1.4 查询运动记录详情

**接口地址**: `GET /workout/record/{id}`

**请求头**:
```json
{
  "Authorization": "Bearer {token}"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 3,
    "userNickname": "张三",
    "workoutTypeId": 1,
    "workoutTypeName": "跑步",
    "workoutCategory": "CARDIO",
    "workoutDate": "2025-12-12",
    "startTime": "2025-12-12 07:00:00",
    "endTime": "2025-12-12 07:30:00",
    "duration": 30,
    "intensity": "MEDIUM",
    "calories": 250,
    "distance": 5.0,
    "steps": 6500,
    "heartRateAvg": 145,
    "heartRateMax": 165,
    "images": [],
    "note": "早晨跑步，感觉不错",
    "feeling": "GOOD",
    "weather": "晴天",
    "location": "健身房跑步机",
    "isCompleted": 1,
    "trainingPlanId": null,
    "source": "MANUAL",
    "createTime": "2025-12-12 07:45:00",
    "updateTime": "2025-12-12 07:45:00",
    "details": []
  }
}
```

---

#### 1.5 分页查询我的运动记录

**接口地址**: `GET /workout/record/my-page`

**请求头**:
```json
{
  "Authorization": "Bearer {token}"
}
```

**请求参数**:
- `current`: 当前页码（默认1）
- `size`: 每页大小（默认10）
- `startDate`: 开始日期（可选，格式：yyyy-MM-dd）
- `endDate`: 结束日期（可选，格式：yyyy-MM-dd）
- `workoutTypeId`: 运动类型ID（可选）
- `intensity`: 运动强度（可选：LOW/MEDIUM/HIGH）
- `isCompleted`: 是否完成（可选：0/1）

**请求示例**:
```
GET /workout/record/my-page?current=1&size=10&startDate=2025-12-01&endDate=2025-12-31&intensity=MEDIUM
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 3,
        "userNickname": "张三",
        "workoutTypeId": 1,
        "workoutTypeName": "跑步",
        "workoutCategory": "CARDIO",
        "workoutDate": "2025-12-12",
        "startTime": "2025-12-12 07:00:00",
        "endTime": "2025-12-12 07:30:00",
        "duration": 30,
        "intensity": "MEDIUM",
        "calories": 250,
        "distance": 5.0,
        "steps": 6500,
        "heartRateAvg": 145,
        "heartRateMax": 165,
        "note": "早晨跑步",
        "feeling": "GOOD",
        "isCompleted": 1,
        "source": "MANUAL",
        "createTime": "2025-12-12 07:45:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 2. 运动类型查询

#### 2.1 查询运动类型列表

**接口地址**: `GET /workout/type/list`

**请求参数**:
- `category`: 运动分类（可选：CARDIO/STRENGTH/FLEXIBILITY/SPORTS/OTHER）

**请求示例**:
```
GET /workout/type/list?category=CARDIO
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "跑步",
      "category": "CARDIO",
      "icon": "/images/workout/running.png",
      "metValue": 8.00,
      "description": "有氧运动，提升心肺功能",
      "sortOrder": 1,
      "status": 1
    },
    {
      "id": 2,
      "name": "快走",
      "category": "CARDIO",
      "icon": "/images/workout/walking.png",
      "metValue": 3.50,
      "description": "低强度有氧运动",
      "sortOrder": 2,
      "status": 1
    }
  ]
}
```

---

### 3. 运动数据统计

#### 3.1 查询运动数据统计汇总

**接口地址**: `GET /workout/statistics/summary`

**请求头**:
```json
{
  "Authorization": "Bearer {token}"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalWorkouts": 25,
    "totalDuration": 1500,
    "totalCalories": 5800,
    "totalDistance": 75.5,
    "avgDuration": 60,
    "workoutDays": 20,
    "streakDays": 7,
    "weekWorkouts": 5,
    "monthWorkouts": 18,
    "cardioPercentage": 65.50,
    "strengthPercentage": 34.50
  }
}
```

---

#### 3.2 查询每日统计数据

**接口地址**: `GET /workout/statistics/daily`

**请求头**:
```json
{
  "Authorization": "Bearer {token}"
}
```

**请求参数**:
- `startDate`: 开始日期（可选，格式：yyyy-MM-dd）
- `endDate`: 结束日期（可选，格式：yyyy-MM-dd）

**请求示例**:
```
GET /workout/statistics/daily?startDate=2025-12-01&endDate=2025-12-12
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 3,
      "statDate": "2025-12-10",
      "totalDuration": 75,
      "totalCalories": 430,
      "totalDistance": 5.0,
      "totalSteps": 6500,
      "workoutCount": 2,
      "cardioDuration": 30,
      "strengthDuration": 45,
      "flexibilityDuration": 0,
      "isRestDay": 0,
      "planCompletionRate": null
    },
    {
      "id": 2,
      "userId": 3,
      "statDate": "2025-12-11",
      "totalDuration": 60,
      "totalCalories": 120,
      "totalDistance": 0,
      "totalSteps": 0,
      "workoutCount": 1,
      "cardioDuration": 0,
      "strengthDuration": 0,
      "flexibilityDuration": 60,
      "isRestDay": 0,
      "planCompletionRate": null
    }
  ]
}
```

---

## 📝 测试场景

### 场景1: 创建一周完整运动记录

**步骤**:
1. 周一: 创建跑步记录（有氧）
2. 周二: 创建深蹲记录（力量训练，带详情）
3. 周三: 创建游泳记录（有氧）
4. 周四: 创建卧推记录（力量训练，带详情）
5. 周五: 创建骑行记录（有氧）
6. 周六: 创建瑜伽记录（柔韧性）
7. 周日: 休息

**验证**:
- 查询运动记录列表，应显示6条记录
- 查询统计汇总，连续运动天数应为6天
- 查询每日统计，应有6天的数据

---

### 场景2: 数据统计验证

**步骤**:
1. 创建多条不同类型的运动记录
2. 查询统计汇总
3. 验证各项数据正确性

**验证点**:
- 总运动次数正确
- 总时长、热量、距离计算正确
- 有氧和力量训练占比正确
- 连续运动天数计算正确
- 本周、本月运动次数正确

---

### 场景3: 运动记录编辑

**步骤**:
1. 创建一条运动记录
2. 更新记录（修改时长、热量等）
3. 查询详情验证修改成功
4. 删除记录
5. 验证记录已删除

---

## ⚠️ 注意事项

1. **日期格式**: 
   - 日期: `yyyy-MM-dd`
   - 日期时间: `yyyy-MM-dd HH:mm:ss`

2. **枚举值**:
   - 运动分类: `CARDIO`, `STRENGTH`, `FLEXIBILITY`, `SPORTS`, `OTHER`
   - 运动强度: `LOW`, `MEDIUM`, `HIGH`
   - 运动感受: `GREAT`, `GOOD`, `NORMAL`, `TIRED`, `BAD`
   - 数据来源: `MANUAL`, `PLAN`, `DEVICE`

3. **权限验证**: 
   - 所有接口都需要携带有效的JWT token
   - 用户只能操作自己的运动记录

4. **数据统计**:
   - 每日统计数据由数据库触发器自动维护
   - 创建/更新/删除记录时会自动更新统计

---

## 🔍 常见问题

**Q1: 创建记录后查询统计数据不准确？**
A: 数据库触发器会自动更新统计，如有问题请检查触发器是否正常执行。

**Q2: 删除记录后统计数据没有更新？**
A: 检查删除触发器是否正常工作，或手动刷新统计数据。

**Q3: 连续运动天数计算不正确？**
A: 连续天数计算基于运动日期，确保记录的 `workoutDate` 字段正确。

---

## 📞 技术支持

如遇到问题，请提供以下信息：
1. 接口地址和请求方法
2. 请求参数
3. 响应内容
4. 错误日志
