package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程数据访问层
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /** 根据学院ID查询所有课程 */
    List<Course> findByDepartmentId(Long departmentId);

    /** 根据教师ID查询所有课程 */
    List<Course> findByTeacherId(Long teacherId);
}