package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 课程实体
 */
@Entity
@Table(name = "courses")
public class Course {

    /** 课程ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 课程名称 */
    @Column(nullable = false)
    private String name;

    /** 课程介绍 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 课程容量 */
    @Column(nullable = false)
    private int capacity;

    /** 先修课程（自关联，多对一，可为null） */
    @ManyToOne
    @JoinColumn(name = "prerequisite_id")
    private Course prerequisite;

    /** 授课教师（多对一） */
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    /** 开设学院（多对一） */
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Course prerequisite) {
        this.prerequisite = prerequisite;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}