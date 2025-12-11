# 教练排班管理系统 - 数据库设计文档

## 📋 文档说明

- **模块名称**: 我的排班（模块9）
- **功能描述**: 教练排班管理、申请审核、打卡记录、统计分析
- **设计日期**: 2025-12-11
- **数据库文件**: `coach_schedule_system.sql`

---

## 🏗️ 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    教练排班管理系统                           │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ 排班规划模块  │  │ 申请审批模块  │  │ 打卡统计模块  │       │
│  └────────┬─────┘  └────────┬─────┘  └────────┬─────┘       │
│           │                │                │                 │
│     ┌─────▼──────┐   ┌─────▼──────┐  ┌──────▼──────┐        │
│     │ 排班表     │   │ 申请表     │  │ 打卡表      │        │
│     │ 规则表     │   │ 请假表     │  │ 统计表      │        │
│     └────────────┘   └────────────┘  └─────────────┘        │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 核心表结构设计

### 1. gym_coach_schedule（教练排班表）

**用途**: 记录教练计划的工作排班，是系统的核心表

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 排班ID |
| coach_id | bigint | FK, NOT NULL | 教练ID |
| work_date | date | NOT NULL | 工作日期 |
| start_time | time | NOT NULL | 开始时间(HH:MM:SS) |
| end_time | time | NOT NULL | 结束时间 |
| work_type | varchar(20) | DEFAULT 'NORMAL' | 工作类型：NORMAL/OVERTIME/SHIFT/HOLIDAY |
| location | varchar(100) | NULL | 工作地点 |
| status | tinyint | DEFAULT 1 | 状态：0-已取消, 1-正常, 2-已完成 |
| remark | varchar(200) | NULL | 备注 |
| create_by | bigint | NULL | 创建者ID(管理员) |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 关键索引

```sql
-- 唯一索引：防止同一教练同一时间段重复排班
UNIQUE KEY `uk_coach_date_time` (`coach_id`, `work_date`, `start_time`, `end_time`)

-- 复合索引：查询教练特定日期的排班
KEY `idx_coach_date` (`coach_id`, `work_date`)

-- 单字段索引：快速查询
KEY `idx_coach_id`, `idx_work_date`, `idx_work_type`, `idx_status`
```

#### 查询示例

```sql
-- 查询教练指定日期的排班
SELECT * FROM gym_coach_schedule 
WHERE coach_id = ? AND work_date = ? AND status = 1;

-- 查询教练月度排班
SELECT * FROM gym_coach_schedule 
WHERE coach_id = ? AND YEAR(work_date) = 2025 AND MONTH(work_date) = 12;

-- 统计加班时数
SELECT SUM(HOUR(TIMEDIFF(end_time, start_time))) 
FROM gym_coach_schedule 
WHERE coach_id = ? AND work_type = 'OVERTIME' AND work_date BETWEEN ? AND ?;
```

---

### 2. gym_schedule_request（排班申请表）

**用途**: 管理教练的调休、加班、换班等申请

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 申请ID |
| coach_id | bigint | FK, NOT NULL | 教练ID |
| request_type | tinyint | NOT NULL | 申请类型：1-调休, 2-加班, 3-换班 |
| target_date | date | NOT NULL | 目标日期 |
| reason | text | NULL | 申请理由 |
| exchange_with_coach_id | bigint | FK, NULL | 换班对象教练ID(仅type=3) |
| exchange_schedule_id | bigint | NULL | 被交换排班ID(仅type=3) |
| status | tinyint | DEFAULT 0 | 申请状态：0-待审批, 1-已通过, 2-已拒绝, 3-已取消 |
| approver_id | bigint | FK, NULL | 审批人ID |
| approve_time | datetime | NULL | 审批时间 |
| approve_remark | varchar(200) | NULL | 审批意见 |
| attachment_url | varchar(255) | NULL | 附件地址(如医疗证明) |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 申请类型枚举

- **1 - 调休(请假)**: 教练请假，无需替班人员
- **2 - 加班**: 教练申请加班，需要记录额外工作时间
- **3 - 换班**: 教练与另一教练交换排班时间

