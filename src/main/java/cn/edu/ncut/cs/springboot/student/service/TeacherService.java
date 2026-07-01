package cn.edu.ncut.cs.springboot.student.service;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师服务
 * 提供课程管理、课件上传、先修课程设置、成绩录入、消息通信等功能
 */
@Service
public class TeacherService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CoursewareRepository coursewareRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ========== 课程管理 ==========

    /**
     * 更新课程信息/介绍
     * 教师只能更新自己授课的课程
     */
    @Transactional
    public Course updateCourse(Long courseId, Course updated, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能修改自己授课的课程");
        }
        course.setName(updated.getName());
        course.setDescription(updated.getDescription());
        course.setCapacity(updated.getCapacity());
        return courseRepository.save(course);
    }

    // ========== 课件管理 ==========

    /**
     * 上传课件
     * @param courseId  课程ID
     * @param fileName  文件名
     * @param filePath  文件存储路径
     */
    @Transactional
    public Courseware uploadCourseware(Long courseId, String fileName, String filePath, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能为自己授课的课程上传课件");
        }
        Courseware courseware = new Courseware();
        courseware.setCourse(course);
        courseware.setFileName(fileName);
        courseware.setFilePath(filePath);
        return coursewareRepository.save(courseware);
    }

    /** 查看课件列表 */
    public List<Courseware> getCoursewares(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能查看自己授课课程的课件");
        }
        return coursewareRepository.findByCourseId(courseId);
    }

    // ========== 先修课程设置 ==========

    /**
     * 设置先修课程
     */
    @Transactional
    public Course setPrerequisite(Long courseId, Long prerequisiteId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能为自己授课的课程设置先修");
        }
        if (prerequisiteId != null) {
            Course prerequisite = courseRepository.findById(prerequisiteId)
                    .orElseThrow(() -> new BusinessException("先修课程不存在"));
            course.setPrerequisite(prerequisite);
        } else {
            course.setPrerequisite(null);
        }
        return courseRepository.save(course);
    }

    // ========== 中签学生查看 ==========

    /** 查看自己授课课程的中签学生 */
    public List<Enrollment> getSelectedEnrollments(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return enrollmentRepository.findByCourseIdIn(courseIds)
                .stream()
                .filter(e -> e.getStatus() == Enrollment.Status.SELECTED)
                .collect(Collectors.toList());
    }

    // ========== 成绩录入 ==========

    /** 录入学生成绩 */
    @Transactional
    public Enrollment setGrade(Long enrollmentId, Double score, Long teacherId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException("选课记录不存在"));
        if (!enrollment.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能为自己授课课程录入成绩");
        }
        if (enrollment.getStatus() != Enrollment.Status.SELECTED) {
            throw new BusinessException("只能为中签学生录入成绩");
        }
        enrollment.setScore(score);
        return enrollmentRepository.save(enrollment);
    }

    // ========== 成绩统计 ==========

    /**
     * 成绩统计
     * 返回分数段人数及及格率（仅统计 SELECTED 状态的选课记录）
     */
    public Map<String, Object> getStats(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能查看自己授课课程的成绩统计");
        }

        List<Enrollment> selected = enrollmentRepository
                .findByCourseIdAndStatus(courseId, Enrollment.Status.SELECTED);

        int total = selected.size();
        int range0_59 = 0, range60_69 = 0, range70_79 = 0, range80_89 = 0, range90_100 = 0;
        int passCount = 0;

        for (Enrollment e : selected) {
            if (e.getScore() == null) continue;
            double score = e.getScore();
            if (score >= 60) passCount++;
            if (score < 60) range0_59++;
            else if (score < 70) range60_69++;
            else if (score < 80) range70_79++;
            else if (score < 90) range80_89++;
            else range90_100++;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("0-59", range0_59);
        stats.put("60-69", range60_69);
        stats.put("70-79", range70_79);
        stats.put("80-89", range80_89);
        stats.put("90-100", range90_100);
        stats.put("passRate", total > 0 ? String.format("%.2f%%", (double) passCount / total * 100) : "0%");

        return stats;
    }

    // ========== 消息管理 ==========

    /** 给学生发消息 */
    @Transactional
    public Message sendMessage(Long courseId, Long studentId, String content, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("只能给自己授课课程的学生发消息");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("学生不存在"));

        Message message = new Message();
        message.setFromUserType("TEACHER");
        message.setFromUserId(teacherId);
        message.setToUserType("STUDENT");
        message.setToUserId(studentId);
        message.setCourse(course);
        message.setContent(content);
        message.setCreateTime(java.time.LocalDateTime.now());
        return messageRepository.save(message);
    }

    /** 查看自己的消息（按时间倒序） */
    public List<Message> getMessages(Long teacherId) {
        return messageRepository.findByToUserTypeAndToUserIdOrderByCreateTimeDesc("TEACHER", teacherId);
    }
}