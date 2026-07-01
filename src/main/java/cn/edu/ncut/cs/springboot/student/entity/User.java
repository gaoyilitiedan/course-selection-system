package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 用户基类实体
 * 使用 JOINED 继承策略，所有子类（Student、Teacher）共享该基类的主键
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    /** 用户ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名（唯一，非空） */
    @Column(unique = true, nullable = false)
    private String username;

    /** 密码（明文存储，简化实验） */
    @Column(nullable = false)
    private String password;

    /** 角色（枚举：ROLE_ADMIN、ROLE_DEPT_ADMIN、ROLE_TEACHER、ROLE_STUDENT） */
    @Column(nullable = false)
    private String role;

    /** 所属学院（多对一，可为null，系统管理员可为null） */
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}