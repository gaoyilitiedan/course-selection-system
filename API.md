# 学生选课系统 — API 接口文档

> 项目基于 Spring Boot 3.x，统一响应格式 `{code, message, data}`  
> Token 传递方式：`Authorization: Bearer <token>`  
> 基础路径：`http://localhost:8080`

---

## 目录

1. [认证接口](#1-认证接口)
2. [系统管理员接口](#2-系统管理员接口)
3. [学院管理员接口](#3-学院管理员接口)
4. [教师接口](#4-教师接口)
5. [学生接口](#5-学生接口)

---

## 1. 认证接口

### 1.1 登录

```
POST /api/auth/login
```

**请求体：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应 (200)：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "uuid-string",
    "userId": 1,
    "username": "admin",
    "role": "ROLE_ADMIN"
  }
}
```

**失败响应 (401)：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 2. 系统管理员接口

> 角色：`ROLE_ADMIN`  
> 前缀：`/api/admin/**`

### 2.1 学生管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/students` | 新增学生 |
| PUT | `/api/admin/students/{id}` | 修改学生 |
| DELETE | `/api/admin/students/{id}` | 删除学生 |
| GET | `/api/admin/students` | 查询学生列表 |

**POST /api/admin/students — 请求体：**

```json
{
  "username": "newstudent",
  "password": "123456",
  "studentNo": "S2024005",
  "name": "刘同学",
  "department": { "id": 1 }
}
```

### 2.2 教师管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/teachers` | 新增教师 |
| PUT | `/api/admin/teachers/{id}` | 修改教师 |
| DELETE | `/api/admin/teachers/{id}` | 删除教师 |
| GET | `/api/admin/teachers` | 查询教师列表 |

**POST /api/admin/teachers — 请求体：**

```json
{
  "username": "teacher3",
  "password": "123456",
  "teacherNo": "T003",
  "name": "王教授",
  "department": { "id": 1 }
}
```

### 2.3 课程管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/courses` | 新增课程 |
| PUT | `/api/admin/courses/{id}` | 修改课程 |
| DELETE | `/api/admin/courses/{id}` | 删除课程 |
| GET | `/api/admin/courses` | 查询课程列表 |

**POST /api/admin/courses — 请求体：**

```json
{
  "name": "数据结构",
  "description": "数据结构与算法课程",
  "capacity": 50,
  "teacher": { "id": 1 },
  "department": { "id": 1 }
}
```

### 2.4 选课管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/enrollments` | 新增选课记录 |
| PUT | `/api/admin/enrollments/{id}` | 修改选课记录 |
| DELETE | `/api/admin/enrollments/{id}` | 删除选课记录 |
| GET | `/api/admin/enrollments` | 查询选课记录 |

**POST /api/admin/enrollments — 请求体：**

```json
{
  "student": { "id": 1 },
  "course": { "id": 2 },
  "status": "PENDING"
}
```

### 2.5 成绩管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/grades` | 录入/修改成绩 |

**POST /api/admin/grades — 请求体：**

```json
{
  "enrollmentId": 1,
  "score": 88.5
}
```

### 2.6 统计图表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/stats/chart` | 导出选课人数柱状图(PNG) |

**响应：** `Content-Type: image/png`，`Content-Disposition: attachment; filename=chart.png`

---

## 3. 学院管理员接口

> 角色：`ROLE_DEPT_ADMIN`  
> 前缀：`/api/dept/**`  
> 学院管理员只能操作本学院的数据

### 3.1 本学院学生管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/students` | 本学院新增学生 |
| PUT | `/api/dept/students/{id}` | 本学院修改学生 |
| DELETE | `/api/dept/students/{id}` | 本学院删除学生 |
| GET | `/api/dept/students` | 本学院学生列表 |

### 3.2 本学院教师管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/teachers` | 本学院新增教师 |
| PUT | `/api/dept/teachers/{id}` | 本学院修改教师 |
| DELETE | `/api/dept/teachers/{id}` | 本学院删除教师 |
| GET | `/api/dept/teachers` | 本学院教师列表 |

### 3.3 本学院课程管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/courses` | 本学院新增课程 |
| PUT | `/api/dept/courses/{id}` | 本学院修改课程 |
| DELETE | `/api/dept/courses/{id}` | 本学院删除课程 |
| GET | `/api/dept/courses` | 本学院课程列表 |

### 3.4 本学院选课管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/enrollments` | 本学院新增选课 |
| PUT | `/api/dept/enrollments/{id}` | 本学院修改选课 |
| DELETE | `/api/dept/enrollments/{id}` | 本学院删除选课 |
| GET | `/api/dept/enrollments` | 本学院选课列表 |

### 3.5 抽签管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/draw/{courseId}` | 对本学院课程抽签 |
| GET | `/api/dept/draw/{courseId}/result` | 查看中签学生名单 |

**POST /api/dept/draw/{courseId} — 抽签规则：**

1. 获取课程所有 `PENDING` 状态选课记录
2. 选课人数 ≤ 课程容量 → 全部设为 `SELECTED`
3. 选课人数 > 课程容量 → 随机抽取 `capacity` 个设为 `SELECTED`，其余设为 `REJECTED`

### 3.6 成绩管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dept/grades` | 录入/修改本学院成绩 |
| GET | `/api/dept/grades` | 查看本学院成绩 |

---

## 4. 教师接口

> 角色：`ROLE_TEACHER`  
> 前缀：`/api/teacher/**`

### 4.1 课程管理

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/api/teacher/courses/{id}` | 更新课程信息/介绍 |

### 4.2 课件管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/teacher/courses/{id}/courseware` | 上传课件（multipart/form-data） |
| GET | `/api/teacher/courses/{id}/courseware` | 查看课件列表 |

**POST /api/teacher/courses/{id}/courseware — 参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| file | MultipartFile | 课件文件 |

### 4.3 先修课程设置

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/api/teacher/courses/{id}/prerequisite` | 设置先修课程 |

**请求体：**

```json
{
  "prerequisiteId": 1
}
```

清除先修课程时 `prerequisiteId` 传 `null`。

### 4.4 中签学生

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/teacher/enrollments/selected` | 查看自己课程中签学生 |

### 4.5 成绩管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/teacher/grades` | 录入学生成绩 |
| GET | `/api/teacher/stats/{courseId}` | 成绩统计 |

**POST /api/teacher/grades — 请求体：**

```json
{
  "enrollmentId": 1,
  "score": 90.0
}
```

**GET /api/teacher/stats/{courseId} — 响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "0-59": 1,
    "60-69": 2,
    "70-79": 3,
    "80-89": 3,
    "90-100": 1,
    "passRate": "90.00%"
  }
}
```

### 4.6 消息管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/teacher/messages` | 给学生发消息 |
| GET | `/api/teacher/messages` | 查看自己的消息 |

**POST /api/teacher/messages — 请求体：**

```json
{
  "courseId": 2,
  "studentId": 1,
  "content": "同学你好，请按时完成作业"
}
```

---

## 5. 学生接口

> 角色：`ROLE_STUDENT`  
> 前缀：`/api/student/**`

### 5.1 选课管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/student/enroll` | 选课（含先修检查） |
| GET | `/api/student/enrollments` | 查询已选课程 |

**POST /api/student/enroll — 请求体：**

```json
{
  "courseId": 2
}
```

**先修课程检查规则：**

课程有先修课程时，必须满足：
1. 学生有先修课程的选课记录
2. 该记录状态为 `SELECTED`
3. 成绩 ≥ 60

不满足时返回：`{"code": 400, "message": "您没有学习该课程的先修课程，不能进行选课！", "data": null}`

### 5.2 个人信息

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/info` | 查询个人信息 |

### 5.3 课程浏览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/courses` | 浏览全部课程信息 |

### 5.4 成绩查询

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/grades` | 查询成绩（仅返回 SELECTED 状态的记录） |

### 5.5 课件下载

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/courseware/{courseId}` | 下载课件（返回文件流） |

### 5.6 消息管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/student/messages` | 给教师发消息 |
| GET | `/api/student/messages` | 查看/回复教师留言 |

**POST /api/student/messages — 请求体：**

```json
{
  "courseId": 2,
  "teacherId": 1,
  "content": "老师，请问这个知识点怎么理解？"
}
```

### 5.7 抽签结果

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/draw-result` | 查询自己所有选课的抽签结果 |

---

## 6. 统一响应格式

**成功：**

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**业务失败：**

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
```

**无权限：**

```json
{
  "code": 403,
  "message": "无权限访问",
  "data": null
}
```

**服务器错误：**

```json
{
  "code": 500,
  "message": "服务器内部错误: 错误详情",
  "data": null
}
```

---

## 7. 认证方式

所有非公开接口需要在请求头中传递 Token：

```
Authorization: Bearer <token>
```

Token 通过登录接口获取，存储在 `TokenStore`（内存 `ConcurrentHashMap`）中。  
系统重启后 Token 失效，需重新登录。