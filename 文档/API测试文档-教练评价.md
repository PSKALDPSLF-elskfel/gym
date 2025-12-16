# 教练评价功能 - API测试文档

## 📋 文档说明
- **模块名称**: 教练评价管理系统
- **开发日期**: 2025-12-12
- **文档版本**: v1.0
- **负责人**: 开发团队

---

## 🗂️ 目录
1. [数据库设计](#数据库设计)
2. [用户端API](#用户端api)
3. [教练端API](#教练端api)
4. [数据字典](#数据字典)
5. [测试用例](#测试用例)

---

## 数据库设计

### 核心表结构

#### 1. gym_coach_review - 教练评价表
```sql
-- 已创建，详见: springboot/database/coach_review_system.sql
```

#### 2. gym_coach_review_statistics - 评价统计表
```sql
-- 已创建，详见: springboot/database/coach_review_system.sql
```

#### 3. gym_review_tag - 评价标签表
```sql
-- 已创建，详见: springboot/database/coach_review_system.sql
```

#### 4. gym_review_helpful - 评价点赞表
```sql
-- 已创建，详见: springboot/database/coach_review_system.sql
```

---

## 用户端API

### 基础URL
```
http://localhost:8080/api/reviews
```

### 1. 创建评价

**接口**: `POST /api/reviews`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "coachId": 1,
  "planId": null,
  "courseBookingId": 1,
  "reviewType": 2,
  "rating": 5,
  "tagList": ["专业", "耐心", "认真负责"],
  "content": "李教练非常专业，课程讲解很细致，动作示范标准，每次上课都能学到很多东西！",
  "images": [
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
  ],
  "isAnonymous": 0
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 1
}
```

**字段说明**:
- `coachId`: 教练ID（必填）
- `planId`: 训练计划ID（训练计划评价时必填）
- `courseBookingId`: 课程预约ID（课程评价时必填）
- `reviewType`: 评价类型（1-训练计划评价，2-课程评价）
- `rating`: 评分（1-5星，必填）
- `tagList`: 评价标签数组
- `content`: 评价内容（必填，最多500字）
- `images`: 图片URL数组（最多9张）
- `isAnonymous`: 是否匿名（0-否，1-是）

---

### 2. 删除评价

**接口**: `DELETE /api/reviews/{reviewId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
- `reviewId`: 评价ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 3. 点赞/取消点赞评价

**接口**: `POST /api/reviews/{reviewId}/helpful`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
- `reviewId`: 评价ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**说明**: 同一接口，第一次调用为点赞，再次调用为取消点赞

---

### 4. 分页查询教练评价列表

**接口**: `GET /api/reviews`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
- `coachId`: 教练ID（可选）
- `rating`: 评分筛选（可选，1-5）
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

**请求示例**:
```
GET /api/reviews?coachId=1&rating=5&pageNum=1&pageSize=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 3,
        "userNickname": "张三",
        "userAvatar": "https://example.com/avatar.jpg",
        "coachId": 1,
        "coachName": "李教练",
        "planId": null,
        "planName": null,
        "courseBookingId": 1,
        "courseName": "瑜伽基础课",
        "reviewType": 2,
        "reviewTypeDesc": "课程评价",
        "rating": 5,
        "tagList": ["专业", "耐心", "认真负责"],
        "content": "李教练非常专业，课程讲解很细致...",
        "images": [],
        "isAnonymous": 0,
        "reply": "感谢您的认可，我会继续努力为大家提供更优质的教学服务！",
        "replyTime": "2025-12-06T10:30:00",
        "isTop": 0,
        "helpfulCount": 5,
        "isHelpfulByCurrentUser": false,
        "status": 1,
        "createTime": "2025-12-05T18:20:00",
        "updateTime": "2025-12-06T10:30:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 5. 获取我的评价列表

**接口**: `GET /api/reviews/my`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

**请求示例**:
```
GET /api/reviews/my?pageNum=1&pageSize=10
```

**响应示例**: 同上

---

### 6. 获取评价详情

**接口**: `GET /api/reviews/{reviewId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
- `reviewId`: 评价ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 3,
    "userNickname": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "coachId": 1,
    "coachName": "李教练",
    "rating": 5,
    "tagList": ["专业", "耐心"],
    "content": "李教练非常专业...",
    "reply": "感谢您的认可...",
    "helpfulCount": 5,
    "isHelpfulByCurrentUser": false,
    "createTime": "2025-12-05T18:20:00"
  }
}
```

---

### 7. 获取教练评价统计

**接口**: `GET /api/reviews/statistics/{coachId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
- `coachId`: 教练ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "coachId": 1,
    "coachName": "李教练",
    "totalReviews": 100,
    "averageRating": 4.85,
    "rating5Count": 85,
    "rating4Count": 12,
    "rating3Count": 2,
    "rating2Count": 1,
    "rating1Count": 0,
    "replyRate": 95.50,
    "lastReviewTime": "2025-12-10T15:30:00"
  }
}
```

---

### 8. 获取评价标签列表

**接口**: `GET /api/reviews/tags`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
- `tagType`: 标签类型（可选，1-正面，2-负面）

**请求示例**:
```
GET /api/reviews/tags?tagType=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "tagName": "专业",
      "tagType": 1,
      "icon": null,
      "usageCount": 150,
      "sortOrder": 1
    },
    {
      "id": 2,
      "tagName": "耐心",
      "tagType": 1,
      "icon": null,
      "usageCount": 120,
      "sortOrder": 2
    }
  ]
}
```

---

## 教练端API

### 基础URL
```
http://localhost:8080/api/coach/reviews
```

### 1. 获取我收到的评价列表

**接口**: `GET /api/coach/reviews/received`

**请求头**:
```
Authorization: Bearer {coach_token}
```

**查询参数**:
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

**请求示例**:
```
GET /api/coach/reviews/received?pageNum=1&pageSize=10
```

**响应示例**: 同用户端评价列表

---

### 2. 回复评价

**接口**: `POST /api/coach/reviews/{reviewId}/reply`

**请求头**:
```
Authorization: Bearer {coach_token}
Content-Type: application/json
```

**路径参数**:
- `reviewId`: 评价ID

**请求体**:
```json
{
  "reply": "感谢您的认可，我会继续努力为大家提供更优质的教学服务！期待下次再见！"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**字段说明**:
- `reply`: 回复内容（必填，最多300字）

---

### 3. 获取我的评价统计

**接口**: `GET /api/coach/reviews/statistics`

**请求头**:
```
Authorization: Bearer {coach_token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "coachId": 1,
    "coachName": "李教练",
    "totalReviews": 100,
    "averageRating": 4.85,
    "rating5Count": 85,
    "rating4Count": 12,
    "rating3Count": 2,
    "rating2Count": 1,
    "rating1Count": 0,
    "replyRate": 95.50,
    "lastReviewTime": "2025-12-10T15:30:00"
  }
}
```

---

### 4. 获取评价详情

**接口**: `GET /api/coach/reviews/{reviewId}`

**请求头**:
```
Authorization: Bearer {coach_token}
```

**路径参数**:
- `reviewId`: 评价ID

**响应示例**: 同用户端评价详情

---

## 数据字典

### 评价类型 (review_type)
| 值 | 说明 |
|----|------|
| 1 | 训练计划评价 |
| 2 | 课程评价 |

### 评价状态 (status)
| 值 | 说明 |
|----|------|
| 0 | 已删除 |
| 1 | 正常 |
| 2 | 已隐藏 |

### 标签类型 (tag_type)
| 值 | 说明 |
|----|------|
| 1 | 正面标签 |
| 2 | 负面标签 |

### 是否匿名 (is_anonymous)
| 值 | 说明 |
|----|------|
| 0 | 实名 |
| 1 | 匿名 |

### 是否置顶 (is_top)
| 值 | 说明 |
|----|------|
| 0 | 否 |
| 1 | 是 |

---

## 测试用例

### 1. 用户创建评价测试

**测试步骤**:
1. 用户登录获取token
2. 调用创建评价接口
3. 验证返回的评价ID

**Postman测试**:
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "coachId": 1,
    "courseBookingId": 1,
    "reviewType": 2,
    "rating": 5,
    "tagList": ["专业", "耐心"],
    "content": "非常好的教练！",
    "isAnonymous": 0
  }'
