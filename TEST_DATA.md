# 学生选课系统 — 测试数据文档

> 系统启动时 `CommandLineRunner` 自动初始化以下测试数据。  
> 数据库使用 H2 内存模式，每次重启数据重置。

---

## 1. 学院数据

| ID | 学院名称 |
|----|----------|
| 1 | 计算机科学与技术学院 |
| 2 | 数学与统计学院 |

---

## 2. 用户数据

### 2.1 系统管理员

| 用户名 | 密码 | 角色 | 所属学院 |
|--------|------|------|----------|
| admin | admin123 | ROLE_ADMIN | 无 |

### 2.2 学院管理员

| 用户名 | 密码 | 角色 | 所属学院 |
|--------|------|------|----------|
| csadmin | csadmin123 | ROLE_DEPT_ADMIN | 计算机科学与技术学院 |
| mathadmin | mathadmin123 | ROLE_DEPT_ADMIN | 数学与统计学院 |

### 2.3 教师

| 用户名 | 密码 | 角色 | 工号 | 姓名 | 所属学院 |
|--------|------|------|------|------|----------|
| teacher1 | 123456 | ROLE_TEACHER | T001 | 张教授 | 计算机科学与技术学院 |
| teacher2 | 123456 | ROLE_TEACHER | T002 | 李教授 | 数学与统计学院 |

### 2.4 学生

| 用户名 | 密码 | 角色 | 学号 | 姓名 | 所属学院 |
|--------|------|------|------|------|----------|
| student1 | 123456 | ROLE_STUDENT | S2024001 | 王同学 | 计算机科学与技术学院 |
| student2 | 123456 | ROLE_STUDENT | S2024002 | 赵同学 | 计算机科学与技术学院 |
| student3 | 123456 | ROLE_STUDENT | S2024003 | 陈同学 | 数学与统计学院 |

---

## 3. 课程数据

| ID | 课程名称 | 容量 | 先修课程 | 授课教师 | 开设学院 |
|----|----------|------|----------|----------|----------|
| 1 | Java程序设计基础 | 100 | 无 | 张教授 (T001) | 计算机科学与技术学院 |
| 2 | Java Web开发 | 2 | Java程序设计基础 (ID:1) | 张教授 (T001) | 计算机科学与技术学院 |
| 3 | 高等数学 | 30 | 无 | 李教授 (T002) | 数学与统计学院 |

> **注意：** Java Web 开发容量设为 2，便于测试"选课人数 > 容量"的抽签场景。  
> 如果选课人数 > 2，则会触发随机抽签逻辑。

---

## 4. 选课记录数据

| ID | 学生 | 课程 | 状态 | 成绩 |
|----|------|------|------|------|
| 1 | 王同学 (S2024001) | Java程序设计基础 | SELECTED | 85.0 |
| 2 | 赵同学 (S2024002) | Java程序设计基础 | SELECTED | 92.0 |
| 3 | 王同学 (S2024001) | Java Web开发 | PENDING | - |
| 4 | 赵同学 (S2024002) | Java Web开发 | PENDING | - |
| 5 | 陈同学 (S2024003) | 高等数学 | PENDING | - |

### 说明

- **记录 1、2**：王同学和赵同学已通过 Java 程序设计基础（成绩 ≥ 60），满足 Java Web 开发的先修条件
- **记录 3、4**：王同学和赵同学已选 Java Web 开发，等待学院管理员抽签
- **记录 5**：陈同学已选高等数学，等待学院管理员抽签

---

## 5. 测试场景预置

### 场景 1：先修课程检查

```
学生：student1 / password: 123456
选课接口：POST /api/student/enroll
请求体：{"courseId": 2}  →  Java Web开发
结果：成功（已有先修课程 SELECTED 且成绩 ≥ 60）
```

### 场景 2：选课抽签（选课人数 > 容量）

```
课程：Java Web开发（容量=2，已有2人选课）
操作：学院管理员执行 POST /api/dept/draw/2
结果：2人 ≤ 容量2 → 全部 SELECTED（不会触发随机）
若要测试随机抽签，需先新增第3个学生选该课程
```

### 场景 3：选课抽签（触发随机）

```
步骤1：以系统管理员 admin 登录
步骤2：POST /api/admin/students 新增一个学生
步骤3：以新学生身份登录，POST /api/student/enroll 选 Java Web开发
步骤4：以 csadmin 登录，POST /api/dept/draw/2
结果：3人 > 容量2 → 随机选2人 SELECTED，1人 REJECTED
```

### 场景 4：成绩管理

```
操作：教师 teacher1 登录
接口：POST /api/teacher/grades
请求体：{"enrollmentId": 1, "score": 90.0}
```

### 场景 5：柱状图导出

```
操作：系统管理员 admin 登录
接口：GET /api/admin/stats/chart
结果：下载 PNG 格式的选课人数柱状图
```

---

## 6. 快速测试 cURL 示例

### 6.1 登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"admin\", \"password\": \"admin123\"}"
```

### 6.2 查询学生列表（管理员）

```bash
curl -X GET http://localhost:8080/api/admin/students \
  -H "Authorization: Bearer <token>"
```

### 6.3 学生选课

```bash
curl -X POST http://localhost:8080/api/student/enroll \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"courseId\": 2}"
```

### 6.4 学院管理员抽签

```bash
curl -X POST http://localhost:8080/api/dept/draw/2 \
  -H "Authorization: Bearer <token>"
```

### 6.5 查看中签名单

```bash
curl -X GET http://localhost:8080/api/dept/draw/2/result \
  -H "Authorization: Bearer <token>"
```

### 6.6 查询抽签结果（学生）

```bash
curl -X GET http://localhost:8080/api/student/draw-result \
  -H "Authorization: Bearer <token>"
```

### 6.7 教师录入成绩

```bash
curl -X POST http://localhost:8080/api/teacher/grades \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"enrollmentId\": 1, \"score\": 92.0}"
```

### 6.8 导出柱状图

```bash
curl -X GET http://localhost:8080/api/admin/stats/chart \
  -H "Authorization: Bearer <token>" \
  --output chart.png
```