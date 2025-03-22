package com.stock.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.stock.tools.utils.SecurityUserUtils;
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
        // 初始化删除标志和删除相关字段
        this.strictInsertFill(metaObject, "delFlag", Boolean.class, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        String userId = SecurityUserUtils.getUserId();
        // 强制设置 updateAt 字段
        metaObject.setValue("updateAt", now);
        // 自动填充更新人
        this.strictUpdateFill(metaObject, "updateBy", String.class, userId);
        this.strictUpdateFill(metaObject, "updateAt", Date.class, now);
        // 检查是否触发了逻辑删除
        Boolean isDeleted = (Boolean) metaObject.getValue("delFlag");
        if (isDeleted != null && isDeleted) {
            this.strictUpdateFill(metaObject, "deleteBy", String.class, userId);
            this.strictUpdateFill(metaObject, "deleteAt", Date.class, now);
        }
    }
}