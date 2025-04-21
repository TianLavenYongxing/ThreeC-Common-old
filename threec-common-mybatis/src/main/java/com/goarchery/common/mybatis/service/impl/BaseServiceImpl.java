package com.goarchery.common.mybatis.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goarchery.common.core.constant.Constant;
import com.goarchery.common.core.model.PageQueryApi;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 基本服务实施
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:42
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public static <T> Page<T> getPage(PageQueryApi pageQueryApi) {
        // 从请求参数中获取分页信息
        int currentPage = Objects.isNull(pageQueryApi.getPage())?1:pageQueryApi.getPage();
        int pageSize = Objects.isNull(pageQueryApi.getLimit())?10:pageQueryApi.getLimit();
        String orderField = pageQueryApi.getOrderField();
        String order = pageQueryApi.getOrder();
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

    public Page<T> page(PageQueryApi pageQueryApi,Map<String,Object> params) {
        return null;
    }

    @Transactional
    public int deleteBatchIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0; // 如果没有 ID，返回 0
        }
        // 调用 DAO 中的物理删除方法
        return baseMapper.deleteBatchIds(ids);
    }

}
