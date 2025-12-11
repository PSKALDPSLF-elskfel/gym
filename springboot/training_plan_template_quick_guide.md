## 训练方案功能 - 快速使用指南

### 1️⃣ 数据库初始化

执行SQL脚本导入数据库：
```bash
mysql -u root -p gym < D:\健身房预约小程序\springboot\database\training_plan_template.sql
```

该脚本会创建：
- `gym_training_plan_template` 表（模板）
- `gym_training_plan_template_detail` 表（模板明细）
- 3个系统内置模板（减脂、增肌、康复）

### 2️⃣ 核心API接口

#### 创建模板（教练）
```
POST /api/training-plan-templates/coach
参数: coachId=4

请求体:
{
  "name": "我的减脂计划",
  "goal": "减脂",
  "description": "针对有氧和核心的综合计划",
  "difficulty": 1,
  "durationDays": 28,
  "details": [
    {
      "dayOfWeek": 1,
      "actionId": 1,
      "sets": 3,
      "reps": 12,
      "weight": 60,
      "restTime": 60,
      "sortOrder": 1,
      "description": "胸部训练"
    }
  ]
}
```

#### 查看模板列表
```
GET /api/training-plan-templates/page
参数: 
  currentPage=1
  pageSize=10
  goal=减脂
  difficulty=1
```

#### 获取教练的模板
```
GET /api/training-plan-templates/coach/4/list
参数: currentPage=1, pageSize=10
```

#### 从模板创建计划
```
POST /api/training-plan-templates/1/create-plan
参数: 
  userId=1（学员ID）
  planName=我的减脂计划（计划名称）
```

### 3️⃣ 系统内置模板说明

**模板ID=1：减脂塑形-初级计划**
- 适合初学者减脂
- 12个训练动作
- 28天周期

**模板ID=2：增肌塑形-中级计划**
- 适合有基础学员增肌
- 14个训练动作
- 28天周期

**模板ID=3：运动康复-康复计划**
- 适合运动损伤恢复
- 8个训练动作
- 21天周期

### 4️⃣ 核心业务流程

```
┌─────────────────────────────┐
│  教练创建训练方案模板       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  模板保存到数据库           │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  学员从模板选择并创建计划   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  计划中自动填充模板信息     │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│  学员可独立修改自己的计划   │
└─────────────────────────────┘
```

### 5️⃣ 权限说明

- **系统模板**：所有用户可见
- **教练模板**：
  - 私有（is_public=0）：仅该教练可用
  - 公开（is_public=1）：对应学员可用
- **计划**：仅所有者可修改

### 6️⃣ 关键表字段说明

**gym_training_plan_template**
| 字段 | 说明 | 示例值 |
|------|------|--------|
| id | 模板ID | 1 |
| coach_id | 教练ID（NULL=系统模板） | 4 |
| name | 模板名称 | 减脂塑形-初级计划 |
| goal | 训练目标 | 减脂/增肌/塑形/康复 |
| difficulty | 难度 | 1(初) / 2(中) / 3(高) |
| duration_days | 推荐周期 | 28 |
| is_public | 是否公开 | 0(私有) / 1(公开) |
| status | 状态 | 0(禁用) / 1(启用) |
| total_exercises | 总动作数 | 12 |

### 7️⃣ 常见操作

**教练创建模板：**
1. 调用 POST /api/training-plan-templates/coach
2. 传入模板信息和训练明细
3. 系统返回创建的模板ID

**学员创建计划：**
1. 调用 GET /api/training-plan-templates/page 查看可用模板
2. 调用 POST /api/training-plan-templates/{id}/create-plan 快速创建计划
3. 系统自动复制模板明细到新计划

**修改或删除模板：**
1. 调用 PUT /api/training-plan-templates/{id} 更新
2. 调用 DELETE /api/training-plan-templates/{id} 删除
3. 已创建的计划不受影响

### 8️⃣ 相关Service方法

```java
// 创建模板
TrainingPlanTemplateResponseDTO createTemplate(Long coachId, TrainingPlanTemplateCreateDTO createDTO);

// 更新模板
TrainingPlanTemplateResponseDTO updateTemplate(Long templateId, TrainingPlanTemplateUpdateDTO updateDTO);

// 删除模板
void deleteTemplate(Long templateId);

// 获取模板详情
TrainingPlanTemplateResponseDTO getTemplateById(Long templateId);

// 分页查询模板
Page<TrainingPlanTemplateResponseDTO> getTemplatePage(int currentPage, int pageSize, Long coachId, String goal, Integer difficulty, Integer status);

// 获取教练模板
Page<TrainingPlanTemplateResponseDTO> getCoachTemplates(Long coachId, int currentPage, int pageSize);

// 从模板创建计划
TrainingPlanResponseDTO createPlanFromTemplate(Long userId, Long templateId, String planName);
```

### ⚠️ 注意事项

1. 创建模板时必须包含至少一个训练明细
2. 教练只能修改自己的模板
3. 从模板创建的计划与模板独立，修改模板不影响已创建的计划
4. 删除模板不会删除已创建的计划
5. 需要在Controller层添加权限验证（仅教练可以创建模板）

---

**下一步**：可在前端（Vue3教练端或小程序）实现模板管理UI界面。
