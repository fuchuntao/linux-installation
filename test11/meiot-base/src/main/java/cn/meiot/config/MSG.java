package cn.meiot.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package cn.meiot.config
 * @Description:
 * @author: 武有
 * @date: 2019/12/6 14:51
 * @Copyright: www.spacecg.cn
 */
@Deprecated
public class MSG {

    /**
     * 推送消息type 这个数用来区分升级了  还有 1.2.3.4.5 这5个数字都不能用 都已经用了
     */
    public static final int UPGRADE=100001;

    public static void main(String[] args) {
        List<Integer> integerList=new ArrayList<>();
        for (int i=0;i<100;i++){
            integerList.add(i);
        }
        for (int i=0;i<integerList.size();i++){
            integerList.remove(i);
        }
        System.out.println("ok");
    }
}
