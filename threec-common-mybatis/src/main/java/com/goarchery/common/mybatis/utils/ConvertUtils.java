package com.goarchery.common.mybatis.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goarchery.common.core.constant.Constant;
import com.goarchery.common.core.exception.BusinessException;
import com.goarchery.common.core.utils.HttpContextUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 通用转换工具类，支持 BeanUtils 与 MapStruct 转换方式
 * 支持语言字段自动匹配、分页封装转换等功能
 *
 * @author Tian
 */
public class ConvertUtils {

    /**
     * 根据当前语言返回 source 对象中指定字段名的值（如 nameZh / nameEn）
     */
    public static String getLocalField(Object source, String fieldName) {
        return getFieldValueByFieldName(fieldName + getLanguage(), source);
    }

    /**
     * Bean 拷贝：source → target（单对象）
     */
    public static <T> T sourceToTarget(Object source, Class<T> target) {
        if (source == null) return null;

        try {
            T targetObject = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetObject);
            return targetObject;
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }
    }

    /**
     * Bean 拷贝：sourceList → targetList
     */
    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) return null;

        List<T> targetList = new ArrayList<>(sourceList.size());
        try {
            for (Object source : sourceList) {
                T targetObject = target.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, targetObject);
                targetList.add(targetObject);
            }
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }
        return targetList;
    }

    /**
     * 带语言字段的转换（单对象）
     */
    public static <T> T sourceToTarget(Object source, Class<T> target, String... fieldNames) {
        if (source == null) return null;

        try {
            T targetObject = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetObject);

            String language = getLanguage();
            for (String fieldName : fieldNames) {
                setFieldValueByFieldName(fieldName, targetObject,
                        getFieldValueByFieldName(fieldName + language, source));
            }

            return targetObject;
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }
    }

    /**
     * 带语言字段的转换（列表）
     */
    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target, String... fieldNames) {
        if (sourceList == null) return null;

        List<T> targetList = new ArrayList<>(sourceList.size());
        String language = getLanguage();

        try {
            for (Object source : sourceList) {
                T targetObject = target.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, targetObject);

                for (String fieldName : fieldNames) {
                    setFieldValueByFieldName(fieldName, targetObject,
                            getFieldValueByFieldName(fieldName + language, source));
                }

                targetList.add(targetObject);
            }
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }

        return targetList;
    }

    /**
     * 获取对象字段值
     */
    public static String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }
    }

    /**
     * 反射设置字段值
     */
    private static void setFieldValueByFieldName(String fieldName, Object object, String value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new BusinessException("convert error", e);
        }
    }

    /**
     * 获取 Accept-Language 对应的语言后缀
     */
    public static String getLanguage() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String acceptLanguage = null;
        if (request != null) {
            acceptLanguage = request.getHeader(Constant.ACCEPT_LANGUAGE_HEADER);
        }

        String language = null;
        if (StringUtils.isNotBlank(acceptLanguage)) {
            language = acceptLanguage.split(";")[0];
        }

        if (Constant.ACCEPT_LANGUAGE_EN.equals(language)) {
            return Constant.DB_LANGUAGE_EN;
        }
        return Constant.DB_LANGUAGE_ZH;
    }

    /**
     * 分页转换（使用 BeanUtils）
     */
    public static <T, R> Page<R> convertPage(Page<T> sourcePage, Class<R> targetClass) {
        if (sourcePage == null) return new Page<>();

        Page<R> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        List<R> targetList = sourceToTarget(sourcePage.getRecords(), targetClass);
        targetPage.setRecords(targetList);
        return targetPage;
    }

    /**
     * 分页转换（使用 MapStruct Mapper）
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Page<R> convertPage(Page<T> sourcePage, Object mapper) {
        if (sourcePage == null) return new Page<>();

        Page<R> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        List<R> targetList = new ArrayList<>(sourcePage.getRecords().size());

        for (T source : sourcePage.getRecords()) {
            R target = sourceToTarget(source, mapper);
            targetList.add(target);
        }

        targetPage.setRecords(targetList);
        return targetPage;
    }

    /**
     * 单对象转换（使用 MapStruct）
     */
    @SuppressWarnings("unchecked")
    public static <S, T> T sourceToTarget(S source, Object mapper) {
        if (source == null) return null;
        try {
            Method method = findMappingMethod(mapper.getClass(), source.getClass());
            if (method != null) {
                return (T) method.invoke(mapper, source);
            }
            throw new BusinessException("No mapping method found for class: " + source.getClass());
        } catch (Exception e) {
            throw new BusinessException("Convert via mapper failed", e);
        }
    }

    /**
     * 根据 source 类型在 mapper 中查找合适的 toDTO 或 toEntity 方法
     */
    private static Method findMappingMethod(Class<?> mapperClass, Class<?> sourceClass) {
        for (Method method : mapperClass.getMethods()) {
            if (method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].isAssignableFrom(sourceClass)) {
                String name = method.getName();
                if (name.startsWith("to") && (name.endsWith("DTO") || name.endsWith("Entity"))) {
                    return method;
                }
            }
        }
        return null;
    }

}
