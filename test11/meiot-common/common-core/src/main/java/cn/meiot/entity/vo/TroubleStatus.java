package cn.meiot.entity.vo;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/17 14:23
 * @Copyright: www.spacecg.cn
 */
public enum  TroubleStatus {

    BAOXIU(0),
    SHOULI(1),
    WANCHENG(2);

    private Integer status;
    public Integer value(){
        return this.status;
    }
    TroubleStatus(Integer status) {
        this.status = status;
    }}
