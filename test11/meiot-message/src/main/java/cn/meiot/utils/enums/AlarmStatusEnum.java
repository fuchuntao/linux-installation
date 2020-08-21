package cn.meiot.utils.enums;

/**
 * @Package cn.meiot.utils.enums
 * @Description:
 * @author: 武有
 * @date: 2020/4/17 9:20
 * @Copyright: www.spacecg.cn
 */
public enum  AlarmStatusEnum {

    YIJIANBAOXIU(0),
    DAISHOULI(1),
    DAIWEIXIU(2),
    YIWEIXIU(3);
    /**
     * 0 一键报修 1待受理 2待维修 3已维修
     */
    private Integer value;

    public Integer value() {
        return this.value;
    }

    AlarmStatusEnum(Integer value) {
        this.value = value;
    }
  }
