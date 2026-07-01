package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 学院数据访问层
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}