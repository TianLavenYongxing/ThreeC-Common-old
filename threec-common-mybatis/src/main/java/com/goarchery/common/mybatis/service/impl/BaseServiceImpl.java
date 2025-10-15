package com.goarchery.common.mybatis.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goarchery.common.core.constant.Constant;
import com.goarchery.common.core.model.PageQueryApi;

import java.util.Optional;

/**
 * 基本服务实施
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:42
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public static <T, Q extends PageQueryApi> Page<T> getPage(Q pageQueryApi) {
        int currentPage = Optional.ofNullable(pageQueryApi.getPage()).orElse(1);
        int pageSize    = Optional.ofNullable(pageQueryApi.getLimit()).orElse(10);
        String orderField = (pageQueryApi.getOrderField() == null || pageQueryApi.getOrderField().isBlank())
                ? "id"
                : pageQueryApi.getOrderField();
        String order = (pageQueryApi.getOrder() == null || pageQueryApi.getOrder().isBlank())
                ? "desc"
                : pageQueryApi.getOrder();

        Page<T> page = new Page<>(currentPage, pageSize);
        if (Constant.ASC.equalsIgnoreCase(order)) {
            page.addOrder(OrderItem.asc(orderField));
        } else if (Constant.DESC.equalsIgnoreCase(order)) {
            page.addOrder(OrderItem.desc(orderField));
        }
        return page;
    }

}
