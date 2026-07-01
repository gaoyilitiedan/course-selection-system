学生选课系统 —— 项目架构与约束文档 v1.0
------------------------

### 项目基本信息

| 项目        | 说明                                           |
| --------- | -------------------------------------------- |
| **项目名称**  | student-course-system                        |
| **语言/版本** | Java 17                                      |
| **框架**    | Spring Boot 3.x                              |
| **数据库**   | H2 内存数据库                                     |
| **ORM**   | Spring Data JPA                              |
| **构建工具**  | Maven                                        |
| **图表库**   | JFreeChart 1.5.4                             |
| **认证方式**  | 自实现 Token 认证（TokenStore），不引入 Spring Security |
| **包名**    | com.course                                   |

* * *

### 一、完整目录结构

text

src/
├── main/
│   ├── java/com/course/
│   │   ├── CourseApplication.java          // 启动类 + 示例数据初始化
│   │   ├── config/
│   │   │   └── WebConfig.java              // 跨域配置
│   │   ├── controller/
│   │   │   ├── AuthController.java         // 登录认证
│   │   │   ├── AdminController.java        // 系统管理员
│   │   │   ├── DeptAdminController.java    // 学院管理员
│   │   │   ├── TeacherController.java      // 教师
│   │   │   └── StudentController.java      // 学生
│   │   ├── entity/
│   │   │   ├── User.java                   // 用户基类
│   │   │   ├── Student.java                // 学生（继承User）
│   │   │   ├── Teacher.java                // 教师（继承User）
│   │   │   ├── Department.java             // 学院
│   │   │   ├── Course.java                 // 课程
│   │   │   ├── Enrollment.java             // 选课记录
│   │   │   ├── Message.java                // 留言消息
│   │   │   └── Courseware.java             // 课件
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── StudentRepository.java
│   │   │   ├── TeacherRepository.java
│   │   │   ├── DepartmentRepository.java
│   │   │   ├── CourseRepository.java
│   │   │   ├── EnrollmentRepository.java
│   │   │   ├── MessageRepository.java
│   │   │   └── CoursewareRepository.java
│   │   ├── service/
│   │   │   ├── AdminService.java
│   │   │   ├── DeptAdminService.java
│   │   │   ├── TeacherService.java
│   │   │   ├── StudentService.java
│   │   │   └── StatisticsService.java
│   │   ├── util/
│   │   │   └── TokenStore.java             // 内存Token管理
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java // 统一异常处理
│   │       └── BusinessException.java      // 业务异常
│   └── resources/
│       ├── application.properties          // 配置文件
│       └── uploads/                        // 课件上传目录
├── pom.xml
└── README.md

* * *

### 二、UML 架构强约束

> **核心原则：所有代码必须严格遵循已生成的 UML 图结构，严禁偏离或修改架构设计。**

#### 1. UML 图法律效力声明

以下 8 份 UML 图构成系统的**完整架构契约**，任何代码实现必须与之完全一致：

| 编号     | 图名称   | 约束对象                     |
| ------ | ----- | ------------------------ |
| **图1** | 用例图   | 功能边界、角色权限                |
| **图2** | 数据流图  | 数据处理路径                   |
| **图3** | 状态转换图 | `Enrollment.status` 状态流转 |
| **图4** | E-R 图 | 实体、属性、关系                 |
| **图5** | 系统结构图 | 分层架构、模块划分                |
| **图6** | 系统类图  | 类名、字段、方法签名               |
| **图7** | 顺序图   | 方法调用顺序与对象交互              |
| **图8** | 程序流程图 | 抽签算法逻辑                   |

* * *

#### 2.分层架构强制约束（图5：系统结构图）

text

┌─────────────────────────────────┐
│         表现层（Controller）      │
│  AuthController                 │
│  AdminController                │
│  DeptAdminController            │
│  TeacherController              │
│  StudentController              │
└──────────────┬──────────────────┘
               │ 调用
┌──────────────▼──────────────────┐
│         业务层（Service）         │
│  AuthService                    │
│  AdminService                   │
│  DeptAdminService               │
│  TeacherService                 │
│  StudentService                 │
│  StatisticsService              │
└──────────────┬──────────────────┘
               │ 调用
┌──────────────▼──────────────────┐
│        数据层（Repository）       │
│  UserRepository                 │
│  StudentRepository              │
│  TeacherRepository              │
│  DepartmentRepository           │
│  CourseRepository               │
│  EnrollmentRepository           │
│  MessageRepository              │
│  CoursewareRepository           │
└─────────────────────────────────┘

