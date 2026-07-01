package cn.edu.ncut.cs.springboot.student.exception;

/**
 * 业务异常
 * 用于在 Service 层抛出可预知的业务错误，由 GlobalExceptionHandler 统一捕获处理
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private int code;

    /**
     * 构造业务异常
     * @param code    错误码
     * @param message 错误描述
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常（默认错误码400）
     * @param message 错误描述
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}