package com.goarchery.common.core.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PageQueryApi {

    @Min(1)
    @JsonSetter(nulls = Nulls.SKIP)
    @Parameter(description = "当前页码，从1开始", required = true)
    private Integer page = 1;

    @Min(1)
    @JsonSetter(nulls = Nulls.SKIP)
    @Parameter(description = "每页显示记录数", required = true)
    private Integer limit = 10;

    @JsonSetter(nulls = Nulls.SKIP)
    @Parameter(description = "排序字段")
    private String orderField = "id";

    @Pattern(regexp = "(?i)asc|desc")
    @JsonSetter(nulls = Nulls.SKIP)
    @Parameter(description = "排序方式，可选值(asc、desc)")
    private String order = "asc";

}