**强制约束**：

* ❌ **严禁** Controller 直接调用 Repository

* ❌ **严禁** 在 Controller 中编写业务逻辑

* ✅ Controller 只能调用对应的 Service

* ✅ Service 只能调用 Repository 或其它 Service

* ✅ 每个 Service 对应一个 Controller，`StatisticsService` 可被多个 Controller 调用

* * *

#### 2. 实体与关系强制约束（图4：E-R 图 + 图6：系统类图）

**实体类名必须与以下完全一致**：

| 实体类          | 对应表名          | 继承关系         |
| ------------ | ------------- | ------------ |
| `User`       | `users`       | 基类           |
| `Student`    | `students`    | extends User |
| `Teacher`    | `teachers`    | extends User |
| `Department` | `departments` | -            |
| `Course`     | `courses`     | -            |
| `Enrollment` | `enrollments` | -            |
| `Message`    | `messages`    | -            |
| `Courseware` | `coursewares` | -            |

**关系强制约束**（E-R 图定义）：

text

Department 1──* Student        （一个学院包含多个学生）
Department 1──* Teacher        （一个学院包含多个教师）
Department 1──* Course         （一个学院开设多门课程）
Teacher    1──* Course         （一个教师授课多门课程）
Student    1──* Enrollment     （一个学生有多条选课记录）
Course     1──* Enrollment     （一门课程有多条选课记录）
Student    1──* Message        （一个学生可发送多条消息）
Teacher    1──* Message        （一个教师可发送多条消息）
Course     1──* Message        （消息关联课程）
Course     1──* Courseware     （一门课程有多个课件）
Course     *──1 Course         （先修课程自关联）

**强制约束**：

* ❌ **严禁**修改实体类名、表名映射

* ❌ **严禁**增删实体间的关系

* ❌ **严禁**修改继承结构

* ✅ 所有 JPA 注解必须与图4 E-R 图一致

* * *

#### 4. 状态转换强制约束（图3：状态转换图）

`Enrollment.status` 的合法状态枚举值**必须**为：

java

public enum Status {
    PENDING,    // 待抽签
    SELECTED,   // 中签
    REJECTED    // 未中签
}

**状态流转路径**（唯一合法路径）：

text

PENDING ──抽签──→ SELECTED   （中签）
PENDING ──抽签──→ REJECTED   （未中签）
SELECTED ──录入成绩──→ [终结]
REJECTED ──→ [终结]

**强制约束**：

* ❌ **严禁**增加或删除状态值

* ❌ **严禁**修改状态枚举名称

* ❌ **严禁**出现图3中不存在的状态流转路径

* ✅ 初始状态必须为 `PENDING`

* ✅ 抽签操作只能由学院管理员触发

* * *

#### 5. 顺序图实现强制约束（图7：学生选课顺序图）

**学生选课方法调用链必须严格遵循**：

text

StudentController.enroll()
  └─→ StudentService.enroll(studentId, courseId)
        ├─→ CourseRepository.findById(courseId)
        ├─→ 检查先修课程逻辑
        │    └─→ EnrollmentRepository.findByStudentAndCourse(student, prerequisite)
        └─→ EnrollmentRepository.save(enrollment)

**强制约束**：

* ❌ **严禁**在 Controller 中直接操作 Repository

* ❌ **严禁**改变方法调用顺序

* ❌ **严禁**在 Service 中绕过先修课程检查

* ✅ `StudentService.enroll()` 签名必须为 `enroll(Student student, Long courseId)`

* ✅ 先修课程检查失败时，必须抛出异常返回错误消息

* * *

#### 6. 抽签算法强制约束（图8：程序流程图）

**抽签算法实现必须严格按照以下流程**：

text

1. 获取课程信息及该课程所有 PENDING 状态选课记录
2. 判断：选课人数 > 课程容量(capacity) ？
   ├── 否 → 将所有 PENDING 记录设为 SELECTED
   └── 是 → 随机选取 capacity 个设为 SELECTED
   
            → 其余设为 REJECTED
3. 批量更新数据库

**强制约束**：

* ❌ **严禁**修改判断条件（人数 > capacity）

* ❌ **严禁**修改随机选取逻辑

* ❌ **严禁**改变算法执行顺序

* ✅ 抽签入口方法名必须为 `draw(Long courseId)`

* ✅ 必须使用 `Collections.shuffle()` 实现随机

* * *

#### 7. 用例功能覆盖强制约束（图1：用例图）

**系统必须实现以下全部用例，不得遗漏或增删**：

