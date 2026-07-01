package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 学生实体，继承 User 基类
 * 使用 JOINED 继承策略，students 表仅存储学生扩展字段
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {

    /** 学号 */
    @Column(nullable = false)
    private String studentNo;

    /** 学生姓名 */
    @Column(nullable = false)
    private String name;

    // ===== Getters & Setters =====

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}