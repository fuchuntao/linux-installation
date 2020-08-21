package cn.meiot.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class Base64Util {

    private static final Logger LOG = LoggerFactory.getLogger(Base64Util.class);
    // 字符串编码
    private static final String UTF_8 = "UTF-8";

    /**
     * 加密字符串
     * @param inputData
     * @return
     */
    public static String decodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.decodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error(inputData, e);
        }
        return null;
    }

    /**
     * 解密加密后的字符串
     * @param inputData
     * @return
     */
    public static String encodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.encodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error(inputData, e);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Base64Util.encodeData("我是中文"));
        String enStr = Base64Util.encodeData("我是中文");
        System.out.println(Base64Util.decodeData(enStr));
    }
}
