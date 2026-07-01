package cn.edu.ncut.cs.springboot.student.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理
 * 使用 @ControllerAdvice 确保所有响应格式统一为 {code, message, data}
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 BusinessException（业务异常）
     * 返回格式：{code: 400, message: "错误描述", data: null}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        result.put("data", null);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理通用 Exception（未知异常）
     * 返回格式：{code: 500, message: "服务器内部错误", data: null}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "服务器内部错误: " + e.getMessage());
        result.put("data", null);
        return ResponseEntity.internalServerError().body(result);
    }
}