package cn.meiot.exception;

/**
 * @Package cn.meiot.exception
 * @Description:
 * @author: 武有
 * @date: 2019/12/28 16:52
 * @Copyright: www.spacecg.cn
 */
public class AlarmException extends RuntimeException {
    public AlarmException(String message) {
        super(message);
    }
}
