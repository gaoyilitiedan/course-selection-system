package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教师数据访问层
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /** 根据学院ID查询所有教师 */
    List<Teacher> findByDepartmentId(Long departmentId);
}