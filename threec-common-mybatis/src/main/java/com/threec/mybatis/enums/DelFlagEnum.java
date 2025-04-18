package com.threec.mybatis.enums;

/**
 * 标志枚举
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:36
 */
public enum DelFlagEnum {
    NORMAL(1),
    DEL(0);

    private final int value;

    DelFlagEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
