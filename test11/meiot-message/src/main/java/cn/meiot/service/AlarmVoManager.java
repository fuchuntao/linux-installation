package cn.meiot.service;

import cn.meiot.entity.vo.AlarmVo;
import cn.meiot.entity.vo.WXMessageVo;
import cn.meiot.exception.AlarmException;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Package cn.meiot.service
 * @Description:
 * @author: 武有
 * @date: 2019/12/28 17:00
 * @Copyright: www.spacecg.cn
 */
public interface AlarmVoManager {
    SimpleDateFormat SD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat SDh = new SimpleDateFormat("HH:mm:ss");

    /**
     * 推送
     */
    void push(AlarmVo alarmVo);

    /**
     * 初始化
     * @param userIds
     * @param projectId
     * @param address
     * @param projectName
     */

    void init(List<Long> userIds, Integer projectId, String address, String projectName);

    /**
     * 微信推送
     * @param wxMessageVo
     */
    void wxPush(WXMessageVo wxMessageVo);



}
