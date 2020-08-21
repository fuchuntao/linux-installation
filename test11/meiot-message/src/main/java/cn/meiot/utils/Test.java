package cn.meiot.utils;

import cn.meiot.entity.vo.StatisticsEventTimeVo;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/10/21 14:22
 * @Copyright: www.spacecg.cn
 */
public class Test {
    public static void main(String[] args) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos=new ArrayList<>();
        statisticsEventTimeVos.add(new StatisticsEventTimeVo("20",88));
        supplementaryData(statisticsEventTimeVos);
        System.out.println(JSONObject.toJSONString(statisticsEventTimeVos));
    }
    private static void supplementaryData(List<StatisticsEventTimeVo> data){
        for (int i=0;i<24;i++){
            try {
                if (Integer.valueOf(data.get(i).getTime())!=i+1){
                    data.add(i,new StatisticsEventTimeVo(String.valueOf(i+1),99));
                }
            }catch (Exception e){
                e.printStackTrace();
                data.add(null);
            }
        }
    }
}
