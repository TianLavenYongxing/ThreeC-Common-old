package com.goarchery.mybatis.utils;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel输出实用程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/10/29 18:41
 */
public class ExcelExportUtils {

    /**
     * 导出 Excel 文件
     * @param response HttpServletResponse
     * @param dataList 数据列表
     * @param clazz 导出类的类型
     * @param fileName 导出的文件名
     * @param sheetName 工作表的名称
     * @param <T> 泛型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(HttpServletResponse response, List<T> dataList, Class<T> clazz, String fileName, String sheetName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet(sheetName)
                .doWrite(dataList);
    }

    // 新增导出模板的方法
    public static <T> void exportTemplate(HttpServletResponse response, Class<T> clazz,CustomSheetWriteHandler customSheetWriteHandler, String fileName, String sheetName) throws IOException {
        // 创建一个空数据列表
        List<T> emptyDataList = new ArrayList<>();

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");

        // 使用 EasyExcel 写入空数据列表
        EasyExcel.write(response.getOutputStream(), clazz).registerWriteHandler(customSheetWriteHandler).sheet(sheetName).doWrite(emptyDataList);
    }
}
