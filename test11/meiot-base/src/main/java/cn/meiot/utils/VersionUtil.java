package cn.meiot.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2020/4/24 11:40
 * @Copyright: www.spacecg.cn
 */
@Slf4j
public class VersionUtil {

    /**
     * s1 > s2 返回正数 s1<s2返回负数 s1=s2 返回0
     * @param s1
     * @param s2
     * @return
     */
    public static Integer eq(String s1,String s2) {
        String[] split = split(s1);
        String[] split1 = split(s2);
        for (int i = 0; i < 3; i++) {
            if (!split[i].equals(split1[i])) {
                log.info("split:{},split1:{}",split[i],split1[i]);
               Integer ss = Integer.valueOf(split[i])-Integer.valueOf(split1[i]);
                log.info("===>eq结果:{}",ss);
                return ss;
            }
        }
        return 0;
//        return getInt(split(s1))-getInt(split(s2));
    }

    public static void main(String[] args) {
        Integer integer = eq("1.1.1", "1.1.1");
        System.out.println(integer);
    }

    public static Integer getInt(String[] split) {
        String str="";
        for (String s : split) {
            str+=s;
        }
        log.info("str:{}" ,str);
        return Integer.valueOf(str);
    }

    public static String[] split(String s) {
        return s.split("\\.");
    }

    public static Integer getSort(String s) {
        return getInt(split(s));
    }
}
