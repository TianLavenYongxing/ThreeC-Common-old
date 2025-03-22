package com.goarchery.mybatis.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入实用程序
 *
 * @author Tian, Laven Yongxing
 * @date 2024/10/29 18:41
 */
public class ExcelImportUtils {

    /**
     * 导入 Excel 文件
     *
     * @param file  上传的 Excel 文件
     * @param clazz 导入类的类型
     * @param <T>   泛型
     * @return 数据列表
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), clazz, new ReadListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                dataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成后可以进行一些操作
            }
        }).sheet().doRead();

        return dataList;
    }

}
