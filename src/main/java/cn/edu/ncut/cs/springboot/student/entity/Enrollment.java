package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 选课记录实体
 * 记录学生选课信息，包含抽签状态和成绩
 */
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class Enrollment {

    /** 选课记录ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 学生（多对一） */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** 课程（多对一） */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** 选课状态（枚举：PENDING待抽签、SELECTED中签、REJECTED未中签） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /** 成绩（可为null） */
    private Double score;

    /**
     * 选课状态枚举
     * 严格遵循状态转换图（图3）定义的合法流转路径
     */
    public enum Status {
        PENDING,    // 待抽签（初始状态）
        SELECTED,   // 中签
        REJECTED    // 未中签
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}