package com.goarchery.common.core.exception;

import com.goarchery.common.core.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:05
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public BusinessException(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public BusinessException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public BusinessException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public BusinessException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }
}
