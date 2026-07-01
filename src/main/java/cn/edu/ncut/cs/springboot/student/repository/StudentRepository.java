package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生数据访问层
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /** 根据学院ID查询所有学生 */
    List<Student> findByDepartmentId(Long departmentId);
}