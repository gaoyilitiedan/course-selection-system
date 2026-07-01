package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Courseware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课件数据访问层
 */
@Repository
public interface CoursewareRepository extends JpaRepository<Courseware, Long> {

    /** 根据课程ID查询所有课件 */
    List<Courseware> findByCourseId(Long courseId);
}