package cn.edu.ncut.cs.springboot.student.service;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学院管理员服务
 * 管理本学院的师生、课程、选课记录、抽签和成绩
 */
@Service
public class DeptAdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ========== 本学院学生管理 ==========

    /** 本学院新增学生 */
    @Transactional
    public Student createStudent(Student student, Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException("学院不存在"));
        student.setDepartment(dept);
        student.setRole("ROLE_STUDENT");
        return studentRepository.save(student);
    }

    /** 本学院修改学生 */
    @Transactional
    public Student updateStudent(Long id, Student updated, Long deptId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        if (!student.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院学生");
        }
        student.setStudentNo(updated.getStudentNo());
        student.setName(updated.getName());
        student.setUsername(updated.getUsername());
        student.setPassword(updated.getPassword());
        return studentRepository.save(student);
    }

    /** 本学院删除学生 */
    @Transactional
    public void deleteStudent(Long id, Long deptId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        if (!student.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院学生");
        }
        studentRepository.deleteById(id);
    }

    /** 本学院学生列表 */
    public List<Student> getStudentsByDept(Long deptId) {
        return studentRepository.findByDepartmentId(deptId);
    }

    // ========== 本学院教师管理 ==========

    /** 本学院新增教师 */
    @Transactional
    public Teacher createTeacher(Teacher teacher, Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException("学院不存在"));
        teacher.setDepartment(dept);
        teacher.setRole("ROLE_TEACHER");
        return teacherRepository.save(teacher);
    }

    /** 本学院修改教师 */
    @Transactional
    public Teacher updateTeacher(Long id, Teacher updated, Long deptId) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        if (!teacher.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院教师");
        }
        teacher.setTeacherNo(updated.getTeacherNo());
        teacher.setName(updated.getName());
        teacher.setUsername(updated.getUsername());
        teacher.setPassword(updated.getPassword());
        return teacherRepository.save(teacher);
    }

    /** 本学院删除教师 */
    @Transactional
    public void deleteTeacher(Long id, Long deptId) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        if (!teacher.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院教师");
        }
        teacherRepository.deleteById(id);
    }

    /** 本学院教师列表 */
    public List<Teacher> getTeachersByDept(Long deptId) {
        return teacherRepository.findByDepartmentId(deptId);
    }

    // ========== 本学院课程管理 ==========

    /** 本学院新增课程 */
    @Transactional
    public Course createCourse(Course course, Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException("学院不存在"));
        course.setDepartment(dept);
        if (course.getTeacher() != null && course.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(course.getTeacher().getId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
            if (!teacher.getDepartment().getId().equals(deptId)) {
                throw new BusinessException("只能选择本学院教师");
            }
            course.setTeacher(teacher);
        }
        return courseRepository.save(course);
    }

    /** 本学院修改课程 */
    @Transactional
    public Course updateCourse(Long id, Course updated, Long deptId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院课程");
        }
        course.setName(updated.getName());
        course.setDescription(updated.getDescription());
        course.setCapacity(updated.getCapacity());
        if (updated.getTeacher() != null && updated.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(updated.getTeacher().getId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
            course.setTeacher(teacher);
        }
        return courseRepository.save(course);
    }

    /** 本学院删除课程 */
    @Transactional
    public void deleteCourse(Long id, Long deptId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院课程");
        }
        courseRepository.deleteById(id);
    }

    /** 本学院课程列表 */
    public List<Course> getCoursesByDept(Long deptId) {
        return courseRepository.findByDepartmentId(deptId);
    }

    // ========== 本学院选课管理 ==========

    /** 本学院新增选课 */
    @Transactional
    public Enrollment createEnrollment(Enrollment enrollment, Long deptId) {
        Student student = studentRepository.findById(enrollment.getStudent().getId())
                .orElseThrow(() -> new BusinessException("学生不存在"));
        if (!student.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院学生");
        }
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院课程");
        }
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(Enrollment.Status.PENDING);
        }
        return enrollmentRepository.save(enrollment);
    }

    /** 本学院修改选课 */
    @Transactional
    public Enrollment updateEnrollment(Long id, Enrollment updated, Long deptId) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        if (!enrollment.getCourse().getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院选课记录");
        }
        enrollment.setStatus(updated.getStatus());
        enrollment.setScore(updated.getScore());
        return enrollmentRepository.save(enrollment);
    }

    /** 本学院删除选课 */
    @Transactional
    public void deleteEnrollment(Long id, Long deptId) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        if (!enrollment.getCourse().getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院选课记录");
        }
        enrollmentRepository.deleteById(id);
    }

    /** 本学院选课列表 */
    public List<Enrollment> getEnrollmentsByDept(Long deptId) {
        List<Course> courses = courseRepository.findByDepartmentId(deptId);
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return enrollmentRepository.findByCourseIdIn(courseIds);
    }

    // ========== 抽签管理 ==========

    /**
     * 对本学院课程执行抽签
     * 严格按照程序流程图（图8）实现：
     * 1. 获取课程所有 PENDING 状态选课记录
     * 2. 判断选课人数 > 课程容量？是则随机抽取 capacity 个，否则全部设为 SELECTED
     * 3. 批量更新数据库
     */
    @Transactional
    public void draw(Long courseId, Long deptId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能对本学院课程抽签");
        }

        // 获取该课程所有 PENDING 状态选课记录
        List<Enrollment> pendingEnrollments = enrollmentRepository
                .findByCourseIdAndStatus(courseId, Enrollment.Status.PENDING);

        if (pendingEnrollments.isEmpty()) {
            throw new BusinessException("没有待抽签的选课记录");
        }

        int capacity = course.getCapacity();

        if (pendingEnrollments.size() <= capacity) {
            // 选课人数 ≤ 课程容量 → 全部设为 SELECTED
            for (Enrollment e : pendingEnrollments) {
                e.setStatus(Enrollment.Status.SELECTED);
            }
        } else {
            // 选课人数 > 课程容量 → 随机选取 capacity 个设为 SELECTED，其余设为 REJECTED
            Collections.shuffle(pendingEnrollments);
            for (int i = 0; i < pendingEnrollments.size(); i++) {
                if (i < capacity) {
                    pendingEnrollments.get(i).setStatus(Enrollment.Status.SELECTED);
                } else {
                    pendingEnrollments.get(i).setStatus(Enrollment.Status.REJECTED);
                }
            }
        }

        // 批量更新数据库
        enrollmentRepository.saveAll(pendingEnrollments);
    }

    /**
     * 查看中签学生名单
     */
    public List<Enrollment> getDrawResult(Long courseId, Long deptId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能查看本学院课程抽签结果");
        }
        return enrollmentRepository.findByCourseIdAndStatus(courseId, Enrollment.Status.SELECTED);
    }

    // ========== 成绩管理 ==========

    /** 录入/修改本学院成绩 */
    @Transactional
    public Enrollment setGrade(Long enrollmentId, Double score, Long deptId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        if (!enrollment.getCourse().getDepartment().getId().equals(deptId)) {
            throw new BusinessException("只能操作本学院成绩");
        }
        if (enrollment.getStatus() != Enrollment.Status.SELECTED) {
            throw new BusinessException("只能为中签学生录入成绩");
        }
        enrollment.setScore(score);
        return enrollmentRepository.save(enrollment);
    }

    /** 查看本学院成绩 */
    public List<Enrollment> getGradesByDept(Long deptId) {
        List<Course> courses = courseRepository.findByDepartmentId(deptId);
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return enrollmentRepository.findByCourseIdIn(courseIds);
    }
}