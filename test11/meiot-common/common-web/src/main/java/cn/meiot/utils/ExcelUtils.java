package cn.meiot.utils;

import cn.meiot.entity.vo.Result;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelUtils {

    /**
     *
     * @param list  导出内容
     * @param fileName  导出文件名
     * @param response   respone
     * @param clazz  导出的类
     * @return
     */
    public static Result export(List<? extends BaseRowModel> list,String fileName, HttpServletResponse response, Class<? extends BaseRowModel> clazz) {

        Result boUtil = Result.getDefaultFalse();
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
        try {
            boUtil = Result.getDefaultTrue();
//            fileName += "_"+new String(
//                    (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).getBytes(), "UTF-8");
            fileName += "_"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Sheet sheet2 = new Sheet(2, 3,clazz, "sheet", null);
            writer.write(list, sheet2);
            //response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xls", "utf-8"));
            //response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            out.flush();
            boUtil.setMsg("导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            boUtil.setMsg("导出失败");
            return boUtil;
        } finally {
            writer.finish();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return boUtil;
        }
    }
}
