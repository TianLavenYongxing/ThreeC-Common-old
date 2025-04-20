package com.goarchery.common.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.goarchery.common.core.utils.SecurityUserUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 字段Meta对象处理程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 18:19
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {

    // todo 完善代码
    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        String userId = SecurityUserUtils.getUserId() == null ? "" : SecurityUserUtils.getUserId();
        this.strictInsertFill(metaObject, "createBy", String.class, userId);
        this.strictInsertFill(metaObject, "createAt", Date.class, now);
        this.strictInsertFill(metaObject, "delFlag", Boolean.class, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        String userId = SecurityUserUtils.getUserId();
        this.strictUpdateFill(metaObject, "updateBy", String.class, userId);
        metaObject.setValue("updateAt", now);
        Boolean isDeleted = (Boolean) metaObject.getValue("delFlag");
        if (isDeleted != null && isDeleted) {
            this.strictUpdateFill(metaObject, "deleteBy", String.class, userId);
            this.strictUpdateFill(metaObject, "deleteAt", Date.class, now);
        }
    }
}