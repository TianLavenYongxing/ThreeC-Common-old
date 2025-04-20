package com.goarchery.common.core.constant;

public enum ErrorCodeEnum {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统内部错误"),

    // 业务错误码（6位数字，前2位表示模块）
    USER_NOT_EXIST(100001, "用户不存在"),
    ORDER_PAY_TIMEOUT(200003, "订单支付超时");

    private final int code;
    private final String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // Getters
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
