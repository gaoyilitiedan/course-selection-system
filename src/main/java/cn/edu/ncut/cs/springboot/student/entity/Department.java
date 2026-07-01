package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 学院实体
 */
@Entity
@Table(name = "departments")
public class Department {

    /** 学院ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 学院名称 */
    @Column(nullable = false)
    private String name;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}