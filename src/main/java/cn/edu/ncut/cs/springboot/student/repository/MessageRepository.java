package cn.edu.ncut.cs.springboot.student.repository;

import cn.edu.ncut.cs.springboot.student.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 留言消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /** 查询某用户收到的消息（按时间倒序） */
    List<Message> findByToUserTypeAndToUserIdOrderByCreateTimeDesc(String toUserType, Long toUserId);

    /** 查询某用户发送的消息（按时间倒序） */
    List<Message> findByFromUserTypeAndFromUserIdOrderByCreateTimeDesc(String fromUserType, Long fromUserId);
}