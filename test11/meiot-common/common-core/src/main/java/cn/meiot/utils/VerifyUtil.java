package cn.meiot.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验
 */
public class VerifyUtil {

    public static Boolean verifyPhone(String account) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9])|(16[6])|(19[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(account);  //registrant_phone  ====  电话号码字段
        boolean isMatch = m.matches();
        if(isMatch){
            return true;
        }
        return false;

    }


    /**
     * 邮箱校验
     * @param email
     * @return
     */
    public static  Boolean verifyEmail(String email){
        if(StringUtils.isEmpty(email)){
            return false;
        }
        ///^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
       // ^([a-z0-9A-Z]+[-|\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$
        String regEx1 = "^([a-z0-9A-Z_.]+[-\\.]?)+[a-z0-9A-Z_.]@([a-z0-9A-Z_.]+[-\\.]?)+[a-z0-9A-Z]+[a-zA-Z]{1,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    public static void main(String[] args) {

        String reg = "^([a-z0-9A-Z_.]+[-\\.]?)+[a-z0-9A-Z_.]@([a-z0-9A-Z_.]+[-\\.]?)+[a-z0-9A-Z]+[a-zA-Z]{1,}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher("1-._2_ghd@1.-_.cn");
        System.out.println(m.matches());

    }


    /**
     * emoji表情过滤
     * @param source  源字符串
     * @param slipStr emoji需要替换的字符串
     * @return
     */
    public static String filterEmoji(String source,String slipStr) {
        if(StringUtils.isNotBlank(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        }else{
            return source;
        }
    }



    /**
     * 队列比较
     * @param <T>
     * @param a
     * @param b
     * @return
     */
    public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
        if(a == null && b == null ){
            return true;
        }
        if(a == null || b == null){
            return false;
        }
        if(a.size() != b.size())
            return false;
        Collections.sort(a);
        Collections.sort(b);
        for(int i=0;i<a.size();i++){
            if(!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }


}