| 角色    | 用例                  | 对应 API 编号    |
| ----- | ------------------- | ------------ |
| 系统管理员 | 管理学生/教师/课程/成绩（增删改查） | 2-18         |
| 系统管理员 | 统计选课人数并导出柱状图        | 19           |
| 学院管理员 | 管理本学院师生/课程/成绩       | 20-35, 38-39 |
| 学院管理员 | 抽签并生成中签名单           | 36-37        |
| 教师    | 编辑课程信息/上传课件/设置先修    | 40-43        |
| 教师    | 查看中签学生              | 44           |
| 教师    | 录入成绩/生成统计           | 45-46        |
| 教师    | 给学生发消息              | 47-48        |
| 学生    | 选课/查看已选课程           | 49-50        |
| 学生    | 查看个人信息              | 51           |
| 学生    | 浏览课程/查看成绩/下载课件      | 52-54        |
| 学生    | 与教师留言               | 55-56        |
| 学生    | 查询抽签结果              | 57           |

**强制约束**：

* ❌ **严禁**遗漏任何一个用例对应的 API

* ❌ **严禁**增加用例图中未出现的功能

* ✅ 每个 API 必须有明确的角色归属

* * *

#### 8. 数据流路径强制约束（图2：数据流图）

**数据必须按照以下路径流动**：

text

用户请求 → Controller → Service → Repository → 数据库
数据库 → Repository → Service → Controller → 用户响应（JSON/文件流）

**强制约束**：

* ❌ **严禁** Controller 直接返回数据库实体

* ❌ **严禁** 跳过 Service 层

* ❌ **严禁** 在 Repository 层处理业务逻辑

* ✅ 文件下载必须通过 Controller 返回 `ResponseEntity<byte[]>`

* ✅ 柱状图导出必须通过 `StatisticsService` 生成字节流

* * *

#### 9. UML 一致性检查清单

在代码完成后，必须通过以下检查：

| 检查项         | 标准                                     |
| ----------- | -------------------------------------- |
| 类图与实体一致性    | 实体类名、字段名、类型、关系与类图完全一致                  |
| 顺序图与调用链一致性  | 方法调用顺序、参数、返回值与顺序图一致                    |
| 状态图与枚举一致性   | 状态枚举值、流转逻辑与状态图一致                       |
| 流程图与算法一致性   | 抽签算法流程与程序流程图一致                         |
| 结构图与分层一致性   | Controller/Service/Repository 分层与结构图一致 |
| E-R图与数据库一致性 | 表名、列名、关系与E-R图一致                        |
| 用例图与API一致性  | 所有用例均有对应API实现，无遗漏无增加                   |

* * *

> **声明**：本约束文档是学生选课系统开发的**唯一合法依据**，所有代码实现必须以本文档及其中引用的 8 份 UML 图为最终标准。任何偏离 UML 架构的代码均视为不合格，必须重构至完全符合约束为止。

### 三、核心约束规则

#### 1. 实体约束

text

User (基类)
├── id: Long (自增主键)
├── username: String (唯一, 非空)
├── password: String (非空, 明文存储, 简化实验)
├── role: String (枚举: ROLE_ADMIN, ROLE_DEPT_ADMIN, ROLE_TEACHER, ROLE_STUDENT)
└── department: Department (多对一, 可为null)
Student extends User
├── studentNo: String
└── name: String
Teacher extends User
├── teacherNo: String
└── name: String
Department
├── id: Long
└── name: String
Course
├── id: Long
├── name: String
├── description: String (课程介绍)
├── capacity: int (容量)
├── prerequisite: Course (先修课程, 多对一, 可为null)
├── teacher: Teacher (授课教师, 多对一)
└── department: Department (开设学院, 多对一)
Enrollment
├── id: Long
├── student: Student (多对一)
├── course: Course (多对一)
├── status: Status (枚举: PENDING, SELECTED, REJECTED)
├── score: Double (可为null, 成绩)
└── (student, course) 唯一约束
Message
├── id: Long
├── fromUserType: String (发送方类型: TEACHER/STUDENT)
├── fromUserId: Long
├── toUserType: String
├── toUserId: Long
├── course: Course (多对一)
├── content: String
└── createTime: LocalDateTime
Courseware
├── id: Long
├── course: Course (多对一)
├── fileName: String
└── filePath: String

* * *

#### 2. API 路径与鉴权约束

