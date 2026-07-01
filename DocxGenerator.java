import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.*;

/**
 * 学生选课系统 — 实验五 Word文档生成器
 * 使用纯Java标准库（无外部依赖）生成 .docx 格式文档
 * .docx 本质是 ZIP 包，内含 XML 文件
 */
public class DocxGenerator {

    // ========== 内容数据 ==========
    // 所有文档内容在此定义，便于维护

    static final String[] HEADINGS = {
        "实验五 软件系统实现与测试 — 学生选课系统",
        "一、系统需求概述",
        "1.1 系统的数据流图 (DFD)",
        "1.2 数据库的E-R图",
        "1.3 状态转换图",
        "1.4 用例图",
        "1.5 数据字典",
        "二、系统概要设计 — 系统结构图",
        "三、系统详细设计",
        "3.1 系统类图",
        "3.2 顺序图",
        "3.3 程序流程图"
    };

    static final String DOC_TITLE = "实验五 软件系统实现与测试 — 学生选课系统需求分析与设计文档";

    public static void main(String[] args) throws Exception {
        String outputPath = "实验五_学生选课系统_需求分析与设计.docx";
        if (args.length > 0) outputPath = args[0];

        Path docxPath = Paths.get(outputPath);
        Files.deleteIfExists(docxPath);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(docxPath.toFile()))) {
            // 1. [Content_Types].xml
            addZipEntry(zos, "[Content_Types].xml", contentTypesXml());

            // 2. _rels/.rels
            addZipEntry(zos, "_rels/.rels", relsXml());

            // 3. word/_rels/document.xml.rels
            addZipEntry(zos, "word/_rels/document.xml.rels", docRelsXml());

            // 4. word/document.xml
            addZipEntry(zos, "word/document.xml", documentXml());
        }

        System.out.println("文档已生成: " + docxPath.toAbsolutePath());
    }

    static void addZipEntry(ZipOutputStream zos, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setSize(content.length);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }

    // ==================== XML 生成 ====================

    static byte[] contentTypesXml() {
        return ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
            "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
            "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
            "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>\n" +
            "</Types>").getBytes(StandardCharsets.UTF_8);
    }

    static byte[] relsXml() {
        return ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
            "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>\n" +
            "</Relationships>").getBytes(StandardCharsets.UTF_8);
    }

    static byte[] docRelsXml() {
        return ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
            "</Relationships>").getBytes(StandardCharsets.UTF_8);
    }

    // ==================== 主文档 XML ====================
    // 这是最核心的部分，包含所有文档内容

    static byte[] documentXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        sb.append("<w:document xmlns:wpc=\"http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas\"\n");
        sb.append("  xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n");
        sb.append("  xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n");
        sb.append("  xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\">\n");
        sb.append("<w:body>\n");

        // ===== 文档属性 =====
        addDocProps(sb);

        // ===== 封面 =====
        addCoverPage(sb);

        // ===== 分页：第1章 系统需求概述 =====
        addPageBreak(sb);
        addChapter1(sb);

        // ===== 分页：1.1 数据流图 =====
        addPageBreak(sb);
        addSectionDFD(sb);

        // ===== 分页：1.2 E-R图 =====
        addPageBreak(sb);
        addSectionER(sb);

        // ===== 分页：1.3 状态转换图 =====
        addPageBreak(sb);
        addSectionState(sb);

        // ===== 分页：1.4 用例图 =====
        addPageBreak(sb);
        addSectionUseCase(sb);

        // ===== 分页：1.5 数据字典 =====
        addPageBreak(sb);
        addSectionDataDict(sb);

        // ===== 第2章 系统概要设计 =====
        addPageBreak(sb);
        addChapter2(sb);

        // ===== 第3章 系统详细设计 =====
        addPageBreak(sb);
        addChapter3(sb);

        // ===== 3.1 类图 =====
        addPageBreak(sb);
        addSectionClassDiagram(sb);

        // ===== 3.2 顺序图 =====
        addPageBreak(sb);
        addSectionSequence(sb);

        // ===== 3.3 程序流程图 =====
        addPageBreak(sb);
        addSectionFlowChart(sb);

        // ===== 附录 =====
        addPageBreak(sb);
        addAppendix(sb);

        sb.append("</w:body>\n</w:document>");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ==================== 文档属性 ====================
    static void addDocProps(StringBuilder sb) {
        sb.append("<w:sectPr>\n");
        sb.append("  <w:pgSz w:w=\"11906\" w:h=\"16838\"/>\n"); // A4
        sb.append("  <w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\"/>\n");
        sb.append("</w:sectPr>\n");
    }

    // ==================== 封面 ====================
    static void addCoverPage(StringBuilder sb) {
        // 空行
        for (int i = 0; i < 6; i++) addEmptyPara(sb);

        // 标题
        addCenteredPara(sb, "实验五 软件系统实现与测试", 44, true);
        addEmptyPara(sb);
        addEmptyPara(sb);
        addCenteredPara(sb, "学生选课系统", 40, true);
        addCenteredPara(sb, "需求分析与设计文档", 36, true);
        addEmptyPara(sb);
        addEmptyPara(sb);
        addEmptyPara(sb);

        // 信息表
        String[][] coverInfo = {
            {"课程名称：", "软件工程"},
            {"实验名称：", "软件系统实现与测试"},
            {"系统名称：", "学生选课系统"},
            {"技术栈：",   "Spring Boot 3 + Vue 3 + H2"},
            {"文档日期：", "2026年6月28日"}
        };
        // 使用居中段落显示信息
        for (String[] row : coverInfo) {
            addCenteredPara(sb, row[0] + "　" + row[1], 24, false);
        }
    }

    // ==================== 第1章 系统需求概述 ====================
    static void addChapter1(StringBuilder sb) {
        addHeading(sb, "一、系统需求概述", 1);
        addHeading(sb, "1. 系统功能需求描述", 2);

        addPara(sb, "学生选课系统是一个基于B/S架构的大学课程选课平台，为系统管理员、学院管理员、教师和学生四类角色提供差异化的功能服务。系统需满足以下核心业务需求：");

        addPara(sb, "（1）系统管理员功能：对学生、教师、课程和选课记录进行增删改查操作；对选课数据进行统计并生成柱状图导出。");
        addPara(sb, "（2）学院管理员功能：对本学院的学生、教师、课程、选课记录和成绩进行增删改查；对本学院课程执行抽签操作，生成中签学生名单。");
        addPara(sb, "（3）教师功能：编辑课程信息与介绍、上传课件、设置先修课程；查看选修自己课程的中签学生名单；录入课程成绩并生成成绩统计表（含各分数段人数与及格率）；向选修自己课程的学生发送消息留言。");
        addPara(sb, "（4）学生功能：浏览课程信息并根据学号密码登录选课系统；查询已选课程与个人信息；选课时系统自动检查先修课程条件（先修课程未修或成绩不合格时拒绝选课并给出提示）；查询选课抽签结果与课程成绩；下载课程课件；与任课教师进行消息通信。");

        addHeading(sb, "核心业务规则", 3);
        addPara(sb, "• 先修课程检查：学生选课时，若课程设有先修课程，学生必须在先修课程中有状态为SELECTED且成绩≥60的选课记录，否则提示\"您没有学习该课程的先修课程，不能进行选课！\"。");
        addPara(sb, "• 抽签算法：学院管理员对课程执行抽签时，若待抽签人数≤课程容量则全部中签；否则随机抽取容量数量学生为中签，其余为未中签。");
        addPara(sb, "• 状态流转：选课状态从PENDING开始，经抽签变为SELECTED或REJECTED，SELECTED后录入成绩到达终态。");
        addPara(sb, "• 学院隔离：学院管理员和教师只能操作本学院范围内的数据。");
    }

    // ==================== 1.1 数据流图 ====================
    static void addSectionDFD(StringBuilder sb) {
        addHeading(sb, "1.1 系统的数据流图 (DFD)", 1);

        addHeading(sb, "1.1.1 顶层数据流图（上下文图）", 2);
        addPara(sb, "顶层DFD展示系统与外部实体之间的数据交互。学生选课系统与四类外部实体（系统管理员、学院管理员、教师、学生）进行交互，以统一的HTTP请求/响应格式进行数据交换。");
        addPara(sb, "外部实体：系统管理员、学院管理员、教师、学生 → 学生选课系统(P0) → H2数据库/文件系统");
        addPara(sb, "数据流：请求数据（JSON/Token）、响应数据（{code, message, data}）、文件流（课件上传/下载、图表导出）。");

        addHeading(sb, "1.1.2 第0层数据流图", 2);
        addPara(sb, "第0层DFD将系统分解为以下处理模块：");
        addPara(sb, "① 认证处理(1.0) — AuthController：接收登录请求→验证用户名密码→生成Token→返回用户信息");
        addPara(sb, "② 系统管理处理(2.0) — AdminController→AdminService：全局学生/教师/课程/选课/成绩CRUD操作");
        addPara(sb, "③ 学院管理处理(3.0) — DeptAdminController→DeptAdminService：本学院CRUD+抽签操作");
        addPara(sb, "④ 教师功能处理(4.0) — TeacherController→TeacherService：课程编辑/课件上传/先修设置/成绩录入与统计/消息发送");
        addPara(sb, "⑤ 学生功能处理(5.0) — StudentController→StudentService：选课(含先修检查)/浏览课程/查询成绩/下载课件/消息通信");
        addPara(sb, "⑥ 统计处理(6.0) — StatisticsService：查询所有课程及选课人数→JFreeChart生成柱状图→输出PNG字节流");
        addPara(sb, "数据存储：D1-Users表、D2-Students表、D3-Teachers表、D4-Courses表、D5-Enrollments表、D6-Messages表、D7-Coursewares表、D8-文件系统(uploads/)、D9-TokenStore(内存)");

        addHeading(sb, "1.1.3 核心数据流描述", 2);
        addDataFlowTable(sb);
    }

    // ==================== 1.2 E-R图 ====================
    static void addSectionER(StringBuilder sb) {
        addHeading(sb, "1.2 数据库的E-R图", 1);

        addHeading(sb, "1.2.1 实体及属性", 2);
        addPara(sb, "系统包含8个核心实体：User（用户基类）、Student（学生，继承User）、Teacher（教师，继承User）、Department（学院）、Course（课程）、Enrollment（选课记录）、Message（留言消息）、Courseware（课件）。");

        addEntityAttrTable(sb);
        addEmptyPara(sb);

        addHeading(sb, "1.2.2 E-R关系说明", 2);
        addPara(sb, "各实体间关系如下：");
        addPara(sb, "1. Department 1 ── * Student（一个学院包含多名学生）");
        addPara(sb, "2. Department 1 ── * Teacher（一个学院包含多名教师）");
        addPara(sb, "3. Department 1 ── * Course（一个学院开设多门课程）");
        addPara(sb, "4. Teacher 1 ── * Course（一名教师授课多门课程）");
        addPara(sb, "5. Student 1 ── * Enrollment（一名学生有多条选课记录）");
        addPara(sb, "6. Course 1 ── * Enrollment（一门课程有多条选课记录）");
        addPara(sb, "7. Course * ── 0..1 Course（课程自关联先修课程，prerequisite可为null）");
        addPara(sb, "8. Course 1 ── * Message（消息关联课程）");
        addPara(sb, "9. Course 1 ── * Courseware（一门课程可有多个课件）");
        addPara(sb, "10. Student 1 ── * Message（学生可发送/接收多条消息）");
        addPara(sb, "11. Teacher 1 ── * Message（教师可发送/接收多条消息）");
        addPara(sb, "12. User基类与Student/Teacher之间为JOINED继承关系");

        addERRelationTable(sb);
        addEmptyPara(sb);

        addHeading(sb, "1.2.3 数据库表结构", 2);
        addTableSchema(sb);
    }

    // ==================== 1.3 状态转换图 ====================
    static void addSectionState(StringBuilder sb) {
        addHeading(sb, "1.3 状态转换图", 1);
        addPara(sb, "选课记录（Enrollment）的状态转换是系统的核心状态机。Enrollment实体在其生命周期中经历三个合法状态：PENDING（待抽签）、SELECTED（中签）、REJECTED（未中签）。");

        addHeading(sb, "状态流转路径", 2);
        addPara(sb, "初始状态 → PENDING（学生选课时自动设置）");
        addPara(sb, "PENDING → SELECTED（学院管理员抽签：人数≤容量全部中签，或随机抽取中签）");
        addPara(sb, "PENDING → REJECTED（学院管理员抽签：人数>容量且未被抽中）");
        addPara(sb, "SELECTED → [终态]（教师或管理员录入成绩后，score != null）");
        addPara(sb, "REJECTED → [终态]（自动终结，不可再变更状态）");

        addStateTransitionTable(sb);
    }

    // ==================== 1.4 用例图 ====================
    static void addSectionUseCase(StringBuilder sb) {
        addHeading(sb, "1.4 用例图", 1);

        addHeading(sb, "1.4.1 系统用例总览", 2);
        addPara(sb, "系统包含4类参与者（Actor）：系统管理员、学院管理员、教师、学生。");
        addPara(sb, "参与者与用例关系如下：");
        addPara(sb, "• 系统管理员参与用例：登录、管理学生(CRUD)、管理教师(CRUD)、管理课程(CRUD)、管理选课(CRUD)、管理成绩、统计选课并导出柱状图");
        addPara(sb, "• 学院管理员参与用例：登录、本学院学生管理(CRUD)、本学院教师管理(CRUD)、本学院课程管理(CRUD)、本学院选课管理(CRUD)、本学院成绩管理、抽签并生成中签名单");
        addPara(sb, "• 教师参与用例：登录、编辑课程信息/介绍、上传课件、设置先修课程、查看中签学生名单、录入成绩、生成成绩统计、给学生发消息");
        addPara(sb, "• 学生参与用例：登录、选课(含先修检查)、查询已选课程、查看个人信息、浏览课程信息、查询成绩、下载课件、给教师发消息、查询抽签结果");

        addUseCaseTable(sb);
    }

    // ==================== 1.5 数据字典 ====================
    static void addSectionDataDict(StringBuilder sb) {
        addHeading(sb, "1.5 数据字典", 1);

        addHeading(sb, "1.5.1 数据项定义", 2);
        addDataItemTable(sb);
        addEmptyPara(sb);

        addHeading(sb, "1.5.2 数据结构定义", 2);
        addDataStructTable(sb);
        addEmptyPara(sb);

        addHeading(sb, "1.5.3 数据存储定义", 2);
        addDataStoreTable(sb);
        addEmptyPara(sb);

        addHeading(sb, "1.5.4 处理逻辑定义", 2);
        addProcessTable(sb);
    }

    // ==================== 第2章 系统概要设计 ====================
    static void addChapter2(StringBuilder sb) {
        addHeading(sb, "二、系统概要设计", 1);
        addHeading(sb, "2.1 系统结构图", 2);

        addPara(sb, "系统采用严格的三层B/S架构：表现层(Controller)、业务层(Service)、数据层(Repository)。前端为独立的Vue 3 SPA应用，通过HTTP REST API与后端通信。");

        addHeading(sb, "2.1.1 三层架构描述", 3);
        addPara(sb, "【表现层 (Controller Layer)】");
        addPara(sb, "• AuthController — 处理登录请求，验证用户名密码，生成Token");
        addPara(sb, "• AdminController — 系统管理员接口，全局CRUD + 图表导出。路径：/api/admin/**");
        addPara(sb, "• DeptAdminController — 学院管理员接口，本学院CRUD + 抽签。路径：/api/dept/**");
        addPara(sb, "• TeacherController — 教师接口，课程/课件/成绩/消息。路径：/api/teacher/**");
        addPara(sb, "• StudentController — 学生接口，选课/浏览/下载/消息。路径：/api/student/**");
        addPara(sb, "职责：接收HTTP请求、Token鉴权（从Authorization头提取Bearer Token）、参数校验、调用Service、返回统一格式{code, message, data}响应");

        addPara(sb, "【业务层 (Service Layer)】");
        addPara(sb, "• AdminService — 全局学生/教师/课程/选课/成绩CRUD，@Transactional事务管理");
        addPara(sb, "• DeptAdminService — 本学院范围CRUD + draw()抽签（Collections.shuffle()随机抽取）");
        addPara(sb, "• TeacherService — 课程编辑、课件上传、先修设置、成绩录入、成绩统计（分段统计+及格率）");
        addPara(sb, "• StudentService — enroll()选课（含先修检查）、课程浏览、课件下载、消息通信");
        addPara(sb, "• StatisticsService — generateChartBytes() JFreeChart生成选课人数柱状图PNG字节流");

        addPara(sb, "【数据层 (Repository Layer)】");
        addPara(sb, "• 8个Spring Data JPA Repository接口：UserRepository、StudentRepository、TeacherRepository、DepartmentRepository、CourseRepository、EnrollmentRepository、MessageRepository、CoursewareRepository");
        addPara(sb, "• 存储介质：H2内存数据库（jdbc:h2:mem:coursedb，8张表）+ 文件系统（uploads/目录存储课件）+ 内存（TokenStore ConcurrentHashMap管理会话）");

        addHeading(sb, "2.1.2 模块划分", 2);
        addModuleTable(sb);
    }

    // ==================== 第3章 系统详细设计 ====================
    static void addChapter3(StringBuilder sb) {
        addHeading(sb, "三、系统详细设计", 1);
        addPara(sb, "本章包含系统类图、顺序图和程序流程图的详细设计。所有设计均与实际代码实现严格一致。");
    }

    // ==================== 3.1 类图 ====================
    static void addSectionClassDiagram(StringBuilder sb) {
        addHeading(sb, "3.1 系统类图", 1);

        addHeading(sb, "3.1.1 实体类设计", 2);
        addPara(sb, "系统包含8个JPA实体类，采用JOINED继承策略：");
        addPara(sb, "1. User（基类）：id, username, password, role(ROLE_ADMIN/DEPT_ADMIN/TEACHER/STUDENT), department(多对一)");
        addPara(sb, "2. Student extends User：studentNo, name");
        addPara(sb, "3. Teacher extends User：teacherNo, name");
        addPara(sb, "4. Department：id, name");
        addPara(sb, "5. Course：id, name, description, capacity, prerequisite(自关联多对一), teacher, department");
        addPara(sb, "6. Enrollment：id, student, course, status(PENDING/SELECTED/REJECTED), score, (student,course)唯一约束");
        addPara(sb, "7. Message：id, fromUserType, fromUserId, toUserType, toUserId, course, content, createTime");
        addPara(sb, "8. Courseware：id, course, fileName, filePath");

        addHeading(sb, "3.1.2 业务层类设计", 2);
        addPara(sb, "5个Service类和1个工具类：");
        addPara(sb, "• AdminService — 全局CRUD：createStudent/updateStudent/deleteStudent/getAllStudents等18个方法");
        addPara(sb, "• DeptAdminService — 学院CRUD+抽签：createStudent/createTeacher/createCourse/draw/getDrawResult/setGrade等20个方法");
        addPara(sb, "• TeacherService — 教师功能：updateCourse/uploadCourseware/setPrerequisite/getSelectedEnrollments/setGrade/getStats/sendMessage等10个方法");
        addPara(sb, "• StudentService — 学生功能：enroll/getEnrollments/getInfo/getCourses/getGrades/sendMessage/getDrawResult等11个方法");
        addPara(sb, "• StatisticsService — 统计图表：generateChartBytes()使用JFreeChart生成柱状图PNG");
        addPara(sb, "• TokenStore — 会话管理：login(User)/getUser(token)/remove(token)基于ConcurrentHashMap");

        addHeading(sb, "3.1.3 实体关系对照表", 2);
        addClassRelationTable(sb);
    }

    // ==================== 3.2 顺序图 ====================
    static void addSectionSequence(StringBuilder sb) {
        addHeading(sb, "3.2 顺序图", 1);

        addHeading(sb, "3.2.1 学生选课顺序图（核心流程）", 2);
        addPara(sb, "学生选课是系统最核心的业务流程，涉及先修课程检查。消息交互顺序如下：");
        addPara(sb, "第1步：学生 → StudentController.enroll() [POST /api/student/enroll, {courseId}]");
        addPara(sb, "第2步：StudentController → TokenStore.getUser(token) [验证Token+ROLE_STUDENT角色]");
        addPara(sb, "第3步：StudentController → StudentService.enroll(student, courseId) [调用业务层]");
        addPara(sb, "第4步：StudentService → CourseRepository.findById(courseId) [查找课程，不存在则抛异常]");
        addPara(sb, "第5步：StudentService → EnrollmentRepository.findByStudentAndCourse(student, course) [检查是否重复选课]");
        addPara(sb, "第6步：[若prerequisite != null] StudentService → EnrollmentRepository.findByStudentAndCourse(student, prerequisite) [查询先修记录]");
        addPara(sb, "第7步：[判断] 先修记录不存在 或 status≠SELECTED 或 score<60 或 score==null → 抛异常\"您没有学习该课程的先修课程，不能进行选课！\"");
        addPara(sb, "第8步：[通过检查] 创建Enrollment对象，设置student、course、status=PENDING");
        addPara(sb, "第9步：StudentService → EnrollmentRepository.save(enrollment) [持久化]");
        addPara(sb, "第10步：返回Enrollment对象 → Controller封装{code:200, data:Enrollment} → 响应学生");

        addHeading(sb, "3.2.2 抽签顺序图", 2);
        addPara(sb, "第1步：学院管理员 → DeptAdminController.draw() [POST /api/dept/draw/{courseId}]");
        addPara(sb, "第2步：DeptAdminController → TokenStore验证Token+ROLE_DEPT_ADMIN角色");
        addPara(sb, "第3步：DeptAdminController → DeptAdminService.draw(courseId, deptId)");
        addPara(sb, "第4步：DeptAdminService → CourseRepository.findById(courseId) [验证课程存在且归属本学院]");
        addPara(sb, "第5步：DeptAdminService → EnrollmentRepository.findByCourseIdAndStatus(courseId, PENDING) [获取所有待抽签记录]");
        addPara(sb, "第6步：[判断] pendingCount ≤ capacity? → 全部设为SELECTED");
        addPara(sb, "第7步：[否则] Collections.shuffle(pendingEnrollments) → 前capacity个→SELECTED，其余→REJECTED");
        addPara(sb, "第8步：DeptAdminService → EnrollmentRepository.saveAll(pendingEnrollments) [批量更新]");
        addPara(sb, "第9步：返回成功");

        addHeading(sb, "3.2.3 成绩统计顺序图", 2);
        addPara(sb, "第1步：教师 → TeacherController.getStats() [GET /api/teacher/stats/{courseId}]");
        addPara(sb, "第2步：TeacherController → TokenStore验证Token+ROLE_TEACHER角色");
        addPara(sb, "第3步：TeacherController → TeacherService.getStats(courseId, teacherId)");
        addPara(sb, "第4步：TeacherService → CourseRepository.findById(courseId) [验证课程归属该教师]");
        addPara(sb, "第5步：TeacherService → EnrollmentRepository.findByCourseIdAndStatus(courseId, SELECTED) [仅统计中签学生]");
        addPara(sb, "第6步：遍历每条记录，按分数段统计：0-59/60-69/70-79/80-89/90-100各区段人数，计算passRate");
        addPara(sb, "第7步：返回Map{total, '0-59', '60-69', '70-79', '80-89', '90-100', passRate}");
    }

    // ==================== 3.3 程序流程图 ====================
    static void addSectionFlowChart(StringBuilder sb) {
        addHeading(sb, "3.3 程序流程图", 1);

        addHeading(sb, "3.3.1 学生选课流程图", 2);
        addPara(sb, "学生发起选课 → 验证Token与角色(ROLE_STUDENT) → 查找课程findById(courseId)");
        addPara(sb, "→ [判断1: 课程是否存在?] 否→返回错误\"课程不存在\"");
        addPara(sb, "→ [判断2: 已选过该课程?] findByStudentAndCourse()，是→返回错误\"您已经选过该课程\"");
        addPara(sb, "→ [判断3: 课程有先修课程? prerequisite != null?]");
        addPara(sb, "   否 ↓（无先修）→ 直接创建选课");
        addPara(sb, "   是 ↓ → 查询先修课程选课记录 findByStudentAndCourse(student, prerequisite)");
        addPara(sb, "   → [判断4: 先修记录存在且status=SELECTED且score≥60?]");
        addPara(sb, "     否 → 返回错误\"您没有学习该课程的先修课程，不能进行选课！\"");
        addPara(sb, "     是 ↓");
        addPara(sb, "→ 创建Enrollment，status=PENDING → enrollmentRepository.save(enrollment) → 返回成功{code:200, data:Enrollment}");

        addHeading(sb, "3.3.2 抽签算法流程图", 2);
        addPara(sb, "学院管理员发起抽签 → 验证Token与角色(ROLE_DEPT_ADMIN) → 查找课程findById(courseId) → 验证课程归属本学院");
        addPara(sb, "→ 获取所有PENDING状态选课记录 findByCourseIdAndStatus(courseId, PENDING)");
        addPara(sb, "→ [判断1: PENDING选课记录为空?] 是→返回\"没有待抽签的选课记录\"");
        addPara(sb, "→ [判断2: pendingCount ≤ capacity(课程容量)?]");
        addPara(sb, "   是 → 全部PENDING记录设为SELECTED");
        addPara(sb, "   否 → Collections.shuffle()随机打乱 → 前capacity个→SELECTED → 其余→REJECTED");
        addPara(sb, "→ 批量更新数据库 enrollmentRepository.saveAll(pendingEnrollments) → 返回成功");

        addHeading(sb, "3.3.3 成绩统计流程图", 2);
        addPara(sb, "教师请求统计 → 验证Token与角色(ROLE_TEACHER) → 查找课程findById(courseId) → 验证课程归属该教师");
        addPara(sb, "→ 获取所有SELECTED状态选课记录 findByCourseIdAndStatus(courseId, SELECTED)");
        addPara(sb, "→ 初始化计数器：total=0, range0_59=0, range60_69=0, range70_79=0, range80_89=0, range90_100=0, passCount=0");
        addPara(sb, "→ 遍历每条Enrollment记录：score为null→跳过；score≥60→passCount++；按分数段归入各区段计数");
        addPara(sb, "→ 计算passRate = (passCount / total) × 100%");
        addPara(sb, "→ 返回Map{total, \"0-59\":N, \"60-69\":N, \"70-79\":N, \"80-89\":N, \"90-100\":N, passRate:\"XX.XX%\"}");
    }

    // ==================== 附录 ====================
    static void addAppendix(StringBuilder sb) {
        addHeading(sb, "附录：UML图与代码一致性对照表", 1);
        addPara(sb, "以下表格验证所有UML图与实际代码实现之间的一致性：");
        addConsistencyTable(sb);
    }

    // ==================== OOXML 辅助方法 ====================

    static void addHeading(StringBuilder sb, String text, int level) {
        String size;
        switch (level) {
            case 1: size = "36"; break;  // 三号 (对应18pt)
            case 2: size = "32"; break;  // 小三
            case 3: size = "28"; break;  // 四号
            default: size = "24"; break;
        }
        sb.append("<w:p><w:pPr><w:pStyle w:val=\"Heading").append(level)
          .append("\"/><w:jc w:val=\"left\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"")
          .append(size).append("\"/><w:szCs w:val=\"").append(size)
          .append("\"/></w:rPr><w:t xml:space=\"preserve\">")
          .append(escapeXml(text)).append("</w:t></w:r></w:p>\n");
    }

    static void addPara(StringBuilder sb, String text) {
        sb.append("<w:p><w:pPr><w:ind w:firstLine=\"480\"/></w:pPr><w:r><w:rPr><w:sz w:val=\"24\"/><w:szCs w:val=\"24\"/></w:rPr><w:t xml:space=\"preserve\">")
          .append(escapeXml(text)).append("</w:t></w:r></w:p>\n");
    }

    static void addCenteredPara(StringBuilder sb, String text, int fontSizeHalfPt, boolean bold) {
        sb.append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr>");
        if (bold) sb.append("<w:b/>");
        sb.append("<w:sz w:val=\"").append(fontSizeHalfPt).append("\"/><w:szCs w:val=\"").append(fontSizeHalfPt).append("\"/></w:rPr><w:t xml:space=\"preserve\">")
          .append(escapeXml(text)).append("</w:t></w:r></w:p>\n");
    }

    static void addEmptyPara(StringBuilder sb) {
        sb.append("<w:p><w:r><w:t xml:space=\"preserve\"> </w:t></w:r></w:p>\n");
    }

    static void addPageBreak(StringBuilder sb) {
        sb.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>\n");
    }

    // ==================== 表格辅助方法 ====================

    static void addTable(StringBuilder sb, String[] header, String[][] rows) {
        sb.append("<w:tbl><w:tblPr><w:tblW w:w=\"9000\" w:type=\"dxa\"/><w:tblBorders>")
          .append("<w:top w:val=\"single\" w:sz=\"4\"/><w:left w:val=\"single\" w:sz=\"4\"/>")
          .append("<w:bottom w:val=\"single\" w:sz=\"4\"/><w:right w:val=\"single\" w:sz=\"4\"/>")
          .append("<w:insideH w:val=\"single\" w:sz=\"4\"/><w:insideV w:val=\"single\" w:sz=\"4\"/>")
          .append("</w:tblBorders></w:tblPr>\n");

        // Header row
        sb.append("<w:tr>");
        for (String h : header) {
            sb.append("<w:tc><w:tcPr><w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"D9E2F3\"/>")
              .append("<w:tcW w:w=\"").append(9000/header.length).append("\" w:type=\"dxa\"/></w:tcPr>")
              .append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"20\"/></w:rPr>")
              .append("<w:t xml:space=\"preserve\">").append(escapeXml(h)).append("</w:t></w:r></w:p></w:tc>");
        }
        sb.append("</w:tr>\n");

        // Data rows
        for (String[] row : rows) {
            sb.append("<w:tr>");
            for (String cell : row) {
                sb.append("<w:tc><w:tcPr><w:tcW w:w=\"").append(9000/header.length).append("\" w:type=\"dxa\"/></w:tcPr>")
                  .append("<w:p><w:r><w:rPr><w:sz w:val=\"18\"/></w:rPr>")
                  .append("<w:t xml:space=\"preserve\">").append(escapeXml(cell != null ? cell : "")).append("</w:t></w:r></w:p></w:tc>");
            }
            sb.append("</w:tr>\n");
        }
        sb.append("</w:tbl>\n");
    }

    // ==================== 数据流描述表 ====================
    static void addDataFlowTable(StringBuilder sb) {
        String[] header = {"数据流名称", "起点", "终点", "数据内容"};
        String[][] rows = {
            {"登录请求", "用户（所有角色）", "AuthController", "username, password"},
            {"登录响应", "AuthController", "用户", "token, userId, username, role"},
            {"CRUD请求", "管理员", "对应Controller", "实体JSON数据 + Bearer Token"},
            {"CRUD响应", "Controller", "管理员", "{code, message, data}"},
            {"选课请求", "学生", "StudentController", "courseId + Token"},
            {"先修检查", "StudentService", "EnrollmentRepository", "student + prerequisite course"},
            {"抽签指令", "学院管理员", "DeptAdminController", "courseId + Token"},
            {"抽签结果", "DeptAdminService", "DB(Enrollments)", "批量更新status"},
            {"柱状图请求", "系统管理员", "AdminController", "Token"},
            {"柱状图响应", "AdminController", "系统管理员", "PNG字节流 (image/png)"},
            {"课件上传", "教师", "TeacherController", "MultipartFile + Token"},
            {"课件文件", "TeacherController", "文件系统(uploads/)", "文件字节流"},
            {"课件下载请求", "学生", "StudentController", "coursewareId + Token"},
            {"课件下载响应", "StudentController", "学生", "文件流 (application/octet-stream)"},
            {"消息发送", "教师/学生", "对应Controller", "courseId, toUserId, content"},
            {"消息存储", "Service", "MessageRepository", "Message实体"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 实体属性表 ====================
    static void addEntityAttrTable(StringBuilder sb) {
        String[] header = {"实体", "属性", "主键", "说明"};
        String[][] rows = {
            {"User", "id, username, password, role, department_id", "id", "用户基类，JOINED继承策略"},
            {"Student", "继承User + studentNo, name", "id(继承)", "学生实体"},
            {"Teacher", "继承User + teacherNo, name", "id(继承)", "教师实体"},
            {"Department", "id, name", "id", "学院/系别"},
            {"Course", "id, name, description, capacity, prerequisite_id, teacher_id, department_id", "id", "课程，自关联先修课程"},
            {"Enrollment", "id, student_id, course_id, status, score", "id", "选课记录，(student_id,course_id)唯一"},
            {"Message", "id, fromUserType, fromUserId, toUserType, toUserId, course_id, content, createTime", "id", "留言消息，双角色通信"},
            {"Courseware", "id, course_id, fileName, filePath", "id", "课件元数据，文件存文件系统"},
        };
        addTable(sb, header, rows);
    }

    // ==================== E-R关系表 ====================
    static void addERRelationTable(StringBuilder sb) {
        String[] header = {"编号", "关系", "类型", "说明"};
        String[][] rows = {
            {"1", "Department → Student", "1 : N", "一个学院包含多名学生"},
            {"2", "Department → Teacher", "1 : N", "一个学院包含多名教师"},
            {"3", "Department → Course", "1 : N", "一个学院开设多门课程"},
            {"4", "Teacher → Course", "1 : N", "一名教师授课多门课程"},
            {"5", "Student → Enrollment", "1 : N", "一名学生有多条选课记录"},
            {"6", "Course → Enrollment", "1 : N", "一门课程有多条选课记录"},
            {"7", "Course → Course(prereq)", "N : 0..1", "课程自关联先修，可为null"},
            {"8", "Course → Message", "1 : N", "消息关联课程"},
            {"9", "Course → Courseware", "1 : N", "一门课程可有多个课件"},
            {"10", "Student → Message", "1 : N", "学生可发送/接收多条消息"},
            {"11", "Teacher → Message", "1 : N", "教师可发送/接收多条消息"},
            {"12", "User → Student/Teacher", "继承(JOINED)", "JPA @Inheritance(strategy=JOINED)"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 数据库表结构 ====================
    static void addTableSchema(StringBuilder sb) {
        String[] header = {"表名", "字段", "类型", "约束"};
        String[][] rows = {
            {"users", "id", "BIGINT", "PK, AUTO_INCREMENT"},
            {"users", "username", "VARCHAR", "UNIQUE, NOT NULL"},
            {"users", "password", "VARCHAR", "NOT NULL"},
            {"users", "role", "VARCHAR", "NOT NULL"},
            {"users", "department_id", "BIGINT", "FK→departments.id, 可为null"},
            {"students", "id", "BIGINT", "PK, FK→users.id (JOINED)"},
            {"students", "student_no", "VARCHAR", "NOT NULL"},
            {"students", "name", "VARCHAR", "NOT NULL"},
            {"teachers", "id", "BIGINT", "PK, FK→users.id (JOINED)"},
            {"teachers", "teacher_no", "VARCHAR", "NOT NULL"},
            {"teachers", "name", "VARCHAR", "NOT NULL"},
            {"departments", "id, name", "BIGINT,VARCHAR", "PK AUTO_INCREMENT"},
            {"courses", "id, name, description, capacity", "BIGINT,VARCHAR,TEXT,INT", "PK/课程名/NOT NULL"},
            {"courses", "prerequisite_id", "BIGINT", "FK→courses.id, 可为null"},
            {"courses", "teacher_id", "BIGINT", "FK→teachers.id, NOT NULL"},
            {"courses", "department_id", "BIGINT", "FK→departments.id, NOT NULL"},
            {"enrollments", "id, student_id, course_id", "BIGINT", "PK + 双FK, (student,course)唯一"},
            {"enrollments", "status", "VARCHAR", "NOT NULL: PENDING/SELECTED/REJECTED"},
            {"enrollments", "score", "DOUBLE", "可为null"},
            {"messages", "id, fromUserType, fromUserId, toUserType, toUserId", "BIGINT,VARCHAR", "PK + 收发双角色标识"},
            {"messages", "course_id, content, createTime", "BIGINT,TEXT,DATETIME", "FK + NOT NULL"},
            {"coursewares", "id, course_id, fileName, filePath", "BIGINT,VARCHAR", "PK + FK + NOT NULL"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 状态转换表 ====================
    static void addStateTransitionTable(StringBuilder sb) {
        String[] header = {"当前状态", "触发事件", "目标状态", "条件", "执行者"};
        String[][] rows = {
            {"-", "学生选课", "PENDING", "通过先修课程检查", "学生"},
            {"PENDING", "抽签", "SELECTED", "选课人数≤容量 或 随机抽中", "学院管理员"},
            {"PENDING", "抽签", "REJECTED", "选课人数>容量 且 未抽中", "学院管理员"},
            {"SELECTED", "录入成绩", "[终结]", "成绩已录入 score!=null", "教师/管理员"},
            {"REJECTED", "-", "[终结]", "自动终结", "-"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 用例描述表 ====================
    static void addUseCaseTable(StringBuilder sb) {
        String[] header = {"编号", "用例名称", "参与者", "前置条件", "后置条件"};
        String[][] rows = {
            {"UC-01", "登录", "所有用户", "无", "获取Token"},
            {"UC-02", "管理学生(CRUD)", "系统管理员", "ROLE_ADMIN", "学生数据更新"},
            {"UC-03", "管理教师(CRUD)", "系统管理员", "ROLE_ADMIN", "教师数据更新"},
            {"UC-04", "管理课程(CRUD)", "系统管理员", "ROLE_ADMIN", "课程数据更新"},
            {"UC-05", "管理选课(CRUD)", "系统管理员", "ROLE_ADMIN", "选课记录更新"},
            {"UC-06", "统计选课导出图表", "系统管理员", "ROLE_ADMIN", "下载PNG柱状图"},
            {"UC-07", "本学院师生/课程/选课管理", "学院管理员", "ROLE_DEPT_ADMIN", "本学院数据更新"},
            {"UC-08", "抽签并生成中签名单", "学院管理员", "课程在本学院", "中签名单生成"},
            {"UC-09", "编辑课程/上传课件/设先修", "教师", "课程归属该教师", "课程/课件更新"},
            {"UC-10", "查中签名单/录成绩/统计", "教师", "课程归属该教师", "成绩数据更新"},
            {"UC-11", "给学生发消息", "教师", "ROLE_TEACHER", "消息已存储"},
            {"UC-12", "选课(含先修检查)", "学生", "ROLE_STUDENT", "Enrollment(PENDING)"},
            {"UC-13", "查已选课/浏览课程/查成绩", "学生", "ROLE_STUDENT", "返回查询结果"},
            {"UC-14", "下载课件", "学生", "ROLE_STUDENT", "下载文件"},
            {"UC-15", "给教师发消息/查抽签结果", "学生", "ROLE_STUDENT", "消息存储/返回结果"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 数据项定义表 ====================
    static void addDataItemTable(StringBuilder sb) {
        String[] header = {"编号", "数据项名", "类型", "取值范围", "简述"};
        String[][] rows = {
            {"D-01", "id", "BIGINT", "自增整数", "唯一标识，主键"},
            {"D-02", "username", "VARCHAR(50)", "字母数字", "登录用户名，唯一"},
            {"D-03", "password", "VARCHAR(50)", "字母数字", "登录密码，明文存储"},
            {"D-04", "role", "VARCHAR(20)", "ROLE_ADMIN/DEPT_ADMIN/TEACHER/STUDENT", "用户角色"},
            {"D-05", "studentNo", "VARCHAR(20)", "S+数字", "学生学号"},
            {"D-06", "teacherNo", "VARCHAR(20)", "T+数字", "教师工号"},
            {"D-07", "name", "VARCHAR(50)", "中文字符", "学生/教师姓名"},
            {"D-08", "department.name", "VARCHAR(100)", "中文字符", "学院名称"},
            {"D-09", "course.name", "VARCHAR(100)", "中文字符", "课程名称"},
            {"D-10", "description", "TEXT", "文本", "课程介绍"},
            {"D-11", "capacity", "INT", "正整数", "课程容量"},
            {"D-12", "status", "VARCHAR(10)", "PENDING/SELECTED/REJECTED", "选课状态枚举"},
            {"D-13", "score", "DOUBLE", "0.0-100.0, 可为null", "课程成绩"},
            {"D-14", "fileName/filePath", "VARCHAR", "文件名字符串", "课件文件名及路径"},
            {"D-15", "fromUserType/toUserType", "VARCHAR(10)", "TEACHER/STUDENT", "消息收发角色"},
            {"D-16", "content", "TEXT", "文本", "消息正文"},
            {"D-17", "createTime", "DATETIME", "日期时间", "消息创建时间戳"},
            {"D-18", "token", "VARCHAR(36)", "UUID格式", "会话标识"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 数据结构定义表 ====================
    static void addDataStructTable(StringBuilder sb) {
        String[] header = {"编号", "数据结构名", "组成", "说明"};
        String[][] rows = {
            {"DS-01", "User", "id+username+password+role+department", "用户基础信息"},
            {"DS-02", "Student", "User+studentNo+name", "学生信息（继承User）"},
            {"DS-03", "Teacher", "User+teacherNo+name", "教师信息（继承User）"},
            {"DS-04", "Department", "id+name", "学院信息"},
            {"DS-05", "Course", "id+name+description+capacity+prereq+teacher+dept", "课程信息"},
            {"DS-06", "Enrollment", "id+student+course+status+score", "选课记录"},
            {"DS-07", "Message", "id+fromType+fromId+toType+toId+course+content+time", "消息"},
            {"DS-08", "Courseware", "id+course+fileName+filePath", "课件信息"},
            {"DS-09", "LoginRequest", "username+password", "登录请求"},
            {"DS-10", "LoginResponse", "token+userId+username+role", "登录响应"},
            {"DS-11", "ApiResponse", "code+message+data", "统一API响应"},
            {"DS-12", "StatsResult", "total+各分段+passRate", "成绩统计结果"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 数据存储定义表 ====================
    static void addDataStoreTable(StringBuilder sb) {
        String[] header = {"编号", "数据存储名", "对应结构", "组织方式", "说明"};
        String[][] rows = {
            {"F-01", "users表", "User", "id索引, username唯一", "用户基础信息"},
            {"F-02", "students表", "Student", "id索引(JOINED继承)", "学生扩展信息"},
            {"F-03", "teachers表", "Teacher", "id索引(JOINED继承)", "教师扩展信息"},
            {"F-04", "departments表", "Department", "id索引", "学院信息"},
            {"F-05", "courses表", "Course", "id索引", "课程信息"},
            {"F-06", "enrollments表", "Enrollment", "id索引, (student_id,course_id)唯一", "选课记录"},
            {"F-07", "messages表", "Message", "id索引", "消息记录"},
            {"F-08", "coursewares表", "Courseware", "id索引", "课件元数据"},
            {"F-09", "uploads/目录", "课件文件", "文件系统", "课件物理存储"},
            {"F-10", "TokenStore", "token→User", "ConcurrentHashMap内存", "Token会话管理"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 处理逻辑定义表 ====================
    static void addProcessTable(StringBuilder sb) {
        String[] header = {"编号", "处理名", "输入", "输出", "处理逻辑"};
        String[][] rows = {
            {"P-01", "登录认证", "username,password", "token,userInfo", "查询users表匹配用户名密码→生成UUID Token→存入TokenStore"},
            {"P-02", "学生选课", "studentId,courseId", "Enrollment(PENDING)", "查找课程→检查重复→检查先修(若有)→创建PENDING选课"},
            {"P-03", "抽签", "courseId,deptId", "中签名单", "验证课程归属→获取PENDING选课→比较容量→随机抽取/全中→批量更新"},
            {"P-04", "成绩录入", "enrollmentId,score", "Enrollment(score更新)", "验证状态=SELECTED→验证权限→设置score"},
            {"P-05", "成绩统计", "courseId", "StatsResult", "获取SELECTED选课→按5个分数段统计→计算及格率"},
            {"P-06", "柱状图生成", "全部课程+选课", "PNG字节流", "查询课程→统计每门选课人数→JFreeChart生成PNG"},
            {"P-07", "先修检查", "student,prereqCourse", "通过/拒绝", "查询先修选课记录→验证status=SELECTED且score≥60"},
            {"P-08", "消息发送", "courseId,toUserId,content", "Message", "创建Message→设置收发方→记录时间戳→保存"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 模块划分表 ====================
    static void addModuleTable(StringBuilder sb) {
        String[] header = {"模块", "子模块", "包含的类", "功能说明"};
        String[][] rows = {
            {"认证模块", "登录", "AuthController", "处理登录请求，验证用户名密码"},
            {"认证模块", "Token管理", "TokenStore", "ConcurrentHashMap维护Token→User映射"},
            {"认证模块", "鉴权", "各Controller check方法", "提取Bearer Token，验证角色权限"},
            {"认证模块", "异常处理", "GlobalExceptionHandler", "@ControllerAdvice统一异常→JSON响应"},
            {"认证模块", "跨域配置", "WebConfig", "CORS配置允许前端跨域请求"},
            {"管理模块", "系统管理", "AdminController+AdminService", "全局学生/教师/课程/选课/成绩CRUD（18个接口）"},
            {"管理模块", "学院管理", "DeptAdminController+DeptAdminService", "本学院CRUD(20个接口)+draw()抽签"},
            {"管理模块", "教师功能", "TeacherController+TeacherService", "课程编辑/课件/先修/成绩/消息(10个方法)"},
            {"管理模块", "学生功能", "StudentController+StudentService", "选课/浏览/下载/消息(11个方法)"},
            {"统计模块", "图表", "StatisticsService", "JFreeChart generateChartBytes()→PNG"},
            {"数据模块", "实体+Repository", "8个Entity+8个Repository", "JPA持久化层，JOINED继承"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 类关系表 ====================
    static void addClassRelationTable(StringBuilder sb) {
        String[] header = {"源类", "目标类", "关系", "JPA/Spring注解"};
        String[][] rows = {
            {"Student", "User", "继承(JOINED)", "@Entity @Table(\"students\")"},
            {"Teacher", "User", "继承(JOINED)", "@Entity @Table(\"teachers\")"},
            {"User", "Department", "多对一", "@ManyToOne @JoinColumn(\"department_id\")"},
            {"Course", "Teacher", "多对一", "@ManyToOne @JoinColumn(\"teacher_id\")"},
            {"Course", "Department", "多对一", "@ManyToOne @JoinColumn(\"department_id\")"},
            {"Course", "Course", "多对一(自关联)", "@ManyToOne @JoinColumn(\"prerequisite_id\")"},
            {"Enrollment", "Student", "多对一", "@ManyToOne @JoinColumn + @UniqueConstraint"},
            {"Enrollment", "Course", "多对一", "@ManyToOne @JoinColumn"},
            {"Message", "Course", "多对一", "@ManyToOne @JoinColumn(\"course_id\")"},
            {"Courseware", "Course", "多对一", "@ManyToOne @JoinColumn(\"course_id\")"},
            {"AdminController", "AdminService", "依赖注入", "@Autowired"},
            {"DeptAdminController", "DeptAdminService", "依赖注入", "@Autowired"},
            {"TeacherController", "TeacherService", "依赖注入", "@Autowired"},
            {"StudentController", "StudentService", "依赖注入", "@Autowired"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 一致性对照表 ====================
    static void addConsistencyTable(StringBuilder sb) {
        String[] header = {"检查项", "UML图约定", "代码实现", "一致性"};
        String[][] rows = {
            {"实体类名", "8个实体(User/Student/Teacher/Department/Course/Enrollment/Message/Courseware)", "entity/包下8个同名Java类", "✓ 一致"},
            {"继承策略", "User基类, Student/Teacher JOINED继承", "@Inheritance(strategy=JOINED)", "✓ 一致"},
            {"状态枚举", "PENDING, SELECTED, REJECTED", "Enrollment.Status枚举三个值", "✓ 一致"},
            {"状态流转", "PENDING→抽签→SELECTED/REJECTED→终结", "draw()方法实现: shuffle+截取", "✓ 一致"},
            {"三层架构", "Controller→Service→Repository", "严格3层, Controller不直接调Repository", "✓ 一致"},
            {"先修检查", "SELECTED且score≥60", "enroll()中检查3个条件", "✓ 一致"},
            {"抽签算法", "人数≤capacity全中,否则shuffle取前capacity", "Collections.shuffle()+前capacity个", "✓ 一致"},
            {"API路径前缀", "/api/{admin,dept,teacher,student}/**", "Controller @RequestMapping注解", "✓ 一致"},
            {"响应格式", "{code, message, data}", "Map<String,Object>或GlobalExceptionHandler", "✓ 一致"},
            {"Token认证", "Authorization: Bearer <token>", "Controller从Header提取, TokenStore验证", "✓ 一致"},
            {"学院隔离", "DeptAdmin/Teacher仅操作本学院", "Service方法中验证deptId匹配", "✓ 一致"},
            {"唯一约束", "(student,course)唯一", "@UniqueConstraint(columnNames=...)", "✓ 一致"},
            {"成绩统计", "5个分数段+及格率,仅统计SELECTED", "getStats()仅查SELECTED,逐条分档", "✓ 一致"},
        };
        addTable(sb, header, rows);
    }

    // ==================== 工具方法 ====================
    static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
