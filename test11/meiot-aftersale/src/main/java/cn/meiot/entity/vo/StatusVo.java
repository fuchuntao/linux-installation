package cn.meiot.entity.vo;

import cn.meiot.exception.MyServiceException;
import cn.meiot.utils.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 12:25
 * @Copyright: www.spacecg.cn
 */
@Data
public class StatusVo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 时间
     */
    private String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    public String getDate() {
        return DateUtil.getCurrentTime();
    }

    public boolean isEmnty(){
        boolean flag=false;
        if (null == id) {
            throw new MyServiceException("Id不能为空","");
        }
        if (null == status) {
            throw new MyServiceException("状态不能为空","");
        }
        return flag;
    }
}
