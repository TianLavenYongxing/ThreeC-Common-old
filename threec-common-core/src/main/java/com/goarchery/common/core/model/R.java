package com.goarchery.common.core.model;

import com.goarchery.common.core.constant.ErrorCodeEnum;
import com.goarchery.common.core.utils.MessageUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;


/**
 * 统一响应封装
 *
 * @param <T> 响应数据类型
 * @author Tian, Laven Yongxing
 */
@Data
@Accessors(chain = true) // 启用链式调用
@Schema(description = "统一响应结果")
public class R<T> implements Serializable {
    /**
     * 成功状态码常量
     */
    public static final int SUCCESS_CODE = ErrorCodeEnum.SUCCESS.getCode();
    /**
     * 成功消息常量
     */
    public static final String SUCCESS_MSG = "success";
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "状态码", example = "200")
    private int code = SUCCESS_CODE;

    @Schema(description = "是否成功", example = "true")
    private boolean success = true;

    @Schema(description = "消息提示", example = "success")
    private String msg = SUCCESS_MSG;

    @Schema(description = "响应数据", implementation = Object.class)
    private T data;

    // ========== 静态工厂方法 ========== //

    public static <T> R<T> ok() {
        return new R<>();
    }

    public static <T> R<T> ok(T data) {
        return new R<T>().setData(data);
    }

    public static <T> R<T> fail(int code, String msg, T data) {
        return new R<T>().setSuccess(false).setCode(code).setMsg(msg).setData(data);
    }

    public static <T> R<T> fail() {
        return fail(ErrorCodeEnum.SYSTEM_ERROR.getCode(), ErrorCodeEnum.SYSTEM_ERROR.getMsg(), null);
    }

    public static <T> R<T> fail(int code) {
        return fail(code, MessageUtils.getMessage(code), null);
    }

    public static <T> R<T> fail(ErrorCodeEnum errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public static <T> R<T> fail(String msg) {
        return fail(ErrorCodeEnum.SYSTEM_ERROR.getCode(), msg, null);
    }

    public static <T> R<T> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> R<T> fail(int code, T data) {
        return fail(code, MessageUtils.getMessage(code), data);
    }
}