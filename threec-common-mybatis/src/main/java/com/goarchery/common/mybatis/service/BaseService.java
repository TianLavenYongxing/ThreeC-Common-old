package com.goarchery.common.mybatis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goarchery.common.core.model.PageQueryApi;

import java.util.List;
import java.util.Map;

/**
 * 基本服务
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 16:46
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 物理删除记录
     * @param ids 要删除的 ID 列表
     * @return 删除的记录数
     */
    int deleteBatchIds(List<String> ids);

    Page<T> page(PageQueryApi pageQuery,Map<String,Object> params);

}