#### 关键索引

```sql
-- 查询教练待审批的申请
KEY `idx_coach_status` (`coach_id`, `status`)

-- 查询指定日期的所有申请
KEY `idx_target_date` (`target_date`)
```

#### 业务流程

```
教练提交申请
    ↓
create: status = 0(待审批)
    ↓
管理员审核 → 通过: status = 1 → 更新排班表
         → 拒绝: status = 2 → 保留原排班
```

---

### 3. gym_schedule_record（排班打卡记录表）

**用途**: 记录教练实际的工作打卡信息，用于考勤管理

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 打卡ID |
| schedule_id | bigint | FK, UQ, NOT NULL | 排班ID |
| coach_id | bigint | FK, NOT NULL | 教练ID |
| check_in_time | datetime | NULL | 打卡入场时间 |
| check_out_time | datetime | NULL | 打卡离场时间 |
| check_in_location | varchar(100) | NULL | 入场地点(如前台、机房) |
| check_out_location | varchar(100) | NULL | 离场地点 |
| status | tinyint | DEFAULT 0 | 打卡状态：0-未打卡, 1-入场, 2-离场, 3-迟到, 4-早退, 5-缺勤 |
| attendance_score | int | DEFAULT 100 | 出勤评分(0-100) |
| remark | varchar(200) | NULL | 备注 |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 打卡状态流程

```
排班创建
  ↓
status = 0(未打卡)
  ↓
教练入场打卡 → status = 1(已入场) → check_in_time = NOW()
  ↓
离场打卡 → status = 2(已离场) → check_out_time = NOW()

// 异常情况
迟到(check_in_time > start_time + 15分钟) → status = 3, attendance_score -= 10
早退(check_out_time < end_time - 15分钟) → status = 4, attendance_score -= 10
缺勤(未打卡且超过end_time) → status = 5, attendance_score = 0
```

#### 关键设计

- **唯一约束**: `UNIQUE KEY (schedule_id)` - 每个排班只能有一条打卡记录
- **自动计算**: 系统需定时任务检查缺勤

---

### 4. gym_schedule_rule（排班规则表）

**用途**: 存储系统级的排班配置规则

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 规则ID |
| rule_name | varchar(100) | NOT NULL | 规则名称 |
| rule_type | varchar(50) | UQ, NOT NULL | 规则类型 |
| rule_value | varchar(500) | NOT NULL | 规则值(JSON格式) |
| description | text | NULL | 规则说明 |
| status | tinyint | DEFAULT 1 | 状态：0-禁用, 1-启用 |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 预置规则

```json
// 1. 工作时间限制
{
  "rule_type": "WORK_TIME_LIMIT",
  "rule_value": {
    "max_hours_per_day": 10,
    "max_hours_per_week": 48
  }
}

// 2. 休息规则
{
  "rule_type": "BREAK_TIME_RULE",
  "rule_value": {
    "min_rest_hours": 11,
    "max_work_days_continuous": 6
  }
}

// 3. 迟到规则
{
  "rule_type": "LATE_RULE",
  "rule_value": {
    "late_threshold_minutes": 15,
    "deduction_points": 10
  }
}
```

---

### 5. gym_coach_leave（教练请假表）

**用途**: 管理教练的长期假期(年假、病假等)

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 请假ID |
| coach_id | bigint | FK, NOT NULL | 教练ID |
| leave_type | varchar(20) | NOT NULL | 请假类型 |
| start_date | date | NOT NULL | 开始日期 |
| end_date | date | NOT NULL | 结束日期 |
| duration_days | int | NOT NULL | 请假天数 |
| reason | text | NULL | 请假原因 |
| status | tinyint | DEFAULT 0 | 申请状态：0-待审批, 1-已批准, 2-已拒绝 |
| approver_id | bigint | FK, NULL | 批准人ID |
| approve_time | datetime | NULL | 批准时间 |
| approve_remark | varchar(200) | NULL | 批准意见 |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 请假类型

