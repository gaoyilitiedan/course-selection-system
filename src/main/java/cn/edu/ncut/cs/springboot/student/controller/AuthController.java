package cn.edu.ncut.cs.springboot.student.controller;

import cn.edu.ncut.cs.springboot.student.entity.User;
import cn.edu.ncut.cs.springboot.student.repository.UserRepository;
import cn.edu.ncut.cs.springboot.student.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供登录接口，无需 Token 认证
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenStore tokenStore;

    /**
     * POST /api/auth/login
     * 登录认证，返回 token
     */

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 参数校验
        if (username == null || password == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "用户名和密码不能为空");
            result.put("data", null);
            return ResponseEntity.badRequest().body(result);
        }

        // 验证用户
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            result.put("data", null);
            return ResponseEntity.status(401).body(result);
        }

        // 生成 Token
        String token = tokenStore.createToken(user);

        // 构建响应
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        if (user.getDepartment() != null) {
            data.put("departmentId", user.getDepartment().getId());
            data.put("departmentName", user.getDepartment().getName());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
}