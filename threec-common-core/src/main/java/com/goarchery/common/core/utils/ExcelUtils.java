package com.goarchery.common.core.utils;

public class ExcelUtils {

    /** 清洗 Sheet 名，避免非法字符/过长 */
    public static String safeSheetName(String name) {
        if (name == null || name.isBlank()) return "未命名组别";
        String cleaned = name.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return cleaned.length() > 31 ? cleaned.substring(0, 31) : cleaned;
    }

}
