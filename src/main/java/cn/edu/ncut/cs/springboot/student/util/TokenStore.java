package cn.edu.ncut.cs.springboot.student.util;

import cn.edu.ncut.cs.springboot.student.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 管理器
 * 使用 ConcurrentHashMap 在内存中维护 token -> User 的映射
 * 不引入 Spring Security，自实现 Token 认证
 */
@Component
public class TokenStore {

    /** 内存 Token 存储 */
    private final Map<String, User> tokenMap = new ConcurrentHashMap<>();

    /**
     * 创建 Token 并关联用户
     * @param user 登录用户
     * @return 生成的 token 字符串
     */
    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, user);
        return token;
    }

    /**
     * 根据 Token 获取用户
     * @param token token 字符串
     * @return 关联的用户，不存在返回 null
     */
    public User getUser(String token) {
        return tokenMap.get(token);
    }

    /**
     * 移除 Token（登出）
     * @param token token 字符串
     */
    public void removeToken(String token) {
        tokenMap.remove(token);
    }

    /**
     * 校验 Token 是否有效
     * @param token token 字符串
     * @return 有效返回 true
     */
    public boolean isValid(String token) {
        return tokenMap.containsKey(token);
    }
}