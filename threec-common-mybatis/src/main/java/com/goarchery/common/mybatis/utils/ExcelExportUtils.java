package com.goarchery.common.mybatis.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.goarchery.common.core.utils.ExcelUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Excel输出实用程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/10/29 18:41
 */
public class ExcelExportUtils {

    // —— 公共：下载头设置 ——
    private static void prepareDownloadHeaders(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encoded + ".xlsx");
    }

    /**
     * 导出 Excel 文件
     *
     * @param response  HttpServletResponse
     * @param dataList  数据列表
     * @param clazz     导出类的类型
     * @param fileName  导出的文件名
     * @param sheetName 工作表的名称
     * @param <T>       泛型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(HttpServletResponse response, List<T> dataList, Class<T> clazz, String fileName, String sheetName) throws IOException {
        prepareDownloadHeaders(response,fileName);
        EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(dataList);
    }

    /**
     * 导出 Excel 文件
     *
     * @param response       HttpServletResponse
     * @param sheetDataMap   包含多个sheet数据的Map，key为sheet名称，value为数据列表
     * @param clazz          导出类的类型
     * @param fileName       导出的文件名
     * @param <T>            泛型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(HttpServletResponse response, Map<String, List<T>> sheetDataMap, Class<T> clazz, String fileName) throws IOException {
        prepareDownloadHeaders(response,fileName);
        // 创建 ExcelWriter 对象
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), clazz).build();
        try {
            int sheetNo = 1; // sheet编号从1开始
            // 写入每个sheet的数据
            for (Map.Entry<String, List<T>> entry : sheetDataMap.entrySet()) {
                String sheetName = entry.getKey();  // 获取sheet名称
                sheetName = ExcelUtils.safeSheetName(sheetName);
                List<T> data = entry.getValue();    // 获取数据列表
                // 创建WriteSheet对象
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetName)
                        .head(clazz)
                        .build();
                // 写入数据
                excelWriter.write(data, writeSheet);
                sheetNo++;
            }
        } finally {
            // 关闭ExcelWriter
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 导出 Excel 文件模版
     *
     * @param response                HttpServletResponse
     * @param clazz                   导出类的类型
     * @param customSheetWriteHandler 自定义表格写入收件箱
     * @param fileName                导出的文件名
     * @param sheetName               工作表的名称
     * @param <T>                     泛型
     * @throws IOException IO异常
     */
    public static <T> void exportTemplate(HttpServletResponse response, Class<T> clazz, CustomSheetWriteHandler customSheetWriteHandler, String fileName, String sheetName) throws IOException {
        // 设置响应头
        prepareDownloadHeaders(response,fileName);
        // 创建一个空数据列表
        List<T> emptyDataList = new ArrayList<>();
        if (Objects.isNull(customSheetWriteHandler)) {
            EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(emptyDataList);
        } else {
            EasyExcel.write(response.getOutputStream(), clazz).registerWriteHandler(customSheetWriteHandler).sheet(sheetName).doWrite(emptyDataList);
        }
    }

    /**
     * 导出 byte[] 类型的 Excel 模版文件
     *
     * @param response HTTP 响应对象
     * @param fileData Excel 文件的字节数据
     * @param fileName 导出文件的名称
     * @throws IOException 可能抛出的异常
     */
    public static void exportByteArrayTemplate(HttpServletResponse response, byte[] fileData, String fileName) throws IOException {
        // 设置下载响应头
        prepareDownloadHeaders(response, fileName);

        // 获取响应输出流
        try (ServletOutputStream out = response.getOutputStream()) {
            // 将文件字节流直接写入响应输出流
            out.write(fileData);
            out.flush();
        } catch (IOException e) {
            // 如果写入文件出错，设置500错误响应
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new IOException("Error writing file data to response.", e);
        }
    }
}
