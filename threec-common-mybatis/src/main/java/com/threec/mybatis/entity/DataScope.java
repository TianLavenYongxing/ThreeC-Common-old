package com.threec.mybatis.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 数据范围
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:34
 */
@Getter
@Setter
@ToString
public class DataScope {

    private String sqlFilter;

    public DataScope(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

}