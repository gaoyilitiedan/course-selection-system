package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Course;
import cn.edu.ncut.cs.springboot.student.entity.Enrollment;
import cn.edu.ncut.cs.springboot.student.entity.Enrollment.Status;
import cn.edu.ncut.cs.springboot.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 选课记录数据访问层
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /** 根据学生和课程查找选课记录 */
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    /** 根据课程ID和状态查询选课记录 */
    List<Enrollment> findByCourseIdAndStatus(Long courseId, Status status);

    /** 根据学生ID查询所有选课记录 */
    List<Enrollment> findByStudentId(Long studentId);

    /** 根据课程ID查询所有选课记录 */
    List<Enrollment> findByCourseId(Long courseId);

    /** 根据课程ID列表查询所有选课记录 */
    List<Enrollment> findByCourseIdIn(List<Long> courseIds);
}