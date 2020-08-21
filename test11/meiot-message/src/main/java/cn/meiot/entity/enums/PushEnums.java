package cn.meiot.entity.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.entity.enums
 * @Description:
 * @author: 武有
 * @date: 2019/12/19 12:03
 * @Copyright: www.spacecg.cn
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum  PushEnums {
    ALL(0,"allPush"),
    PERSONAL(5,"personalPush"),
    ENTERPRISE(2,"enterprisePush");
    private Integer name;
    private String value;
    // 将数据缓存到map中
    private static final Map<Integer, String> map = new HashMap<Integer, String>();
    static {
        for (PushEnums push : PushEnums.values()) {
            map.put(push.getName(), push.getValue());
        }
    }
    public static String getValueByName(Integer name) {
        return map.get(name);
    }
}
