# 教练排班管理API测试文档

## 1. 创建排班

### 请求
```
POST http://localhost:8889/coach-schedule/schedule
Content-Type: application/json

{
  "coachId": 1,
  "workDate": "2025-12-15",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "workType": "NORMAL",
  "location": "主训练区",
  "remark": "日常排班"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 2. 查询排班

### 请求
```
GET http://localhost:8889/coach-schedule/schedule/1
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "coachId": 1,
    "workDate": "2025-12-15",
    "startTime": "09:00:00",
    "endTime": "17:00:00",
    "workType": "NORMAL",
    "location": "主训练区",
    "status": 1,
    "remark": "日常排班",
    "createTime": "2025-12-11T22:00:00",
    "updateTime": "2025-12-11T22:00:00"
  }
}
```

## 3. 查询教练月度排班

### 请求
```
GET http://localhost:8889/coach-schedule/schedule/monthly?coachId=1&year=2025&month=12
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "coachId": 1,
      "workDate": "2025-12-15",
      "startTime": "09:00:00",
      "endTime": "17:00:00",
      "workType": "NORMAL",
      "location": "主训练区",
      "status": 1,
      "remark": "日常排班",
      "createTime": "2025-12-11T22:00:00",
      "updateTime": "2025-12-11T22:00:00"
    }
  ]
}
```

## 4. 更新排班

### 请求
```
PUT http://localhost:8889/coach-schedule/schedule/1
Content-Type: application/json

{
  "location": "VIP训练区",
  "remark": "调整到VIP区域"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 5. 创建排班申请

### 请求
```
POST http://localhost:8889/coach-schedule/request?coachId=1
Content-Type: application/json

{
  "requestType": 1,
  "targetDate": "2025-12-20",
  "reason": "身体不适需要休息"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 6. 查询教练的排班申请记录

### 请求
```
GET http://localhost:8889/coach-schedule/request/list?coachId=1&page=1&size=10
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "coachId": 1,
        "requestType": 1,
        "targetDate": "2025-12-20",
        "reason": "身体不适需要休息",
        "status": 0,
        "createTime": "2025-12-11T22:05:00",
        "updateTime": "2025-12-11T22:05:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

## 7. 审批排班申请

### 请求
```
PUT http://localhost:8889/coach-schedule/request/1/approve?adminId=1
Content-Type: application/json

{
  "status": 1,
  "approveRemark": "同意请假申请"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 8. 打卡入场

### 请求
```
POST http://localhost:8889/coach-schedule/attendance/check-in?coachId=1
Content-Type: application/json

{
  "scheduleId": 1,
  "location": "前台",
  "remark": "准时到达"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 9. 打卡离场

### 请求
```
POST http://localhost:8889/coach-schedule/attendance/check-out?coachId=1
Content-Type: application/json

{
  "scheduleId": 1,
  "location": "前台",
  "remark": "下班离场"
}
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 10. 查询教练的打卡记录

### 请求
```
GET http://localhost:8889/coach-schedule/attendance/records?coachId=1&page=1&size=10
```

### 响应
```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "scheduleId": 1,
        "coachId": 1,
        "checkInTime": "2025-12-11T09:00:00",
        "checkOutTime": "2025-12-11T17:00:00",
        "checkInLocation": "前台",
        "checkOutLocation": "前台",
        "status": 2,
        "attendanceScore": 100,
        "remark": "准时到达; 下班离场",
        "createTime": "2025-12-11T09:00:00",
        "updateTime": "2025-12-11T17:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```