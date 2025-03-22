package com.goarchery.tools.constant;

/**
 * 恒定
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 15:59
 */
public interface Constant {
    /**
     * 成功
     */
    int SUCCESS = 200;
    /**
     * 失败
     */
    int FAIL = 500;
    /**
     * OK
     */
    String OK = "OK";
    /**
     * httpSUCCESS
     */
    Long HTTP_SUCCESS = 200L;
    /**
     * 用户标识
     */
    String USER_KEY = "userId";
    /**
     * 菜单根节点标识
     */
    Long MENU_ROOT = 0L;
    /**
     * 部门根节点标识
     */
    Long DEPT_ROOT = 0L;
    /**
     * 数据字典根节点标识
     */
    Long DICT_ROOT = 0L;
    /**
     * 升序
     */
    String ASC = "asc";
    /**
     * 降序
     */
    String DESC = "desc";
    /**
     * 删除字段名
     */
    String DEL_FLAG = "del_flag";
    /**
     * 启用停用
     */
    String VALID_FLAG = "valid_Flag";
    /**
     * 创建时间字段名
     */
    String CREATE_DATE = "create_date";

    /**
     * 数据权限过滤
     */
    String SQL_FILTER = "sqlFilter";
    /**
     * 当前页码
     */
    String PAGE = "page";
    /**
     * 每页显示记录数
     */
    String LIMIT = "limit";
    /**
     * 排序字段
     */
    String ORDER_FIELD = "orderField";
    /**
     * 排序方式
     */
    String ORDER = "order";
    /**
     * token header
     */
    String TOKEN_HEADER = "token";
    /**
     * 租户编码
     */
    String TENANT_CODE = "tenant_code";
    /**
     * 默认租户
     */
    Long DEFAULT_TENANT = 1000L;
    /**
     * 应用编号
     */
    String APPCODE="APPCODE";
    /**
     * 前端-英文
     */
    String ACCEPT_LANGUAGE_EN="en-US";
    /**
     * 前端-中文
     */
    String ACCEPT_LANGUAGE_ZH="zh-CN";
    /**
     * 数据库-英文
     */
    String DB_LANGUAGE_EN="En";
    /**
     * 数据库-中文
     */
    String DB_LANGUAGE_ZH="Zh";
    /**
     * language header
     */
    String ACCEPT_LANGUAGE_HEADER="Accept-Language";
}