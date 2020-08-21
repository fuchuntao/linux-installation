package cn.meiot.utils.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * @Package cn.meiot.utils.enums
 * @Description:
 * @author: 武有
 * @date: 2020/4/2 9:52
 * @Copyright: www.spacecg.cn
 */
@Slf4j
public enum  TypeEnum {
    /*报警*/
    ALARM(1),

    /*预警*/
    EARLYWARNING(2);
        private Integer value;
        private String name;

    TypeEnum(Integer value) {
        this.value = value;
    }


    public Integer value() {
        return this.value;
    }
    public static String getName(Integer value){
        if (value == null || value > 2) {
            log.info("TypeEnum:getName:{}",value);
        }
        switch (value) {
            case 1:
                return "报警";
            case 2:
                return "预警";
            default:
                return "未知";
        }
    }
   }
