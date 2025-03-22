package com.goarchery.mybatis.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goarchery.mybatis.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 基本服务实施
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:42
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    // todo 完善修改和逻辑删除功能 使用SecurityUserUtils获取修改人
    @Autowired
    protected M baseDao;

    public static <T> Page<T> getPage(Map<String, Object> params) {
        // 从请求参数中获取分页信息
        int currentPage = Integer.parseInt(params.get(Constant.PAGE).toString());
        int pageSize = Integer.parseInt(params.get(Constant.LIMIT).toString());
        String orderField = (String) params.get(Constant.LIMIT);
        String order = (String) params.get(Constant.ORDER);
        // 创建分页对象
        Page<T> page = new Page<>(currentPage, pageSize);
        if (orderField != null && order != null) {
            if (Constant.ASC.equalsIgnoreCase(order)) {
                page.addOrder(OrderItem.asc(orderField));
            } else if (Constant.DESC.equalsIgnoreCase(order)) {
                page.addOrder(OrderItem.desc(orderField));
            }
        }
        return page;
    }

    @Transactional
    public int deleteBatchIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0; // 如果没有 ID，返回 0
        }
        return baseDao.deleteBatchIds(ids); // 调用 DAO 中的物理删除方法
    }

    public Page<T> page(Map<String, Object> params) {
        Page<T> page = getPage(params);
        baseMapper.selectPage(page, null);
        return page;
    }

}