- **ANNUAL**: 年假(带薪)
- **SICK**: 病假(可能需要证明)
- **PERSONAL**: 事假(可能无薪)
- **MATERNITY**: 产假(法定假期)
- **UNPAID**: 无薪假

#### 业务逻辑

```
请假申请 → 待审批
  ↓
审批通过 → 已批准 → 需要删除对应日期的排班 或 创建HOLIDAY类型的排班
  ↓
审批拒绝 → 已拒绝
```

---

### 6. gym_schedule_statistics（排班统计表）

**用途**: 缓存排班统计数据，用于提高查询性能

#### 字段说明

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | bigint | PK, AI | 统计ID |
| coach_id | bigint | FK, NOT NULL | 教练ID |
| statistics_date | date | NOT NULL | 统计日期(月份, 格式: 2025-12-01) |
| total_hours | decimal(10,2) | DEFAULT 0 | 总工作时数 |
| normal_hours | decimal(10,2) | DEFAULT 0 | 正常工作时数 |
| overtime_hours | decimal(10,2) | DEFAULT 0 | 加班时数 |
| work_days | int | DEFAULT 0 | 工作天数 |
| absent_days | int | DEFAULT 0 | 缺勤天数 |
| late_days | int | DEFAULT 0 | 迟到天数 |
| leave_days | int | DEFAULT 0 | 请假天数 |
| average_attendance_score | int | DEFAULT 100 | 平均出勤评分 |
| create_time | datetime | DEFAULT NOW | 创建时间 |
| update_time | datetime | AUTO UPDATE | 更新时间 |

#### 唯一约束

```sql
UNIQUE KEY `uk_coach_date` (`coach_id`, `statistics_date`)
```

#### 计算逻辑(月度)

```sql
total_hours = SUM(HOUR(TIMEDIFF(end_time, start_time)))
normal_hours = SUM(for type=NORMAL)
overtime_hours = SUM(for type=OVERTIME)
work_days = COUNT(DISTINCT work_date) WHERE status IN (1, 2)
absent_days = COUNT(WHERE status=5)
late_days = COUNT(WHERE record.status=3)
leave_days = SUM(duration_days) FROM gym_coach_leave WHERE approved
average_attendance_score = AVG(attendance_score)
```

---

## 🔗 表间关系图

```
┌─────────────────────┐
│    gym_coach        │ (教练基本表)
├─────────────────────┤
│ id (PK)             │
│ user_id             │
│ ...                 │
└──────────┬──────────┘
           │ (1对多)
           │
    ┌──────┴──────┬───────────┬─────────┬──────────┐
    │             │           │         │          │
    ▼             ▼           ▼         ▼          ▼
┌──────────┐ ┌──────────┐ ┌──────┐ ┌──────┐ ┌─────────┐
│ Schedule │ │ Request  │ │Leave │ │Record│ │Stat... │
│   (排班) │ │  (申请)  │ │(假期)│ │(打卡)│ │(统计)  │
└──────────┘ └──────────┘ └──────┘ └──────┘ └─────────┘
    │ (1对1)     │ (多对多)
    │            │
    ▼            ▼
┌──────────┐ ┌──────────┐
│ Record   │ │Schedule  │ (换班)
│  (打卡)  │ │(对象教练)│
└──────────┘ └──────────┘
```

---

## 📈 数据流程

### 1. 排班规划流程

```
管理员 → 创建排班(gym_coach_schedule)
       ↓
       记录到排班表
       ↓
教练查看 → 对应日期的排班
```

### 2. 申请审批流程

```
教练 → 提交申请(gym_schedule_request, status=0)
    ↓
管理员 → 审批
      ↓ 通过(status=1)
      更新排班 或 创建调休排班
      ↓ 拒绝(status=2)
      保持原样
```

### 3. 打卡记录流程

```
教练入场 → check_in(gym_schedule_record)
       ↓
       status = 1, check_in_time = NOW()
       ↓
教练离场 → check_out(gym_schedule_record)
       ↓
       status = 2, check_out_time = NOW()
       ↓
系统验证 → 是否迟到/早退/缺勤
       ↓
计算出勤评分 → attendance_score
```

### 4. 统计生成流程

