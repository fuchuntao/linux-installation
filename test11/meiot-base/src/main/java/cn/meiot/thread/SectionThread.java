package cn.meiot.thread;

import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.mapper.WhiteListMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Package cn.meiot.thread
 * @Description:
 * @author: 武有
 * @date: 2019/11/27 11:31
 * @Copyright: www.spacecg.cn
 */

public class SectionThread extends Thread {
    private Firmware firmware;

    @Autowired
    private WhiteListMapper whiteListMapper;
    @Override
    public void run() {
      List<WhiteList> whiteLists= whiteListMapper.selectList(new QueryWrapper<WhiteList>().eq("firmware_id",firmware.getId()));
      if (null==whiteLists || whiteLists.size()<=0){
          return;
      }

    }
    public SectionThread(Firmware firmware){
        this.firmware=firmware;
    }
}
