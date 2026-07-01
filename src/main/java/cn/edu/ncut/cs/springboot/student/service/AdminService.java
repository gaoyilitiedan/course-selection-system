package cn.edu.ncut.cs.springboot.student.service;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 系统管理员服务
 * 提供系统管理员级别的学生/教师/课程/选课/成绩管理功能
 */
@Service
public class AdminService {

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

    // ========== 学生管理 ==========

    /** 新增学生 */
    @Transactional
    public Student createStudent(Student student) {
        // 重新加载 Department 为受管实体，避免 TransientObjectException
        if (student.getDepartment() != null && student.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(student.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            student.setDepartment(dept);
        }
        student.setRole("ROLE_STUDENT");
        return studentRepository.save(student);
    }

    /** 修改学生 */
    @Transactional
    public Student updateStudent(Long id, Student updated) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("学生不存在"));
        student.setStudentNo(updated.getStudentNo());
        student.setName(updated.getName());
        student.setUsername(updated.getUsername());
        student.setPassword(updated.getPassword());
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            student.setDepartment(dept);
        }
        return studentRepository.save(student);
    }

    /** 删除学生 */
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException("学生不存在");
        }
        studentRepository.deleteById(id);
    }

    /** 查询学生列表 */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ========== 教师管理 ==========

    /** 新增教师 */
    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        // 重新加载 Department 为受管实体，避免 TransientObjectException
        if (teacher.getDepartment() != null && teacher.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(teacher.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            teacher.setDepartment(dept);
        }
        teacher.setRole("ROLE_TEACHER");
        return teacherRepository.save(teacher);
    }

    /** 修改教师 */
    @Transactional
    public Teacher updateTeacher(Long id, Teacher updated) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new BusinessException("教师不存在"));
        teacher.setTeacherNo(updated.getTeacherNo());
        teacher.setName(updated.getName());
        teacher.setUsername(updated.getUsername());
        teacher.setPassword(updated.getPassword());
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            teacher.setDepartment(dept);
        }
        return teacherRepository.save(teacher);
    }

    /** 删除教师 */
    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new BusinessException("教师不存在");
        }
        teacherRepository.deleteById(id);
    }

    /** 查询教师列表 */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // ========== 课程管理 ==========

    /** 新增课程 */
    @Transactional
    public Course createCourse(Course course) {
        // 验证教师存在
        if (course.getTeacher() != null && course.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(course.getTeacher().getId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
            course.setTeacher(teacher);
        }
        // 验证学院存在
        if (course.getDepartment() != null && course.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(course.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            course.setDepartment(dept);
        }
        return courseRepository.save(course);
    }

    /** 修改课程 */
    @Transactional
    public Course updateCourse(Long id, Course updated) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        course.setName(updated.getName());
        course.setDescription(updated.getDescription());
        course.setCapacity(updated.getCapacity());
        if (updated.getTeacher() != null && updated.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(updated.getTeacher().getId())
                    .orElseThrow(() -> new BusinessException("教师不存在"));
            course.setTeacher(teacher);
        }
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new BusinessException("学院不存在"));
            course.setDepartment(dept);
        }
        return courseRepository.save(course);
    }

    /** 删除课程 */
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new BusinessException("课程不存在");
        }
        courseRepository.deleteById(id);
    }

    /** 查询课程列表 */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ========== 选课管理 ==========

    /** 新增选课记录 */
    @Transactional
    public Enrollment createEnrollment(Enrollment enrollment) {
        // 验证学生和课程
        Student student = studentRepository.findById(enrollment.getStudent().getId())
                .orElseThrow(() -> new BusinessException("学生不存在"));
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new BusinessException("课程不存在"));
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(Enrollment.Status.PENDING);
        }
        return enrollmentRepository.save(enrollment);
    }

    /** 修改选课记录 */
    @Transactional
    public Enrollment updateEnrollment(Long id, Enrollment updated) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        enrollment.setStatus(updated.getStatus());
        enrollment.setScore(updated.getScore());
        return enrollmentRepository.save(enrollment);
    }

    /** 删除选课记录 */
    @Transactional
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new BusinessException("选课记录不存在");
        }
        enrollmentRepository.deleteById(id);
    }

    /** 查询所有选课记录 */
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // ========== 成绩管理 ==========

    /** 录入/修改成绩 */
    @Transactional
    public Enrollment setGrade(Long enrollmentId, Double score) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        if (enrollment.getStatus() != Enrollment.Status.SELECTED) {
            throw new BusinessException("只能为中签学生录入成绩");
        }
        enrollment.setScore(score);
        return enrollmentRepository.save(enrollment);
    }

    // ========== 学院管理 ==========

    /** 查询所有学院 */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}