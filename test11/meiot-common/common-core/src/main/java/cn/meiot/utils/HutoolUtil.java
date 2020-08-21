package cn.meiot.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

public class HutoolUtil {

    private static byte[] key ;

    private static DES des;

    /**
     * 获取密钥
     */
    private  static void getSecret()  {
        key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
    }

    /**
     * 加密
     * @param content    加密的内容
     * @return
     */
    public static  String encryption(String content){
        //每次加密都会获取新的密钥
        getSecret();
        des = SecureUtil.des(key);
        byte[] encrypt = des.encrypt(content);
        //加密为16进制，解密为原字符串
        String encryptHex = des.encryptHex(content);
        return encryptHex;
    }

    /**
     * 解密
     * @param content 加密的内容
     * @return
     */
    public static String decryption(String content){
        des = SecureUtil.des(key);
        String decryptStr = des.decryptStr(content);
        return decryptStr;
    }



}