| 角色    | API 前缀            | Token Header                    |
| ----- | ----------------- | ------------------------------- |
| 系统管理员 | `/api/admin/**`   | `Authorization: Bearer <token>` |
| 学院管理员 | `/api/dept/**`    | `Authorization: Bearer <token>` |
| 教师    | `/api/teacher/**` | `Authorization: Bearer <token>` |
| 学生    | `/api/student/**` | `Authorization: Bearer <token>` |
| 公开    | `/api/auth/**`    | 无需Token                         |

**Token 传递方式**：从请求头 `Authorization` 中提取 `Bearer` 后的值。  
**Token 存储**：`TokenStore` 使用 `ConcurrentHashMap<String, User>` 在内存中维护。

* * *

#### 3. 接口清单（必须全部实现，不得增减）

| 序号  | 接口                                           | 角色    | 说明             |
| --- | -------------------------------------------- | ----- | -------------- |
| 1   | `POST /api/auth/login`                       | 公开    | 登录，返回token     |
| 2   | `POST /api/admin/students`                   | 系统管理员 | 新增学生           |
| 3   | `PUT /api/admin/students/{id}`               | 系统管理员 | 修改学生           |
| 4   | `DELETE /api/admin/students/{id}`            | 系统管理员 | 删除学生           |
| 5   | `GET /api/admin/students`                    | 系统管理员 | 查询学生列表         |
| 6   | `POST /api/admin/teachers`                   | 系统管理员 | 新增教师           |
| 7   | `PUT /api/admin/teachers/{id}`               | 系统管理员 | 修改教师           |
| 8   | `DELETE /api/admin/teachers/{id}`            | 系统管理员 | 删除教师           |
| 9   | `GET /api/admin/teachers`                    | 系统管理员 | 查询教师列表         |
| 10  | `POST /api/admin/courses`                    | 系统管理员 | 新增课程           |
| 11  | `PUT /api/admin/courses/{id}`                | 系统管理员 | 修改课程           |
| 12  | `DELETE /api/admin/courses/{id}`             | 系统管理员 | 删除课程           |
| 13  | `GET /api/admin/courses`                     | 系统管理员 | 查询课程列表         |
| 14  | `POST /api/admin/enrollments`                | 系统管理员 | 手动增加选课记录       |
| 15  | `PUT /api/admin/enrollments/{id}`            | 系统管理员 | 修改选课记录         |
| 16  | `DELETE /api/admin/enrollments/{id}`         | 系统管理员 | 删除选课记录         |
| 17  | `GET /api/admin/enrollments`                 | 系统管理员 | 查询选课记录         |
| 18  | `POST /api/admin/grades`                     | 系统管理员 | 录入/修改成绩        |
| 19  | `GET /api/admin/stats/chart`                 | 系统管理员 | 导出选课人数柱状图(PNG) |
| 20  | `POST /api/dept/students`                    | 学院管理员 | 本学院新增学生        |
| 21  | `PUT /api/dept/students/{id}`                | 学院管理员 | 本学院修改学生        |
| 22  | `DELETE /api/dept/students/{id}`             | 学院管理员 | 本学院删除学生        |
| 23  | `GET /api/dept/students`                     | 学院管理员 | 本学院学生列表        |
| 24  | `POST /api/dept/teachers`                    | 学院管理员 | 本学院新增教师        |
| 25  | `PUT /api/dept/teachers/{id}`                | 学院管理员 | 本学院修改教师        |
| 26  | `DELETE /api/dept/teachers/{id}`             | 学院管理员 | 本学院删除教师        |
| 27  | `GET /api/dept/teachers`                     | 学院管理员 | 本学院教师列表        |
| 28  | `POST /api/dept/courses`                     | 学院管理员 | 本学院新增课程        |
| 29  | `PUT /api/dept/courses/{id}`                 | 学院管理员 | 本学院修改课程        |
| 30  | `DELETE /api/dept/courses/{id}`              | 学院管理员 | 本学院删除课程        |
| 31  | `GET /api/dept/courses`                      | 学院管理员 | 本学院课程列表        |
| 32  | `POST /api/dept/enrollments`                 | 学院管理员 | 本学院增加选课        |
| 33  | `PUT /api/dept/enrollments/{id}`             | 学院管理员 | 本学院修改选课        |
| 34  | `DELETE /api/dept/enrollments/{id}`          | 学院管理员 | 本学院删除选课        |
| 35  | `GET /api/dept/enrollments`                  | 学院管理员 | 本学院选课列表        |
| 36  | `POST /api/dept/draw/{courseId}`             | 学院管理员 | 对本学院课程抽签       |
| 37  | `GET /api/dept/draw/{courseId}/result`       | 学院管理员 | 查看中签学生名单       |
| 38  | `POST /api/dept/grades`                      | 学院管理员 | 录入/修改本学院成绩     |
| 39  | `GET /api/dept/grades`                       | 学院管理员 | 查看本学院成绩        |
| 40  | `PUT /api/teacher/courses/{id}`              | 教师    | 更新课程信息/介绍      |
| 41  | `POST /api/teacher/courses/{id}/courseware`  | 教师    | 上传课件           |
| 42  | `GET /api/teacher/courses/{id}/courseware`   | 教师    | 查看课件列表         |
| 43  | `PUT /api/teacher/courses/{id}/prerequisite` | 教师    | 设置先修课程         |
| 44  | `GET /api/teacher/enrollments/selected`      | 教师    | 查看自己课程中签学生     |
| 45  | `POST /api/teacher/grades`                   | 教师    | 录入学生成绩         |
| 46  | `GET /api/teacher/stats/{courseId}`          | 教师    | 成绩统计（分段人数、及格率） |
| 47  | `POST /api/teacher/messages`                 | 教师    | 给学生发消息         |
| 48  | `GET /api/teacher/messages`                  | 教师    | 查看自己的消息        |
| 49  | `POST /api/student/enroll`                   | 学生    | 选课（含先修检查）      |
| 50  | `GET /api/student/enrollments`               | 学生    | 查询已选课程         |
| 51  | `GET /api/student/info`                      | 学生    | 查询个人信息         |
| 52  | `GET /api/student/courses`                   | 学生    | 浏览课程信息         |
| 53  | `GET /api/student/grades`                    | 学生    | 查询成绩           |
| 54  | `GET /api/student/courseware/{courseId}`     | 学生    | 下载课件           |
| 55  | `POST /api/student/messages`                 | 学生    | 给教师发消息         |
| 56  | `GET /api/student/messages`                  | 学生    | 查看/回复教师留言      |
| 57  | `GET /api/student/draw-result`               | 学生    | 查询自己选课抽签结果     |

