package com.goarchery.common.core.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class PageQueryApi {

    @Parameter(description = "当前页码，从1开始", required = true)
    private Integer page;

    @Parameter(description = "每页显示记录数", required = true)
    private Integer limit;

    @Parameter(description = "排序字段")
    private String orderField;

    @Parameter(description = "排序方式，可选值(asc、desc)")
    private String order;

}