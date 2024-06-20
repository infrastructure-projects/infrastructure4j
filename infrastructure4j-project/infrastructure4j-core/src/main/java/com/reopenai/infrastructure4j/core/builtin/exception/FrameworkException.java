package com.reopenai.infrastructure4j.core.builtin.exception;

import com.reopenai.infrastructure4j.beans.builtin.ErrorCode;
import com.reopenai.infrastructure4j.core.i18n.I18nUtil;

/**
 * 框架异常
 *
 * @author Allen Huang
 */
public class FrameworkException extends RuntimeException {
    /**
     * 异常代码
     */
    private final ErrorCode errorCode;
    /**
     * 异常参数，此参数可用于构建国际化的异常信息
     */
    private final Object[] args;

    public FrameworkException(ErrorCode errorCode, Object... args) {
        super(I18nUtil.parseLocaleMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public FrameworkException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(I18nUtil.parseLocaleMessage(errorCode, args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public FrameworkException(String code, String message) {
        super(message);
        this.errorCode = ErrorCode.temporary(code);
        this.args = new Object[0];
    }

    public FrameworkException(Throwable throwable, String code, String message) {
        super(message, throwable);
        this.errorCode = ErrorCode.temporary(code);
        this.args = new Object[0];
    }

    public FrameworkException(Throwable throwable, String code, String message, Object[] args) {
        super(message, throwable);
        this.errorCode = ErrorCode.temporary(code);
        this.args = args;
    }

}
