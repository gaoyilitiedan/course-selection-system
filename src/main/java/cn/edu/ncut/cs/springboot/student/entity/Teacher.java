package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 教师实体，继承 User 基类
 * teachers 表仅存储教师扩展字段
 */
@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "id")
public class Teacher extends User {

    /** 教师工号 */
    @Column(nullable = false)
    private String teacherNo;

    /** 教师姓名 */
    @Column(nullable = false)
    private String name;

    // ===== Getters & Setters =====

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}