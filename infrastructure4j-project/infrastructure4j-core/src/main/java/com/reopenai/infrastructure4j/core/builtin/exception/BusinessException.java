package com.reopenai.infrastructure4j.core.builtin.exception;

import com.reopenai.infrastructure4j.beans.builtin.ErrorCode;

/**
 * 业务异常
 *
 * @author Allen Huang
 */
public class BusinessException extends FrameworkException {

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public BusinessException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(cause, errorCode, args);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(Throwable throwable, String code, String message) {
        super(throwable, code, message);
    }

    public BusinessException(Throwable throwable, String code, String message, Object[] args) {
        super(throwable, code, message, args);
    }

}