```
每月1日或定时任务
  ↓
统计前一个月的数据
  ↓
从gym_coach_schedule、gym_schedule_record、gym_coach_leave聚合
  ↓
生成或更新gym_schedule_statistics
```

---

## 🔍 常见查询

### 查询排班

```sql
-- 查询教练本月排班日历
SELECT work_date, start_time, end_time, work_type, status
FROM gym_coach_schedule
WHERE coach_id = 1 
  AND YEAR(work_date) = 2025 
  AND MONTH(work_date) = 12
ORDER BY work_date, start_time;

-- 查询教练某周的排班
SELECT * FROM gym_coach_schedule
WHERE coach_id = 1
  AND work_date BETWEEN '2025-12-08' AND '2025-12-14'
ORDER BY work_date;
```

### 查询申请

```sql
-- 查询教练待审批的申请
SELECT * FROM gym_schedule_request
WHERE coach_id = 1 AND status = 0
ORDER BY create_time DESC;

-- 查询管理员待审批的所有申请
SELECT * FROM gym_schedule_request
WHERE status = 0
ORDER BY create_time DESC;
```

### 统计报表

```sql
-- 查询教练月度统计
SELECT * FROM gym_schedule_statistics
WHERE coach_id = 1 AND statistics_date = '2025-12-01';

-- 查询所有教练的工作时数排名
SELECT coach_id, total_hours, work_days, average_attendance_score
FROM gym_schedule_statistics
WHERE statistics_date = '2025-12-01'
ORDER BY total_hours DESC;
```

---

## ⚙️ 性能优化建议

### 索引优化

```sql
-- 已实现的索引
1. 单字段索引：coach_id, work_date, status等
2. 复合索引：(coach_id, work_date), (coach_id, status)
3. 唯一索引：防止重复数据

-- 可选的额外索引
ALTER TABLE gym_coach_schedule 
  ADD INDEX idx_work_type_date (work_type, work_date);

ALTER TABLE gym_schedule_request 
  ADD INDEX idx_approver_status (approver_id, status);
```

### 分区建议

对于大数据量系统，可按年月分区：

```sql
ALTER TABLE gym_coach_schedule 
PARTITION BY RANGE (YEAR(work_date)) (
  PARTITION p2024 VALUES LESS THAN (2025),
  PARTITION p2025 VALUES LESS THAN (2026),
  PARTITION p2026 VALUES LESS THAN MAXVALUE
);
```

---

## 📝 初始化脚本

所有表都已在 `coach_schedule_system.sql` 中定义，包括：

1. ✅ 表结构创建
2. ✅ 主外键约束
3. ✅ 索引创建
4. ✅ 初始规则数据插入

**执行方式**:

```bash
mysql -u root -p gym < springboot/database/coach_schedule_system.sql
```

---

## 🎯 使用场景

### 场景1：教练查看月度排班
```
查询条件: coach_id, year, month
返回: gym_coach_schedule记录
用于: 日历视图展示
```

### 场景2：教练请假申请
```
创建: gym_schedule_request (request_type=1)
状态流转: 待审批 → 已通过/已拒绝
关联操作: 创建HOLIDAY类型排班
```

### 场景3：打卡考勤
```
入场: 创建gym_schedule_record, status=1
离场: 更新gym_schedule_record, status=2
异常: 系统自动检测迟到/早退/缺勤
评分: 计算attendance_score
```

### 场景4：月度考勤统计
```
定时任务: 每月生成统计
聚合: 汇总所有排班、打卡、请假数据
结果: gym_schedule_statistics
```

---

## 📦 文件位置

- **SQL文件**: `D:\健身房预约小程序\springboot\database\coach_schedule_system.sql`
- **本设计文档**: `D:\健身房预约小程序\springboot\database\coach_schedule_system_design.md`

---

## ✅ 检查清单

- [x] 表结构设计完整
- [x] 索引优化合理
- [x] 外键约束正确
- [x] 初始数据预置
- [x] 字段注释详细
- [x] 支持业务流程

---

**下一步**: 基于本设计创建Java实体类、Mapper接口、Service和Controller
