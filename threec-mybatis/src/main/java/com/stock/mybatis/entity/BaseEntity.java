package com.stock.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 基本实体
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:33
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createAt;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateAt;
    /**
     * 删除人
     */
    @TableField(fill = FieldFill.UPDATE)
    private String deleteBy;
    /**
     * 删除时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date deleteAt;
    /**
     * 删除标志（0未删除，1已删除）
     */
    @TableLogic
    private boolean delFlag;
    /**
     * 乐观锁字段
     */
    @Version
    private Integer version;
    /**
     * 备注
     */
    private String remark;
    /**
     * 拓展字段
     */
    private String extend;
}
