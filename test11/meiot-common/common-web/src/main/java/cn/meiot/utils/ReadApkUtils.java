package cn.meiot.utils;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2020/3/16 9:44
 * @Copyright: www.spacecg.cn
 */
public class ReadApkUtils {
    public static final String VERSION_NAME="VersionName";
    public static final String VERSION_CODE="VersionCode";
    public static final String PACKAGE_NAME="PackageName";
    public static final String LABEL="Label";
    public static void main(String[] args) {
        File file=new File("C:\\Users\\Administrator\\Desktop\\apk.apk");
        ReadApkUtils.apkFile(file);
    }
    private ReadApkUtils(){

    }
    public static Map<String,String> apkFile(File file){
        ApkFile apkFile=null;
        Map<String,String> map=null;
        try {
             apkFile=new ApkFile(file);
            ApkMeta apkMeta = apkFile.getApkMeta();
            map=new HashMap<>();
            map.put(VERSION_NAME,apkMeta.getVersionName());
            map.put(VERSION_CODE,apkMeta.getVersionCode().toString());
            map.put(PACKAGE_NAME,apkMeta.getPackageName());
            map.put(LABEL,apkMeta.getLabel());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  map;
    }
}
