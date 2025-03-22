package com.goarchery.tools.utils;

import com.goarchery.tools.constant.Constant;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全用户实用程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/07/12 18:59
 */
public class SecurityUserUtils {
    /**
     * 获取用户ID
     */
    public static String getUserId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if(request == null){
            return null;
        }

        String userId = (String) request.getAttribute(Constant.USER_KEY);
        if(StringUtils.isBlank(userId)){
            return null;
        }
        return userId;
    }
}
