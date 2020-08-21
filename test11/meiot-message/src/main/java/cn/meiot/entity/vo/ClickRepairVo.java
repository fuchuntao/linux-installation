package cn.meiot.entity.vo;

import cn.meiot.exception.MyServiceException;
import cn.meiot.utils.ErrorCodeUtil;

import javax.validation.constraints.NotNull;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/17 9:39
 * @Copyright: www.spacecg.cn
 */
public class ClickRepairVo {

    /**
     * 故障消息ID
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 备注
     */
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (null == id) {
            throw new MyServiceException(ErrorCodeUtil.MESSAGE_PARAMETER_NOT_NULL);
        }
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
