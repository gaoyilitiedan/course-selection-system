package cn.edu.ncut.cs.springboot.student;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;

/**
 * 学生选课系统 - 启动类
 * 使用 Spring Boot 3.x，H2 数据库
 */
@SpringBootApplication
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

    /**
     * 示例数据初始化
     * 系统启动时自动创建学院、用户、课程、选课记录等示例数据
     * 所有数据创建前都会检查是否已存在，避免重复插入报错
     */
    @Bean
    CommandLineRunner initData(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository) {
        return args -> {
            // 确保上传目录存在
            new File("src/main/resources/uploads/").mkdirs();

            System.out.println("========================================");
            System.out.println("  开始初始化数据...");
            System.out.println("========================================");

            // ===== 创建学院 =====
            Department csDept;
            Department mathDept;

            if (departmentRepository.count() == 0) {
                csDept = new Department();
                csDept.setName("计算机科学与技术学院");
                csDept = departmentRepository.save(csDept);

                mathDept = new Department();
                mathDept.setName("数学与统计学院");
                mathDept = departmentRepository.save(mathDept);

                System.out.println("✅ 学院创建成功");
            } else {
                List<Department> depts = departmentRepository.findAll();
                csDept = depts.get(0);
                mathDept = depts.size() > 1 ? depts.get(1) : depts.get(0);
                System.out.println("ℹ️ 学院已存在，跳过创建");
            }

            // ===== 创建系统管理员 =====
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("✅ 系统管理员创建成功");
            } else {
                System.out.println("ℹ️ 系统管理员已存在，跳过创建");
            }

            // ===== 创建学院管理员 =====
            if (userRepository.findByUsername("csadmin").isEmpty()) {
                User csAdmin = new User();
                csAdmin.setUsername("csadmin");
                csAdmin.setPassword("csadmin123");
                csAdmin.setRole("ROLE_DEPT_ADMIN");
                csAdmin.setDepartment(csDept);
                userRepository.save(csAdmin);
                System.out.println("✅ 计算机学院管理员创建成功");
            } else {
                System.out.println("ℹ️ 计算机学院管理员已存在，跳过创建");
            }

            if (userRepository.findByUsername("mathadmin").isEmpty()) {
                User mathAdmin = new User();
                mathAdmin.setUsername("mathadmin");
                mathAdmin.setPassword("mathadmin123");
                mathAdmin.setRole("ROLE_DEPT_ADMIN");
                mathAdmin.setDepartment(mathDept);
                userRepository.save(mathAdmin);
                System.out.println("✅ 数学学院管理员创建成功");
            } else {
                System.out.println("ℹ️ 数学学院管理员已存在，跳过创建");
            }

            // ===== 创建教师 =====
            Teacher teacher1;
            Teacher teacher2;

            if (teacherRepository.count() == 0) {
                teacher1 = new Teacher();
                teacher1.setUsername("teacher1");
                teacher1.setPassword("123456");
                teacher1.setRole("ROLE_TEACHER");
                teacher1.setTeacherNo("T001");
                teacher1.setName("张教授");
                teacher1.setDepartment(csDept);
                teacher1 = teacherRepository.save(teacher1);

                teacher2 = new Teacher();
                teacher2.setUsername("teacher2");
                teacher2.setPassword("123456");
                teacher2.setRole("ROLE_TEACHER");
                teacher2.setTeacherNo("T002");
                teacher2.setName("李教授");
                teacher2.setDepartment(mathDept);
                teacher2 = teacherRepository.save(teacher2);

                System.out.println("✅ 教师创建成功");
            } else {
                List<Teacher> teachers = teacherRepository.findAll();
                teacher1 = teachers.get(0);
                teacher2 = teachers.size() > 1 ? teachers.get(1) : teachers.get(0);
                System.out.println("ℹ️ 教师已存在，跳过创建");
            }

            // ===== 创建学生 =====
            Student student1;
            Student student2;
            Student student3;

            if (studentRepository.count() == 0) {
                student1 = new Student();
                student1.setUsername("student1");
                student1.setPassword("123456");
                student1.setRole("ROLE_STUDENT");
                student1.setStudentNo("S2024001");
                student1.setName("王同学");
                student1.setDepartment(csDept);
                student1 = studentRepository.save(student1);

                student2 = new Student();
                student2.setUsername("student2");
                student2.setPassword("123456");
                student2.setRole("ROLE_STUDENT");
                student2.setStudentNo("S2024002");
                student2.setName("赵同学");
                student2.setDepartment(csDept);
                student2 = studentRepository.save(student2);

                student3 = new Student();
                student3.setUsername("student3");
                student3.setPassword("123456");
                student3.setRole("ROLE_STUDENT");
                student3.setStudentNo("S2024003");
                student3.setName("陈同学");
                student3.setDepartment(mathDept);
                student3 = studentRepository.save(student3);

                System.out.println("✅ 学生创建成功");
            } else {
                List<Student> students = studentRepository.findAll();
                student1 = students.get(0);
                student2 = students.size() > 1 ? students.get(1) : students.get(0);
                student3 = students.size() > 2 ? students.get(2) : students.get(0);
                System.out.println("ℹ️ 学生已存在，跳过创建");
            }

            // ===== 创建课程 =====
            Course prerequisite;
            Course course1;
            Course course2;

            if (courseRepository.count() == 0) {
                // 先修课程：Java基础
                prerequisite = new Course();
                prerequisite.setName("Java程序设计基础");
                prerequisite.setDescription("Java语言基础课程，涵盖面向对象编程核心概念");
                prerequisite.setCapacity(100);
                prerequisite.setTeacher(teacher1);
                prerequisite.setDepartment(csDept);
                prerequisite = courseRepository.save(prerequisite);

                // 主课程：Java Web开发（以Java基础为先修）
                course1 = new Course();
                course1.setName("Java Web开发");
                course1.setDescription("基于Spring Boot的Web应用开发，需要先修Java程序设计基础");
                course1.setCapacity(2);
                course1.setPrerequisite(prerequisite);
                course1.setTeacher(teacher1);
                course1.setDepartment(csDept);
                course1 = courseRepository.save(course1);

                // 数学课程
                course2 = new Course();
                course2.setName("高等数学");
                course2.setDescription("高等数学课程，涵盖微积分、线性代数等");
                course2.setCapacity(30);
                course2.setTeacher(teacher2);
                course2.setDepartment(mathDept);
                course2 = courseRepository.save(course2);

                System.out.println("✅ 课程创建成功");
            } else {
                List<Course> courses = courseRepository.findAll();
                prerequisite = courses.get(0);
                course1 = courses.size() > 1 ? courses.get(1) : courses.get(0);
                course2 = courses.size() > 2 ? courses.get(2) : courses.get(0);
                System.out.println("ℹ️ 课程已存在，跳过创建");
            }

            // ===== 创建选课记录 =====
            if (enrollmentRepository.count() == 0) {
                // student1 先修课程（成绩85分，已通过）
                Enrollment prereqEnroll = new Enrollment();
                prereqEnroll.setStudent(student1);
                prereqEnroll.setCourse(prerequisite);
                prereqEnroll.setStatus(Enrollment.Status.SELECTED);
                prereqEnroll.setScore(85.0);
                enrollmentRepository.save(prereqEnroll);

                // student2 先修课程（成绩92分，已通过）
                Enrollment prereqEnroll2 = new Enrollment();
                prereqEnroll2.setStudent(student2);
                prereqEnroll2.setCourse(prerequisite);
                prereqEnroll2.setStatus(Enrollment.Status.SELECTED);
                prereqEnroll2.setScore(92.0);
                enrollmentRepository.save(prereqEnroll2);

                // student1 选 Java Web开发（待抽签）
                Enrollment enroll1 = new Enrollment();
                enroll1.setStudent(student1);
                enroll1.setCourse(course1);
                enroll1.setStatus(Enrollment.Status.PENDING);
                enrollmentRepository.save(enroll1);

                // student2 选 Java Web开发（待抽签）
                Enrollment enroll2 = new Enrollment();
                enroll2.setStudent(student2);
                enroll2.setCourse(course1);
                enroll2.setStatus(Enrollment.Status.PENDING);
                enrollmentRepository.save(enroll2);

                // student3 选高等数学（待抽签）
                Enrollment enroll3 = new Enrollment();
                enroll3.setStudent(student3);
                enroll3.setCourse(course2);
                enroll3.setStatus(Enrollment.Status.PENDING);
                enrollmentRepository.save(enroll3);

                System.out.println("✅ 选课记录创建成功");
            } else {
                System.out.println("ℹ️ 选课记录已存在，跳过创建");
            }

            System.out.println("========================================");
            System.out.println("  学生选课系统启动完成！");
            System.out.println("  H2 控制台: http://localhost:8080/h2-console");
            System.out.println("  JDBC URL: jdbc:h2:file:./data/coursedb");
            System.out.println("========================================");
            System.out.println("  📋 测试账号信息：");
            System.out.println("  系统管理员: admin / admin123");
            System.out.println("  学院管理员: csadmin / csadmin123");
            System.out.println("  教师: teacher1 / 123456");
            System.out.println("  学生: student1 / 123456");
            System.out.println("========================================");
        };
    }
}