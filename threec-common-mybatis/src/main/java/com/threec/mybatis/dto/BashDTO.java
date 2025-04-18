package com.threec.mybatis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BashDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "删除标志（0已删除，1未删除）", example = "1")
    private boolean delFlag;

    @NotNull(message = "version field cannot be null")
    @Schema(description = "乐观锁", example = "1")
    private Integer version;

/*
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Integer createBy;

    @NotNull
    @ApiModelProperty(value = "创建人")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Integer updateBy;

    @ApiModelProperty(value = "更新人")
    private Date updateTime;

    @ApiModelProperty(value = "删除时间")
    private Date delTime;

    @ApiModelProperty(value = "删除人")
    private Integer delBy;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "拓展字段")
    private String extend;
 */

}
