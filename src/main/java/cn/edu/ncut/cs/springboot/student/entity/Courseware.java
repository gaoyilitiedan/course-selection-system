package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;

/**
 * 课件实体
 * 记录上传课件的文件名和存储路径，课件文件存储在文件系统
 */
@Entity
@Table(name = "coursewares")
public class Courseware {

    /** 课件ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属课程（多对一） */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** 文件名 */
    @Column(nullable = false)
    private String fileName;

    /** 文件存储路径 */
    @Column(nullable = false)
    private String filePath;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}