* * *

#### 4. 统一响应格式

json

// 成功
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
// 失败
{
  "code": 400,
  "message": "错误描述",
  "data": null
}

使用 `@ControllerAdvice` 统一处理异常，确保所有响应格式一致。

* * *

#### 5. 文件上传/下载规范

* 上传目录：`src/main/resources/uploads/`

* 文件访问通过 Controller 接口 `/api/teacher/courses/{id}/courseware` 和 `/api/student/courseware/{courseId}`

* 课件存储在文件系统，实体 `Courseware` 记录文件名和路径

* * *

#### 6. 先修课程检查规则

学生选课时，必须满足以下任一条件才可选课：

1. 课程无先修课程（prerequisite = null）

2. 学生在先修课程中有选课记录，且状态为 `SELECTED`，且成绩 ≥ 60

不满足时返回错误消息：`"您没有学习该课程的先修课程，不能进行选课！"`

* * *

#### 7. 统计图表规范

* 系统管理员导出选课人数柱状图，使用 `JFreeChart` 生成 PNG 格式

* 横轴：课程名称，纵轴：选课人数（所有 status 均计入）

* 响应 `Content-Type: image/png`，`Content-Disposition: attachment; filename=chart.png`

* * *

#### 8. 抽签规则

* 学院管理员对某课程执行抽签

* 从所有 `PENDING` 状态的选课记录中，随机选取 `capacity` 个设为 `SELECTED`，其余设为 `REJECTED`

* 只有本学院管理员可操作本学院课程

* * *

#### 9. 成绩统计规则

* 教师查看某课程成绩统计

* 返回分数段（0-59, 60-69, 70-79, 80-89, 90-100）人数及及格率

* 仅统计 `SELECTED` 状态的选课记录

* * *

#### 10. H2 数据库配置

properties

spring.datasource.url=jdbc:h2:mem:coursedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

* * *

### 四、开发注意事项

1. **不要引入 Spring Security**，认证使用 `TokenStore`

2. **所有控制器必须标注 `@RestController`**

3. **实体类使用 `@Entity` + `@Table`，继承关系使用 `@Inheritance(strategy = InheritanceType.JOINED)`**

4. **角色校验在每个 Controller 方法开头通过 Token 获取 User 并检查 role**

5. **不实现前端页面**，纯后端 REST API

6. **不添加需求文档未提及的功能**

7. **所有枚举类型使用 `@Enumerated(EnumType.STRING)`**

8. **日期统一使用 `LocalDateTime`**

9. **课程容量为 `int`，不做负数校验（简化）**

10. **密码明文存储，不做加密（简化学生实验）**