```

**预期结果**:
- 返回code为200
- 返回评价ID
- 数据库新增一条评价记录
- 统计表数据更新

---

### 2. 教练回复评价测试

**测试步骤**:
1. 教练登录获取token
2. 查询收到的评价列表
3. 选择一条评价进行回复

**Postman测试**:
```bash
curl -X POST http://localhost:8080/api/coach/reviews/1/reply \
  -H "Authorization: Bearer {coach_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "reply": "感谢您的认可！"
  }'
```

**预期结果**:
- 返回code为200
- 评价记录增加回复内容和回复时间
- 回复率统计更新

---

### 3. 点赞评价测试

**测试步骤**:
1. 用户登录
2. 查看评价列表
3. 点赞某条评价
4. 再次点击取消点赞

**Postman测试**:
```bash
# 点赞
curl -X POST http://localhost:8080/api/reviews/1/helpful \
  -H "Authorization: Bearer {token}"

# 取消点赞（再次调用相同接口）
curl -X POST http://localhost:8080/api/reviews/1/helpful \
  -H "Authorization: Bearer {token}"
```

**预期结果**:
- 第一次调用：点赞数+1，数据库新增记录
- 第二次调用：点赞数-1，数据库删除记录

---

### 4. 评价统计准确性测试

**测试步骤**:
1. 为某教练创建多条不同评分的评价
2. 查询该教练的评价统计
3. 验证统计数据准确性

**验证点**:
- 总评价数正确
- 平均评分计算正确
- 各星级数量统计正确
- 回复率计算正确

---

### 5. 匿名评价测试

**测试步骤**:
1. 创建匿名评价（isAnonymous=1）
2. 查询评价列表
3. 验证用户昵称显示为"匿名用户"

**预期结果**:
- userNickname显示为"匿名用户"
- userAvatar为空或默认头像
- 其他信息正常显示

---

## 🚀 快速测试指南

### 1. 导入SQL
```bash
mysql -u root -p gym < springboot/database/coach_review_system.sql
```

### 2. 启动后端服务
```bash
cd springboot
mvn spring-boot:run
```

### 3. 访问Swagger文档
```
http://localhost:8080/doc.html
```

### 4. 测试流程
1. 注册/登录用户获取token
2. 查询教练列表，选择教练
3. 创建评价
4. 查看评价列表
5. 点赞评价
6. 教练登录回复评价
7. 查看统计数据

---

## 📊 性能指标

### 响应时间要求
- 创建评价: < 500ms
- 查询列表: < 300ms
- 统计数据: < 200ms
- 点赞操作: < 100ms

### 并发支持
- 支持1000+并发用户
- 数据库连接池: 50

---

## ⚠️ 注意事项

1. **评价唯一性**: 同一用户对同一训练计划或课程只能评价一次
2. **删除限制**: 用户只能删除自己的评价
3. **回复限制**: 教练只能回复自己收到的评价，且每条评价只能回复一次
4. **图片限制**: 最多上传9张评价图片
5. **内容长度**: 评价内容最多500字，回复内容最多300字
6. **标签使用**: 标签会自动统计使用次数，用于推荐热门标签

---

## 📝 更新日志

### v1.0 (2025-12-12)
- ✅ 完成数据库设计
- ✅ 完成实体类和Mapper
- ✅ 完成DTO定义
- ✅ 完成Service业务逻辑
- ✅ 完成用户端Controller
- ✅ 完成教练端Controller
- ✅ 创建API测试文档

---

## 📞 技术支持

如遇问题请查看:
1. Swagger API文档: http://localhost:8080/doc.html
2. 日志文件: logs/spring-boot-logger.log
3. 数据库设计: springboot/database/coach_review_system.sql
