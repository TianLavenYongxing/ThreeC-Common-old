package com.stock.mybatis.vo;

import lombok.Data;

import java.util.List;

/**
 * 通过下拉列表对象
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 18:22
 */
@Data
public class DictVO {
    private Long id;
    private String code;
    private String name;
    private String param;
    private List<DictVO> childDictVO;
}