package cn.edu.ncut.cs.springboot.student.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 留言消息实体
 * 支持教师与学生之间的双向留言通信
 */
@Entity
@Table(name = "messages")
public class Message {

    /** 消息ID（自增主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 发送方类型（TEACHER 或 STUDENT） */
    @Column(name = "from_user_type", nullable = false)
    private String fromUserType;

    /** 发送方用户ID */
    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    /** 接收方类型（TEACHER 或 STUDENT） */
    @Column(name = "to_user_type", nullable = false)
    private String toUserType;

    /** 接收方用户ID */
    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    /** 关联课程（多对一） */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** 消息内容 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUserType() {
        return fromUserType;
    }

    public void setFromUserType(String fromUserType) {
        this.fromUserType = fromUserType;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserType() {
        return toUserType;
    }

    public void setToUserType(String toUserType) {
        this.toUserType = toUserType;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}