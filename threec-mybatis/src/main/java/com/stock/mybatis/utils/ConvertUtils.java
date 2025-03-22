package com.stock.mybatis.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.tools.constant.Constant;
import com.stock.tools.exception.BusinessException;
import com.stock.tools.utils.HttpContextUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 转换实用程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 15:47
 */
public class ConvertUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConvertUtils.class);

    public static String getLocalField(Object source,String fieldName){
        return getFieldValueByFieldName(fieldName+ getLanguage(),source);
    }

    public static <T> T sourceToTarget(Object source, Class<T> target){
        if(source == null){
            return null;
        }
        T targetObject = null;
        try {
            targetObject = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetObject);
        } catch (Exception e) {
            logger.error("convert error ", e);
        }

        return targetObject;
    }

    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target){
        if(sourceList == null){
            return null;
        }

        List<T> targetList = new ArrayList<>(sourceList.size());
        try {
            for(Object source : sourceList){
                T targetObject = target.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, targetObject);
                targetList.add(targetObject);
            }
        }catch (Exception e){
            logger.error("convert error ", e);
        }

        return targetList;
    }
    public static <T> T sourceToTarget(Object source, Class<T> target,String... fieldNames){
        if(source == null){
            return null;
        }
        T targetObject = null;
        try {
            targetObject = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetObject);
            String language = getLanguage();
            for (String fieldName: fieldNames) {
                setFieldValueByFieldName(fieldName,targetObject,getFieldValueByFieldName(fieldName+language,source));
            }
        } catch (Exception e) {
            logger.error("convert error ", e);
        }

        return targetObject;
    }
    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target,String... fieldNames){
        if(sourceList == null){
            return null;
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        String language = getLanguage();
        try {
            for(Object source : sourceList){
                T targetObject = target.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, targetObject);
                for (String fieldName: fieldNames) {
                    setFieldValueByFieldName(fieldName,targetObject,getFieldValueByFieldName(fieldName+language,source));
                }
                targetList.add(targetObject);
            }
        }catch (Exception e){
            logger.error("convert error ", e);
        }
        return targetList;
    }

    public static String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  field.get(object).toString();
        } catch (Exception e) {
            throw new BusinessException("convert error ", e);
        }
    }

    private static void setFieldValueByFieldName(String fieldName, Object object,String value) {
        try {
            // 获取obj类的字节文件对象
            Class<?> c = object.getClass();
            // 获取该类的成员变量
            Field f = c.getDeclaredField(fieldName);
            // 取消语言访问检查
            f.setAccessible(true);
            // 给变量赋值
            f.set(object, value);
        } catch (Exception e) {
            throw new BusinessException("convert error ", e);
        }
    }

    public static String getLanguage() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String acceptLanguage= null;
        if (request != null) {
            acceptLanguage = request.getHeader(Constant.ACCEPT_LANGUAGE_HEADER);
        }
        String language=null;
        if (StringUtils.isNotBlank(acceptLanguage)){
            language =acceptLanguage.split(";")[0];
        }
        if (Constant.ACCEPT_LANGUAGE_EN.equals(language) ){
            language=Constant.DB_LANGUAGE_EN;
        }else if(Constant.ACCEPT_LANGUAGE_ZH.equals(language)){
            language=Constant.DB_LANGUAGE_ZH;
        }else {
            language=Constant.DB_LANGUAGE_ZH;
        }
        return language;
    }
    public static <T, R> Page<R> convertPage(Page<T> sourcePage, Class<R> targetClass) {
        // 创建目标 Page 对象
        Page<R> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());

        // 转换内容
        List<R> targetList = sourceToTarget(sourcePage.getRecords(), targetClass);
        targetPage.setRecords(targetList);

        return targetPage;
    }
}