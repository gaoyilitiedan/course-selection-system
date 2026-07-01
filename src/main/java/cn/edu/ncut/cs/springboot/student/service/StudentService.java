package cn.edu.ncut.cs.springboot.student.service;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生服务
 * 提供选课、查看信息、浏览课程、查询成绩、下载课件、留言等功能
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CoursewareRepository coursewareRepository;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 学生选课
     * 严格遵循顺序图（图7）定义的方法调用链：
     * 1. 查找课程
     * 2. 检查先修课程（学生必须已修先修课程且成绩≥60）
     * 3. 保存选课记录（初始状态 PENDING）
     */
    @Transactional
    public Enrollment enroll(Student student, Long courseId) {
        // 查找课程
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));

        // 检查是否已经选过该课程
        Optional<Enrollment> existing = enrollmentRepository.findByStudentAndCourse(student, course);
        if (existing.isPresent()) {
            throw new BusinessException("您已经选过该课程，不能重复选课");
        }

        // 检查先修课程
        Course prerequisite = course.getPrerequisite();
        if (prerequisite != null) {
            // 查询学生是否在先修课程中有选课记录且状态为 SELECTED 且成绩 ≥ 60
            Optional<Enrollment> prereqEnroll = enrollmentRepository
                    .findByStudentAndCourse(student, prerequisite);

            if (prereqEnroll.isEmpty() ||
                prereqEnroll.get().getStatus() != Enrollment.Status.SELECTED ||
                prereqEnroll.get().getScore() == null ||
                prereqEnroll.get().getScore() < 60) {
                throw new BusinessException("您没有学习该课程的先修课程，不能进行选课！");
            }
        }

        // 创建选课记录，初始状态为 PENDING
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.Status.PENDING);
        return enrollmentRepository.save(enrollment);
    }

    /** 查询已选课程 */
    public List<Enrollment> getEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    /** 查询个人信息 */
    public Student getInfo(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("学生不存在"));
    }

    /** 浏览课程信息 */
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    /** 查询成绩 */
    public List<Enrollment> getGrades(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getStatus() == Enrollment.Status.SELECTED)
                .toList();
    }

    /** 根据ID获取单个课件 */
    public Courseware getCourseware(Long coursewareId) {
        return coursewareRepository.findById(coursewareId)
                .orElseThrow(() -> new BusinessException("课件不存在"));
    }

    /** 根据课程ID查询课件列表 */
    public List<Courseware> getCoursewaresByCourse(Long courseId) {
        return coursewareRepository.findByCourseId(courseId);
    }

    /** 给教师发消息 */
    @Transactional
    public Message sendMessage(Long courseId, Long teacherId, String content, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));

        Message message = new Message();
        message.setFromUserType("STUDENT");
        message.setFromUserId(studentId);
        message.setToUserType("TEACHER");
        message.setToUserId(teacherId);
        message.setCourse(course);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    /** 查看/回复教师留言 */
    public List<Message> getMessages(Long studentId) {
        return messageRepository.findByToUserTypeAndToUserIdOrderByCreateTimeDesc("STUDENT", studentId);
    }

    /** 查询自己选课的抽签结果 */
    public List<Enrollment> getDrawResult(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}